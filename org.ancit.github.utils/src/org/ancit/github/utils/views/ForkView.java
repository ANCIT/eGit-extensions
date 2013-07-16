package org.ancit.github.utils.views;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.ui.internal.repository.tree.RefNode;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

public class ForkView extends ViewPart implements ISelectionListener {

	public static final String ID = "org.ancit.github.utils.views.ForkView"; //$NON-NLS-1$
	private RefNode refNode;
	private TreeViewer treeViewer;
	private ForkViewContentProvider provider;
	private Graph g;
	private Composite container;

	public ForkView() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout());

		treeViewer = new TreeViewer(container);
		provider = new ForkViewContentProvider();
		treeViewer.setContentProvider(provider);
		treeViewer.setLabelProvider(new ForkViewLabelProvider());
		
		g = new Graph(container, SWT.NONE);

		
		
		getSite().getPage().addSelectionListener(this);
		
		createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// TODO Auto-generated method stub
		IStructuredSelection sSelection = (IStructuredSelection)selection;
		Object firstElement = sSelection.getFirstElement();
		if (firstElement instanceof RefNode) {
			RefNode selectedRefNode=(RefNode) firstElement;
			
			@SuppressWarnings("restriction")
			String branchSelected=selectedRefNode.getObject().getName();
			if(!branchSelected.startsWith("refs/remotes/")) {
				this.refNode = selectedRefNode;
				branchSelected=branchSelected.substring(branchSelected.lastIndexOf("/")+1);
				
				String remote = refNode.getRepository().getConfig().getString(
					    ConfigConstants.CONFIG_BRANCH_SECTION, branchSelected,
					    ConfigConstants.CONFIG_KEY_REMOTE);
				
				RemoteConfig rc;
				try {
					rc = new RemoteConfig(refNode.getRepository().getConfig(),
							remote);
					List<URIish> urIs = rc.getURIs();
					String uri = urIs.get(0).toString();
					
					
						uri = uri.replace("https://github.com/", "")
								.replace("git@github.com:", "").replace(".git", "");
						String repoOwner = uri.substring(0, uri.lastIndexOf("/"));
						String repositoryName=uri.split("/")[1];
						
						GitHubClient client = createClient(null);
						RepositoryService service = new RepositoryService();
						Repository currentRepo = service.getRepository(repoOwner, repositoryName);
						
						RepositoryId repo;
						if(currentRepo.isFork()) {
						Repository parentRepo = currentRepo.getParent();
						

						repo = new RepositoryId(parentRepo.getOwner().getLogin(), parentRepo.getName());
						} else {
							repo = new RepositoryId(currentRepo.getOwner().getLogin(), currentRepo.getName());
						}
						
//						List<Repository> forks = service.getForks(repo);
//						for (Repository repository : forks) {
//							System.out.println(repository.getUrl()+": "+repository.getMasterBranch());
//						}
						 
						List<RepositoryId> repos= new ArrayList<RepositoryId>();
						repos.add(repo);
						
						provider.setService(service);
						treeViewer.setInput(repos);
						
						g.getConnections().clear();
//						Arrays.asList(container.getChildren()).remove(g);
//						g = new Graph(container, SWT.NONE);
						
						 List nodes = g.getNodes();
						 for (Object object : nodes) {
							 if (object instanceof GraphNode) {
								GraphNode node = (GraphNode) object;
								node.dispose();
								
							}
						}
						 
						 List connections = g.getConnections();
						 for (Object object : connections) {
							 if (object instanceof GraphConnection) {
								 GraphConnection conn = (GraphConnection) object;
								conn.dispose();
								
							}
						}
						GraphNode n = new GraphNode(g, SWT.NONE, "Paper");
						GraphNode n2 = new GraphNode(g, SWT.NONE, "Rock");
						GraphNode n3 = new GraphNode(g, SWT.NONE, "Scissors");
						GraphNode n4 = new GraphNode(g, SWT.NONE, "USer10");
						new GraphConnection(g, SWT.NONE, n, n2);
						new GraphConnection(g, SWT.NONE, n, n3);
						new GraphConnection(g, SWT.NONE, n, n4);
						g.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
//						g.pack();
						
					
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				this.refNode = null;
			}

		}
	}
	
	/**
	 * Create client for url
	 *
	 * @param url
	 * @return client
	 * @throws IOException
	 */
	protected GitHubClient createClient(String url) throws IOException {
		GitHubClient client = null;
		if (url != null) {
			URL parsed = new URL(url);
			client = new GitHubClient(parsed.getHost(), parsed.getPort(),
					parsed.getProtocol());
		} else
			client = new GitHubClient();
		return configure(client);
	}
	
	/**
	 * Configure client
	 *
	 * @param client
	 * @return specified client
	 */
	protected GitHubClient configure(GitHubClient client) {
//		String user = "subramanyamcs";
//		String password = "l0g1n2git";
//		client.setCredentials(user, password);
		client.setUserAgent("github");
		return client;
	}

}
