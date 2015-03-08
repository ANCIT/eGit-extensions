package org.ancit.github.utils.forkvis.views;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.ancit.github.utils.Activator;
import org.ancit.github.utils.forkvis.model.ForkConnection;
import org.ancit.github.utils.forkvis.model.ForkNode;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.core.viewers.EntityConnectionData;

public class RealForkVisualisationLabelProvider extends LabelProvider {

	//
	// private Map<ImageDescriptor, Image> _cachedImages =
	// new HashMap<ImageDescriptor, Image>();
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

					if (imageIO.getWidth() > 32 || imageIO.getHeight() > 32) {
						final BufferedImage bufferedImage = new BufferedImage(
								32, 32, BufferedImage.TYPE_INT_RGB);
						final Graphics2D graphics2D = bufferedImage
								.createGraphics();
						graphics2D.setComposite(AlphaComposite.Src);
						graphics2D.drawImage(imageIO, 0, 0, 32, 32, null);
						graphics2D.dispose();

						imageIO = bufferedImage;
					}

					ImageIO.write(
							imageIO,
							"png",
							new File(System.getProperty("user.dir")
									+ System.getProperty("file.separator")
									+ "gravatar.png"));
					Image image = new Image(Display.getDefault(),
							System.getProperty("user.dir")
									+ System.getProperty("file.separator")
									+ "gravatar.png");

					return image;
				} else {
					ImageDescriptor imageDesc = Activator
							.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
									"icons/repo1.png");
					return imageDesc.createImage();
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

	// @Override
	// public void dispose() {
	// for (Image img : _cachedImages.values()) {
	// img.dispose();
	// }
	// _cachedImages.clear();
	// super.dispose();
	// }

}
