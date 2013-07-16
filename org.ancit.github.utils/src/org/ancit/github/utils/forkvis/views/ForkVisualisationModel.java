package org.ancit.github.utils.forkvis.views;
import java.util.ArrayList;
import java.util.List;

import org.ancit.github.utils.forkvis.model.ForkConnection;
import org.ancit.github.utils.forkvis.model.ForkNode;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;

public class ForkVisualisationModel {
  private List<ForkConnection> connections;
  private List<ForkNode> nodes;

  public ForkVisualisationModel(RepositoryId repo, List<Repository> forks) {
    // Image here a fancy DB access
    // Now create a few nodes
	 
    nodes = new ArrayList<ForkNode>();
    connections = new ArrayList<ForkConnection>();
    
    ForkNode parentNode = new ForkNode("1", repo.getName(), repo);
    nodes.add(parentNode);
    
    for (Repository repository : forks) {
    	 ForkNode node = new ForkNode(repository.getOwner().getLogin(), repository.getOwner().getLogin()+"/"+repository.getName(), repository);
    	 nodes.add(node);
    	 ForkConnection connect = new ForkConnection("Connection "+parentNode.getName()+":"+node.getName(), repository.getOwner().getLogin()+"/"+repository.getName(), parentNode,
    			 node);
    	 connections.add(connect);
    }
    
    // Because we are lasy we save the info about the connections in the
    // nodes

    for (ForkConnection connection : connections) {
      connection.getSource().getConnectedTo()
          .add(connection.getDestination());
    }
  }

  public List<ForkNode> getNodes() {
    return nodes;
  }
}