package ch.basler.importthem.handlers;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ProjectMapContent implements ITreeContentProvider {

	private ProjectMap map;

	@Override
	public void dispose() {
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		map = (ProjectMap) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		map  = (ProjectMap) inputElement;
		return map.keySet().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof String) {
			return map.get(parentElement).toArray();			
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	
}
