package org.ancit.github.utils.preferences;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class GitHubExtensionsPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	private Text txtUserName;
	private Text txtPassword;
	private Text txtHost;

	public GitHubExtensionsPreferencePage() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @wbp.parser.constructor
	 */
	public GitHubExtensionsPreferencePage(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public GitHubExtensionsPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(new GridLayout(1, false));
		
		Group grpLoginDetails = new Group(container, SWT.NONE);
		grpLoginDetails.setLayout(new GridLayout(2, false));
		grpLoginDetails.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpLoginDetails.setText("Login Details");
		
		Label lblUserName = new Label(grpLoginDetails, SWT.NONE);
		lblUserName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUserName.setText("User Name");
		
		txtUserName = new Text(grpLoginDetails, SWT.BORDER);
		txtUserName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblPassword = new Label(grpLoginDetails, SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPassword.setText("Password");
		
		txtPassword = new Text(grpLoginDetails, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblHost = new Label(grpLoginDetails, SWT.NONE);
		lblHost.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHost.setText("GitHub Enterprise Host");

		txtHost = new Text(grpLoginDetails, SWT.BORDER);
		txtHost.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtHost.setToolTipText("Enter your Enterprise URL, eg: git.eclipse.org");
		ISecurePreferences preferences = SecurePreferencesFactory.getDefault();
		if (preferences.nodeExists("eGitUserInfo")) {
			ISecurePreferences node = preferences.node("eGitUserInfo");
			try {
				txtUserName.setText(node.get("eGIT_USERNAME", "n/a"));
				txtPassword.setText(node.get("eGIT_PASSWORD", "n/a"));
				txtHost.setText(node.get("eGIT_HOST", ""));
			} catch (StorageException e1) {
				e1.printStackTrace();
			}
		}
		
		return container;
	}
	
	@Override
	protected void performApply() {
		ISecurePreferences preferences = SecurePreferencesFactory
	            .getDefault();
	        ISecurePreferences node = preferences.node("eGitUserInfo");
	        try {
	          node.put("eGIT_USERNAME", txtUserName.getText(), false);
	          node.put("eGIT_PASSWORD", txtPassword.getText(), true);
	          node.put("eGIT_HOST", txtHost.getText(), false);
	        } catch (StorageException e1) {
	          e1.printStackTrace();
	        }
	}

}
