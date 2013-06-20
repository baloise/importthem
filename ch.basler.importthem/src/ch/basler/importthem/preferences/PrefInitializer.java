package ch.basler.importthem.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import ch.basler.importthem.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PrefInitializer extends AbstractPreferenceInitializer {

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
   */
  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    store.setDefault(PrefConstants.GROOVY_SCRIPT, "\n" + "import org.eclipse.core.internal.resources.Folder;\n"
        + "import org.eclipse.core.resources.IFile;\n" + "import static ch.basler.importthem.handlers.Util.*\n" + "\n"
        + "/**\n" + " * @param map comes in empty - you fill it with working set name -> .project file mappings\n"
        + " * @param selection the folders selected by the user\n" + " */\n"
        + "def importThem(Map<String, Set<IFile>> map, Folder[] selection){\n" + "	seekProjects(map,selection)\n"
        + "	// you'd better use the dev mode to edit this\n"
        + "	// the purpose of this property is to eventually get a managed eclipse install \n"
        + "	// something like workspace mechanic \n" + "}");
  }

}
