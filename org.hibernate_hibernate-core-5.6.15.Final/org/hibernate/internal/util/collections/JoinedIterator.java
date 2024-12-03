/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.collections;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class JoinedIterator<T>
implements Iterator<T> {
    private Iterator<T>[] wrappedIterators;
    private int currentIteratorIndex;
    private Iterator<T> currentIterator;
    private Iterator<T> lastUsedIterator;

    public JoinedIterator(List<Iterator<T>> wrappedIterators) {
        this(wrappedIterators.toArray(new Iterator[wrappedIterators.size()]));
    }

    public JoinedIterator(Iterator<T> ... iteratorsToWrap) {
        if (iteratorsToWrap == null) {
            throw new NullPointerException("Iterators to join were null");
        }
        this.wrappedIterators = iteratorsToWrap;
    }

    @Override
    public boolean hasNext() {
        this.updateCurrentIterator();
        return this.currentIterator.hasNext();
    }

    @Override
    public T next() {
        this.updateCurrentIterator();
        return this.currentIterator.next();
    }

    @Override
    public void remove() {
        this.updateCurrentIterator();
        this.lastUsedIterator.remove();
    }

    protected void updateCurrentIterator() {
        if (this.currentIterator == null) {
            this.currentIterator = this.wrappedIterators.length == 0 ? Collections.emptyList().iterator() : this.wrappedIterators[0];
            this.lastUsedIterator = this.currentIterator;
        }
        while (!this.currentIterator.hasNext() && this.currentIteratorIndex < this.wrappedIterators.length - 1) {
            ++this.currentIteratorIndex;
            this.currentIterator = this.wrappedIterators[this.currentIteratorIndex];
        }
    }
}

