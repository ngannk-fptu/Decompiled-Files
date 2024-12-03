/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.xbl;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public interface NodeXBL {
    public Node getXblParentNode();

    public NodeList getXblChildNodes();

    public NodeList getXblScopedChildNodes();

    public Node getXblFirstChild();

    public Node getXblLastChild();

    public Node getXblPreviousSibling();

    public Node getXblNextSibling();

    public Element getXblFirstElementChild();

    public Element getXblLastElementChild();

    public Element getXblPreviousElementSibling();

    public Element getXblNextElementSibling();

    public Element getXblBoundElement();

    public Element getXblShadowTree();

    public NodeList getXblDefinitions();
}

