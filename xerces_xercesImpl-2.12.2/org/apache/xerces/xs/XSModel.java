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
import org.apache.xerces.xs.XSNamespaceItemList;
import org.apache.xerces.xs.XSNotationDeclaration;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTypeDefinition;

public interface XSModel {
    public StringList getNamespaces();

    public XSNamespaceItemList getNamespaceItems();

    public XSNamedMap getComponents(short var1);

    public XSNamedMap getComponentsByNamespace(short var1, String var2);

    public XSObjectList getAnnotations();

    public XSElementDeclaration getElementDeclaration(String var1, String var2);

    public XSAttributeDeclaration getAttributeDeclaration(String var1, String var2);

    public XSTypeDefinition getTypeDefinition(String var1, String var2);

    public XSAttributeGroupDefinition getAttributeGroup(String var1, String var2);

    public XSModelGroupDefinition getModelGroupDefinition(String var1, String var2);

    public XSNotationDeclaration getNotationDeclaration(String var1, String var2);

    public XSIDCDefinition getIDCDefinition(String var1, String var2);

    public XSObjectList getSubstitutionGroup(XSElementDeclaration var1);
}

