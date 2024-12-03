/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class FilterIterator<E>
implements Iterator<E> {
    protected final Filter<E> filter;
    protected final Iterator<E> iterator;
    private E next = null;
    private E current = null;

    public FilterIterator(Iterator<E> iterator, Filter<E> filter) {
        if (iterator == null) {
            throw new IllegalArgumentException("iterator == null");
        }
        if (filter == null) {
            throw new IllegalArgumentException("filter == null");
        }
        this.iterator = iterator;
        this.filter = filter;
    }

    @Override
    public boolean hasNext() {
        while (this.next == null && this.iterator.hasNext()) {
            E e = this.iterator.next();
            if (!this.filter.accept(e)) continue;
            this.next = e;
            break;
        }
        return this.next != null;
    }

    @Override
    public E next() {
        if (this.hasNext()) {
            this.current = this.next;
            this.next = null;
            return this.current;
        }
        throw new NoSuchElementException("Iteration has no more elements.");
    }

    @Override
    public void remove() {
        if (this.current == null) {
            throw new IllegalStateException("Iteration has no current element.");
        }
        this.iterator.remove();
    }

    public static interface Filter<E> {
        public boolean accept(E var1);
    }
}

