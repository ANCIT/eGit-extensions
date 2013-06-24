package org.ancit.github.utils.pr.actions;

import org.ancit.github.utils.pr.wizards.PullRequestWizard;
import org.eclipse.egit.ui.internal.repository.tree.RefNode;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class RaisePullRequestAction implements IObjectActionDelegate {

	private Repository myRepository;

	public RaisePullRequestAction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
		PullRequestWizard wizard = new PullRequestWizard(myRepository);
		WizardDialog wDialog = new WizardDialog(Display.getDefault().getActiveShell(), wizard){
			@Override
			protected void configureShell(Shell newShell) {
				super.configureShell(newShell);
				newShell.setSize(1250, 650);
			}
		};
		wDialog.open();

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		IStructuredSelection sSelection = (IStructuredSelection)selection;
		Object firstElement = sSelection.getFirstElement();
		if (firstElement instanceof RefNode) {
			myRepository = ((RefNode) firstElement).getRepository();
			
		}

	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub

	}

}
