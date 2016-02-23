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
 * Copyright 2008-2009 Sun Microsystems, Inc.
 */

package org.opends.server.admin;



/**
 * A visitor of relation definitions, in the style of the visitor
 * design pattern. Classes implementing this interface can query
 * relation definitions in a type-safe manner when the kind of
 * relation definition is unknown at compile time. When a visitor is
 * passed to a relation definition's accept method, the corresponding
 * visit method most applicable to that relation definition is
 * invoked.
 *
 * @param <R>
 *          The return type of this visitor's methods. Use
 *          {@link java.lang.Void} for visitors that do not need to
 *          return results.
 * @param <P>
 *          The type of the additional parameter to this visitor's
 *          methods. Use {@link java.lang.Void} for visitors that do
 *          not need an additional parameter.
 */
public interface RelationDefinitionVisitor<R, P> {

  /**
   * Visit an instantiable relation definition.
   *
   * @param <C>
   *          The type of client managed object configuration that the
   *          relation definition refers to.
   * @param <S>
   *          The type of server managed object configuration that the
   *          relation definition refers to.
   * @param rd
   *          The instantiable relation definition to visit.
   * @param p
   *          A visitor specified parameter.
   * @return Returns a visitor specified result.
   */
  <C extends ConfigurationClient, S extends Configuration> R visitInstantiable(
      InstantiableRelationDefinition<C, S> rd, P p);



  /**
   * Visit a set relation definition.
   *
   * @param <C>
   *          The type of client managed object configuration that the
   *          relation definition refers to.
   * @param <S>
   *          The type of server managed object configuration that the
   *          relation definition refers to.
   * @param rd
   *          The set relation definition to visit.
   * @param p
   *          A visitor specified parameter.
   * @return Returns a visitor specified result.
   */
  <C extends ConfigurationClient, S extends Configuration> R visitSet(
      SetRelationDefinition<C, S> rd, P p);



  /**
   * Visit an optional relation definition.
   *
   * @param <C>
   *          The type of client managed object configuration that the
   *          relation definition refers to.
   * @param <S>
   *          The type of server managed object configuration that the
   *          relation definition refers to.
   * @param rd
   *          The optional relation definition to visit.
   * @param p
   *          A visitor specified parameter.
   * @return Returns a visitor specified result.
   */
  <C extends ConfigurationClient, S extends Configuration> R visitOptional(
      OptionalRelationDefinition<C, S> rd, P p);



  /**
   * Visit a singleton relation definition.
   *
   * @param <C>
   *          The type of client managed object configuration that the
   *          relation definition refers to.
   * @param <S>
   *          The type of server managed object configuration that the
   *          relation definition refers to.
   * @param rd
   *          The singleton relation definition to visit.
   * @param p
   *          A visitor specified parameter.
   * @return Returns a visitor specified result.
   */
  <C extends ConfigurationClient, S extends Configuration> R visitSingleton(
      SingletonRelationDefinition<C, S> rd, P p);

}
