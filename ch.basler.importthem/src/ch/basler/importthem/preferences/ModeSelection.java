package ch.basler.importthem.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ModeSelection extends Composite {

	
	public final Button btnProd;
	public final Button btnDev;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ModeSelection(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));
		btnProd = new Button(this, SWT.RADIO);
		btnProd.setText("Prod");
		new Label(this, SWT.NONE);

		btnDev = new Button(this, SWT.RADIO);
		btnDev.setText("Dev");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
