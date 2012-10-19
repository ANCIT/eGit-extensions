package org.ancit.git.visualization.model;

import java.util.ArrayList;

import org.eclipse.jgit.revplot.PlotCommit;

public class CommitTree {

	PlotCommit commit;
	CommitTreeObject commitObject;
	
	public CommitTree(PlotCommit plotCommit) {
		this.commit = plotCommit;
		commitObject.setLane(plotCommit.getLane().getPosition());
		commitObject.setName(plotCommit.abbreviate(7).name());
		commitObject.setpCommit(plotCommit);
	}
	
	ArrayList<CommitTreeObject> siblings;
	
	public ArrayList<CommitTreeObject> getSiblings() {
		if(siblings == null) {
			siblings = new ArrayList<CommitTreeObject>();
			
			int noOfChildren = commit.getChildCount();
			
			for (int i = 0; i < noOfChildren; i++) {
				PlotCommit childCommit = commit.getChild(i);
				int childPosition = childCommit.getLane().getPosition();
				if(childPosition == 0) {
					CommitTreeObject child = new CommitTreeObject();
					child.setpCommit(childCommit);
					child.setLane(childPosition);
					child.setName(childCommit.abbreviate(7).name());
					siblings.add(child);
				}
			}
			
		}
		return siblings;
	}
	
	
}
