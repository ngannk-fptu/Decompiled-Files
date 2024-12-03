/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.traverse;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.axiom.om.OMNode;

public abstract class OMFilterIterator
implements Iterator {
    private final Iterator parent;
    private OMNode nextNode;
    private boolean noMoreNodes;

    public OMFilterIterator(Iterator parent) {
        this.parent = parent;
    }

    protected abstract boolean matches(OMNode var1);

    public boolean hasNext() {
        if (this.noMoreNodes) {
            return false;
        }
        if (this.nextNode != null) {
            return true;
        }
        while (this.parent.hasNext()) {
            OMNode node = (OMNode)this.parent.next();
            if (!this.matches(node)) continue;
            this.nextNode = node;
            return true;
        }
        this.noMoreNodes = true;
        return false;
    }

    public Object next() {
        if (this.hasNext()) {
            OMNode result = this.nextNode;
            this.nextNode = null;
            return result;
        }
        throw new NoSuchElementException();
    }

    public void remove() {
        this.parent.remove();
    }
}

