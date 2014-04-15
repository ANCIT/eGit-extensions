package org.ancit.github.utils.pr.wizards;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.ancit.github.utils.pr.dialog.AuthenticationDialog;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.PullRequestMarker;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.ui.UIPreferences;
import org.eclipse.egit.ui.internal.repository.tree.RefNode;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

public class PullRequestWizardPage extends WizardPage {
	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public String getText(Object element) {
			if (element instanceof PullRequest) {
				PullRequest pr = (PullRequest) element;
				return pr.getNumber() + ":" + pr.getTitle();		
			}
			return super.getText(element);
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof RepositoryCommit) {
				RepositoryCommit commit = (RepositoryCommit) element;
				switch (columnIndex) {
				case 0:
					return commit.getAuthor().getLogin();
				case 1:
					return commit.getCommit().getMessage();
				case 2:
					return new SimpleDateFormat("EEE, MMM dd, yyyy").format(commit.getCommit().getAuthor().getDate());
				case 3:
					return commit.getSha().substring(0, 6);

				default:
					return "";
				}
			}
			
			return getText(element);
		}
	}

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
	private Table table;
	private PullRequestService prService;
	private TableViewer tableViewer;
	private Combo toBranch;
	private Combo fromBranch;
	private Button btnGeneratePullRequest;
	private Table commitTable;
	private RepositoryId repo;
	private TableViewer commitViewer;
	protected Action copyUrlAction;
	private CommitService commitService;

	
	/**
	 * Create the wizard.
	 * @param myRepository 
	 */
	public PullRequestWizardPage(RefNode refNode) {
		super("wizardPage");
		setTitle("Pull Request Creation");
		setDescription("Select Branch to Raise Pull Request.");
		
		this.refNode=refNode;
		this.myRepository = refNode.getRepository();
		
		GitHubClient client = new GitHubClient();
		configure(client);
		
		prService = new PullRequestService(client);
		commitService = new CommitService(client);
		
	}
	

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		this.setPageComplete(false);
		
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
			txtTitle.addFocusListener(new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent e) {
					if(txtDescription.getText().trim().isEmpty()){
					txtDescription.setText(txtTitle.getText());
					}
				}
				
				@Override
				public void focusGained(FocusEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			// create the decoration for the text component
			final ControlDecoration deco = new ControlDecoration(txtTitle, SWT.TOP
			  | SWT.LEFT);

			// use an existing image
			Image image = FieldDecorationRegistry.getDefault()
			  .getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION)
			  .getImage();

			// set description and image
			deco.setDescriptionText("Use DownArrow to see possible values");
			deco.setImage(image);

			// always show decoration
			deco.setShowOnlyOnFocus(false);
			
			Label lblDescription = new Label(grpPullRequestInfo, SWT.NONE);
			lblDescription.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblDescription.setText("Description");
			
			txtDescription = new Text(grpPullRequestInfo, SWT.BORDER);
			txtDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
			toBranch = new Combo(container, SWT.READ_ONLY);
			toBranch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			GridDataFactory.fillDefaults().grab(true, false).applyTo(toBranch);
		
			Label label = new Label(container, SWT.NONE);
			label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			label.setText("<-");
			label.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_BACK));
		
			fromBranch = new Combo(container, SWT.READ_ONLY);
			fromBranch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			
			btnGeneratePullRequest = new Button(container, SWT.NONE);
			
			btnGeneratePullRequest.setText("Create");
			
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
			if(!remote.isEmpty() && !remote.equals(".")){
				fromBranch.setText(remote+"/"+merge);
			}else{
				fromBranch.setText(fromBranch.getItem(0));
			}
			toBranch.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					List<PullRequest> pullRequest = getPullRequests(false);
					tableViewer.setInput(pullRequest);
					setDescription(null);
				}
			});
			
			
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
					List<PullRequest> pullRequest = getPullRequests(true);
					tableViewer.setInput(pullRequest);

				} else {
					setErrorMessage("Enter Valid Information in Title/Description.");
					setPageComplete(true);
				}
			}
		});
		
		ISecurePreferences preferences = SecurePreferencesFactory.getDefault();
		if (!preferences.nodeExists("eGitUserInfo")) {
			setMessage("Use PreferenceStore to configure your GitHub Account to avoid being prompted for UserName and Password everytime.", MessageDialog.INFORMATION);
		}
		
		setControl(container);
		
		SashForm sashForm = new SashForm(container, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		
		tableViewer = new TableViewer(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnPullRequests = new TableColumn(table, SWT.NONE);
		tblclmnPullRequests.setWidth(225);
		tblclmnPullRequests.setText("Pull Requests");
		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if(selection.isEmpty()) {
					return;
				}
				
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection sSelection = (IStructuredSelection) selection;
					Object firstElement = sSelection.getFirstElement();
					if (firstElement instanceof PullRequest) {
						PullRequest pr = (PullRequest) firstElement;
						
						try {
							List<RepositoryCommit> commits = prService.getCommits(repo, pr.getNumber());
							commitViewer.setInput(commits);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
				}
			}
		});
		makeActions();
		hookContextMenu();
		
		final TabFolder tabFolder = new TabFolder(sashForm, SWT.NONE);
		
		TabItem tbtmCommits = new TabItem(tabFolder, SWT.NONE);
		tbtmCommits.setText("Commits");
		
		commitViewer = new TableViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		commitTable = commitViewer.getTable();
		commitTable.setLinesVisible(true);
		commitTable.setHeaderVisible(true);
		commitViewer.setContentProvider(ArrayContentProvider.getInstance());
		commitViewer.setLabelProvider(new TableLabelProvider());
		
		
		
		tbtmCommits.setControl(commitTable);
		
		TableColumn tblclmnUser = new TableColumn(commitTable, SWT.NONE);
		tblclmnUser.setWidth(100);
		tblclmnUser.setText("User");
		
		TableColumn tblclmnDescription = new TableColumn(commitTable, SWT.NONE);
		tblclmnDescription.setWidth(500);
		tblclmnDescription.setText("Description");
		
		TableColumn tblclmnDate = new TableColumn(commitTable, SWT.NONE);
		tblclmnDate.setWidth(150);
		tblclmnDate.setText("Date");
		
		TableColumn tblclmnCommitId = new TableColumn(commitTable, SWT.NONE);
		tblclmnCommitId.setWidth(100);
		tblclmnCommitId.setText("Commit ID");
		
		TabItem tbtmFilesChanged = new TabItem(tabFolder, SWT.NONE);
		tbtmFilesChanged.setText("Files Changed");
		
		final TabItem tbtmBrowser = new TabItem(tabFolder, SWT.NONE);
		tbtmBrowser.setText("Browser");
		
			browser = new Browser(tabFolder, SWT.NONE);
			tbtmBrowser.setControl(browser);
			browser.setUrl("https://github.com/login");
			sashForm.setWeights(new int[] {114, 447});
			
			commitViewer.addDoubleClickListener(new IDoubleClickListener() {
				
				@Override
				public void doubleClick(DoubleClickEvent event) {
					IStructuredSelection sSelection = (IStructuredSelection)event.getSelection();
					Object firstElement = sSelection.getFirstElement();
					if (firstElement instanceof RepositoryCommit) {
						RepositoryCommit commit = (RepositoryCommit) firstElement;
						browser.setUrl("https://github.com/"+commit.getAuthor().getLogin()+"/"+repositoryName+"/commit/"+commit.getSha());
						tabFolder.setSelection(tbtmBrowser);
					}
					
				}
			});
			
			

			
				Set<String> commits = getComments();
				
				// help the user with the possible inputs
				// "." and "#" activate the content proposals
				char[] autoActivationCharacters = new char[] { '.', '#' ,','};
				KeyStroke keyStroke;
				//
				try {
				  keyStroke = KeyStroke.getInstance(SWT.ARROW_DOWN);
				  ContentProposalAdapter adapter = new ContentProposalAdapter(txtTitle,
				    new TextContentAdapter(),
				    new SimpleContentProposalProvider(commits.toArray(new String[commits.size()])),
				      keyStroke, autoActivationCharacters);
				  adapter.setPropagateKeys(true);
				  adapter.setAutoActivationDelay(1500);
				  adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_INSERT);
				    } catch (Exception e1) {
				  e1.printStackTrace();
				} 
	}

	private void makeActions() {
		copyUrlAction = new Action("Copy URL") {
			@Override
			public void run() {
				IStructuredSelection sSelection = ((IStructuredSelection)tableViewer.getSelection());
				if(!sSelection.isEmpty()) {
					PullRequest pullRequest = (PullRequest)sSelection.getFirstElement();
					Clipboard cp = new Clipboard(Display.getDefault());
					TextTransfer textTransfer = TextTransfer.getInstance();
			        cp.setContents(new Object[] { pullRequest.getHtmlUrl() },
			            new Transfer[] { textTransfer });
				}
			}
		};
		
	}


	private void hookContextMenu() {
		MenuManager mgr = new MenuManager();
		mgr.setRemoveAllWhenShown(true);
		mgr.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(copyUrlAction);
			}
		});
		
		Menu menu = mgr.createContextMenu(tableViewer.getTable());
		tableViewer.getTable().setMenu(menu);
		
		
	}


	protected List<PullRequest> getPullRequests(boolean isCreation) {
		getBranchConfiguration(toBranch, TO_BRANCH);
		getBranchConfiguration(fromBranch, FROM_BRANCH);
		String[] toList = toBranchName.split(":");
		repo = new RepositoryId(toList[0], repositoryName);
		List<PullRequest> allPullRequests = new ArrayList<PullRequest>();
		try {
			allPullRequests = prService.getPullRequests(repo, "open");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<PullRequest> brPullRequests = new ArrayList<PullRequest>();
		boolean pullRequestExists =false;
		String errorMessage = "";
		for (PullRequest pullRequest : allPullRequests) {
			if(pullRequest.getHead().getRef().equals(toList[1])) {
				brPullRequests.add(pullRequest);
				if (pullRequest.getHead().getUser().getLogin().equals(prService.getClient().getUser())) {
					pullRequestExists = true;
					errorMessage=pullRequest.getBase().getLabel()+" & "+pullRequest.getHead().getLabel();
				}
			}
		}
		
		if(!isCreation) {
		
		if(pullRequestExists) {
			btnGeneratePullRequest.setEnabled(false);
			setErrorMessage("Pull Request already Exists btw "+errorMessage);
		}else {
			btnGeneratePullRequest.setEnabled(true);
			setErrorMessage(null);
		}
		
		}
		return brPullRequests;
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
				uri = uri.replace("https://github.com/", "").replace("ssh://git@github.com/", "")
						.replace("git@github.com:", "").replace(".git", "");
				fromBranchName = uri.substring(0, uri.lastIndexOf("/"));
				fromBranchName += ":" + branchName;

				repositoryName=uri.split("/")[1];
				baseURL = "https://github.com/" + uri + "/compare/";
			} else {
				uri = uri.replace("https://github.com/", "").replace(
						"git@github.com:", "").replace("ssh://git@github.com/", "");
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
			setErrorMessage(null);
			setMessage("Pull request created Successfully..!");
			browser.setUrl(newPullRequest.getHtmlUrl());
			
			setPageComplete(true);
			
		} catch (IOException e) {
			setErrorMessage(e.getMessage());
			e.printStackTrace();
			browser.setUrl("https://github.com/"+toBranchName.split(":")[0]+"/"+repositoryName+"/pulls");
			setPageComplete(false);
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
		
		ISecurePreferences preferences = SecurePreferencesFactory.getDefault();
		if (preferences.nodeExists("eGitUserInfo")) {
			ISecurePreferences node = preferences.node("eGitUserInfo");
			try {
				String user = node.get("eGIT_USERNAME", "n/a");
				String password = node.get("eGIT_PASSWORD", "n/a");
				if(user.isEmpty() || password.isEmpty()) {
					setErrorMessage("UserName or Password is not configured properly in Preferences");
					getUserAuthentication(client);
				}
				client.setCredentials(user, password);
				client.setUserAgent(user);
			} catch (StorageException e1) {
				e1.printStackTrace();
			}
		} else {

			getUserAuthentication(client);
		}
		return client;
	}


	private void getUserAuthentication(GitHubClient client) {
		AuthenticationDialog dialog = new AuthenticationDialog(Display
				.getDefault().getActiveShell());

		if (IDialogConstants.OK_ID == dialog.open()) {
			user = dialog.getUsername();
			String password = dialog.getPasword();
			client.setCredentials(user, password);
			client.setUserAgent(user);
		}
	}
	
	private Set<String> getComments() {
		IPreferenceStore preferenceStore = org.eclipse.egit.ui.Activator.getDefault().getPreferenceStore();
		String all = preferenceStore.getString(
				UIPreferences.COMMIT_DIALOG_HISTORY_MESSAGES);
		if (all.length() == 0)
			return Collections.emptySet();
		int max = preferenceStore.getInt(
				UIPreferences.COMMIT_DIALOG_HISTORY_SIZE);
		if (max < 1)
			return Collections.emptySet();
		XMLMemento memento;
		try {
			memento = XMLMemento.createReadRoot(new StringReader(all));
		} catch (WorkbenchException e) {
			org.eclipse.egit.ui.Activator.logError(
					"Error reading commit message history", e); //$NON-NLS-1$
			return Collections.emptySet();
		}
		Set<String> messages = new LinkedHashSet<String>();
		for (IMemento child : memento.getChildren("message")) {
			messages.add(child.getTextData());
			if (messages.size() == max)
				break;
		}
		return messages;
	}

}
