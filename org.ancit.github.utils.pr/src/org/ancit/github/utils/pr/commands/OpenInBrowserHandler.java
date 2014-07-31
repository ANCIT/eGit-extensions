package org.ancit.github.utils.pr.commands;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.egit.ui.internal.repository.tree.RefNode;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenInBrowserHandler implements IHandler {

	private RefNode refNode;
	private boolean isGitHub = false;
	private String url = "";
	private String branchSelected;

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		IStructuredSelection sSelection = (IStructuredSelection) HandlerUtil
				.getCurrentSelection(event);
		IResource resource;
		Object firstElement = sSelection.getFirstElement();
		if (firstElement instanceof RefNode) {
			this.refNode = (RefNode) firstElement;
			Repository myRepository = refNode.getRepository();
			branchSelected = refNode.getObject().getName()
					.replace("refs/remotes/", "").replace("refs/heads/", "");
			url = getURIFromRepository(myRepository);
			if (isGitHub)
				url = url + "/tree/" + branchSelected;
		} else if (firstElement instanceof IResource) {
			resource = (IResource) firstElement;
			calculateURL(resource);
		} else if (firstElement instanceof IJavaProject) {
			IJavaProject fragment = (IJavaProject) firstElement;
			resource = fragment.getResource();
			calculateURL(resource);
		} else if (firstElement instanceof IPackageFragment) {
			IPackageFragment fragment = (IPackageFragment) firstElement;
			resource = fragment.getResource();
			calculateURL(resource);
		} else if (firstElement instanceof ICompilationUnit) {
			ICompilationUnit compUnit = (ICompilationUnit) firstElement;
			resource = compUnit.getResource();
			calculateURL(resource);
		} else if (firstElement instanceof IPackageFragmentRoot) {
			IPackageFragmentRoot srcFolder = (IPackageFragmentRoot) firstElement;
			resource = srcFolder.getResource();
			calculateURL(resource);
		}
		try {
			if (url != null) {
				IWebBrowser externalBrowser = PlatformUI.getWorkbench()
						.getBrowserSupport().getExternalBrowser();
				// uri = uri.replace(".git", "");
				externalBrowser.openURL(new URL(url));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void calculateURL(IResource resource) {
		String resourcePath = resource.getLocation().toOSString();
		RepositoryMapping repoMap = RepositoryMapping.getMapping(resource
				.getProject());
		if (repoMap != null) {
			Repository repository = repoMap.getRepository();
			resourcePath = resourcePath.replace(
					repository.getWorkTree().getAbsolutePath(), "").replace(
					"\\", "/");
			try {
				branchSelected = repository.getBranch();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (resource instanceof IFile) {
				url = getURIFromRepository(repository);
				if (isGitHub) {
					url = url + "/blob/" + branchSelected.toString()
							+ resourcePath;
				} else {
					url = url + "/src/" + getCommitId(repository)
							+ resourcePath + "?at=" + branchSelected;
				}
			} else if (resource instanceof IFolder
					|| resource instanceof IProject) {
				url = getURIFromRepository(repository);
				if (isGitHub) {
					url = url + "/tree/" + branchSelected.toString()
							+ resourcePath;
				} else {
					url = url + "/src/" + getCommitId(repository)
							+ resourcePath + "?at=" + branchSelected;
				}

			}
		} else {
			url = null;
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"Error", "Resource is not available on Git Repository");
		}
	}

	private String getURIFromRepository(Repository repository) {
		if (repository != null) {
			try {
				String remote = repository.getConfig().getString(
						ConfigConstants.CONFIG_BRANCH_SECTION, branchSelected,
						ConfigConstants.CONFIG_KEY_REMOTE);
				RemoteConfig rc = new RemoteConfig(repository.getConfig(),
						remote);
				List<URIish> urIs = rc.getURIs();
				if (!urIs.isEmpty()) {
					url = urIs.get(0).toString();
					if (url.startsWith("git@github.com:")) {
						url = url.replace("git@github.com:",
								"https://github.com/").replace(".git", "");
						isGitHub = true;
					} else if (url.startsWith("https://github.com/")) {
						url = url.replace(".git", "");
						isGitHub = true;
					} else if (url.startsWith("https://bitbucket.org/")) {
						url = url.replace(".git", "");
					} else if (url.startsWith("bit@bitbucket.org")) {
						url = url.replace("git@bitbucket.org:",
								"http://bitbucket.org/").replace(".git", "");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return url;
	}

	private String getCommitId(Repository repository) {
		try {
			return repository.resolve(repository.getFullBranch()).getName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}
