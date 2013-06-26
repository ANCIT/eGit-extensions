package org.ancit.github.utils.pr.wizards;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.ancit.github.utils.pr.dialog.AuthenticationDialog;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.PullRequestMarker;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.ui.internal.repository.tree.RefNode;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.CredentialItem.Username;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class PullRequestWizardPage extends WizardPage {

	private Repository myRepository = null;
	private String baseURL;
	private String toBranchName;
	private String fromBranchName;
	private RefNode refNode;
	private int FROM_BRANCH = 1;
	private int TO_BRANCH = 2;
	private String repositoryName;
	private Browser browser;
	private Text txtTitle;
	private Text txtDescription;
	private String user;

	
	/**
	 * Create the wizard.
	 * @param myRepository 
	 */
	public PullRequestWizardPage(RefNode refNode) {
		super("wizardPage");
		setTitle("Pull Request Creation");
		setDescription("Select Branch to Raise Pull Request.");
		setMessage("You should be logged in to work on Private Repositories.", MessageDialog.INFORMATION);
		this.refNode=refNode;
		this.myRepository = refNode.getRepository();
	}
	

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		
		
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(4, false));
			
			Group grpPullRequestInfo = new Group(container, SWT.NONE);
			grpPullRequestInfo.setLayout(new GridLayout(2, false));
			grpPullRequestInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
			grpPullRequestInfo.setText("Pull Request Info");
			
			Label lblTitle = new Label(grpPullRequestInfo, SWT.NONE);
			lblTitle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblTitle.setText("Title");
			
			txtTitle = new Text(grpPullRequestInfo, SWT.BORDER);
			txtTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			
			Label lblDescription = new Label(grpPullRequestInfo, SWT.NONE);
			lblDescription.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblDescription.setText("Description");
			
			txtDescription = new Text(grpPullRequestInfo, SWT.BORDER);
			txtDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
			final Combo toBranch = new Combo(container, SWT.READ_ONLY);
			toBranch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			GridDataFactory.fillDefaults().grab(true, false).applyTo(toBranch);
		
			Label label = new Label(container, SWT.NONE);
			label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			label.setText("<-");
			label.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_BACK));
		
			final Combo fromBranch = new Combo(container, SWT.READ_ONLY);
			fromBranch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			
			Button btnGeneratePullRequest = new Button(container, SWT.NONE);
			
			btnGeneratePullRequest.setText("Create");
			new Label(container, SWT.NONE);
			new Label(container, SWT.NONE);
			new Label(container, SWT.NONE);
			new Label(container, SWT.NONE);
		
			browser = new Browser(container, SWT.NONE);
			browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
			browser.setUrl("https://github.com/login");
			
		try {
			for (Ref ref : myRepository.getRefDatabase()
					.getRefs(Constants.R_REMOTES).values()) {
				String name = ref.getName().trim();
				name = name.replace("refs/remotes/", "");
				toBranch.add(name);
				fromBranch.add(name);
			}
			
			String branchSelected=refNode.getObject().getName();
			branchSelected=branchSelected.substring(branchSelected.lastIndexOf("/")+1);
			
			String remote = myRepository.getConfig().getString(
				    ConfigConstants.CONFIG_BRANCH_SECTION, branchSelected,
				    ConfigConstants.CONFIG_KEY_REMOTE);
			String merge = myRepository.getConfig().getString(
				    ConfigConstants.CONFIG_BRANCH_SECTION, branchSelected,
				    ConfigConstants.CONFIG_KEY_MERGE);

			merge=merge.substring(merge.lastIndexOf("/")+1);
			//System.out.println(remote+"/"+merge);
			toBranch.setText(toBranch.getItem(0));
			fromBranch.setText(remote+"/"+merge);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		btnGeneratePullRequest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(toBranch.getText().length() > 0 && fromBranch.getText().length() > 0 && txtDescription.getText().length() > 0 && txtTitle.getText().length() > 0) {
					getBranchConfiguration(toBranch, TO_BRANCH);
					getBranchConfiguration(fromBranch,FROM_BRANCH);
//					browser.setUrl(createURL());
					setMessage(null);					
					generatePullRequest();
				} else {
					setErrorMessage("Enter Valid Information in Title/Description.");
				}
			}
		});
		
		setControl(container);
	}

	private void getBranchConfiguration(final Combo branch, int type) {
		StoredConfig config = myRepository.getConfig();
		String refName = branch.getText();
		String branchName = refName.substring(refName.indexOf("/")+1);
		String remoteName = refName.substring(0,refName.indexOf("/"));
		
		//System.out.println("Branch Name "+branchName);
		RemoteConfig rc;
		try {
			rc = new RemoteConfig(config,
					remoteName);
			List<URIish> urIs = rc.getURIs();
			String uri = urIs.get(0).toString();
			
			if (type == FROM_BRANCH) {
				uri = uri.replace("https://github.com/", "")
						.replace("git@github.com:", "").replace(".git", "");
				fromBranchName = uri.substring(0, uri.lastIndexOf("/"));
				fromBranchName += ":" + branchName;

				repositoryName=uri.split("/")[1];
				baseURL = "https://github.com/" + uri + "/compare/";
			} else {
				uri = uri.replace("https://github.com/", "").replace(
						"git@github.com:", "");
				toBranchName = uri.substring(0, uri.lastIndexOf("/"));
				toBranchName += ":" + branchName;
			}
			
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
	}
	
	protected void generatePullRequest() {
		// TODO Auto-generated method stub
		try {
			GitHubClient client = new GitHubClient();
			configure(client);
			
			PullRequestService prService = new PullRequestService(client);
			
			PullRequest request = new PullRequest();
			request.setBody(txtDescription.getText());
			request.setTitle(txtTitle.getText());
			PullRequestMarker headMarker = new PullRequestMarker();
			headMarker.setRef(fromBranchName);
			headMarker.setLabel(fromBranchName);
			request.setHead(headMarker);
			
			
			
			headMarker = new PullRequestMarker();
			String[] toList = toBranchName.split(":");
			headMarker.setRef(toList[1]);
			headMarker.setLabel(toList[1]);
			
			request.setBase(headMarker);
			RepositoryId repo = new RepositoryId(toList[0], repositoryName);
			
			PullRequest newPullRequest = prService.createPullRequest(repo, request);
			System.out.println(newPullRequest.getHtmlUrl());
			setErrorMessage(null);
			setMessage("Pull request created Successfully..!");
			browser.setUrl(newPullRequest.getHtmlUrl());
			
		} catch (IOException e) {
			setErrorMessage(e.getMessage());
			e.printStackTrace();
			browser.setUrl("https://github.com/"+toBranchName.split(":")[0]+"/"+repositoryName+"/pulls");
		}

		
	}
	
	 private String createURL() {
		  String url = baseURL + toBranchName +"..."+fromBranchName;
		  return url;
	 }
	
	/**
	 * Configure client
	 *
	 * @param client
	 * @return specified client
	 */
	protected GitHubClient configure(GitHubClient client) {
		AuthenticationDialog dialog = new AuthenticationDialog(Display
				.getDefault().getActiveShell());

		if (IDialogConstants.OK_ID == dialog.open()) {
			user = dialog.getUsername();
			String password = dialog.getPasword();
			client.setCredentials(user, password);
			client.setUserAgent(user);
		}
		return client;
	}

}
