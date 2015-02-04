package org.ancit.github.utils.forkvis.views;
import java.util.ArrayList;

import org.ancit.github.utils.forkvis.model.ForkNode;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

public class ForkVisualisationContentProvider extends ArrayContentProvider  implements IGraphEntityContentProvider {

  @Override
  public Object[] getConnectedTo(Object entity) {
    if (entity instanceof ForkNode) {
    	ForkNode node = (ForkNode) entity;
      return node.getConnectedTo().toArray();
    }
    return new ArrayList().toArray();
  }
} 