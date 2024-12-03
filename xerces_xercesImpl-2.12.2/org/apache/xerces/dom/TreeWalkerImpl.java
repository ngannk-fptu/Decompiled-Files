/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.NodeImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

public class TreeWalkerImpl
implements TreeWalker {
    private boolean fEntityReferenceExpansion = false;
    int fWhatToShow = -1;
    NodeFilter fNodeFilter;
    Node fCurrentNode;
    Node fRoot;
    private boolean fUseIsSameNode;

    public TreeWalkerImpl(Node node, int n, NodeFilter nodeFilter, boolean bl) {
        this.fCurrentNode = node;
        this.fRoot = node;
        this.fUseIsSameNode = this.useIsSameNode(node);
        this.fWhatToShow = n;
        this.fNodeFilter = nodeFilter;
        this.fEntityReferenceExpansion = bl;
    }

    @Override
    public Node getRoot() {
        return this.fRoot;
    }

    @Override
    public int getWhatToShow() {
        return this.fWhatToShow;
    }

    public void setWhatShow(int n) {
        this.fWhatToShow = n;
    }

    @Override
    public NodeFilter getFilter() {
        return this.fNodeFilter;
    }

    @Override
    public boolean getExpandEntityReferences() {
        return this.fEntityReferenceExpansion;
    }

    @Override
    public Node getCurrentNode() {
        return this.fCurrentNode;
    }

    @Override
    public void setCurrentNode(Node node) {
        if (node == null) {
            String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
            throw new DOMException(9, string);
        }
        this.fCurrentNode = node;
    }

    @Override
    public Node parentNode() {
        if (this.fCurrentNode == null) {
            return null;
        }
        Node node = this.getParentNode(this.fCurrentNode);
        if (node != null) {
            this.fCurrentNode = node;
        }
        return node;
    }

    @Override
    public Node firstChild() {
        if (this.fCurrentNode == null) {
            return null;
        }
        Node node = this.getFirstChild(this.fCurrentNode);
        if (node != null) {
            this.fCurrentNode = node;
        }
        return node;
    }

    @Override
    public Node lastChild() {
        if (this.fCurrentNode == null) {
            return null;
        }
        Node node = this.getLastChild(this.fCurrentNode);
        if (node != null) {
            this.fCurrentNode = node;
        }
        return node;
    }

    @Override
    public Node previousSibling() {
        if (this.fCurrentNode == null) {
            return null;
        }
        Node node = this.getPreviousSibling(this.fCurrentNode);
        if (node != null) {
            this.fCurrentNode = node;
        }
        return node;
    }

    @Override
    public Node nextSibling() {
        if (this.fCurrentNode == null) {
            return null;
        }
        Node node = this.getNextSibling(this.fCurrentNode);
        if (node != null) {
            this.fCurrentNode = node;
        }
        return node;
    }

    @Override
    public Node previousNode() {
        Node node;
        if (this.fCurrentNode == null) {
            return null;
        }
        Node node2 = this.getPreviousSibling(this.fCurrentNode);
        if (node2 == null) {
            node2 = this.getParentNode(this.fCurrentNode);
            if (node2 != null) {
                this.fCurrentNode = node2;
                return this.fCurrentNode;
            }
            return null;
        }
        Node node3 = node = this.getLastChild(node2);
        while (node != null) {
            node3 = node;
            node = this.getLastChild(node3);
        }
        node = node3;
        if (node != null) {
            this.fCurrentNode = node;
            return this.fCurrentNode;
        }
        if (node2 != null) {
            this.fCurrentNode = node2;
            return this.fCurrentNode;
        }
        return null;
    }

    @Override
    public Node nextNode() {
        if (this.fCurrentNode == null) {
            return null;
        }
        Node node = this.getFirstChild(this.fCurrentNode);
        if (node != null) {
            this.fCurrentNode = node;
            return node;
        }
        node = this.getNextSibling(this.fCurrentNode);
        if (node != null) {
            this.fCurrentNode = node;
            return node;
        }
        Node node2 = this.getParentNode(this.fCurrentNode);
        while (node2 != null) {
            node = this.getNextSibling(node2);
            if (node != null) {
                this.fCurrentNode = node;
                return node;
            }
            node2 = this.getParentNode(node2);
        }
        return null;
    }

    Node getParentNode(Node node) {
        if (node == null || this.isSameNode(node, this.fRoot)) {
            return null;
        }
        Node node2 = node.getParentNode();
        if (node2 == null) {
            return null;
        }
        short s = this.acceptNode(node2);
        if (s == 1) {
            return node2;
        }
        return this.getParentNode(node2);
    }

    Node getNextSibling(Node node) {
        return this.getNextSibling(node, this.fRoot);
    }

    Node getNextSibling(Node node, Node node2) {
        if (node == null || this.isSameNode(node, node2)) {
            return null;
        }
        Node node3 = node.getNextSibling();
        if (node3 == null) {
            node3 = node.getParentNode();
            if (node3 == null || this.isSameNode(node3, node2)) {
                return null;
            }
            short s = this.acceptNode(node3);
            if (s == 3) {
                return this.getNextSibling(node3, node2);
            }
            return null;
        }
        short s = this.acceptNode(node3);
        if (s == 1) {
            return node3;
        }
        if (s == 3) {
            Node node4 = this.getFirstChild(node3);
            if (node4 == null) {
                return this.getNextSibling(node3, node2);
            }
            return node4;
        }
        return this.getNextSibling(node3, node2);
    }

    Node getPreviousSibling(Node node) {
        return this.getPreviousSibling(node, this.fRoot);
    }

    Node getPreviousSibling(Node node, Node node2) {
        if (node == null || this.isSameNode(node, node2)) {
            return null;
        }
        Node node3 = node.getPreviousSibling();
        if (node3 == null) {
            node3 = node.getParentNode();
            if (node3 == null || this.isSameNode(node3, node2)) {
                return null;
            }
            short s = this.acceptNode(node3);
            if (s == 3) {
                return this.getPreviousSibling(node3, node2);
            }
            return null;
        }
        short s = this.acceptNode(node3);
        if (s == 1) {
            return node3;
        }
        if (s == 3) {
            Node node4 = this.getLastChild(node3);
            if (node4 == null) {
                return this.getPreviousSibling(node3, node2);
            }
            return node4;
        }
        return this.getPreviousSibling(node3, node2);
    }

    Node getFirstChild(Node node) {
        if (node == null) {
            return null;
        }
        if (!this.fEntityReferenceExpansion && node.getNodeType() == 5) {
            return null;
        }
        Node node2 = node.getFirstChild();
        if (node2 == null) {
            return null;
        }
        short s = this.acceptNode(node2);
        if (s == 1) {
            return node2;
        }
        if (s == 3 && node2.hasChildNodes()) {
            Node node3 = this.getFirstChild(node2);
            if (node3 == null) {
                return this.getNextSibling(node2, node);
            }
            return node3;
        }
        return this.getNextSibling(node2, node);
    }

    Node getLastChild(Node node) {
        if (node == null) {
            return null;
        }
        if (!this.fEntityReferenceExpansion && node.getNodeType() == 5) {
            return null;
        }
        Node node2 = node.getLastChild();
        if (node2 == null) {
            return null;
        }
        short s = this.acceptNode(node2);
        if (s == 1) {
            return node2;
        }
        if (s == 3 && node2.hasChildNodes()) {
            Node node3 = this.getLastChild(node2);
            if (node3 == null) {
                return this.getPreviousSibling(node2, node);
            }
            return node3;
        }
        return this.getPreviousSibling(node2, node);
    }

    short acceptNode(Node node) {
        if (this.fNodeFilter == null) {
            if ((this.fWhatToShow & 1 << node.getNodeType() - 1) != 0) {
                return 1;
            }
            return 3;
        }
        if ((this.fWhatToShow & 1 << node.getNodeType() - 1) != 0) {
            return this.fNodeFilter.acceptNode(node);
        }
        return 3;
    }

    private boolean useIsSameNode(Node node) {
        if (node instanceof NodeImpl) {
            return false;
        }
        Document document = node.getNodeType() == 9 ? (Document)node : node.getOwnerDocument();
        return document != null && document.getImplementation().hasFeature("Core", "3.0");
    }

    private boolean isSameNode(Node node, Node node2) {
        return this.fUseIsSameNode ? node.isSameNode(node2) : node == node2;
    }
}

