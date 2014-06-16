package org.ancit.github.utils.pr.actions;

import java.net.URISyntaxException;
import java.util.List;

import org.ancit.github.utils.pr.dialog.BitBucketPRDialog;
import org.ancit.github.utils.pr.wizards.PullRequestWizard;
import org.eclipse.egit.ui.internal.repository.tree.RefNode;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class RaisePullRequestAction implements IObjectActionDelegate {

	private RefNode refNode;
	private boolean isGitHub;
	private String uri;
	private String branchSelected;

	public RaisePullRequestAction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
		if (isGitHub) {
			PullRequestWizard wizard = new PullRequestWizard(refNode);
			WizardDialog wDialog = new WizardDialog(Display.getDefault()
					.getActiveShell(), wizard) {
				@Override
				protected void configureShell(Shell newShell) {
					super.configureShell(newShell);
					newShell.setSize(1250, 650);
				}
			};
			wDialog.open();
		} else {
			BitBucketPRDialog dialog = new BitBucketPRDialog(Display.getDefault().getActiveShell(), uri, branchSelected);
			dialog.open();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		IStructuredSelection sSelection = (IStructuredSelection)selection;
		Object firstElement = sSelection.getFirstElement();
		if (firstElement instanceof RefNode) {
			this.refNode=(RefNode) firstElement;
			
			branchSelected = refNode.getObject().getName();
			

			if (!branchSelected.startsWith("refs/remotes/")) {
				branchSelected = branchSelected.substring(branchSelected
						.lastIndexOf("/") + 1);

				Repository myRepository = refNode.getRepository();
				String remote = myRepository.getConfig().getString(
						ConfigConstants.CONFIG_BRANCH_SECTION, branchSelected,
						ConfigConstants.CONFIG_KEY_REMOTE);
				try {
					RemoteConfig rc = new RemoteConfig(
							myRepository.getConfig(), remote);
					List<URIish> urIs = rc.getURIs();
					if (!urIs.isEmpty()) {
						uri = urIs.get(0).toString();
						if (uri.contains("github.com")) {
							action.setEnabled(true);
							isGitHub = true;
						} else if (uri.contains("bitbucket.org")) {
							action.setEnabled(true);
							isGitHub = false;
						} else {
							action.setEnabled(false);
						}
						return;
					}
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				action.setEnabled(true);
			}
			else{
				action.setEnabled(false);
			}

		}

	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub

	}

}
