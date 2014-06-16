package org.ancit.github.utils.pr.dialog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import org.ancit.bitbucket.utils.pr.BitBucketRestClient;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class BitBucketPRDialog extends Dialog {
	private Text txtUsername;
	private Text txtPassword;
	private Text txtTitle;
	private Text txtDescription;
	private Text txtSrcreponame;
	private Text txtSrcbranchname;
	private Text txtDestreponame;
	private Text txtDestbranchname;
	private String uri;
	private String branchSelected;
	private String repoName;

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param branchSelected 
	 * @param uri 
	 */
	public BitBucketPRDialog(Shell parentShell, String uri, String branchSelected) {
		super(parentShell);
		this.uri = uri;
		this.branchSelected = branchSelected;
		repoName = uri.replace("https://bitbucket.org/", "").replace(".git", "");
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		
		Group grpAuthentication = new Group(container, SWT.NONE);
		grpAuthentication.setLayout(new GridLayout(2, false));
		grpAuthentication.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpAuthentication.setText("Authentication");
		
		Label lblUsername = new Label(grpAuthentication, SWT.NONE);
		lblUsername.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUsername.setText("Username :");
		
		txtUsername = new Text(grpAuthentication, SWT.BORDER);
		txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtUsername.setText(repoName.split("/")[0]);
		
		Label lblPassword = new Label(grpAuthentication, SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPassword.setText("Password : ");
		
		txtPassword = new Text(grpAuthentication, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Group grpPullRequest = new Group(container, SWT.NONE);
		grpPullRequest.setLayout(new GridLayout(2, false));
		grpPullRequest.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpPullRequest.setText("Pull Request");
		
		Label lblTitle = new Label(grpPullRequest, SWT.NONE);
		lblTitle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTitle.setText("Title : ");
		
		txtTitle = new Text(grpPullRequest, SWT.BORDER);
		txtTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblDescription = new Label(grpPullRequest, SWT.NONE);
		lblDescription.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDescription.setText("Description :");
		
		txtDescription = new Text(grpPullRequest, SWT.BORDER | SWT.WRAP);
		txtDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite composite = new Composite(grpPullRequest, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		Group grpSource = new Group(composite, SWT.NONE);
		grpSource.setLayout(new GridLayout(2, false));
		grpSource.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpSource.setText("Source");
		
		Label lblRepositoryName = new Label(grpSource, SWT.NONE);
		lblRepositoryName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRepositoryName.setText("Repository Name : ");
		
		txtSrcreponame = new Text(grpSource, SWT.BORDER);
		txtSrcreponame.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		txtSrcreponame.setText(repoName);
		
		Label lblBranchName = new Label(grpSource, SWT.NONE);
		lblBranchName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBranchName.setText("Branch Name : ");
		
		txtSrcbranchname = new Text(grpSource, SWT.BORDER);
		txtSrcbranchname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtSrcbranchname.setText(branchSelected);
		
		Group grpDestination = new Group(composite, SWT.NONE);
		grpDestination.setLayout(new GridLayout(2, false));
		grpDestination.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpDestination.setText("Destination");
		
		Label lblRepositoryName_1 = new Label(grpDestination, SWT.NONE);
		lblRepositoryName_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRepositoryName_1.setText("Repository Name : ");
		
		txtDestreponame = new Text(grpDestination, SWT.BORDER);
		txtDestreponame.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblBranchName_1 = new Label(grpDestination, SWT.NONE);
		lblBranchName_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBranchName_1.setText("Branch Name : ");
		
		txtDestbranchname = new Text(grpDestination, SWT.BORDER);
		txtDestbranchname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtDestbranchname.setText(branchSelected);

		return container;
	}

	/**
	 * Create contents of the button bar.
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
		return new Point(716, 353);
	}
	
	@Override
	protected void okPressed() {
		//https://bitbucket.org/api/2.0/repositories/annamalai_chockalingam/message-translators/pullrequests/
		String url = "https://bitbucket.org/api/2.0/repositories/"+txtDestreponame.getText()+"/pullrequests/";
		
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("{");
		strBuffer.append("\n");
		strBuffer.append("\"title\":\""+txtTitle.getText()+"\",");strBuffer.append("\n");
		strBuffer.append("\"description\":\""+txtDescription.getText()+"\",");strBuffer.append("\n");
		strBuffer.append("\"source\": {");strBuffer.append("\n");
		strBuffer.append("\"branch\": {");strBuffer.append("\n");
		strBuffer.append("\"name\":\""+txtSrcbranchname.getText()+"\"");strBuffer.append("\n");
		strBuffer.append("},");strBuffer.append("\n");
		strBuffer.append("\"repository\": {");strBuffer.append("\n");
		strBuffer.append("\"full_name\":\""+txtSrcreponame.getText()+"\"");strBuffer.append("\n");
		strBuffer.append("}");strBuffer.append("\n");
		strBuffer.append("},\"destination\": {");strBuffer.append("\n");
		strBuffer.append("\"branch\":{");strBuffer.append("\n");
		strBuffer.append("\"name\":\""+txtDestbranchname.getText()+"\"");strBuffer.append("\n");
		strBuffer.append("}");strBuffer.append("\n");
		strBuffer.append("}");strBuffer.append("\n");
		strBuffer.append("}");strBuffer.append("\n");
		
		
		System.out.println(strBuffer);
		
		try {
			BitBucketRestClient.createPR(url, strBuffer.toString(), txtUsername.getText(), txtPassword.getText());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.okPressed();
	}
}
