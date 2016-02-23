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
 * Copyright 2006-2008 Sun Microsystems, Inc.
 * Portions Copyright 2014-2015 ForgeRock AS.
 */
package org.opends.server.api;

import org.opends.server.config.ConfigEntry;
import org.forgerock.opendj.config.server.ConfigChangeResult;
import org.forgerock.i18n.LocalizableMessageBuilder;

/**
 * This interface defines the methods that a Directory Server
 * component should implement if it wishes to be able to receive
 * notification if entries below a configuration entry are removed.
 */
@org.opends.server.types.PublicAPI(
     stability=org.opends.server.types.StabilityLevel.VOLATILE,
     mayInstantiate=false,
     mayExtend=true,
     mayInvoke=false)
public interface ConfigDeleteListener
{
  /**
   * Indicates whether it is acceptable to remove the provided
   * configuration entry.
   *
   * @param  configEntry         The configuration entry that will be
   *                             removed from the configuration.
   * @param  unacceptableReason  A buffer to which this method can
   *                             append a human-readable message
   *                             explaining why the proposed delete is
   *                             not acceptable.
   *
   * @return  {@code true} if the proposed entry may be removed from
   *          the configuration, or {@code false} if not.
   */
  boolean configDeleteIsAcceptable(ConfigEntry configEntry,
                      LocalizableMessageBuilder unacceptableReason);



  /**
   * Attempts to apply a new configuration based on the provided
   * deleted entry.
   *
   * @param  configEntry  The new configuration entry that has been
   *                      deleted.
   *
   * @return  Information about the result of processing the
   *          configuration change.
   */
  ConfigChangeResult applyConfigurationDelete(ConfigEntry configEntry);
}

