package org.ancit.github.utils.forkvis.views;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.ancit.github.utils.GithubService;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.ui.internal.repository.tree.RefNode;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jgit.lib.ConfigConstants;
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
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

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

						ForkVisualisationModel model = new ForkVisualisationModel(
								repo, forks);
						viewer.setInput(model.getNodes());
						LayoutAlgorithm layout = setLayout();
						viewer.setLayoutAlgorithm(layout, true);
						viewer.applyLayout();

					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						MessageDialog
								.openWarning(
										Display.getDefault().getActiveShell(),
										"Forbidden Access - Private Github Repositories",
										"You are attempting to access a Private Repository in Github. This option is not available at the moment.");
					}
				} else {
					this.refNode = null;
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