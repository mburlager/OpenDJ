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
 * Portions Copyright 2011-2015 ForgeRock AS.
 */
package org.opends.server.types;

import static org.testng.AssertJUnit.assertEquals;

import org.opends.server.core.SubtreeSpecificationTestCase;
import org.opends.server.types.DN;
import org.opends.server.types.SubtreeSpecification;
import org.testng.annotations.Test;

/**
 * This class defines a set of tests for the
 * {@link org.opends.server.types.SubtreeSpecification} class.
 */
public final class TestSubtreeSpecification extends
    SubtreeSpecificationTestCase {

  /** Cached root DN. */
  private DN rootDN = DN.rootDN();

  /**
   * Tests the {@link SubtreeSpecification#valueOf(DN, String)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testValueOf1() throws Exception {

    String input = "{}";
    String output = "{ }";

    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        input);
    assertEquals(output, ss.toString());
  }

  /**
   * Tests the {@link SubtreeSpecification#valueOf(DN, String)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testValueOf2() throws Exception {

    String input = "  {    }    ";
    String output = "{ }";

    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        input);
    assertEquals(output, ss.toString());
  }

  /**
   * Tests the {@link SubtreeSpecification#valueOf(DN, String)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testValueOf3() throws Exception {

    String input = "{ base \"dc=sun, dc=com\" }";
    String output = "{ base \"dc=sun,dc=com\" }";

    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        input);
    assertEquals(output, ss.toString());
  }

  /**
   * Tests the {@link SubtreeSpecification#valueOf(DN, String)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testValueOf4() throws Exception {

    String input = "{base \"dc=sun, dc=com\"}";
    String output = "{ base \"dc=sun,dc=com\" }";

    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        input);
    assertEquals(output, ss.toString());
  }

  /**
   * Tests the {@link SubtreeSpecification#valueOf(DN, String)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testValueOf5() throws Exception {

    String input = "{ base \"dc=sun, dc=com\", "
        + "specificationFilter item:ds-config-rootDN }";
    String output = "{ base \"dc=sun,dc=com\", "
        + "specificationFilter item:ds-config-rootDN }";

    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        input);
    assertEquals(output, ss.toString());
  }

  /**
   * Tests the {@link SubtreeSpecification#valueOf(DN, String)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testValueOf6() throws Exception {

    String input = "{ base \"dc=sun, dc=com\", minimum 0 , maximum 10, "
        + "specificExclusions {chopBefore:\"o=abc\", "
        + "chopAfter:\"o=xyz\"} , specificationFilter not:not:item:foo }";
    String output = "{ base \"dc=sun,dc=com\", "
        + "specificExclusions { chopBefore:\"o=abc\", "
        + "chopAfter:\"o=xyz\" }, maximum 10, specificationFilter "
        + "not:not:item:foo }";

    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        input);
    assertEquals(output, ss.toString());
  }

  /**
   * Tests the {@link SubtreeSpecification#valueOf(DN, String)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testValueOf7() throws Exception {

    String input = "{ base \"\", minimum 0,maximum 10,"
        + "specificExclusions {chopBefore:\"o=abc\","
        + "chopAfter:\"o=xyz\"},specificationFilter not:not:item:foo}";
    String output = "{ specificExclusions { chopBefore:\"o=abc\", "
        + "chopAfter:\"o=xyz\" }, "
        + "maximum 10, specificationFilter not:not:item:foo }";

    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        input);
    assertEquals(output, ss.toString());
  }

  /**
   * Tests the {@link SubtreeSpecification#valueOf(DN, String)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testValueOf8() throws Exception {

    String input = "{ specificationFilter and:{item:top, item:person} }";
    String output = "{ specificationFilter and:{item:top, item:person} }";

    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        input);
    assertEquals(output, ss.toString());
  }

  /**
   * Tests the {@link SubtreeSpecification#valueOf(DN, String)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testValueOf9() throws Exception {

    String input = "{ specificationFilter or:{item:top, item:person} }";
    String output = "{ specificationFilter or:{item:top, item:person} }";

    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        input);
    assertEquals(output, ss.toString());
  }

  /**
   * Tests the {@link SubtreeSpecification#valueOf(DN, String)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testValueOf10() throws Exception {

    String input = "{ specificationFilter "
        + "or:{item:top, item:foo, and:{item:one, item:two}} }";
    String output = "{ specificationFilter "
        + "or:{item:top, item:foo, and:{item:one, item:two}} }";

    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        input);
    assertEquals(output, ss.toString());
  }

  /**
   * Tests the {@link SubtreeSpecification#valueOf(DN, String)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testValueOf11() throws Exception {

    String input = "{ base \"dc=sun, dc=com\", "
        + "specificationFilter \"(objectClass=*)\" }";
    String output = "{ base \"dc=sun,dc=com\", "
        + "specificationFilter \"(objectClass=*)\" }";

    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        input);
    assertEquals(output, ss.toString());
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches1() throws Exception {
    DN dn = DN.valueOf("dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\" }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(true, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches2() throws Exception {
    DN dn = DN.valueOf("dc=com");

    String value = "{ base \"dc=sun, dc=com\" }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(false, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches3() throws Exception {
    DN dn = DN.valueOf("dc=foo, dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\" }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(true, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches4() throws Exception {
    DN dn = DN.valueOf("dc=foo, dc=bar, dc=com");

    String value = "{ base \"dc=sun, dc=com\" }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(false, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches5() throws Exception {
    DN dn = DN.valueOf("dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\", minimum 1 }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(false, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches6() throws Exception {
    DN dn = DN.valueOf("dc=abc, dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\", minimum 1 }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(true, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches7() throws Exception {
    DN dn = DN.valueOf("dc=xyz, dc=abc, dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\", minimum 1 }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(true, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches8() throws Exception {
    DN dn = DN.valueOf("dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\", maximum 0 }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(true, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches9() throws Exception {
    DN dn = DN.valueOf("dc=foo, dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\", maximum 0 }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(false, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches10() throws Exception {
    DN dn = DN.valueOf("dc=bar, dc=foo, dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\", maximum 1 }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(false, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches11() throws Exception {
    DN dn = DN.valueOf("dc=bar, dc=foo, dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\", maximum 2 }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(true, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches12() throws Exception {
    DN dn = DN.valueOf("dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\", "
        + "specificExclusions { chopAfter:\"\" } }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(true, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches13() throws Exception {
    DN dn = DN.valueOf("dc=foo, dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\", "
        + "specificExclusions { chopAfter:\"\" } }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(false, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches14() throws Exception {
    DN dn = DN.valueOf("dc=foo, dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\", "
        + "specificExclusions { chopAfter:\"dc=foo\" } }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(true, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches15() throws Exception {
    DN dn = DN.valueOf("dc=bar, dc=foo, dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\", "
        + "specificExclusions { chopAfter:\"dc=foo\" } }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(false, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches16() throws Exception {
    DN dn = DN.valueOf("dc=foo, dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\", "
        + "specificExclusions { chopBefore:\"dc=foo\" } }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(false, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches17() throws Exception {
    DN dn = DN.valueOf("dc=bar, dc=foo, dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\", "
        + "specificExclusions { chopBefore:\"dc=foo\" } }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(false, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches18() throws Exception {
    DN dn = DN.valueOf("dc=abc, dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\", "
        + "specificExclusions { chopBefore:\"dc=foo\" } }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(true, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches19() throws Exception {
    DN dn = DN.valueOf("dc=abc, dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\", "
        + "specificationFilter item:person }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(true, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches20() throws Exception {
    DN dn = DN.valueOf("dc=abc, dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\", "
        + "specificationFilter item:organization }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(false, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches21() throws Exception {
    DN dn = DN.valueOf("dc=abc, dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\", "
        + "specificationFilter not:item:person }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(false, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@link SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches22() throws Exception {
    DN dn = DN.valueOf("dc=abc, dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\", "
        + "specificationFilter not:item:organization }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(true, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@code SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches23() throws Exception {
    DN dn = DN.valueOf("dc=abc, dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\", "
        + "specificationFilter \"(objectClass=person)\" }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(true, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }

  /**
   * Tests the {@code SubtreeSpecification#isWithinScope(Entry)}
   * method.
   *
   * @throws Exception
   *           If the test failed unexpectedly.
   */
  @Test
  public void testMatches24() throws Exception {
    DN dn = DN.valueOf("dc=abc, dc=sun, dc=com");

    String value = "{ base \"dc=sun, dc=com\", "
        + "specificationFilter \"(objectClass=organization)\" }";
    SubtreeSpecification ss = SubtreeSpecification.valueOf(rootDN,
        value);

    assertEquals(false, ss
        .isWithinScope(createEntry(dn, getObjectClasses())));
  }
}
