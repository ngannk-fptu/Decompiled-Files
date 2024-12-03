/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.traversal;

import org.apache.batik.dom.AbstractNode;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

public class DOMTreeWalker
implements TreeWalker {
    protected Node root;
    protected int whatToShow;
    protected NodeFilter filter;
    protected boolean expandEntityReferences;
    protected Node currentNode;

    public DOMTreeWalker(Node n, int what, NodeFilter nf, boolean exp) {
        this.root = n;
        this.whatToShow = what;
        this.filter = nf;
        this.expandEntityReferences = exp;
        this.currentNode = this.root;
    }

    @Override
    public Node getRoot() {
        return this.root;
    }

    @Override
    public int getWhatToShow() {
        return this.whatToShow;
    }

    @Override
    public NodeFilter getFilter() {
        return this.filter;
    }

    @Override
    public boolean getExpandEntityReferences() {
        return this.expandEntityReferences;
    }

    @Override
    public Node getCurrentNode() {
        return this.currentNode;
    }

    @Override
    public void setCurrentNode(Node n) {
        if (n == null) {
            throw ((AbstractNode)this.root).createDOMException((short)9, "null.current.node", null);
        }
        this.currentNode = n;
    }

    @Override
    public Node parentNode() {
        Node result = this.parentNode(this.currentNode);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }

    @Override
    public Node firstChild() {
        Node result = this.firstChild(this.currentNode);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }

    @Override
    public Node lastChild() {
        Node result = this.lastChild(this.currentNode);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }

    @Override
    public Node previousSibling() {
        Node result = this.previousSibling(this.currentNode, this.root);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }

    @Override
    public Node nextSibling() {
        Node result = this.nextSibling(this.currentNode, this.root);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }

    @Override
    public Node previousNode() {
        Node n;
        Node result = this.previousSibling(this.currentNode, this.root);
        if (result == null) {
            result = this.parentNode(this.currentNode);
            if (result != null) {
                this.currentNode = result;
            }
            return result;
        }
        Node last = n = this.lastChild(result);
        while (n != null) {
            last = n;
            n = this.lastChild(last);
        }
        this.currentNode = last != null ? last : result;
        return this.currentNode;
    }

    @Override
    public Node nextNode() {
        Node result = this.firstChild(this.currentNode);
        if (result != null) {
            this.currentNode = result;
            return this.currentNode;
        }
        result = this.nextSibling(this.currentNode, this.root);
        if (result != null) {
            this.currentNode = result;
            return this.currentNode;
        }
        Node parent = this.currentNode;
        do {
            if ((parent = this.parentNode(parent)) != null) continue;
            return null;
        } while ((result = this.nextSibling(parent, this.root)) == null);
        this.currentNode = result;
        return this.currentNode;
    }

    protected Node parentNode(Node n) {
        if (n == this.root) {
            return null;
        }
        Node result = n;
        do {
            if ((result = result.getParentNode()) != null) continue;
            return null;
        } while ((this.whatToShow & 1 << result.getNodeType() - 1) == 0 || this.filter != null && this.filter.acceptNode(result) != 1);
        return result;
    }

    protected Node firstChild(Node n) {
        if (n.getNodeType() == 5 && !this.expandEntityReferences) {
            return null;
        }
        Node result = n.getFirstChild();
        if (result == null) {
            return null;
        }
        switch (this.acceptNode(result)) {
            case 1: {
                return result;
            }
            case 3: {
                Node t = this.firstChild(result);
                if (t == null) break;
                return t;
            }
        }
        return this.nextSibling(result, n);
    }

    protected Node lastChild(Node n) {
        if (n.getNodeType() == 5 && !this.expandEntityReferences) {
            return null;
        }
        Node result = n.getLastChild();
        if (result == null) {
            return null;
        }
        switch (this.acceptNode(result)) {
            case 1: {
                return result;
            }
            case 3: {
                Node t = this.lastChild(result);
                if (t == null) break;
                return t;
            }
        }
        return this.previousSibling(result, n);
    }

    protected Node previousSibling(Node n, Node root) {
        while (n != root) {
            Node result = n.getPreviousSibling();
            if (result == null) {
                result = n.getParentNode();
                if (result == null || result == root) {
                    return null;
                }
                if (this.acceptNode(result) == 3) {
                    n = result;
                    continue;
                }
                return null;
            }
            switch (this.acceptNode(result)) {
                case 1: {
                    return result;
                }
                case 3: {
                    Node t = this.lastChild(result);
                    if (t == null) break;
                    return t;
                }
            }
            n = result;
        }
        return null;
    }

    protected Node nextSibling(Node n, Node root) {
        while (n != root) {
            Node result = n.getNextSibling();
            if (result == null) {
                result = n.getParentNode();
                if (result == null || result == root) {
                    return null;
                }
                if (this.acceptNode(result) == 3) {
                    n = result;
                    continue;
                }
                return null;
            }
            switch (this.acceptNode(result)) {
                case 1: {
                    return result;
                }
                case 3: {
                    Node t = this.firstChild(result);
                    if (t == null) break;
                    return t;
                }
            }
            n = result;
        }
        return null;
    }

    protected short acceptNode(Node n) {
        if ((this.whatToShow & 1 << n.getNodeType() - 1) != 0) {
            if (this.filter == null) {
                return 1;
            }
            return this.filter.acceptNode(n);
        }
        return 3;
    }
}

