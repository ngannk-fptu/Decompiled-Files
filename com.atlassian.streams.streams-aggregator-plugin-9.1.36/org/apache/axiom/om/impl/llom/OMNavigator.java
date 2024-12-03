/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.llom.IContainer;

public class OMNavigator {
    protected OMSerializable node;
    private boolean visited;
    private OMSerializable next;
    private OMSerializable root;
    private boolean backtracked;
    private boolean end = false;
    private boolean start = true;
    private boolean isDataSourceALeaf = false;

    public OMNavigator() {
    }

    public OMNavigator(OMSerializable node) {
        this.init(node);
    }

    public void init(OMSerializable node) {
        this.next = node;
        this.root = node;
        this.backtracked = false;
    }

    public void setDataSourceIsLeaf(boolean value) {
        this.isDataSourceALeaf = value;
    }

    public OMSerializable getNext() {
        if (this.next == null) {
            return null;
        }
        this.node = this.next;
        this.visited = this.backtracked;
        this.backtracked = false;
        this.updateNextNode();
        if (this.root.equals(this.node)) {
            if (!this.start) {
                this.end = true;
            } else {
                this.start = false;
            }
        }
        return this.node;
    }

    public OMNode next() {
        return (OMNode)this.getNext();
    }

    private void updateNextNode() {
        if (!this.isLeaf(this.next) && !this.visited) {
            OMNode firstChild = this._getFirstChild((OMContainer)this.next);
            if (firstChild != null) {
                this.next = firstChild;
            } else if (this.next.isComplete()) {
                this.backtracked = true;
            } else {
                this.next = null;
            }
        } else if (this.next instanceof OMDocument) {
            this.next = null;
        } else {
            OMNode nextNode = (OMNode)this.next;
            OMContainer parent = nextNode.getParent();
            OMNode nextSibling = this.getNextSibling(nextNode);
            if (nextSibling != null) {
                this.next = nextSibling;
            } else if (parent != null && parent.isComplete()) {
                this.next = parent;
                this.backtracked = true;
            } else {
                this.next = null;
            }
        }
    }

    private boolean isLeaf(OMSerializable n) {
        if (n instanceof OMContainer) {
            return this.isDataSourceALeaf && this.isOMSourcedElement(n) && n != this.root;
        }
        return true;
    }

    private boolean isOMSourcedElement(OMSerializable node) {
        if (node instanceof OMSourcedElement) {
            try {
                return ((OMSourcedElement)node).getDataSource() != null;
            }
            catch (UnsupportedOperationException e) {
                return false;
            }
        }
        return false;
    }

    private OMNode _getFirstChild(OMContainer node) {
        if (this.isOMSourcedElement(node)) {
            OMNode first;
            for (OMNode sibling = first = node.getFirstOMChild(); sibling != null; sibling = sibling.getNextOMSibling()) {
            }
            return first;
        }
        return ((IContainer)node).getFirstOMChildIfAvailable();
    }

    private OMNode getNextSibling(OMNode node) {
        if (this.isOMSourcedElement(node)) {
            return node.getNextOMSibling();
        }
        return ((OMNodeEx)node).getNextOMSiblingIfAvailable();
    }

    public boolean visited() {
        return this.visited;
    }

    public void step() {
        if (!this.end) {
            this.next = this.node;
            this.updateNextNode();
        }
    }

    public boolean isNavigable() {
        if (this.end) {
            return false;
        }
        return this.next != null;
    }

    public boolean isCompleted() {
        return this.end;
    }
}

