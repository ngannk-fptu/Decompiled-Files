/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.jackrabbit.commons.predicate.Predicate;

public class FilterIterator<T>
implements Iterator<T> {
    private final Iterator<T> iterator;
    private final Predicate predicate;
    private T next = null;

    public FilterIterator(Iterator<T> iterator, Predicate predicate) {
        this.iterator = iterator;
        this.predicate = predicate;
    }

    @Override
    public boolean hasNext() {
        while (this.next == null && this.iterator.hasNext()) {
            T e = this.iterator.next();
            if (!this.predicate.evaluate(e)) continue;
            this.next = e;
        }
        return this.next != null;
    }

    @Override
    public T next() {
        if (this.hasNext()) {
            T e = this.next;
            this.next = null;
            return e;
        }
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

