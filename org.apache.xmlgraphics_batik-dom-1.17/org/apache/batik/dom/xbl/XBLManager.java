/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.xbl;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public interface XBLManager {
    public void startProcessing();

    public void stopProcessing();

    public boolean isProcessing();

    public Node getXblParentNode(Node var1);

    public NodeList getXblChildNodes(Node var1);

    public NodeList getXblScopedChildNodes(Node var1);

    public Node getXblFirstChild(Node var1);

    public Node getXblLastChild(Node var1);

    public Node getXblPreviousSibling(Node var1);

    public Node getXblNextSibling(Node var1);

    public Element getXblFirstElementChild(Node var1);

    public Element getXblLastElementChild(Node var1);

    public Element getXblPreviousElementSibling(Node var1);

    public Element getXblNextElementSibling(Node var1);

    public Element getXblBoundElement(Node var1);

    public Element getXblShadowTree(Node var1);

    public NodeList getXblDefinitions(Node var1);
}

