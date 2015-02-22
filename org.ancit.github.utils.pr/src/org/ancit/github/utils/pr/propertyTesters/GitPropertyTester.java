package org.ancit.github.utils.pr.propertyTesters;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IResource;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.egit.ui.internal.repository.tree.RefNode;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jgit.lib.Repository;

public class GitPropertyTester extends PropertyTester {

	public GitPropertyTester() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (property.equals("isGit")) {
			IResource resource;
			if (receiver instanceof RefNode) {
				RefNode refNode = (RefNode) receiver;
				Repository myRepository = refNode.getRepository();
				if (myRepository != null) {
					return true;
				}
			} else if (receiver instanceof IResource) {
				resource = (IResource) receiver;
				return isGitProject(resource);
			} else if (receiver instanceof IJavaProject) {
				IJavaProject fragment = (IJavaProject) receiver;
				resource = fragment.getResource();
				return isGitProject(resource);
			} else if (receiver instanceof IPackageFragment) {
				IPackageFragment fragment = (IPackageFragment) receiver;
				resource = fragment.getResource();
				return isGitProject(resource);
			} else if (receiver instanceof ICompilationUnit) {
				ICompilationUnit compUnit = (ICompilationUnit) receiver;
				resource = compUnit.getResource();
				return isGitProject(resource);
			} else if (receiver instanceof IPackageFragmentRoot) {
				IPackageFragmentRoot srcFolder = (IPackageFragmentRoot) receiver;
				resource = srcFolder.getResource();
				return isGitProject(resource);
			}
		}
		return false;
	}

	private boolean isGitProject(IResource resource) {
		String resourcePath = resource.getLocation().toOSString();
		RepositoryMapping repoMap = RepositoryMapping.getMapping(resource
				.getProject());
		if (repoMap != null) {
			return true;
		}
		return false;
	}

}
