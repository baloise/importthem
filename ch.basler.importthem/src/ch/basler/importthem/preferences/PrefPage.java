package ch.basler.importthem.preferences;

import java.io.FileInputStream;
import java.io.IOException;

import ch.basler.importthem.Activator;
import ch.basler.importthem.dev.DevProject;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.preferences.formatter.SnippetPreview.PreviewSnippet;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class PrefPage extends PreferencePage implements IWorkbenchPreferencePage {

	private Text text;
	Listener deselectProd = new Listener() {

		@Override
		public void handleEvent(Event event) {
			Button source = (Button) event.widget;
			if (source.getSelection())
				return;
			try {
				DevProject.createDevProject();
				text.setEnabled(false);
				DevProject.openEditor();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};
	
	Listener deselectDev = new Listener() {

		@Override
		public void handleEvent(Event event) {
			Button source = (Button) event.widget;
			if (source.getSelection())
				return;
			try {
				// copy script and delete dev project 
				text.setText(DevProject.getScriptContent());
				text.setEnabled(true);
				DevProject.delete(null);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};
	
	public PrefPage() {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("A demonstration of a preference page implementation");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}

	@Override
	public boolean performOk() {
		getPreferenceStore().setValue(PrefConstants.GROOVY_SCRIPT, text.getText());
		return true;
	}

	@Override
	protected Control createContents(Composite parent) {
		noDefaultAndApplyButton();
		parent.setLayout(new GridLayout(1, true));
		text = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		String prefValue = getPreferenceStore().getString(PrefConstants.GROOVY_SCRIPT);
		text.setText(prefValue);
		
		boolean devMode = DevProject.exists();
		text.setEnabled(!devMode);
		
		ModeSelection selection = new ModeSelection(parent, SWT.NO);
		
		selection.btnDev.setSelection(devMode);
		selection.btnProd.setSelection(!devMode);
		selection.btnProd.addListener(SWT.Selection, deselectProd);
		selection.btnDev.addListener(SWT.Selection, deselectDev);
		
		return parent;

	}

}