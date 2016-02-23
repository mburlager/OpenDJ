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
 * Copyright 2015 ForgeRock AS.
 */
package org.opends.server.backends.jeb;

import java.util.List;

import org.forgerock.i18n.LocalizableMessage;
import org.forgerock.opendj.config.server.ConfigException;
import org.opends.server.admin.std.server.JEBackendCfg;
import org.opends.server.backends.pluggable.BackendImpl;
import org.opends.server.backends.pluggable.spi.Storage;
import org.opends.server.core.ServerContext;

/** Class defined in the configuration for this backend type. */
public final class JEBackend extends BackendImpl<JEBackendCfg>
{
  @Override
  public boolean isConfigurationAcceptable(JEBackendCfg cfg, List<LocalizableMessage> unacceptableReasons,
      ServerContext serverContext)
  {
    return JEStorage.isConfigurationAcceptable(cfg, unacceptableReasons, serverContext);
  }

  @Override
  protected Storage configureStorage(JEBackendCfg cfg, ServerContext serverContext) throws ConfigException
  {
    return new JEStorage(cfg, serverContext);
  }
}
