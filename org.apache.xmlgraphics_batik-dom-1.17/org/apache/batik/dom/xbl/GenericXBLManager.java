/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.xbl;

import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.xbl.XBLManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GenericXBLManager
implements XBLManager {
    protected boolean isProcessing;

    @Override
    public void startProcessing() {
        this.isProcessing = true;
    }

    @Override
    public void stopProcessing() {
        this.isProcessing = false;
    }

    @Override
    public boolean isProcessing() {
        return this.isProcessing;
    }

    @Override
    public Node getXblParentNode(Node n) {
        return n.getParentNode();
    }

    @Override
    public NodeList getXblChildNodes(Node n) {
        return n.getChildNodes();
    }

    @Override
    public NodeList getXblScopedChildNodes(Node n) {
        return n.getChildNodes();
    }

    @Override
    public Node getXblFirstChild(Node n) {
        return n.getFirstChild();
    }

    @Override
    public Node getXblLastChild(Node n) {
        return n.getLastChild();
    }

    @Override
    public Node getXblPreviousSibling(Node n) {
        return n.getPreviousSibling();
    }

    @Override
    public Node getXblNextSibling(Node n) {
        return n.getNextSibling();
    }

    @Override
    public Element getXblFirstElementChild(Node n) {
        Node m;
        for (m = n.getFirstChild(); m != null && m.getNodeType() != 1; m = m.getNextSibling()) {
        }
        return (Element)m;
    }

    @Override
    public Element getXblLastElementChild(Node n) {
        Node m;
        for (m = n.getLastChild(); m != null && m.getNodeType() != 1; m = m.getPreviousSibling()) {
        }
        return (Element)m;
    }

    @Override
    public Element getXblPreviousElementSibling(Node n) {
        Node m = n;
        while ((m = m.getPreviousSibling()) != null && m.getNodeType() != 1) {
        }
        return (Element)m;
    }

    @Override
    public Element getXblNextElementSibling(Node n) {
        Node m = n;
        while ((m = m.getNextSibling()) != null && m.getNodeType() != 1) {
        }
        return (Element)m;
    }

    @Override
    public Element getXblBoundElement(Node n) {
        return null;
    }

    @Override
    public Element getXblShadowTree(Node n) {
        return null;
    }

    @Override
    public NodeList getXblDefinitions(Node n) {
        return AbstractNode.EMPTY_NODE_LIST;
    }
}

