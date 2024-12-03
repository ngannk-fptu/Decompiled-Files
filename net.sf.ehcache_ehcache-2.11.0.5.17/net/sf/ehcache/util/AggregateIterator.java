/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class AggregateIterator<T>
implements Iterator<T> {
    private final Collection<?> removeColl;
    private final Iterator<Iterator<T>> iterators;
    private Iterator<T> currentIterator;
    private T next;
    private T current;

    public AggregateIterator(Collection<?> collRemove, List<Iterator<T>> listIterators) {
        this.removeColl = collRemove;
        this.iterators = listIterators.iterator();
        while (this.iterators.hasNext()) {
            this.currentIterator = this.getNextIterator();
            while (this.currentIterator.hasNext()) {
                this.next = this.currentIterator.next();
                if (this.removeColl.contains(this.next)) continue;
                return;
            }
        }
        this.next = null;
    }

    @Override
    public final boolean hasNext() {
        return this.next != null;
    }

    @Override
    public final T next() {
        T nextCandidate;
        if (this.next == null) {
            throw new NoSuchElementException();
        }
        T returnNext = this.next;
        this.current = returnNext;
        this.next = null;
        if (this.currentIterator == null) {
            throw new NoSuchElementException();
        }
        while (this.currentIterator.hasNext()) {
            nextCandidate = this.currentIterator.next();
            if (this.removeColl.contains(nextCandidate)) continue;
            this.next = nextCandidate;
            return returnNext;
        }
        while (this.iterators.hasNext()) {
            this.currentIterator = this.iterators.next();
            while (this.currentIterator.hasNext()) {
                nextCandidate = this.currentIterator.next();
                if (this.removeColl.contains(nextCandidate)) continue;
                this.next = nextCandidate;
                return returnNext;
            }
        }
        return returnNext;
    }

    @Override
    public final void remove() {
        if (this.current == null) {
            throw new IllegalStateException();
        }
        this.removeColl.remove(this.current);
        this.current = null;
    }

    private Iterator<T> getNextIterator() {
        return this.iterators.next();
    }
}

