/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt;

import java.util.List;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;

public class GVTTreeWalker {
    protected GraphicsNode gvtRoot;
    protected GraphicsNode treeRoot;
    protected GraphicsNode currentNode;

    public GVTTreeWalker(GraphicsNode treeRoot) {
        this.gvtRoot = treeRoot.getRoot();
        this.treeRoot = treeRoot;
        this.currentNode = treeRoot;
    }

    public GraphicsNode getRoot() {
        return this.treeRoot;
    }

    public GraphicsNode getGVTRoot() {
        return this.gvtRoot;
    }

    public void setCurrentGraphicsNode(GraphicsNode node) {
        if (node.getRoot() != this.gvtRoot) {
            throw new IllegalArgumentException("The node " + node + " is not part of the document " + this.gvtRoot);
        }
        this.currentNode = node;
    }

    public GraphicsNode getCurrentGraphicsNode() {
        return this.currentNode;
    }

    public GraphicsNode previousGraphicsNode() {
        GraphicsNode result = this.getPreviousGraphicsNode(this.currentNode);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }

    public GraphicsNode nextGraphicsNode() {
        GraphicsNode result = this.getNextGraphicsNode(this.currentNode);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }

    public GraphicsNode parentGraphicsNode() {
        if (this.currentNode == this.treeRoot) {
            return null;
        }
        CompositeGraphicsNode result = this.currentNode.getParent();
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }

    public GraphicsNode getNextSibling() {
        GraphicsNode result = GVTTreeWalker.getNextSibling(this.currentNode);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }

    public GraphicsNode getPreviousSibling() {
        GraphicsNode result = GVTTreeWalker.getPreviousSibling(this.currentNode);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }

    public GraphicsNode firstChild() {
        GraphicsNode result = GVTTreeWalker.getFirstChild(this.currentNode);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }

    public GraphicsNode lastChild() {
        GraphicsNode result = GVTTreeWalker.getLastChild(this.currentNode);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }

    protected GraphicsNode getNextGraphicsNode(GraphicsNode node) {
        if (node == null) {
            return null;
        }
        GraphicsNode n = GVTTreeWalker.getFirstChild(node);
        if (n != null) {
            return n;
        }
        n = GVTTreeWalker.getNextSibling(node);
        if (n != null) {
            return n;
        }
        n = node;
        while ((n = n.getParent()) != null && n != this.treeRoot) {
            GraphicsNode t = GVTTreeWalker.getNextSibling(n);
            if (t == null) continue;
            return t;
        }
        return null;
    }

    protected GraphicsNode getPreviousGraphicsNode(GraphicsNode node) {
        GraphicsNode t;
        if (node == null) {
            return null;
        }
        if (node == this.treeRoot) {
            return null;
        }
        GraphicsNode n = GVTTreeWalker.getPreviousSibling(node);
        if (n == null) {
            return node.getParent();
        }
        while ((t = GVTTreeWalker.getLastChild(n)) != null) {
            n = t;
        }
        return n;
    }

    protected static GraphicsNode getLastChild(GraphicsNode node) {
        if (!(node instanceof CompositeGraphicsNode)) {
            return null;
        }
        CompositeGraphicsNode parent = (CompositeGraphicsNode)node;
        List children = parent.getChildren();
        if (children == null) {
            return null;
        }
        if (children.size() >= 1) {
            return (GraphicsNode)children.get(children.size() - 1);
        }
        return null;
    }

    protected static GraphicsNode getPreviousSibling(GraphicsNode node) {
        CompositeGraphicsNode parent = node.getParent();
        if (parent == null) {
            return null;
        }
        List children = parent.getChildren();
        if (children == null) {
            return null;
        }
        int index = children.indexOf(node);
        if (index - 1 >= 0) {
            return (GraphicsNode)children.get(index - 1);
        }
        return null;
    }

    protected static GraphicsNode getFirstChild(GraphicsNode node) {
        if (!(node instanceof CompositeGraphicsNode)) {
            return null;
        }
        CompositeGraphicsNode parent = (CompositeGraphicsNode)node;
        List children = parent.getChildren();
        if (children == null) {
            return null;
        }
        if (children.size() >= 1) {
            return (GraphicsNode)children.get(0);
        }
        return null;
    }

    protected static GraphicsNode getNextSibling(GraphicsNode node) {
        CompositeGraphicsNode parent = node.getParent();
        if (parent == null) {
            return null;
        }
        List children = parent.getChildren();
        if (children == null) {
            return null;
        }
        int index = children.indexOf(node);
        if (index + 1 < children.size()) {
            return (GraphicsNode)children.get(index + 1);
        }
        return null;
    }
}

