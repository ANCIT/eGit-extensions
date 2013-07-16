package org.ancit.github.utils.views;

import java.io.IOException;
import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;



public class ForkViewContentProvider implements ITreeContentProvider {
	RepositoryService service;

	public void setService(RepositoryService service) {
		this.service = service;
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		if (inputElement instanceof List) {
			List forks = (List) inputElement;
			return forks.toArray();
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof RepositoryId) {
			List<Repository> forks = null;
			try {
				RepositoryId repo = (RepositoryId) parentElement;
				forks = service.getForks(repo);
					for (Repository repository : forks) {
					System.out.println(repository.getUrl()+": "+repository.getMasterBranch());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return forks.toArray();
			
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof RepositoryId) {
			return true;
			
		}
		return false;
	}

}
