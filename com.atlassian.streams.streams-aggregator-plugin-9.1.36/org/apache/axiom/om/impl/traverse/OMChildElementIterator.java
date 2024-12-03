/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.traverse;

import java.util.Iterator;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;

public class OMChildElementIterator
implements Iterator {
    protected OMNode currentChild;
    protected OMNode lastChild;
    protected boolean nextCalled = false;
    protected boolean removeCalled = false;

    public OMChildElementIterator(OMElement currentChild) {
        this.currentChild = currentChild;
    }

    public void remove() {
        if (!this.nextCalled) {
            throw new IllegalStateException("next method has not yet being called");
        }
        if (this.removeCalled) {
            throw new IllegalStateException("remove has already being called");
        }
        this.removeCalled = true;
        if (this.lastChild == null) {
            throw new OMException("cannot remove a child at this stage!");
        }
        this.lastChild.detach();
    }

    public boolean hasNext() {
        return this.currentChild != null;
    }

    public Object next() {
        this.nextCalled = true;
        this.removeCalled = false;
        if (this.hasNext()) {
            this.lastChild = this.currentChild;
            do {
                this.currentChild = this.currentChild.getNextOMSibling();
            } while (this.currentChild != null && this.currentChild.getType() != 1);
            return this.lastChild;
        }
        return null;
    }
}

