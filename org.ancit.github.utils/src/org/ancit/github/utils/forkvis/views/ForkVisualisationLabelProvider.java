package org.ancit.github.utils.forkvis.views;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.ancit.github.utils.forkvis.model.ForkConnection;
import org.ancit.github.utils.forkvis.model.ForkNode;
import org.eclipse.draw2d.IFigure;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;

public class ForkVisualisationLabelProvider extends LabelProvider implements IEntityStyleProvider{
	@Override
	public String getText(Object element) {
		if (element instanceof ForkNode) {
			ForkNode myNode = (ForkNode) element;
			return myNode.getName();
		}
		// Not called with the IGraphEntityContentProvider
		if (element instanceof ForkConnection) {
			ForkConnection myConnection = (ForkConnection) element;
			return myConnection.getLabel();
		}

		if (element instanceof EntityConnectionData) {
			EntityConnectionData test = (EntityConnectionData) element;
			return "";
		}
		throw new RuntimeException("Wrong type: "
				+ element.getClass().toString());
	}

	@Override
	public Image getImage(Object element) {
		try {

			if (element instanceof ForkNode) {
				Object domainObject = ((ForkNode) element).getDomainObject();
				if (domainObject instanceof Repository) {
					Repository repository = (Repository) domainObject;
					String imageURL = repository
							.getOwner()
							.getAvatarUrl()
							.substring(
									0,
									repository.getOwner().getAvatarUrl()
											.indexOf("?") + 1)
							+ "s=32";
					BufferedImage imageIO = ImageIO.read(new URL(imageURL));
					ImageIO.write(imageIO, "png", new File(System.getProperty("user.dir")+"\\gravatar.png"));
					Image image = new Image(Display.getDefault(),
							System.getProperty("user.dir")+"\\gravatar.png");
					return image;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return super.getImage(element);
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
	public Color getBackgroundColour(Object entity) {
		if (entity instanceof ForkNode) {
			ForkNode forkNode = (ForkNode) entity;
			Object domainObject = forkNode.getDomainObject();
			if (domainObject instanceof RepositoryId) {
				return Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
				
			}
		}
		return null;
	}

	@Override
	public Color getForegroundColour(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFigure getTooltip(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean fisheyeNode(Object entity) {
		// TODO Auto-generated method stub
		return false;
	}
}