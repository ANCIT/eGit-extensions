package org.ancit.github.utils.forkvis.views;

import java.awt.DisplayMode;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.ancit.github.utils.Activator;
import org.ancit.github.utils.GithubService;
import org.ancit.github.utils.forkvis.model.ForkNode;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.ui.internal.fetch.FetchOperationUI;
import org.eclipse.egit.ui.internal.repository.tree.RefNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;

public class ForkZestView extends ViewPart implements IZoomableWorkbenchPart,
		ISelectionListener {
	public static final String ID = "de.vogella.zest.jface.view";
	private GraphViewer viewer;
	private RefNode refNode;

	public void createPartControl(Composite parent) {
		viewer = new GraphViewer(parent, SWT.BORDER);
		viewer.setContentProvider(new ForkVisualisationContentProvider());
		viewer.setLabelProvider(new ForkVisualisationLabelProvider());

		fillToolBar();

		getSite().getPage().addSelectionListener(this);
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

		final Action addRemoteAction = new Action("Fetch") {
			
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
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				// TODO Auto-generated method stub
				if (event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					if (selection.getFirstElement() instanceof ForkNode) {
						ForkNode forkNode = (ForkNode) selection.getFirstElement();
						addRemoteAction.setEnabled(true);
					}else
					{
						addRemoteAction.setEnabled(false);

					}
					
				}
			}
		});
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

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			Object firstElement = sSelection.getFirstElement();
			
			if (firstElement instanceof RefNode) {
				
				if(refNode != null && (refNode.equals(firstElement) || refNode.getRepository().equals(((RefNode)firstElement).getRepository()))) {
					return;
				}
				
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
					} catch (Exception e) {
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
	
	@Override
	public void dispose() {
		 ISelectionService s = getSite().getWorkbenchWindow().getSelectionService();
	     s.removeSelectionListener(this);
	     super.dispose();
	}
}