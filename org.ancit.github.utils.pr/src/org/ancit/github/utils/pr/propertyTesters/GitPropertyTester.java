package org.ancit.github.utils.pr.propertyTesters;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IResource;
import org.eclipse.egit.core.project.RepositoryMapping;

public class GitPropertyTester extends PropertyTester {

	public GitPropertyTester() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if(property.equals("isGit")) {
			if (receiver instanceof IResource) {
				IResource resource = (IResource) receiver;
				RepositoryMapping repoMap = RepositoryMapping.getMapping(resource
						.getProject());
				if (repoMap != null) {
					return true;
				}
			}
		}
		return false;
	}

}
