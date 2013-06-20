package ch.basler.importthem.handlers;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

public class Util {

	
  /**
 * scans the folders recursively and adds one entry (parent folder name -> .project) for every .project file found 
 * @param workingSet2dotProjects the map of workings set names -> .project files
 * @param folders the folders to scan
 * @return the in-parameter workingSet2dotProjects
 */
static Map<String, Set<IFile>> seekProjects(Map<String, Set<IFile>> workingSet2dotProjects, Folder... folders) throws Exception {
    for (Folder folder : folders) {
      addIfProject(folder, workingSet2dotProjects);
      for (IResource res : folder.members()) {
        if (res instanceof Folder) {
          seekProjects(workingSet2dotProjects, (Folder)res);
        }
      }
    }
    return workingSet2dotProjects;
  }

  static void addIfProject(Folder folder, Map<String, Set<IFile>> workingSet2dotProjects) {
    IFile dotProject = folder.getFile(".project");
    if (dotProject.exists()) {
      workingSet2dotProjects.get(folder.getParent().getName()).add(dotProject);
    }
  }

}
