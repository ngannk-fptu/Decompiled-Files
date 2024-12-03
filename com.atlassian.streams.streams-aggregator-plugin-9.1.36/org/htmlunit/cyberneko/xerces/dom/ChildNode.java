/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import org.htmlunit.cyberneko.xerces.dom.CoreDocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.NodeImpl;
import org.w3c.dom.Node;

public abstract class ChildNode
extends NodeImpl {
    protected ChildNode previousSibling;
    protected ChildNode nextSibling;

    protected ChildNode(CoreDocumentImpl ownerDocument) {
        super(ownerDocument);
    }

    @Override
    public Node cloneNode(boolean deep) {
        ChildNode newnode = (ChildNode)super.cloneNode(deep);
        newnode.previousSibling = null;
        newnode.nextSibling = null;
        newnode.isFirstChild(false);
        return newnode;
    }

    @Override
    public Node getParentNode() {
        return this.isOwned() ? this.ownerNode : null;
    }

    @Override
    final NodeImpl parentNode() {
        return this.isOwned() ? this.ownerNode : null;
    }

    @Override
    public Node getNextSibling() {
        return this.nextSibling;
    }

    @Override
    public Node getPreviousSibling() {
        return this.isFirstChild() ? null : this.previousSibling;
    }

    @Override
    final ChildNode previousSibling() {
        return this.isFirstChild() ? null : this.previousSibling;
    }
}

