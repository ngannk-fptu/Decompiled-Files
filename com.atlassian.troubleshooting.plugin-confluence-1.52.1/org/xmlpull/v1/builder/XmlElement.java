/*
 * Decompiled with CFR 0.152.
 */
package org.xmlpull.v1.builder;

import java.util.Iterator;
import org.xmlpull.v1.builder.Iterable;
import org.xmlpull.v1.builder.XmlAttribute;
import org.xmlpull.v1.builder.XmlBuilderException;
import org.xmlpull.v1.builder.XmlContained;
import org.xmlpull.v1.builder.XmlContainer;
import org.xmlpull.v1.builder.XmlNamespace;

public interface XmlElement
extends XmlContainer,
XmlContained,
Cloneable {
    public static final String NO_NAMESPACE = "";

    public Object clone() throws CloneNotSupportedException;

    public String getBaseUri();

    public void setBaseUri(String var1);

    public XmlContainer getRoot();

    public XmlContainer getParent();

    public void setParent(XmlContainer var1);

    public XmlNamespace getNamespace();

    public String getNamespaceName();

    public void setNamespace(XmlNamespace var1);

    public String getName();

    public void setName(String var1);

    public Iterator attributes();

    public XmlAttribute addAttribute(XmlAttribute var1);

    public XmlAttribute addAttribute(String var1, String var2);

    public XmlAttribute addAttribute(XmlNamespace var1, String var2, String var3);

    public XmlAttribute addAttribute(String var1, XmlNamespace var2, String var3, String var4);

    public XmlAttribute addAttribute(String var1, XmlNamespace var2, String var3, String var4, boolean var5);

    public XmlAttribute addAttribute(String var1, String var2, String var3, String var4, String var5, boolean var6);

    public void ensureAttributeCapacity(int var1);

    public String getAttributeValue(String var1, String var2);

    public XmlAttribute attribute(String var1);

    public XmlAttribute attribute(XmlNamespace var1, String var2);

    public XmlAttribute findAttribute(String var1, String var2);

    public boolean hasAttributes();

    public void removeAttribute(XmlAttribute var1);

    public void removeAllAttributes();

    public Iterator namespaces();

    public XmlNamespace declareNamespace(String var1, String var2);

    public XmlNamespace declareNamespace(XmlNamespace var1);

    public void ensureNamespaceDeclarationsCapacity(int var1);

    public boolean hasNamespaceDeclarations();

    public XmlNamespace lookupNamespaceByPrefix(String var1);

    public XmlNamespace lookupNamespaceByName(String var1);

    public XmlNamespace newNamespace(String var1);

    public XmlNamespace newNamespace(String var1, String var2);

    public void removeAllNamespaceDeclarations();

    public Iterator children();

    public void addChild(Object var1);

    public void addChild(int var1, Object var2);

    public XmlElement addElement(XmlElement var1);

    public XmlElement addElement(int var1, XmlElement var2);

    public XmlElement addElement(String var1);

    public XmlElement addElement(XmlNamespace var1, String var2);

    public boolean hasChildren();

    public boolean hasChild(Object var1);

    public void ensureChildrenCapacity(int var1);

    public XmlElement findElementByName(String var1);

    public XmlElement findElementByName(String var1, String var2);

    public XmlElement findElementByName(String var1, XmlElement var2);

    public XmlElement findElementByName(String var1, String var2, XmlElement var3);

    public XmlElement element(int var1);

    public XmlElement requiredElement(XmlNamespace var1, String var2) throws XmlBuilderException;

    public XmlElement element(XmlNamespace var1, String var2);

    public XmlElement element(XmlNamespace var1, String var2, boolean var3);

    public Iterable elements(XmlNamespace var1, String var2);

    public void insertChild(int var1, Object var2);

    public XmlElement newElement(String var1);

    public XmlElement newElement(XmlNamespace var1, String var2);

    public XmlElement newElement(String var1, String var2);

    public void removeAllChildren();

    public void removeChild(Object var1);

    public void replaceChild(Object var1, Object var2);

    public Iterable requiredElementContent();

    public String requiredTextContent();

    public void replaceChildrenWithText(String var1);
}

