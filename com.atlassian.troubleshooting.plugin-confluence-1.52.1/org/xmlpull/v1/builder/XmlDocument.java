/*
 * Decompiled with CFR 0.152.
 */
package org.xmlpull.v1.builder;

import org.xmlpull.v1.builder.Iterable;
import org.xmlpull.v1.builder.XmlComment;
import org.xmlpull.v1.builder.XmlContainer;
import org.xmlpull.v1.builder.XmlDoctype;
import org.xmlpull.v1.builder.XmlElement;
import org.xmlpull.v1.builder.XmlNamespace;
import org.xmlpull.v1.builder.XmlNotation;
import org.xmlpull.v1.builder.XmlProcessingInstruction;

public interface XmlDocument
extends XmlContainer,
Cloneable {
    public Object clone() throws CloneNotSupportedException;

    public Iterable children();

    public XmlElement getDocumentElement();

    public XmlElement requiredElement(XmlNamespace var1, String var2);

    public XmlElement element(XmlNamespace var1, String var2);

    public XmlElement element(XmlNamespace var1, String var2, boolean var3);

    public Iterable notations();

    public Iterable unparsedEntities();

    public String getBaseUri();

    public String getCharacterEncodingScheme();

    public void setCharacterEncodingScheme(String var1);

    public Boolean isStandalone();

    public String getVersion();

    public boolean isAllDeclarationsProcessed();

    public void setDocumentElement(XmlElement var1);

    public void addChild(Object var1);

    public void insertChild(int var1, Object var2);

    public void removeAllChildren();

    public XmlComment newComment(String var1);

    public XmlComment addComment(String var1);

    public XmlDoctype newDoctype(String var1, String var2);

    public XmlDoctype addDoctype(String var1, String var2);

    public XmlElement addDocumentElement(String var1);

    public XmlElement addDocumentElement(XmlNamespace var1, String var2);

    public XmlProcessingInstruction newProcessingInstruction(String var1, String var2);

    public XmlProcessingInstruction addProcessingInstruction(String var1, String var2);

    public void removeAllUnparsedEntities();

    public XmlNotation addNotation(String var1, String var2, String var3, String var4);

    public void removeAllNotations();
}

