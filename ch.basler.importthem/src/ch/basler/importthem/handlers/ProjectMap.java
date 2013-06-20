package ch.basler.importthem.handlers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;

public final class ProjectMap extends HashMap<String, Set<IFile>> {
	private static final long serialVersionUID = 1L;

	@Override
    public Set<IFile> get(Object key) {
      Set<IFile> value = super.get(key);
      if (value == null) {
        value = new HashSet<IFile>();
        put((String)key, value);
      }
      return value;
    }
}