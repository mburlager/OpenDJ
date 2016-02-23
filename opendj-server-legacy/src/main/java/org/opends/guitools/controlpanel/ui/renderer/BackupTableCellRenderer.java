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
import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

import org.opends.guitools.controlpanel.datamodel.BackupDescriptor;
import org.opends.guitools.controlpanel.datamodel.BackupTableModel;
import org.opends.guitools.controlpanel.ui.ColorAndFontConstants;

/**
 * Renderer of the table that contains the list of backups (it is used in the
 * tables of the verify backup and restore panels).
 *
 */
public class BackupTableCellRenderer extends DefaultTableCellRenderer
{
  private static final long serialVersionUID = -4645902129785751854L;
  private DateFormat formatter = DateFormat.getDateInstance(DateFormat.FULL);
  private File backupParentPath;
  private static final Border fullBorder = BorderFactory.createCompoundBorder(
      BorderFactory.createMatteBorder(1, 0, 0, 0,
          ColorAndFontConstants.gridColor),
          BorderFactory.createEmptyBorder(4, 4, 4, 4));
  private static final Border incrementalBorder =
    BorderFactory.createEmptyBorder(4, 4, 4, 4);

  /**
   * Default constructor.
   *
   */
  public BackupTableCellRenderer()
  {
    setForeground(ColorAndFontConstants.tableForeground);
    setBackground(ColorAndFontConstants.tableBackground);
  }


  /**
   * Sets the path to which the backups are relative.
   * @param backupParentPath the path to which the backups are relative.
   */
  public void setParentPath(File backupParentPath)
  {
    this.backupParentPath = backupParentPath;
  }

  /** {@inheritDoc} */
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column)
  {
    String s;
    boolean isDate = false;
    boolean isFull = ((BackupTableModel)table.getModel()).get(row).getType()
    == BackupDescriptor.Type.FULL;
    if (value instanceof File)
    {
      File f = (File)value;
      s = "";
      boolean isParent = false;
      while (f != null)
      {
        if (!f.equals(backupParentPath))
        {
          if (s.length() == 0)
          {
            s = f.getName();
          }
          else
          {
            s = f.getName() + File.separator + s;
          }
        }
        else
        {
          isParent = true;
          break;
        }
        f = f.getParentFile();
      }
      if (isParent)
      {
        if (!isFull)
        {
          s = "  "+s;
        }
      }
      else
      {
        s = value.toString();
      }
    }
    else if (value instanceof Date)
    {
      isDate = true;
      s = formatter.format((Date)value);
    }
    else if (value instanceof BackupDescriptor.Type)
    {
      if (isFull)
      {
        s = "Full";
      }
      else
      {
        s = "Incremental";
      }
    }
    else if (value instanceof String)
    {
      s = (String)value;
    }
    else
    {
      throw new IllegalArgumentException(
          "Unknown class for "+value+": "+" row: "+row+ "column: "+column);
    }
    super.getTableCellRendererComponent(table, s, isSelected, hasFocus, row, column);
    if (isFull && row != 0)
    {
      setBorder(fullBorder);
    }
    else
    {
      setBorder(incrementalBorder);
    }
    setHorizontalAlignment(isDate ? SwingConstants.RIGHT : SwingConstants.LEFT);

    return this;
  }
}
