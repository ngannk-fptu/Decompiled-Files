/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.events.NodeEventTarget;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public interface ExtendedNode
extends Node,
NodeEventTarget {
    public void setNodeName(String var1);

    public boolean isReadonly();

    public void setReadonly(boolean var1);

    public void setOwnerDocument(Document var1);

    public void setParentNode(Node var1);

    public void setPreviousSibling(Node var1);

    public void setNextSibling(Node var1);

    public void setSpecified(boolean var1);
}

