/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.NodeImpl;
import org.w3c.dom.Node;

public abstract class ChildNode
extends NodeImpl {
    static final long serialVersionUID = -6112455738802414002L;
    protected ChildNode previousSibling;
    protected ChildNode nextSibling;

    protected ChildNode(CoreDocumentImpl coreDocumentImpl) {
        super(coreDocumentImpl);
    }

    public ChildNode() {
    }

    @Override
    public Node cloneNode(boolean bl) {
        ChildNode childNode = (ChildNode)super.cloneNode(bl);
        childNode.previousSibling = null;
        childNode.nextSibling = null;
        childNode.isFirstChild(false);
        return childNode;
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

