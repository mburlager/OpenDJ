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
 * Copyright 2016 ForgeRock AS.
 */
package org.opends.server.util;

import org.forgerock.opendj.ldap.schema.AttributeType;

import static org.opends.server.schema.SchemaConstants.SYNTAX_AUTH_PASSWORD_OID;
import static org.opends.server.schema.SchemaConstants.SYNTAX_USER_PASSWORD_OID;

/** Utility methods related to schema. */
public class SchemaUtils
{

  /** Private constructor to prevent instantiation. */
  private SchemaUtils() {
    // No implementation required.
  }

  /** Represents a password type, including a "not a password" value. */
  public enum PasswordType
  {
    /** Auth Password. */
    AUTH_PASSWORD,
    /** User Password. */
    USER_PASSWORD,
    /** Not a password. */
    NOT_A_PASSWORD
  }

  /**
   * Checks if the provided attribute type contains a password.
   *
   * @param attrType
   *            The attribute type to check.
   * @return a PasswordTypeCheck result
   */
  public static PasswordType checkPasswordType(AttributeType attrType)
  {
    final String syntaxOID = attrType.getSyntax().getOID();
    if (syntaxOID.equals(SYNTAX_AUTH_PASSWORD_OID))
    {
      return PasswordType.AUTH_PASSWORD;
    }
    else if (attrType.hasName("userPassword") || syntaxOID.equals(SYNTAX_USER_PASSWORD_OID))
    {
      return PasswordType.USER_PASSWORD;
    }
    return PasswordType.NOT_A_PASSWORD;
  }
}
