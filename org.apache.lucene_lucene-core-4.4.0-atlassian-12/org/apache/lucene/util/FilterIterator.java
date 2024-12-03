/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class FilterIterator<T>
implements Iterator<T> {
    private final Iterator<T> iterator;
    private T next = null;
    private boolean nextIsSet = false;

    protected abstract boolean predicateFunction(T var1);

    public FilterIterator(Iterator<T> baseIterator) {
        this.iterator = baseIterator;
    }

    @Override
    public final boolean hasNext() {
        return this.nextIsSet || this.setNext();
    }

    @Override
    public final T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        assert (this.nextIsSet);
        try {
            T t = this.next;
            return t;
        }
        finally {
            this.nextIsSet = false;
            this.next = null;
        }
    }

    @Override
    public final void remove() {
        throw new UnsupportedOperationException();
    }

    private boolean setNext() {
        while (this.iterator.hasNext()) {
            T object = this.iterator.next();
            if (!this.predicateFunction(object)) continue;
            this.next = object;
            this.nextIsSet = true;
            return true;
        }
        return false;
    }
}

