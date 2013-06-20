package ch.basler.importthem.handlers;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DumpDialog extends TitleAreaDialog {

  private Map<String, Set<IFile>> projectMap;

  public DumpDialog(Shell parentShell, Map<String, Set<IFile>> projectMap2) {
    super(parentShell);
    projectMap = projectMap2;
  }

  @Override
  public void create() {
    super.create();
    // Set the title
    setTitle("importThem");
    // Set the message
 setMessage("You are in Dev mode. This is a dry run.", 
        IMessageProvider.NONE);

  }

  @Override
  protected Control createDialogArea(Composite parent) {
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    parent.setLayout(layout);

    TreeViewer treeViewer = new TreeViewer(parent);
    IContentProvider content = new ProjectMapContent();
	treeViewer.setContentProvider(content);
	treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
	treeViewer.setLabelProvider(new LabelProvider());
    treeViewer.setInput(projectMap);
    treeViewer.expandAll();
    return parent;
  }

  @Override
	public boolean isHelpAvailable() {
	  return false;
  }
  
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    GridData gridData = new GridData();
    gridData.verticalAlignment = GridData.FILL;
    gridData.horizontalSpan = 3;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = SWT.CENTER;

    parent.setLayoutData(gridData);
    // Create Add button
    // Own method as we need to overview the SelectionAdapter
    createOkButton(parent, OK, "Close", true);
    // Add a SelectionListener

   
  }

  protected Button createOkButton(Composite parent, int id, 
      String label,
      boolean defaultButton) {
    // increment the number of columns in the button bar
    ((GridLayout) parent.getLayout()).numColumns++;
    Button button = new Button(parent, SWT.PUSH);
    button.setText(label);
    button.setFont(JFaceResources.getDialogFont());
    button.setData(new Integer(id));
    button.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        if (isValidInput()) {
          okPressed();
        }
      }
    });
    if (defaultButton) {
      Shell shell = parent.getShell();
      if (shell != null) {
        shell.setDefaultButton(button);
      }
    }
    setButtonLayoutData(button);
    return button;
  }

  private boolean isValidInput() {
    return true;
  }
  
  @Override
  protected boolean isResizable() {
    return true;
  }


} 