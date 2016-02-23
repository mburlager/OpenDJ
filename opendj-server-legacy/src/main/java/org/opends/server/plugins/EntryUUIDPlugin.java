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
 * Portions Copyright 2014-2016 ForgeRock AS.
 */
package org.opends.server.plugins;

import static org.opends.messages.PluginMessages.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.forgerock.i18n.LocalizableMessage;
import org.forgerock.opendj.config.server.ConfigChangeResult;
import org.forgerock.opendj.config.server.ConfigException;
import org.opends.server.admin.server.ConfigurationChangeListener;
import org.opends.server.admin.std.meta.PluginCfgDefn;
import org.opends.server.admin.std.server.EntryUUIDPluginCfg;
import org.opends.server.admin.std.server.PluginCfg;
import org.opends.server.api.plugin.DirectoryServerPlugin;
import org.opends.server.api.plugin.PluginResult;
import org.opends.server.api.plugin.PluginType;
import org.opends.server.core.DirectoryServer;
import org.forgerock.opendj.ldap.schema.AttributeType;
import org.opends.server.types.*;
import org.opends.server.types.operation.PreOperationAddOperation;

/**
 * This class implements a Directory Server plugin that will add the entryUUID
 * attribute to an entry whenever it is added or imported as per RFC 4530.  For
 * entries added over LDAP, the entryUUID will be based on a semi-random UUID
 * (which is still guaranteed to be unique).  For entries imported from LDIF,
 * the UUID will be constructed from the entry DN using a repeatable algorithm.
 * This will ensure that LDIF files imported in parallel across multiple systems
 * will have identical entryUUID values.
 */
public final class EntryUUIDPlugin
       extends DirectoryServerPlugin<EntryUUIDPluginCfg>
       implements ConfigurationChangeListener<EntryUUIDPluginCfg>
{
  /** The name of the entryUUID attribute type. */
  private static final String ENTRYUUID = "entryuuid";

  /** The attribute type for the "entryUUID" attribute. */
  private final AttributeType entryUUIDType;
  /** The current configuration for this plugin. */
  private EntryUUIDPluginCfg currentConfig;

  /** Mandatory default constructor of this Directory Server plugin. */
  public EntryUUIDPlugin()
  {
    entryUUIDType = DirectoryServer.getAttributeType(ENTRYUUID);
  }

  @Override
  public final void initializePlugin(Set<PluginType> pluginTypes,
                                     EntryUUIDPluginCfg configuration)
         throws ConfigException
  {
    currentConfig = configuration;
    configuration.addEntryUUIDChangeListener(this);

    // Make sure that the plugin has been enabled for the appropriate types.
    for (PluginType t : pluginTypes)
    {
      switch (t)
      {
        case LDIF_IMPORT:
        case PRE_OPERATION_ADD:
          // These are acceptable.
          break;

        default:
          throw new ConfigException(ERR_PLUGIN_ENTRYUUID_INVALID_PLUGIN_TYPE.get(t));
      }
    }
  }



  /** {@inheritDoc} */
  @Override
  public final void finalizePlugin()
  {
    currentConfig.removeEntryUUIDChangeListener(this);
  }



  @Override
  public final PluginResult.ImportLDIF
               doLDIFImport(LDIFImportConfig importConfig, Entry entry)
  {
    // See if the entry being imported already contains an entryUUID attribute.
    // If so, then leave it alone.
    List<Attribute> uuidList = entry.getAttribute(entryUUIDType);
    if (!uuidList.isEmpty())
    {
      return PluginResult.ImportLDIF.continueEntryProcessing();
    }


    // Construct a new UUID.  In order to make sure that UUIDs are consistent
    // when the same LDIF is generated on multiple servers, we'll base the UUID
    // on the byte representation of the normalized DN.
    byte[] dnBytes = entry.getName().toNormalizedByteString().toByteArray();
    UUID uuid = UUID.nameUUIDFromBytes(dnBytes);

    uuidList = Attributes.createAsList(entryUUIDType, uuid.toString());
    entry.putAttribute(entryUUIDType, uuidList);

    // We shouldn't ever need to return a non-success result.
    return PluginResult.ImportLDIF.continueEntryProcessing();
  }



  /** {@inheritDoc} */
  @Override
  public final PluginResult.PreOperation
               doPreOperation(PreOperationAddOperation addOperation)
  {
    // See if the entry being added already contains an entryUUID attribute.
    // It shouldn't, since it's NO-USER-MODIFICATION, but if it does then leave
    // it alone.
    Map<AttributeType,List<Attribute>> operationalAttributes =
         addOperation.getOperationalAttributes();
    List<Attribute> uuidList = operationalAttributes.get(entryUUIDType);
    if (uuidList != null)
    {
      return PluginResult.PreOperation.continueOperationProcessing();
    }


    // Construct a new random UUID.
    UUID uuid = UUID.randomUUID();
    uuidList = Attributes.createAsList(entryUUIDType, uuid.toString());

    // Add the attribute to the entry and return.
    addOperation.setAttribute(entryUUIDType, uuidList);
    return PluginResult.PreOperation.continueOperationProcessing();
  }



  /** {@inheritDoc} */
  @Override
  public boolean isConfigurationAcceptable(PluginCfg configuration,
                                           List<LocalizableMessage> unacceptableReasons)
  {
    EntryUUIDPluginCfg cfg = (EntryUUIDPluginCfg) configuration;
    return isConfigurationChangeAcceptable(cfg, unacceptableReasons);
  }



  /** {@inheritDoc} */
  @Override
  public boolean isConfigurationChangeAcceptable(
                      EntryUUIDPluginCfg configuration,
                      List<LocalizableMessage> unacceptableReasons)
  {
    boolean configAcceptable = true;

    // Ensure that the set of plugin types contains only LDIF import and
    // pre-operation add.
    for (PluginCfgDefn.PluginType pluginType : configuration.getPluginType())
    {
      switch (pluginType)
      {
        case LDIFIMPORT:
        case PREOPERATIONADD:
          // These are acceptable.
          break;


        default:
          unacceptableReasons.add(ERR_PLUGIN_ENTRYUUID_INVALID_PLUGIN_TYPE.get(pluginType));
          configAcceptable = false;
      }
    }

    return configAcceptable;
  }



  /** {@inheritDoc} */
  @Override
  public ConfigChangeResult applyConfigurationChange(
                                 EntryUUIDPluginCfg configuration)
  {
    currentConfig = configuration;
    return new ConfigChangeResult();
  }
}

