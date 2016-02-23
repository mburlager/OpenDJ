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
package org.opends.server.authorization.dseecompat;

/**
 * This class provides an enumeration of the allowed authmethod types.
 */
public enum EnumAuthMethod {

    /**
     * The enumeration type when the bind rule has specified authentication of
     * none.
     */
    AUTHMETHOD_NONE          ("none"),

    /**
     * The enumeration type when the bind rule has specified authentication of
     * simple.
     */
    AUTHMETHOD_SIMPLE        ("simple"),

    /**
     * The enumeration type when the bind rule has specified authentication of
     * ssl client auth.
     */
    AUTHMETHOD_SSL           ("ssl"),

    /**
     * The enumeration type when the bind rule has specified authentication of
     * a sasl mechanism.
     */
    AUTHMETHOD_SASL          ("sasl");

    /**
     * Creates a new enumeration type for this authmethod.
     * @param authmethod The authemethod name.
     */
    EnumAuthMethod (String authmethod){
    }

}
