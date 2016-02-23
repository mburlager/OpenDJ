/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyright [year] [name of copyright owner]".
 *
 * Copyright 2008-2010 Sun Microsystems, Inc.
 * Portions Copyright 2015 ForgeRock AS.
 */
package org.opends.guitools.controlpanel.ui.nodes;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Abstract class with some common methods for all the nodes in the
 * 'Manage Index' tree.
 */
public abstract class AbstractIndexTreeNode extends DefaultMutableTreeNode
{
  private static final long serialVersionUID = 8055748310817873273L;
  private String name;
  /**
   * Constructor.
   * @param name the name of the node.
   */
  protected AbstractIndexTreeNode(String name)
  {
    super(name);
    this.name = name;
  }

  /**
   * Returns the name.
   *
   * @return the name
   */
  public String getName()
  {
    return name;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isRoot()
  {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isLeaf()
  {
    return true;
  }
}
