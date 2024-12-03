/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs;

import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeGroupDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSIDCDefinition;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSNotationDeclaration;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTypeDefinition;

public interface XSNamespaceItem {
    public String getSchemaNamespace();

    public XSNamedMap getComponents(short var1);

    public XSObjectList getAnnotations();

    public XSElementDeclaration getElementDeclaration(String var1);

    public XSAttributeDeclaration getAttributeDeclaration(String var1);

    public XSTypeDefinition getTypeDefinition(String var1);

    public XSAttributeGroupDefinition getAttributeGroup(String var1);

    public XSModelGroupDefinition getModelGroupDefinition(String var1);

    public XSNotationDeclaration getNotationDeclaration(String var1);

    public XSIDCDefinition getIDCDefinition(String var1);

    public StringList getDocumentLocations();
}

