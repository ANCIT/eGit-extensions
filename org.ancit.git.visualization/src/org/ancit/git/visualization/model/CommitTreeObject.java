package org.ancit.git.visualization.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.revplot.PlotCommit;

public class CommitTreeObject {
	
	String name;
	PlotCommit pCommit;
	List<CommitTreeObject> children;
	int lane;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public PlotCommit getpCommit() {
		return pCommit;
	}
	
	public void setpCommit(PlotCommit pCommit) {
		this.pCommit = pCommit;
	}
	
	public List<CommitTreeObject> getChildren() {
		if(children == null) {
			children  = new ArrayList<CommitTreeObject>();
			int noOfChildren = pCommit.getChildCount();
			
			for (int i = 0; i < noOfChildren; i++) {
				PlotCommit childCommit = pCommit.getChild(i);
				int childPosition = childCommit.getLane().getPosition();
				if(childPosition == getLane()+1) {
					CommitTreeObject child = new CommitTreeObject();
					child.setpCommit(childCommit);
					child.setLane(childPosition);
					child.setName(childCommit.abbreviate(7).name());
					children.add(child);
				}
			}
			
		}
		return children;
	}
	
	public void setLane(int lane) {
		this.lane = lane;
	}
	
	public int getLane() {
		return lane;
	}
	

}
