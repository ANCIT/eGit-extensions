package org.ancit.github.utils.pr.dialog;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AuthenticationDialog extends Dialog {
	private Text txtUsername;
	private Text txtPassword;
	private String userName;
	private String password;
	private Button btnStoreToSecure;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public AuthenticationDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));

		Label lblUsername = new Label(container, SWT.NONE);
		lblUsername.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblUsername.setText("UserName : ");

		txtUsername = new Text(container, SWT.BORDER);
		txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lblPassword = new Label(container, SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblPassword.setText("Password : ");

		txtPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		btnStoreToSecure = new Button(container, SWT.CHECK);
		btnStoreToSecure.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 2, 1));
		btnStoreToSecure.setText("Store to Secure Storage");

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(453, 161);
	}

	@Override
	protected void okPressed() {
		// TODO Auto-generated method stub
		userName = txtUsername.getText();
		password = txtPassword.getText();

		if (btnStoreToSecure.getSelection()) {

			ISecurePreferences preferences = SecurePreferencesFactory
					.getDefault();
			ISecurePreferences node = preferences.node("eGitUserInfo");
			try {
				node.put("eGIT_USERNAME", userName, false);
				node.put("eGIT_PASSWORD", password, true);
			} catch (StorageException e1) {
				e1.printStackTrace();
			}

		}

		super.okPressed();

	}

	public String getUsername() {
		// TODO Auto-generated method stub
		return userName;
	}

	public String getPasword() {
		// TODO Auto-generated method stub
		return password;
	}

}
