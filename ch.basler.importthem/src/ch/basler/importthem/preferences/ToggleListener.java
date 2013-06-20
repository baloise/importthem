package ch.basler.importthem.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

public class ToggleListener implements Listener {

	private final Button[] buttons;	
	
	public ToggleListener(Button ... buttons) {
		this.buttons = buttons;
		for (Button b : buttons) {
			b.addListener(SWT.Selection, this);
		}
	}


	@Override
	public void handleEvent(Event event) {
		for (Button b : buttons) {
				b.setSelection(event.widget == b);				
		}
	}

}
