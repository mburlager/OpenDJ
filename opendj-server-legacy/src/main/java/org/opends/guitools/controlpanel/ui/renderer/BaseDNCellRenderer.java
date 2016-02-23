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
 * Copyright 2008 Sun Microsystems, Inc.
 * Portions Copyright 2015 ForgeRock AS.
 */

package org.opends.guitools.controlpanel.ui.renderer;

import java.awt.Component;

import javax.swing.JTable;

import org.opends.guitools.controlpanel.datamodel.BaseDNTableModel;
import org.opends.guitools.controlpanel.util.Utilities;

/**
 * Class used to render the base DN table cells.
 */
public class BaseDNCellRenderer extends CustomCellRenderer
{
  private static final long serialVersionUID = -256719167426289735L;

  /**
   * Default constructor.
   */
  public BaseDNCellRenderer()
  {
    super();
  }

  /** {@inheritDoc} */
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    String text = (String)value;
    if (text == BaseDNTableModel.NOT_AVAILABLE)
    {
      Utilities.setNotAvailable(this);
    }
    else if (text == BaseDNTableModel.NOT_AVAILABLE_AUTHENTICATION_REQUIRED)
    {
      Utilities.setNotAvailableBecauseAuthenticationIsRequired(this);
    }
    else if (text == BaseDNTableModel.NOT_AVAILABLE_SERVER_DOWN)
    {
      Utilities.setNotAvailableBecauseServerIsDown(this);
    }
    else
    {
      Utilities.setTextValue(this, text);
    }
    return super.getTableCellRendererComponent(table, getText(),
        isSelected, hasFocus, row, column);
  }
}
