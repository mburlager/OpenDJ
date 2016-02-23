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
 * Portions Copyright 2014 ForgeRock AS.
 */
package org.opends.server.api;
import org.forgerock.i18n.LocalizableMessage;



import java.util.List;
import java.util.Set;

import org.opends.server.admin.std.server.PasswordValidatorCfg;
import org.forgerock.opendj.config.server.ConfigException;
import org.opends.server.types.*;
import org.forgerock.opendj.ldap.ByteString;
import org.forgerock.i18n.LocalizableMessageBuilder;


/**
 * This class defines the set of methods and structures that must be
 * implemented by a Directory Server module that may be used to
 * determine whether a proposed password is acceptable for a user.
 *
 * @param  <T>  The type of configuration handled by this password
 *              validator.
 */
@org.opends.server.types.PublicAPI(
     stability=org.opends.server.types.StabilityLevel.UNCOMMITTED,
     mayInstantiate=false,
     mayExtend=true,
     mayInvoke=false)
public abstract class PasswordValidator
       <T extends PasswordValidatorCfg>
{
  /**
   * Initializes this password validator based on the information in
   * the provided configuration entry.
   *
   * @param  configuration  The configuration to use to initialize
   *                        this password validator.
   *
   * @throws  ConfigException  If an unrecoverable problem arises in
   *                           the process of performing the
   *                           initialization.
   *
   * @throws  InitializationException  If a problem occurs during
   *                                   initialization that is not
   *                                   related to the server
   *                                   configuration.
   */
  public abstract void initializePasswordValidator(T configuration)
         throws ConfigException, InitializationException;



  /**
   * Indicates whether the provided configuration is acceptable for
   * this password validator.  It should be possible to call this
   * method on an uninitialized password validator instance in order
   * to determine whether the password validator would be able to use
   * the provided configuration.
   * <BR><BR>
   * Note that implementations which use a subclass of the provided
   * configuration class will likely need to cast the configuration
   * to the appropriate subclass type.
   *
   * @param  configuration        The password validator configuration
   *                              for which to make the determination.
   * @param  unacceptableReasons  A list that may be used to hold the
   *                              reasons that the provided
   *                              configuration is not acceptable.
   *
   * @return  {@code true} if the provided configuration is acceptable
   *          for this password validator, or {@code false} if not.
   */
  public boolean isConfigurationAcceptable(
                      PasswordValidatorCfg configuration,
                      List<LocalizableMessage> unacceptableReasons)
  {
    // This default implementation does not perform any special
    // validation.  It should be overridden by password validator
    // implementations that wish to perform more detailed validation.
    return true;
  }



  /**
   * Performs any finalization that might be required when this
   * password validator is unloaded.  No action is taken in the
   * default implementation.
   */
  public void finalizePasswordValidator()
  {
    // No action is required by default.
  }



  /**
   * Indicates whether the provided password is acceptable for use by
   * the specified user.  If the password is determined to be
   * unacceptable, then a human-readable explanation should be
   * appended to the provided buffer.
   *
   * @param  newPassword       The proposed clear-text password that
   *                           should be validated.
   * @param  currentPasswords  The set of clear-text current passwords
   *                           for the user (if available).  Note that
   *                           the current passwords may not always be
   *                           available, and this may not comprise
   *                           entire set of passwords currently
   *                           for the user.
   * @param  operation         The operation that is being used to set
   *                           the password.  It may be an add, a
   *                           modify, or a password modify operation.
   * @param  userEntry         The entry for the user whose password
   *                           is being changed.
   * @param  invalidReason     The buffer to which the human-readable
   *                           explanation should be appended if it is
   *                           determined that the password is not
   *                           acceptable.
   *
   * @return  {@code true} if the password is acceptable, or
   *          {@code false} if not.
   */
  public abstract boolean passwordIsAcceptable(ByteString newPassword,
                               Set<ByteString> currentPasswords,
                               Operation operation,
                               Entry userEntry,
                               LocalizableMessageBuilder invalidReason);
}

