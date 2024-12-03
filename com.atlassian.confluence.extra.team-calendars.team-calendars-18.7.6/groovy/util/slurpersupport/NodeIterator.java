/*
 * Decompiled with CFR 0.152.
 */
package groovy.util.slurpersupport;

import java.util.Iterator;

public abstract class NodeIterator
implements Iterator {
    private static final Object DELAYED_INIT = new Object();
    private final Iterator iter;
    private Object nextNode;

    public NodeIterator(Iterator iter) {
        this.iter = iter;
        this.nextNode = DELAYED_INIT;
    }

    private void initNextNode() {
        if (this.nextNode == DELAYED_INIT) {
            this.nextNode = this.getNextNode(this.iter);
        }
    }

    @Override
    public boolean hasNext() {
        this.initNextNode();
        return this.nextNode != null;
    }

    public Object next() {
        this.initNextNode();
        try {
            Object object = this.nextNode;
            return object;
        }
        finally {
            this.nextNode = this.getNextNode(this.iter);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    protected abstract Object getNextNode(Iterator var1);
}

