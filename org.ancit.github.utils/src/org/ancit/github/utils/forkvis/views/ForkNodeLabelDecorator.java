package org.ancit.github.utils.forkvis.views;

import java.io.IOException;

import org.ancit.github.utils.Activator;
import org.ancit.github.utils.GithubService;
import org.ancit.github.utils.forkvis.model.ForkNode;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.WatcherService;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class ForkNodeLabelDecorator extends LabelProvider implements
		ILightweightLabelDecorator {

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void decorate(Object element, IDecoration decoration) {
		// TODO Auto-generated method stub
		GitHubClient client = null;
		try {
			client = GithubService.createClient(null);
			WatcherService watchService = new WatcherService(client);
			if (element instanceof ForkNode) {
				ForkNode forkNode = (ForkNode) element;
				Object domainObject = forkNode.getDomainObject();
				if (domainObject instanceof RepositoryId) {
					RepositoryId repositoryId = (RepositoryId) domainObject;
					if ((watchService.isWatching(repositoryId))) {
						decoration.addOverlay(Activator
								.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
										"icons/star.png"));
					}

				}
				if (domainObject instanceof Repository) {
					Repository repository = (Repository) domainObject;
					if ((watchService.isWatching(repository))) {
						decoration.addOverlay(Activator
								.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
										"icons/star.png"));
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}