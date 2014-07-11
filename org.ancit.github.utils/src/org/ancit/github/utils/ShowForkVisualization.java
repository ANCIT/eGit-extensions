package org.ancit.github.utils;

import java.lang.reflect.InvocationTargetException;

import org.ancit.github.utils.forkvis.views.ForkZestView;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.ui.internal.fetch.FetchOperationUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ShowForkVisualization implements IObjectActionDelegate {

	private IStructuredSelection selection;
	private ForkZestView showView;

	public ShowForkVisualization() {
		
	}

	@Override
	public void run(IAction action) {
		try {
			showView = (ForkZestView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.ancit.github.utils.forkvis.views.ForkZestView");
			
			new ProgressMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell())
					.run(false, true, new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor) {

							monitor.beginTask("Fetch and Display Fork", IProgressMonitor.UNKNOWN);
							
							showView.selectionChanged(selection);
							
							monitor.done();
							
						}
					});
					
		} catch (PartInitException e) {						
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = (IStructuredSelection) selection;
		// TODO Auto-generated method stub

	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub

	}

}
