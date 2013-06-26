package org.ancit.github.utils.pr.wizards;

import org.eclipse.egit.ui.internal.repository.tree.RefNode;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.lib.Repository;

public class PullRequestWizard extends Wizard {

	private RefNode refNode;


	public PullRequestWizard(RefNode refNode) {
		this.refNode = refNode;
		setWindowTitle("GitHub - Pull Request Creation Wizard");
	}

	@Override
	public void addPages() {
		addPage(new PullRequestWizardPage(refNode));
	}

	@Override
	public boolean performFinish() {
		return true;
	}

}
