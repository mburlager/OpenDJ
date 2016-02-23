<!--
  The contents of this file are subject to the terms of the Common Development and
  Distribution License (the License). You may not use this file except in compliance with the
  License.

  You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
  specific language governing permission and limitations under the License.

  When distributing Covered Software, include this CDDL Header Notice in each file and include
  the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
  Header, with the fields enclosed by brackets [] replaced by your own identifying
  information: "Portions Copyright [year] [name of copyright owner]".

  Copyright 2008 Sun Microsystems, Inc.
  ! -->
<xsl:stylesheet version="1.0" xmlns:adm="http://www.opends.org/admin"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!-- 
    Templates for processing java class properties.
  -->
  <xsl:template match="adm:java-class" mode="java-value-type">
    <xsl:value-of select="'String'" />
  </xsl:template>
  <xsl:template match="adm:java-class" mode="java-definition-type">
    <xsl:value-of select="'ClassPropertyDefinition'" />
  </xsl:template>
  <xsl:template match="adm:java-class" mode="java-definition-ctor">
    <xsl:for-each select="adm:instance-of">
      <!-- 
        The first instance of element added to the definition
        will become the primary type for the class. This first
        element is guaranteed to be the first instance-of field
        appearing in the property's definition heirarchy working
        up from the bottom.
      -->
      <xsl:value-of
        select="concat('      builder.addInstanceOf(&quot;',
                       normalize-space(), '&quot;);&#xa;')" />
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
