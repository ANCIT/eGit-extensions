package org.ancit.github.utils.forkvis.model;
public class ForkConnection {
  final String id; 
  final String label; 
  final ForkNode source;
  final ForkNode destination;
  
  public ForkConnection(String id, String label, ForkNode source, ForkNode destination) {
    this.id = id;
    this.label = label;
    this.source = source;
    this.destination = destination;
  }

  public String getLabel() {
    return label;
  }
  
  public ForkNode getSource() {
    return source;
  }
  public ForkNode getDestination() {
    return destination;
  }
  
} 