/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.domassign;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

public class GenericTreeWalker
implements TreeWalker {
    int whatToShow;
    Node currentNode;
    Node root;

    public GenericTreeWalker(Node root, int whatToShow) {
        this.root = root;
        this.currentNode = root;
        this.whatToShow = whatToShow;
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
        return null;
    }

    @Override
    public boolean getExpandEntityReferences() {
        return true;
    }

    @Override
    public Node getCurrentNode() {
        return this.currentNode;
    }

    @Override
    public void setCurrentNode(Node node) {
        this.currentNode = node;
    }

    @Override
    public Node parentNode() {
        if (this.currentNode == null) {
            return null;
        }
        Node node = this.getParentNode(this.currentNode);
        if (node != null) {
            this.currentNode = node;
        }
        return node;
    }

    @Override
    public Node firstChild() {
        if (this.currentNode == null) {
            return null;
        }
        Node node = this.getFirstChild(this.currentNode);
        if (node != null) {
            this.currentNode = node;
        }
        return node;
    }

    @Override
    public Node lastChild() {
        if (this.currentNode == null) {
            return null;
        }
        Node node = this.getLastChild(this.currentNode);
        if (node != null) {
            this.currentNode = node;
        }
        return node;
    }

    @Override
    public Node previousSibling() {
        if (this.currentNode == null) {
            return null;
        }
        Node node = this.getPreviousSibling(this.currentNode);
        if (node != null) {
            this.currentNode = node;
        }
        return node;
    }

    @Override
    public Node nextSibling() {
        if (this.currentNode == null) {
            return null;
        }
        Node node = this.getNextSibling(this.currentNode);
        if (node != null) {
            this.currentNode = node;
        }
        return node;
    }

    @Override
    public Node previousNode() {
        Node lastChild;
        if (this.currentNode == null) {
            return null;
        }
        Node result = this.getPreviousSibling(this.currentNode);
        if (result == null) {
            result = this.getParentNode(this.currentNode);
            if (result != null) {
                this.currentNode = result;
                return result;
            }
            return null;
        }
        Node prev = lastChild = this.getLastChild(result);
        while (lastChild != null) {
            prev = lastChild;
            lastChild = this.getLastChild(prev);
        }
        lastChild = prev;
        if (lastChild != null) {
            this.currentNode = lastChild;
            return lastChild;
        }
        this.currentNode = result;
        return result;
    }

    @Override
    public Node nextNode() {
        if (this.currentNode == null) {
            return null;
        }
        Node result = this.getFirstChild(this.currentNode);
        if (result != null) {
            this.currentNode = result;
            return result;
        }
        result = this.getNextSibling(this.currentNode);
        if (result != null) {
            this.currentNode = result;
            return result;
        }
        Node parent = this.getParentNode(this.currentNode);
        while (parent != null) {
            result = this.getNextSibling(parent);
            if (result != null) {
                this.currentNode = result;
                return result;
            }
            parent = this.getParentNode(parent);
        }
        return null;
    }

    private Node getParentNode(Node node) {
        if (node == null || node == this.root) {
            return null;
        }
        Node newNode = node.getParentNode();
        if (newNode == null) {
            return null;
        }
        short accept = this.acceptNode(newNode);
        if (accept == 1) {
            return newNode;
        }
        return this.getParentNode(newNode);
    }

    private Node getNextSibling(Node node) {
        if (node == null || node == this.root) {
            return null;
        }
        Node newNode = node.getNextSibling();
        if (newNode == null) {
            newNode = node.getParentNode();
            if (newNode == null || node == this.root) {
                return null;
            }
            short parentAccept = this.acceptNode(newNode);
            if (parentAccept == 3) {
                return this.getNextSibling(newNode);
            }
            return null;
        }
        short accept = this.acceptNode(newNode);
        if (accept == 1) {
            return newNode;
        }
        if (accept == 3) {
            Node fChild = this.getFirstChild(newNode);
            if (fChild == null) {
                return this.getNextSibling(newNode);
            }
            return fChild;
        }
        return this.getNextSibling(newNode);
    }

    private Node getPreviousSibling(Node node) {
        if (node == null || node == this.root) {
            return null;
        }
        Node newNode = node.getPreviousSibling();
        if (newNode == null) {
            newNode = node.getParentNode();
            if (newNode == null || node == this.root) {
                return null;
            }
            short parentAccept = this.acceptNode(newNode);
            if (parentAccept == 3) {
                return this.getPreviousSibling(newNode);
            }
            return null;
        }
        short accept = this.acceptNode(newNode);
        if (accept == 1) {
            return newNode;
        }
        if (accept == 3) {
            Node fChild = this.getLastChild(newNode);
            if (fChild == null) {
                return this.getPreviousSibling(newNode);
            }
            return fChild;
        }
        return this.getPreviousSibling(newNode);
    }

    private Node getFirstChild(Node node) {
        if (node == null) {
            return null;
        }
        Node newNode = node.getFirstChild();
        if (newNode == null) {
            return null;
        }
        short accept = this.acceptNode(newNode);
        if (accept == 1) {
            return newNode;
        }
        if (accept == 3 && newNode.hasChildNodes()) {
            return this.getFirstChild(newNode);
        }
        return this.getNextSibling(newNode);
    }

    private Node getLastChild(Node node) {
        if (node == null) {
            return null;
        }
        Node newNode = node.getLastChild();
        if (newNode == null) {
            return null;
        }
        short accept = this.acceptNode(newNode);
        if (accept == 1) {
            return newNode;
        }
        if (accept == 3 && newNode.hasChildNodes()) {
            return this.getLastChild(newNode);
        }
        return this.getPreviousSibling(newNode);
    }

    private short acceptNode(Node node) {
        if ((this.whatToShow & 1 << node.getNodeType() - 1) != 0) {
            return 1;
        }
        return 3;
    }

    public static abstract class Traversal<T> {
        protected Object source;
        protected TreeWalker walker;

        public Traversal(TreeWalker walker, Object source) {
            this.source = source;
            this.walker = walker;
        }

        public Traversal(Document doc, Object source, int whatToShow) {
            this.walker = new GenericTreeWalker(doc.getDocumentElement(), whatToShow);
            this.source = source;
        }

        public void listTraversal(T result) {
            Node checkpoint = null;
            Node current = this.walker.nextNode();
            while (current != null) {
                checkpoint = this.walker.getCurrentNode();
                this.processNode(result, current, this.source);
                this.walker.setCurrentNode(checkpoint);
                current = this.walker.nextNode();
            }
        }

        public void levelTraversal(T result) {
            Node checkpoint = null;
            Node current = checkpoint = this.walker.getCurrentNode();
            this.processNode(result, current, this.source);
            this.walker.setCurrentNode(checkpoint);
            Node n = this.walker.firstChild();
            while (n != null) {
                this.levelTraversal(result);
                n = this.walker.nextSibling();
            }
            this.walker.setCurrentNode(checkpoint);
        }

        protected abstract void processNode(T var1, Node var2, Object var3);

        public Traversal<T> reset(TreeWalker walker, Object source) {
            this.walker = walker;
            this.source = source;
            return this;
        }
    }
}

