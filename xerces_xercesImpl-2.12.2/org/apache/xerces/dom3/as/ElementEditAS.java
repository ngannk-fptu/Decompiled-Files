/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom3.as;

import org.apache.xerces.dom3.as.NodeEditAS;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public interface ElementEditAS
extends NodeEditAS {
    public NodeList getDefinedElementTypes();

    public short contentType();

    public boolean canSetAttribute(String var1, String var2);

    public boolean canSetAttributeNode(Attr var1);

    public boolean canSetAttributeNS(String var1, String var2, String var3);

    public boolean canRemoveAttribute(String var1);

    public boolean canRemoveAttributeNS(String var1, String var2);

    public boolean canRemoveAttributeNode(Node var1);

    public NodeList getChildElements();

    public NodeList getParentElements();

    public NodeList getAttributeList();

    public boolean isElementDefined(String var1);

    public boolean isElementDefinedNS(String var1, String var2, String var3);
}

