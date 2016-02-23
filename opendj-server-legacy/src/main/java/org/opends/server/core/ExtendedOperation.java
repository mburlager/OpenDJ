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
 * Copyright 2006-2009 Sun Microsystems, Inc.
 * Portions Copyright 2014-2015 ForgeRock AS.
 */
package org.opends.server.core;

import org.opends.server.types.Operation;
import org.forgerock.opendj.ldap.ByteString;


/**
 * This interface defines an extended operation, which can perform virtually any
 * kind of task.
 */
public interface ExtendedOperation
       extends Operation
{
  /**
   * Retrieves the OID for the request associated with this extended
   * operation.
   *
   * @return  The OID for the request associated with this extended
   *          operation.
   */
  String getRequestOID();



  /**
   * Retrieves the value for the request associated with this extended
   * operation.
   *
   * @return  The value for the request associated with this extended
   *          operation.
   */
  ByteString getRequestValue();



  /**
   * Retrieves the OID to include in the response to the client.
   *
   * @return  The OID to include in the response to the client.
   */
  String getResponseOID();



  /**
   * Specifies the OID to include in the response to the client.
   *
   * @param  responseOID  The OID to include in the response to the
   *                      client.
   */
  void setResponseOID(String responseOID);



  /**
   * Retrieves the value to include in the response to the client.
   *
   * @return  The value to include in the response to the client.
   */
  ByteString getResponseValue();



  /**
   * Specifies the value to include in the response to the client.
   *
   * @param  responseValue  The value to include in the response to
   *                        the client.
   */
  void setResponseValue(ByteString responseValue);
}

