package org.ancit.github.utils.forkvis.views;

import java.util.List;

import org.ancit.github.utils.forkvis.model.ForkNode;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;

public class ForkVisualisationLabelProvider extends DecoratingLabelProvider
		implements IEntityStyleProvider {

	public ForkVisualisationLabelProvider(ILabelDecorator decorator) {
		super(new RealForkVisualisationLabelProvider(), decorator);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Color getNodeHighlightColor(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getBorderColor(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getBorderHighlightColor(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBorderWidth(Object entity) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public IFigure getTooltip(Object entity) {
		// TODO Auto-generated method stub
		if (entity instanceof ForkNode) {
			ForkNode forkNode = (ForkNode) entity;
			Object domainObj = forkNode.getDomainObject();
			Label nodeLabel = new Label();
			StringBuffer labelStr = new StringBuffer();
			if (domainObj instanceof RepositoryId) {
				RepositoryId repoId = (RepositoryId) domainObj;
				List<ForkNode> noforks = ((ForkNode) entity).getConnectedTo();
				int n = 0;
				for (ForkNode noForkElement : noforks) {
					n++;
				}
				labelStr.append("Forks : " + n);
				nodeLabel.setText(labelStr.toString());
			} else if (domainObj instanceof Repository) {
				Repository repo = (Repository) domainObj;
				labelStr.append("Stars : " + repo.getWatchers()).append(
						"\nForks:" + repo.getForks());
				nodeLabel.setText(labelStr.toString());
			}
			return nodeLabel;
		}
		return null;
	}

	@Override
	public boolean fisheyeNode(Object entity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Color getForeground(Object element) {
		// TODO Auto-generated method stub
		if (element instanceof ForkNode) {
			ForkNode forkNode = (ForkNode) element;
			Object domainObject = forkNode.getDomainObject();
			if (domainObject instanceof RepositoryId) {
				return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);

			}
		}
		return ColorConstants.darkBlue;
	}

	@Override
	public Color getBackground(Object element) {
		// TODO Auto-generated method stub
		if (element instanceof ForkNode) {
			ForkNode forkNode = (ForkNode) element;
			Object domainObject = forkNode.getDomainObject();
			if (domainObject instanceof RepositoryId) {
				return Display.getDefault().getSystemColor(SWT.COLOR_GREEN);

			}
		}
		return null;
	}

	@Override
	public Color getBackgroundColour(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getForegroundColour(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}