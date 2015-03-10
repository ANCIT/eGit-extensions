package org.ancit.github.utils.forkvis.views;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.ancit.github.utils.Activator;
import org.ancit.github.utils.GithubService;
import org.ancit.github.utils.forkvis.dialogs.RepositorySelectionDialog;
import org.ancit.github.utils.forkvis.model.ForkNode;
import org.ancit.github.utils.preferences.GitHubExtensionsPreferencePage;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.WatcherService;
import org.eclipse.egit.ui.internal.clone.GitCloneWizard;
import org.eclipse.egit.ui.internal.fetch.FetchOperationUI;
import org.eclipse.egit.ui.internal.repository.tree.RefNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;

public class ForkZestView extends ViewPart implements IZoomableWorkbenchPart {
	public ForkZestView() {
	}
	public static final String ID = "org.ancit.github.utils.forkvis.views.ForkZestView";
	private GraphViewer viewer;
	private RefNode refNode;
	private Text txtReponame;
	private Button btnShowOnlineForks;
	private Button btnBrowseRepo;
	private SearchRepository repository;
	private Action addRemoteAction;
	private Action forkAction;
	private Action starRepoAction;
	
	public void createPartControl(Composite parent) {
		
		GridLayout gl_parent = new GridLayout();
		gl_parent.numColumns = 2;
		parent.setLayout(gl_parent);
		
		btnShowOnlineForks = new Button(parent, SWT.CHECK);
		btnShowOnlineForks.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
					txtReponame.setEnabled(btnShowOnlineForks.getSelection());
					btnBrowseRepo.setEnabled(btnShowOnlineForks.getSelection());
					viewer.setInput(new Object());
					addRemoteAction.setEnabled(false);
					forkAction.setEnabled(false);
					starRepoAction.setEnabled(false);
			}
		});
		btnShowOnlineForks.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnShowOnlineForks.setText("Show Online Forks");
		
		txtReponame = new Text(parent, SWT.BORDER);
		txtReponame.setText("repoName");
		txtReponame.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtReponame.setEnabled(false);
		txtReponame.setEditable(false);
		
		btnBrowseRepo = new Button(parent, SWT.NONE);
		btnBrowseRepo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final RepositorySelectionDialog dialog = new RepositorySelectionDialog(Display.getDefault().getActiveShell());
				if(IDialogConstants.OK_ID == dialog.open()) {
					txtReponame.setText(dialog.getSearchRepo().getOwner()+"/"+dialog.getSearchRepo().getName());
					repository = dialog.getSearchRepo();
					try {
						new ProgressMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell())
						.run(false, true, new IRunnableWithProgress() {
							public void run(IProgressMonitor monitor) {

								monitor.beginTask("Fetch and Display Fork", IProgressMonitor.UNKNOWN);
								
								showForksView(repository);
								
								monitor.done();
								
							}
						});
					} catch (InvocationTargetException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
				}
			}
		});
		btnBrowseRepo.setText("...");
		btnBrowseRepo.setEnabled(false);
		
		viewer = new GraphViewer(parent, SWT.BORDER);
		Control control = viewer.getControl();
		control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		viewer.setContentProvider(new ForkVisualisationContentProvider());
		// viewer.setLabelProvider(new ForkVisualisationLabelProvider());
		viewer.setLabelProvider(new ForkVisualisationLabelProvider(PlatformUI.getWorkbench()
						.getDecoratorManager().getLabelDecorator()));
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				// TODO Auto-generated method stub
				ISelection selection = event.getSelection();
				if (!selection.isEmpty()) {
					if (event.getSelection() instanceof IStructuredSelection) {
						IStructuredSelection selection2 = (IStructuredSelection) event
								.getSelection();
						if (selection2.getFirstElement() instanceof ForkNode) {
							ForkNode forkNode = (ForkNode) selection2
									.getFirstElement();
							Object domainObject = forkNode.getDomainObject();
							String url = "";
							if (domainObject instanceof RepositoryId) {
								RepositoryId repoId = (RepositoryId) domainObject;
								url = "https://github.com/" + repoId.getOwner()
										+ "/" + repoId.getName() + ".git";
							} else if (domainObject instanceof Repository) {
								Repository repo = (Repository) domainObject;
								url = repo.getHtmlUrl();
							}

							try {
								IWebBrowser browser = PlatformUI
										.getWorkbench()
										.getBrowserSupport()
										.createBrowser(
												IWorkbenchBrowserSupport.AS_EDITOR,
												url, url, url);
								browser.openURL(new URL(url));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		});

		fillToolBar();

//		getSite().getPage().addSelectionListener(this);
	}

	private LayoutAlgorithm setLayout() {
		LayoutAlgorithm layout;
		// layout = new
		// SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		// layout = new
		// TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		// layout = new
		// GridLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		// layout = new
		// HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		layout = new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		return layout;

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */

	public void setFocus() {
	}

	private void fillToolBar() {
		ZoomContributionViewItem toolbarZoomContributionViewItem = new ZoomContributionViewItem(
				this);
		IActionBars bars = getViewSite().getActionBars();
		bars.getMenuManager().add(toolbarZoomContributionViewItem);

		addRemoteAction = new Action("Fetch") {
			
			@Override
			public void run() {
				
				ISelection selection = viewer.getSelection();
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection nodeSelected= (IStructuredSelection) selection;
					if (nodeSelected.getFirstElement() instanceof ForkNode) {
						ForkNode forkNode = (ForkNode) nodeSelected.getFirstElement();
						Object domainObject = forkNode.getDomainObject();
						if (domainObject instanceof RepositoryId) {
							RepositoryId repositoryId = (RepositoryId) domainObject;
							String url="https://github.com/"+ repositoryId.getOwner()+"/"+repositoryId.getName()+".git";
							addRemoteAndFetch(url, repositoryId.getOwner());
							
						}else
						{
							if (domainObject instanceof Repository) {
								Repository repository = (Repository) domainObject;
								addRemoteAndFetch(repository.getCloneUrl(), repository.getOwner().getLogin());
							}
							
							
						}
						
					}
				}
			}
		};
		addRemoteAction.setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/fetch.gif"));
		addRemoteAction.setEnabled(false);
		getViewSite().getActionBars().getToolBarManager().add(addRemoteAction);
		
		forkAction = new Action("Fork && Clone") {
			public void run() {
				GitHubClient client = null;
				String forkRepo;
				boolean cloneRepo;
				try {
					client = GithubService.createClient(null);
					RepositoryService service1 = new RepositoryService(client);

					try {
						Repository clientRepository = service1.getRepository(
								client.getUser(), repository.getName());
						forkRepo=clientRepository.getCloneUrl();
						cloneRepo = MessageDialog.openQuestion(Display
								.getDefault().getActiveShell(),
								" Already Forked!!", repository.getOwner()
										+ "/"
										+ repository.getName()
										+ " is Already Forked"
										+ " \n\n Do you wish to clone "
										+ clientRepository.getOwner()
												.getLogin() + "/"
										+ clientRepository.getName() + "?");

					} catch (RequestException e) {
						Repository forkedRepository = service1
								.forkRepository(repository);
						forkRepo = forkedRepository.getCloneUrl();
						cloneRepo = MessageDialog.openQuestion(Display
								.getDefault().getActiveShell(),
								"Fork Successfull", repository.getOwner()
										+ "/"
										+ repository.getName()
										+ " is successfully forked to "
										+ forkedRepository.getHtmlUrl()
										+ " \n\n Do you also want to clone "
										+ forkedRepository.getOwner()
												.getLogin() + "/"
										+ forkedRepository.getName() + "?");
						refreshForkView(repository);
					}
					if (cloneRepo) {
						WizardDialog d = new WizardDialog(Display
								.getDefault().getActiveShell(),
								new GitCloneWizard(forkRepo
										));
						d.open();
					}
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					MessageDialog.openWarning(Display.getDefault()
							.getActiveShell(), "UnknownHostException",
							"UnknownHostException " + e.getMessage()
									+ "\n Check your Internet connection ");
				} catch(RequestException e){
					forbiddenAccess(e); 
					
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		};
		forkAction.setImageDescriptor(Activator.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/forkView.png"));
		forkAction.setEnabled(false);
		getViewSite().getActionBars().getToolBarManager().add(forkAction);
		
		starRepoAction = new Action("Star/Unstar") {
			GitHubClient client = null;

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					client = GithubService.createClient(null);
					WatcherService watchService = new WatcherService(client);
					Repository repo = null;
					RepositoryId repositoryId;
					IStructuredSelection selection = (IStructuredSelection) viewer
							.getSelection();
					if (selection instanceof IStructuredSelection) {
						IStructuredSelection selection2 = (IStructuredSelection) selection;
						ForkNode forkNode = (ForkNode) selection2
								.getFirstElement();
						Object domainObject = forkNode.getDomainObject();
						if (domainObject instanceof RepositoryId) {
							repositoryId = (RepositoryId) domainObject;
							if (!(watchService.isWatching(repositoryId))) {
								watchService.watch(repositoryId);
								MessageDialog.openInformation(Display
										.getDefault().getActiveShell(),
										"Star Repository", "Repository "
												+ repositoryId.getOwner() + "/"
												+ repositoryId.getName()
												+ " is stared by "+client.getUser());
							} else {
								watchService.unwatch(repositoryId);
								MessageDialog.openInformation(Display
										.getDefault().getActiveShell(),
										"Unstar Repository", "Repository "
												+ repositoryId.getOwner() + "/"
												+ repositoryId.getName()
												+ " is unstared by "+client.getUser());
							}refreshForkView(repositoryId);
						}
						else if (domainObject instanceof Repository) {
							 repo= (Repository) domainObject;
							if (!(watchService.isWatching(repo))) {
								watchService.watch(repo);
								MessageDialog.openInformation(Display
										.getDefault().getActiveShell(),
										"Star Repository",
										"Repository " + repo.getOwner().getLogin() + "/"
												+ repo.getName()
												+ " is stared by "+client.getUser());
							} else {
								watchService.unwatch(repo);
								MessageDialog.openInformation(Display
										.getDefault().getActiveShell(),
										"Unstar Repository",
										"Repository " + repo.getOwner().getLogin() + "/"
												+ repo.getName()
												+ " is unstared by "+client.getUser());
							}refreshForkView(repo);
						}

					}
				} catch(RequestException e){
					forbiddenAccess(e); 
					
				}catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					MessageDialog
					.openWarning(
							Display.getDefault().getActiveShell(),
							"UnknownHostException","UnknownHostException "
									+e.getMessage() +"\n Check your Internet connection ");
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
//					MessageDialog
//							.openWarning(
//									Display.getDefault().getActiveShell(),
//									"Forbidden Access ",
//									e.getMessage()
//											+ "\nPlease make sure you have configured valid Github credentials in \n Windows > Preferences > eGit-extensions > github-extensions");
				
					 
				}
			}
		};
		starRepoAction.setImageDescriptor(Activator.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/star.png"));
		starRepoAction.setEnabled(false);
		getViewSite().getActionBars().getToolBarManager().add(starRepoAction);
		

		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				// TODO Auto-generated method stub
				if (event.getSelection() instanceof IStructuredSelection) {
					addRemoteAction.setEnabled(false);
					forkAction.setEnabled(false);
					starRepoAction.setEnabled(false);
					IStructuredSelection selection = (IStructuredSelection) event
							.getSelection();
					if (selection.getFirstElement() instanceof ForkNode) {
						ForkNode forkNode = (ForkNode) selection
								.getFirstElement();
						if (!btnShowOnlineForks.getSelection()) {
							addRemoteAction.setEnabled(true);
						} else {
						forkAction.setEnabled(true);
						}
						starRepoAction.setEnabled(true);
					} 
				}
			}
		});
		createTreeViewerMenu();
	}
	 private MenuManager createTreeViewerMenu() {
		  MenuManager menuMgr = new MenuManager(null);
		  menuMgr.setRemoveAllWhenShown(true);
		  menuMgr.addMenuListener(new IMenuListener() {

		   public void menuAboutToShow(IMenuManager manager) {
		    IStructuredSelection sel =(IStructuredSelection) viewer.getSelection();
		    if (!sel.isEmpty()) {
		     if (sel.getFirstElement() instanceof ForkNode) {
		    	 ForkNode forkNode = (ForkNode) sel.getFirstElement();
		      manager.add(addRemoteAction);
		      manager.add(forkAction);
		      manager.add(starRepoAction);
		     }}
		   }});
		  org.eclipse.swt.widgets.Menu menu = menuMgr.createContextMenu(viewer.getControl());
		  viewer.getControl().setMenu(menu);
		  return menuMgr;

		 }
		  
	
	
	public void addRemoteAndFetch(String url,String remoteName) {

		try {
			final RemoteConfig rc = new RemoteConfig(refNode.getRepository().getConfig(), remoteName);
			StringBuilder defaultRefSpec = new StringBuilder();
			   defaultRefSpec.append('+');
			   defaultRefSpec.append(Constants.R_HEADS);
			   defaultRefSpec.append('*').append(':');
			   defaultRefSpec.append(Constants.R_REMOTES);
			   defaultRefSpec.append(rc.getName());
			   defaultRefSpec.append(RefSpec.WILDCARD_SUFFIX);
			   rc.addFetchRefSpec(new RefSpec(defaultRefSpec.toString()));
			   
			rc.addURI(new URIish(url));
			rc.update(refNode.getRepository().getConfig());
			
			refNode.getRepository().getConfig().save();
			
			new ProgressMonitorDialog(ForkZestView.this.getSite().getShell()).run(false, true,
				       new IRunnableWithProgress() {
				        public void run(IProgressMonitor monitor)
				          throws InvocationTargetException,
				          InterruptedException {
				         int timeout = 10000;
				         FetchOperationUI op = new FetchOperationUI(
				           refNode.getRepository(), rc, timeout, false);
				         op.start();
				        }
				       });
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public AbstractZoomableViewer getZoomableViewer() {
		return viewer;
	}
	
public void showForksView(Object obj){
	 GitHubClient client;
	try {
		client = GithubService.createClient(null);
		RepositoryService service;
		 service= new RepositoryService(client);
		
		displayForkVisilazition(obj, service);
	} catch (RequestException e) {
		RepositoryService service;
		 service= new RepositoryService();
		
		try {
			displayForkVisilazition(obj, service);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
		catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

			
}

private void displayForkVisilazition(Object obj, RepositoryService service)
		throws IOException {
	Repository currentRepo = null;

	if (obj instanceof SearchRepository) {
		SearchRepository searchRepo = (SearchRepository) obj;
		currentRepo = service.getRepository(searchRepo);
		
	}else if(obj instanceof Repository){
		Repository searchRepo = (Repository) obj;
		currentRepo = service.getRepository(searchRepo);
		
	}else if(obj instanceof RepositoryId){
		RepositoryId searchRepo = (RepositoryId) obj;
		currentRepo = service.getRepository(searchRepo);
		
	}
	RepositoryId repo;
	if (currentRepo.isFork()) {
		Repository parentRepo = currentRepo.getParent();

		repo = new RepositoryId(parentRepo.getOwner()
				.getLogin(), parentRepo.getName());
	} else {
		repo = new RepositoryId(currentRepo.getOwner()
				.getLogin(), currentRepo.getName());
	}

	List<Repository> forks = service.getForks(repo);

	final ForkVisualisationModel model = new ForkVisualisationModel(
			repo, forks);
	Display.getDefault().asyncExec(new Runnable() {
		public void run() {
			viewer.setInput(model.getNodes());
			LayoutAlgorithm layout = setLayout();
			viewer.setLayoutAlgorithm(layout, true);
			viewer.applyLayout();
		}
	});
}


	public void selectionChanged(ISelection selection) {

//		if(btnShowOnlineForks.getSelection()) {
//			refNode = null;
//		}
		
		btnShowOnlineForks.setSelection(false);
		txtReponame.setText("");
		btnBrowseRepo.setEnabled(false);
		
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			Object firstElement = sSelection.getFirstElement();
			
			if (firstElement instanceof RefNode) {
				
//				if(refNode != null) {
//					return;
//				}
				
				RefNode selectedRefNode = (RefNode) firstElement;
				@SuppressWarnings("restriction")
				String branchSelected = selectedRefNode.getObject().getName();
				if (!branchSelected.startsWith("refs/remotes/")) {
					this.refNode = selectedRefNode;
					branchSelected = branchSelected.substring(branchSelected
							.lastIndexOf("/") + 1);

					String remote = refNode
							.getRepository()
							.getConfig()
							.getString(ConfigConstants.CONFIG_BRANCH_SECTION,
									branchSelected,
									ConfigConstants.CONFIG_KEY_REMOTE);

					RemoteConfig rc;
					try {
						rc = new RemoteConfig(refNode.getRepository()
								.getConfig(), remote);
						List<URIish> urIs = rc.getURIs();
						if(urIs.size() > 0) {
						String uri = urIs.get(0).toString();

						uri = uri.replace("https://github.com/", "")
								.replace("git@github.com:", "")
								.replace(".git", "");
						String repoOwner = uri.substring(0,
								uri.lastIndexOf("/"));
						String repositoryName = uri.split("/")[1];

						// To be used when we provide support for Private
						// Repositories
						 GitHubClient client =
						 GithubService.createClient(null);

						RepositoryService service = new RepositoryService(client);
						Repository currentRepo = service.getRepository(
								repoOwner, repositoryName);

						RepositoryId repo;
						if (currentRepo.isFork()) {
							Repository parentRepo = currentRepo.getParent();

							repo = new RepositoryId(parentRepo.getOwner()
									.getLogin(), parentRepo.getName());
						} else {
							repo = new RepositoryId(currentRepo.getOwner()
									.getLogin(), currentRepo.getName());
						}

						List<Repository> forks = service.getForks(repo);

						final ForkVisualisationModel model = new ForkVisualisationModel(
								repo, forks);
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								viewer.setInput(model.getNodes());
								LayoutAlgorithm layout = setLayout();
								viewer.setLayoutAlgorithm(layout, true);
								viewer.applyLayout();
							}
						});
						} else {
							MessageDialog
							.openWarning(
									Display.getDefault().getActiveShell(),"Invalid Remote Configuration","Remote URL for the Selected Branch does not exists. \n Right Click > Configure Branch > Select Valid Remote");
						}

					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						MessageDialog
						.openWarning(
								Display.getDefault().getActiveShell(),
								"UnknownHostException","UnknownHostException "
								+e.getMessage() +"\n Check your Internet connection ");
					}catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						MessageDialog
								.openWarning(
										Display.getDefault().getActiveShell(),
										"Forbidden Access - Private Github Repositories",
										"You are attempting to access a Private Repository in Github without Logging In.\nUse Preference Store to Configure your Github Account\nWindows > Preferences > eGit-extensions > github-extensions" + e.getMessage());
					}
				} else {
					this.refNode = null;
					viewer.setInput(new ArrayList());
					LayoutAlgorithm layout = setLayout();
					viewer.setLayoutAlgorithm(layout, true);
					viewer.applyLayout();
				}
			}
		}

	}
	private void refreshForkView(final Object repo) {
		// TODO Auto-generated method stub
		UIJob job = new UIJob("Star/Unstar job") {
			
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				// TODO Auto-generated method stub
				showForksView(repo);
				return Status.OK_STATUS;
			}
		};
		job.setSystem(false);;
		job.schedule(0);
	}

	private void forbiddenAccess(IOException e) {
		IPreferencePage page = new GitHubExtensionsPreferencePage();  
		 PreferenceManager mgr = new PreferenceManager();  
		 IPreferenceNode node = new PreferenceNode( "com.cypress.cx3.ui.preferences.CX3ConfigurationPreferencePage", page);  
		 mgr.addToRoot(node);  
		 PreferenceDialog dialog = new PreferenceDialog(Display  
		 .getDefault().getActiveShell(), mgr);  
		 dialog.create();  
		 dialog.setMessage("Forbidden Access"); 
		 dialog.setErrorMessage(e.getMessage()+" Uname/Password is wrong.");
		 dialog.open();
	}
	
}