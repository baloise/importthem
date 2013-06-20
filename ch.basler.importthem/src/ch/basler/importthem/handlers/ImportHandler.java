package ch.basler.importthem.handlers;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.basler.importthem.Activator;
import ch.basler.importthem.dev.DevProject;
import ch.basler.importthem.preferences.PrefConstants;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * http://plugination.org/?page_id=44
 * http://svn.codespot.com/a/eclipselabs.org/plugination
 * /trunk/idetools/org.plugination.featureworkingsets/org.
 * plugination.featureworkingsets
 * /src/org/plugination/featureworkingsets/SyncFeaturesWithWorkingSetsHandler
 * .java
 * 
 * http://eclipse.dzone.com/articles/eclipse-working-sets-explained
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ImportHandler extends AbstractHandler {

	public ImportHandler() {
	}

	public Object execute(final ExecutionEvent event) throws ExecutionException {

		final IStructuredSelection structuredSelection = (IStructuredSelection) HandlerUtil.getActiveMenuSelection(event);
		final Folder[] selection = (Folder[]) structuredSelection.toList().toArray(new Folder[0]);
		Map<String, Set<IFile>> projectMap = new ProjectMap();
		 

		String script = getScript();

		Binding binding = new Binding();
		binding.setVariable("projectMap", projectMap);
		binding.setVariable("selection", selection);
		GroovyShell shell = new GroovyShell(getClass().getClassLoader(), binding );
		Script parse = shell.parse(script);
		Method[] declaredMethods = parse.getClass().getDeclaredMethods();
		
		for (Method method : declaredMethods) {
			if("importThem".equals(method.getName())){
				try {
					Object res = method.invoke(parse, new Object[]{projectMap,selection});
					if(res instanceof ProjectMap){
						projectMap = (ProjectMap) res;
					} 
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		}
		

		if (DevProject.exists()) {
			dump(projectMap);
			return true;
		}
		final Map<String, Set<IFile>> finalProjectMap = projectMap;
		IWorkspaceRunnable job = new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) {
				monitor.beginTask("Generating working sets", IProgressMonitor.UNKNOWN);
				try {
					try {
						imprtAll(finalProjectMap, monitor);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} finally {
					monitor.done();
				}
			}
		};
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			workspace.run(job, null);
		} catch (CoreException e) {
			throw new ExecutionException(e.getMessage(), e);
		}
		return null;
	}

	private void dump(Map<String, Set<IFile>> projectMap) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		DumpDialog dialog = new DumpDialog(shell, projectMap);
		dialog.create();
		dialog.open(); 
		System.out.println(projectMap);
	}

	private String getScript() throws ExecutionException {
		if (DevProject.exists()) {
			try {
				return DevProject.getScriptContent();
			} catch (Exception e) {
				throw new ExecutionException("Could not read " + DevProject.SCRIPT.getAbsolutePath(), e);
			}
		}
		return Activator.getDefault().getPreferenceStore().getString(PrefConstants.GROOVY_SCRIPT);
	}

	private IContainer root(IContainer container) {
		return container.getParent() == null ? container : root(container.getParent());
	}

	private void imprtAll(Map<String, Set<IFile>> workingSet2dotProjects, IProgressMonitor monitor) throws CoreException {
		if (workingSet2dotProjects == null || workingSet2dotProjects.isEmpty()) {
			return;
		}
		monitor.subTask("Importing");
		SubProgressMonitor sub = new SubProgressMonitor(monitor, workingSet2dotProjects.values().size() + 1);
		for (String workingSetname : workingSet2dotProjects.keySet()) {
			Set<IFile> dotProjects = workingSet2dotProjects.get(workingSetname);
			if (!dotProjects.isEmpty()) {
				IWorkingSet workingSet = createWorkingSet(workingSetname);
				List<IProject> projects = new ArrayList<IProject>();
				for (IFile dotProject : dotProjects) {
					projects.add(imprt(dotProject, sub));
					monitor.worked(1);
					sub.worked(1);
				}
				workingSet.setElements(concat(workingSet.getElements(), projects.toArray(new IAdaptable[projects.size()])));
			}
		}
		refreshPackageExplorer();
		sub.worked(1);
		sub.done();
	}

	public static <T> T[] concat(T[] first, T... second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	private IWorkingSet createWorkingSet(String name) {
		IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
		IWorkingSet ws = workingSetManager.getWorkingSet(name);
		if (ws == null) {
			ws = workingSetManager.createWorkingSet(name, new IAdaptable[0]);
			ws.setId("org.eclipse.jdt.ui.JavaWorkingSetPage");
			workingSetManager.addWorkingSet(ws);
		}
		return ws;
	}

	private void refreshPackageExplorer() {
		PackageExplorerPart explorer = getActivePackageExplorer();
		if (explorer != null) {
			explorer.rootModeChanged(PackageExplorerPart.WORKING_SETS_AS_ROOTS);
			IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
			IWorkingSet[] sortedWorkingSets = workingSetManager.getAllWorkingSets();
			explorer.getWorkingSetModel().addWorkingSets(sortedWorkingSets);
			explorer.getWorkingSetModel().configured();
		}
	}

	public static IProject imprt(IFile dotProject, IProgressMonitor monitor) throws CoreException {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProjectDescription projectDescription;
		projectDescription = workspace.loadProjectDescription(dotProject.getLocation());
		IProject project = workspace.getRoot().getProject(projectDescription.getName());
		JavaCapabilityConfigurationPage.createProject(project, projectDescription.getLocationURI(), monitor);
		return project;
	}

	private PackageExplorerPart getActivePackageExplorer() {
		final Object[] findView = new Object[1];
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				findView[0] = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(JavaUI.ID_PACKAGES);
				if (findView[0] == null) {
					try {
						findView[0] = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(JavaUI.ID_PACKAGES);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			}
		});
		return (PackageExplorerPart) findView[0];
	}
}
