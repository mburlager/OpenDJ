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
 */

package org.forgerock.opendj.config;

import java.util.Collection;
import java.util.Collections;

/**
 * An interface which can be used to initialize the contents of a managed
 * object.
 */
public interface PropertyProvider {

    /**
     * A property provider which always returns empty property values,
     * indicating default behavior.
     */
    PropertyProvider DEFAULT_PROVIDER = new PropertyProvider() {

        /** {@inheritDoc} */
        public <T> Collection<T> getPropertyValues(PropertyDefinition<T> d) {
            return Collections.<T> emptySet();
        }

    };

    /**
     * Get the property values associated with the specified property
     * definition.
     * <p>
     * Implementations are not required to validate the values that they
     * provide. Specifically:
     * <ul>
     * <li>they do not need to guarantee that the provided values are valid
     * according to the property's syntax
     * <li>they do not need to provide values for mandatory properties
     * <li>they do not need to ensure that single-valued properties do contain
     * at most one value.
     * </ul>
     * The returned set of values is allowed to contain duplicates.
     *
     * @param <T>
     *            The underlying type of the property.
     * @param d
     *            The Property definition.
     * @return Returns a newly allocated set containing a copy of the property's
     *         values. An empty set indicates that the property has no values
     *         defined and any default behavior is applicable.
     * @throws IllegalArgumentException
     *             If this property provider does not recognise the requested
     *             property definition.
     */
    <T> Collection<T> getPropertyValues(PropertyDefinition<T> d);
}
