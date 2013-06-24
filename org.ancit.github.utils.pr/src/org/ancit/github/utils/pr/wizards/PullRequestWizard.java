package org.ancit.github.utils.pr.wizards;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.lib.Repository;

public class PullRequestWizard extends Wizard {

	private final Repository myRepository;

	public PullRequestWizard(Repository myRepository) {
		this.myRepository = myRepository;
		setWindowTitle("GitHub - Pull Request Creation Wizard");
	}

	@Override
	public void addPages() {
		addPage(new PullRequestWizardPage(myRepository));
	}

	@Override
	public boolean performFinish() {
		return true;
	}

}
