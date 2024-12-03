/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.iterator;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LazyIteratorChain<T>
implements Iterator<T> {
    private final Iterator<Iterator<T>> iterators;
    private Iterator<T> currentIterator;
    private Boolean hasNext;

    public static <T> Iterator<T> chain(Iterator<Iterator<T>> iterators) {
        return new LazyIteratorChain<T>(iterators);
    }

    public static <T> Iterator<T> chain(Iterator<T> ... iterators) {
        return new LazyIteratorChain<T>(iterators);
    }

    public LazyIteratorChain(Iterator<Iterator<T>> iterators) {
        this.iterators = iterators;
    }

    public LazyIteratorChain(Iterator<T> ... iterators) {
        this.iterators = Arrays.asList(iterators).iterator();
    }

    @Override
    public boolean hasNext() {
        if (this.hasNext == null) {
            while ((this.currentIterator == null || !this.currentIterator.hasNext()) && this.iterators.hasNext()) {
                this.currentIterator = this.iterators.next();
            }
            this.hasNext = this.currentIterator != null && this.currentIterator.hasNext();
        }
        return this.hasNext;
    }

    @Override
    public T next() {
        if (this.hasNext()) {
            this.hasNext = null;
            return this.currentIterator.next();
        }
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

