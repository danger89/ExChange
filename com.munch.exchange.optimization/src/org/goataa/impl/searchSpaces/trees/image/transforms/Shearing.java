// Copyright (c) 2010 Thomas Weise (http://www.it-weise.de/, tweise@gmx.de)
// GNU LESSER GENERAL PUBLIC LICENSE (Version 2.1, February 1999)

package org.goataa.impl.searchSpaces.trees.image.transforms;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.goataa.impl.searchSpaces.trees.Node;
import org.goataa.impl.searchSpaces.trees.NodeType;
import org.goataa.impl.searchSpaces.trees.image.GraphicsContext;
import org.goataa.impl.searchSpaces.trees.image.GraphicsNode;
import org.goataa.impl.searchSpaces.trees.math.real.RealFunction;

/**
 * Shear a given area. Three children: x shearing, y shearing, filler
 *
 * @author Thomas Weise
 */
public class Shearing extends GraphicsNode {
  /** a constant required by Java serialization */
  private static final long serialVersionUID = 1;

  /**
   * Create a node with the given children
   *
   * @param pchildren
   *          the child nodes
   * @param in
   *          the node information record
   */
  public Shearing(final Node<?>[] pchildren,
      final NodeType<Shearing, RealFunction> in) {
    super(pchildren, in, false);
  }

  /**
   * The compute functon of graphics contexts
   *
   * @param gc
   *          the graphics context
   * @return the result
   */
  @Override
  public double compute(final GraphicsContext gc) {
    final Graphics2D g;
    double sh1, sh2, res;
    final AffineTransform at;

    if (!(gc.step())) {
      return 0d;
    }

    sh1 = this.get(0).compute(gc);
    if (Double.isNaN(sh1) || Double.isInfinite(sh1)) {
      return 0d;
    }

    sh2 = this.get(1).compute(gc);
    if (Double.isNaN(sh2) || Double.isInfinite(sh2)) {
      return 0d;
    }

    g = gc.getGraphics();
    at = g.getTransform();
    g.shear(Math.max(-10d, Math.min(10d, sh1)),//
        Math.max(-10d, Math.min(10d, sh2)));
    res = this.get(2).compute(gc);
    g.setTransform(at);

    return res;
  }

  /**
   * Fill in the text associated with this node
   *
   * @param sb
   *          the string builder
   */
  @Override
  public void fillInText(final StringBuilder sb) {
    sb.append("shear(x="); //$NON-NLS-1$
    this.get(0).fillInText(sb);
    sb.append(",y="); //$NON-NLS-1$
    this.get(1).fillInText(sb);
    sb.append(",fill="); //$NON-NLS-1$
    this.get(2).fillInText(sb);
    sb.append(')');
  }
}