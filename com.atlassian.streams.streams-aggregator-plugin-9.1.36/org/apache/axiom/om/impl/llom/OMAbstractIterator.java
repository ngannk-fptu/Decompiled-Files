/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSerializable;

public abstract class OMAbstractIterator
implements Iterator {
    private OMSerializable currentNode;
    private OMContainer currentParent;
    private OMSerializable nextNode;
    private boolean noMoreNodes;
    private boolean nextCalled;

    public OMAbstractIterator(OMSerializable firstNode) {
        if (firstNode == null) {
            this.noMoreNodes = true;
        } else {
            this.nextNode = firstNode;
        }
    }

    protected abstract OMSerializable getNextNode(OMSerializable var1);

    public boolean hasNext() {
        if (this.noMoreNodes) {
            return false;
        }
        if (this.nextNode != null) {
            return true;
        }
        if (this.currentNode instanceof OMNode && ((OMNode)this.currentNode).getParent() != this.currentParent) {
            throw new ConcurrentModificationException("The current node has been removed using a method other than Iterator#remove()");
        }
        this.nextNode = this.getNextNode(this.currentNode);
        this.noMoreNodes = this.nextNode == null;
        return !this.noMoreNodes;
    }

    public Object next() {
        if (this.hasNext()) {
            this.currentNode = this.nextNode;
            this.currentParent = this.currentNode instanceof OMNode ? ((OMNode)this.currentNode).getParent() : null;
            this.nextNode = null;
            this.nextCalled = true;
            return this.currentNode;
        }
        throw new NoSuchElementException();
    }

    public void remove() {
        if (!this.nextCalled) {
            throw new IllegalStateException("next() has not yet been called");
        }
        this.hasNext();
        if (this.currentNode instanceof OMNode) {
            ((OMNode)this.currentNode).detach();
        }
        this.nextCalled = false;
    }
}

