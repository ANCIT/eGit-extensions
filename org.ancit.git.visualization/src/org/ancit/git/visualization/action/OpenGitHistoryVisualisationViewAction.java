package org.ancit.git.visualization.action;

import org.ancit.git.visualization.model.CommitTree;
import org.ancit.git.visualization.model.CommitTreeObject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class OpenGitHistoryVisualisationViewAction implements
		IObjectActionDelegate {

	private PlotCommit commit;

	public OpenGitHistoryVisualisationViewAction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
		CommitTree tree = new CommitTree(commit);
		for (CommitTreeObject commitTreeObject : tree.getSiblings()) {
			System.out.println(commitTreeObject.getName());
		}

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			Object selectedObject = sSelection.getFirstElement();
			if (selectedObject instanceof PlotCommit) {
				commit = (PlotCommit) selectedObject;				
			}
			
		}

	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub

	}

}
