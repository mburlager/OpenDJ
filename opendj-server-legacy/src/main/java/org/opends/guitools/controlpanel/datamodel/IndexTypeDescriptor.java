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
package org.opends.guitools.controlpanel.datamodel;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.opends.server.admin.std.meta.BackendIndexCfgDefn;
import org.opends.server.util.RemoveOnceNewConfigFrameworkIsUsed;

/**
 * Defines the set of values for the index type and provides adaptors to convert
 * from/to corresponding configuration classes.
 */
@RemoveOnceNewConfigFrameworkIsUsed
public enum IndexTypeDescriptor
{
  /**
   * This index type is used to improve the efficiency of searches using
   * approximate matching search filters.
   */
  APPROXIMATE(BackendIndexCfgDefn.IndexType.APPROXIMATE,
      org.forgerock.opendj.server.config.meta.BackendIndexCfgDefn.IndexType.APPROXIMATE),

  /**
   * This index type is used to improve the efficiency of searches using
   * equality search filters.
   */
  EQUALITY(BackendIndexCfgDefn.IndexType.EQUALITY,
      org.forgerock.opendj.server.config.meta.BackendIndexCfgDefn.IndexType.EQUALITY),

  /**
   * This index type is used to improve the efficiency of searches using
   * extensible matching search filters.
   */
  EXTENSIBLE(BackendIndexCfgDefn.IndexType.EXTENSIBLE,
      org.forgerock.opendj.server.config.meta.BackendIndexCfgDefn.IndexType.EXTENSIBLE),

  /**
   * This index type is used to improve the efficiency of searches using
   * "greater than or equal to" or "less then or equal to" search filters.
   */
  ORDERING(BackendIndexCfgDefn.IndexType.ORDERING,
      org.forgerock.opendj.server.config.meta.BackendIndexCfgDefn.IndexType.ORDERING),

  /**
   * This index type is used to improve the efficiency of searches using the
   * presence search filters.
   */
  PRESENCE(BackendIndexCfgDefn.IndexType.PRESENCE,
      org.forgerock.opendj.server.config.meta.BackendIndexCfgDefn.IndexType.PRESENCE),

  /**
   * This index type is used to improve the efficiency of searches using
   * substring search filters.
   */
  SUBSTRING(BackendIndexCfgDefn.IndexType.SUBSTRING,
      org.forgerock.opendj.server.config.meta.BackendIndexCfgDefn.IndexType.SUBSTRING);

  private final BackendIndexCfgDefn.IndexType oldConfigBackendIndexType;
  private final org.forgerock.opendj.server.config.meta.BackendIndexCfgDefn.IndexType backendIndexType;

  private IndexTypeDescriptor(final BackendIndexCfgDefn.IndexType oldConfigBackendIndexType,
      final org.forgerock.opendj.server.config.meta.BackendIndexCfgDefn.IndexType backendIndexType)
  {
    this.oldConfigBackendIndexType = oldConfigBackendIndexType;
    this.backendIndexType = backendIndexType;
  }

  /**
   * Convert the index type to the equivalent
   * {@code BackendIndexCfgDefn.IndexType}.
   *
   * @return The index type to the equivalent
   *         {@code BackendIndexCfgDefn.IndexType}
   */
  public BackendIndexCfgDefn.IndexType toBackendIndexType()
  {
    return oldConfigBackendIndexType;
  }

  private static IndexTypeDescriptor fromBackendIndexType(final BackendIndexCfgDefn.IndexType indexType)
  {
    switch (indexType)
    {
    case APPROXIMATE:
      return APPROXIMATE;
    case EQUALITY:
      return EQUALITY;
    case EXTENSIBLE:
      return EXTENSIBLE;
    case ORDERING:
      return ORDERING;
    case PRESENCE:
      return PRESENCE;
    case SUBSTRING:
      return SUBSTRING;
    default:
      throw new IllegalArgumentException("No IndexTypeDescriptor corresponding to: " + indexType);
    }
  }

  /**
   * Convert the provided {@code Set<BackendIndexCfgDefn.IndexType>} to a
   * {@code Set<IndexTypeDescriptor>}.
   *
   * @param indexTypes
   *          A set of {@code Set<BackendIndexCfgDefn.IndexType>}
   * @return A set of {@code Set<IndexTypeDescriptor>} corresponding to the
   *         provided {@code Set<BackendIndexCfgDefn.IndexType>}
   */
  public static Set<IndexTypeDescriptor> fromBackendIndexTypes(final Set<BackendIndexCfgDefn.IndexType> indexTypes)
  {
    final Set<IndexTypeDescriptor> indexTypeDescriptors = new LinkedHashSet<>();
    for (final BackendIndexCfgDefn.IndexType indexType : indexTypes)
    {
      indexTypeDescriptors.add(fromBackendIndexType(indexType));
    }
    return indexTypeDescriptors;
  }

  /**
   * Convert the provided {@code Set<IndexTypeDescriptor>} to a
   * {@code Set<BackendIndexCfgDefn.IndexType>}.
   *
   * @param indexTypeDescriptors
   *          A set of {@code Set<IndexTypeDescriptor>}
   * @return A set of {@code Set<BackendIndexCfgDefn.IndexType>} corresponding
   *         to the provided {@code Set<IndexTypeDescriptor>}
   */
  public static Set<BackendIndexCfgDefn.IndexType> toBackendIndexTypes(
      final Set<IndexTypeDescriptor> indexTypeDescriptors)
  {
    final Set<BackendIndexCfgDefn.IndexType> indexTypes = new LinkedHashSet<>();
    for (final IndexTypeDescriptor indexTypeDescriptor : indexTypeDescriptors)
    {
      indexTypes.add(indexTypeDescriptor.toBackendIndexType());
    }
    return indexTypes;
  }

  /**
   * Convert the provided {@code Set<IndexTypeDescriptor>} to a
   * {@code Set<org.forgerock.opendj.server.config.meta.BackendIndexCfgDefn.IndexType>}.
   *
   * @param indexTypeDescriptors
   *          A set of {@code Set<IndexTypeDescriptor>}
   * @return A set of
   *         {@code Set<org.forgerock.opendj.server.config.meta.BackendIndexCfgDefn.IndexType>}
   *         corresponding to the provided {@code Set<IndexTypeDescriptor>}
   */
  public static Set<org.forgerock.opendj.server.config.meta.BackendIndexCfgDefn.IndexType> toNewConfigBackendIndexTypes(
      final Set<IndexTypeDescriptor> indexTypeDescriptors)
  {
    Set<org.forgerock.opendj.server.config.meta.BackendIndexCfgDefn.IndexType> newConfigIndexTypes = new HashSet<>();
    for (IndexTypeDescriptor indexType : indexTypeDescriptors)
    {
      newConfigIndexTypes.add(indexType.backendIndexType);
    }
    return newConfigIndexTypes;
  }

}
