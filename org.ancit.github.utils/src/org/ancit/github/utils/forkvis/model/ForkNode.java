package org.ancit.github.utils.forkvis.model;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.egit.github.core.RepositoryId;

public class ForkNode {
  private final String id;
  private final String name;
  private List<ForkNode> connections;
  private Object domainObject;

  public ForkNode(String id, String name, Object repo) {
    this.id = id;
    this.name = name;
    this.connections = new ArrayList<ForkNode>();
    this.domainObject = repo;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public List<ForkNode> getConnectedTo() {
    return connections;
  }
  
  public Object getDomainObject() {
	return domainObject;
  }

} 