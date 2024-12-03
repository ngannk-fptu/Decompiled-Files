/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractLazyIterator<T>
implements Iterator<T> {
    private boolean fetchNext = true;
    private T next;

    protected AbstractLazyIterator() {
    }

    @Override
    public boolean hasNext() {
        if (this.fetchNext) {
            this.next = this.getNext();
            this.fetchNext = false;
        }
        return this.next != null;
    }

    @Override
    public T next() {
        if (this.fetchNext) {
            this.next = this.getNext();
        } else {
            this.fetchNext = true;
        }
        if (this.next == null) {
            throw new NoSuchElementException();
        }
        return this.next;
    }

    protected abstract T getNext();

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

