package org.ancit.github.utils.pr.wizards;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.AuthenticationEvent;
import org.eclipse.swt.browser.AuthenticationListener;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

public class PullRequestWizardPage extends WizardPage {

	private Repository myRepository = null;
	private String baseURL;
	private String toBranchName;
	private String fromBranchName;
	
	/**
	 * Create the wizard.
	 * @param myRepository 
	 */
	public PullRequestWizardPage(Repository myRepository) {
		super("wizardPage");
		setTitle("Pull Request Creation");
		setDescription("Select Branch to Raise Pull Request.");
		setMessage("You should be logged in to work on Private Repositories.", MessageDialog.INFORMATION);
		this.myRepository = myRepository;
	}
	

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		
		
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(4, false));
		
			final Combo toBranch = new Combo(container, SWT.READ_ONLY);
			toBranch.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					
						StoredConfig config = myRepository.getConfig();
						String refName = toBranch.getText();
						String branchName = refName.substring(refName.indexOf("/")+1);
						String remoteName = refName.substring(0,refName.indexOf("/"));
						
						System.out.println("Branch Name "+branchName);
						RemoteConfig rc;
						try {
							rc = new RemoteConfig(config,
									remoteName);
							List<URIish> urIs = rc.getURIs();
							String uri = urIs.get(0).toString();
							uri = uri.replace("https://github.com/", "").replace("git@github.com:", "");
							toBranchName = uri.substring(0, uri.lastIndexOf("/"));
							toBranchName += ":"+branchName;
							
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						}
						
						
						
					
				}
			});
			toBranch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			GridDataFactory.fillDefaults().grab(true, false).applyTo(toBranch);
		
			Label label = new Label(container, SWT.NONE);
			label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			label.setText("<-");
		
			final Combo fromBranch = new Combo(container, SWT.READ_ONLY);
			fromBranch.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					StoredConfig config = myRepository.getConfig();
					String refName = fromBranch.getText();
					String branchName = refName.substring(refName.indexOf("/")+1);
					String remoteName = refName.substring(0,refName.indexOf("/"));
					
					System.out.println("Branch Name "+branchName);
					RemoteConfig rc;
					try {
						rc = new RemoteConfig(config,
								remoteName);
						List<URIish> urIs = rc.getURIs();
						String uri = urIs.get(0).toString();
						
						uri = uri.replace("https://github.com/", "").replace("git@github.com:", "").replace(".git", "");
						fromBranchName = uri.substring(0, uri.lastIndexOf("/"));
						fromBranchName += ":"+branchName;
						
						baseURL = "https://github.com/"+ uri + "/compare/";
						
						
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			});
			fromBranch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			
			Button btnGeneratePullRequest = new Button(container, SWT.NONE);
			
			btnGeneratePullRequest.setText("Pull");
		
			final Browser browser = new Browser(container, SWT.NONE);
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		btnGeneratePullRequest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(toBranch.getText().length() > 0 && fromBranch.getText().length() > 0) {
					browser.setUrl(createURL());
					setMessage(null);
				}
			}
		});
		
		setControl(container);
	}
	
	private String createURL() {
		String url = baseURL + toBranchName +"..."+fromBranchName;
		return url;
	}
	

}
