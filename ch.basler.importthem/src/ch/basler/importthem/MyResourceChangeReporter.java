package ch.basler.importthem;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;

   public class MyResourceChangeReporter implements IResourceChangeListener {
      public void resourceChanged(IResourceChangeEvent event) {
         IResource res = event.getResource();

               System.out.print("Project ");
               System.out.print(res.getFullPath());
               System.out.println(" is about to be deleted.");
    
      }
   }