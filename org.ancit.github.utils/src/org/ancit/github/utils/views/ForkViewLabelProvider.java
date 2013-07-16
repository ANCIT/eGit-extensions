package org.ancit.github.utils.views;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class ForkViewLabelProvider extends LabelProvider {
	
	@Override
	public String getText(Object element) {
		// TODO Auto-generated method stub
		
		if (element instanceof Repository) {
			Repository repository = (Repository) element;
			return repository.getOwner().getLogin()+"/"+repository.getName();
		}
		return super.getText(element);
	}
	
	@Override
	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		try {
			
			if (element instanceof Repository) {
				Repository repository = (Repository) element;
				String imageURL=repository.getOwner().getAvatarUrl().substring(0, repository.getOwner().getAvatarUrl().indexOf("?")+1)+"s=32";
				BufferedImage imageIO = ImageIO.read(new URL(imageURL));
				ImageIO.write(imageIO, "png", new File("/tmp/file1.png"));
				Image image = new Image(Display.getDefault(), "/tmp/file1.png");
				return image;
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

}
