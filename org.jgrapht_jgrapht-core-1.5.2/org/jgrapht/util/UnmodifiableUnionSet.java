/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.util;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class UnmodifiableUnionSet<E>
extends AbstractSet<E>
implements Serializable {
    private static final long serialVersionUID = -1937327799873331354L;
    private final Set<E> first;
    private final Set<E> second;

    public UnmodifiableUnionSet(Set<E> first, Set<E> second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);
        this.first = first;
        this.second = second;
    }

    @Override
    public Iterator<E> iterator() {
        return new UnionIterator(this.orderSetsBySize());
    }

    @Override
    public int size() {
        SetSizeOrdering ordering = this.orderSetsBySize();
        Set bigger = ordering.bigger;
        int count = ordering.biggerSize;
        for (Object e : ordering.smaller) {
            if (bigger.contains(e)) continue;
            ++count;
        }
        return count;
    }

    @Override
    public boolean contains(Object o) {
        return this.first.contains(o) || this.second.contains(o);
    }

    private SetSizeOrdering orderSetsBySize() {
        int firstSize = this.first.size();
        int secondSize = this.second.size();
        if (secondSize > firstSize) {
            return new SetSizeOrdering(this.second, this.first, secondSize, firstSize);
        }
        return new SetSizeOrdering(this.first, this.second, firstSize, secondSize);
    }

    private class UnionIterator
    implements Iterator<E> {
        private SetSizeOrdering ordering;
        private boolean inBiggerSet;
        private Iterator<E> iterator;
        private E cur;

        UnionIterator(SetSizeOrdering ordering) {
            this.ordering = ordering;
            this.inBiggerSet = true;
            this.iterator = ordering.bigger.iterator();
            this.cur = this.prefetch();
        }

        @Override
        public boolean hasNext() {
            if (this.cur != null) {
                return true;
            }
            this.cur = this.prefetch();
            return this.cur != null;
        }

        @Override
        public E next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            Object result = this.cur;
            this.cur = null;
            return result;
        }

        private E prefetch() {
            block3: {
                Object elem;
                while (true) {
                    if (this.inBiggerSet) {
                        if (this.iterator.hasNext()) {
                            return this.iterator.next();
                        }
                        this.inBiggerSet = false;
                        this.iterator = this.ordering.smaller.iterator();
                        continue;
                    }
                    if (!this.iterator.hasNext()) break block3;
                    elem = this.iterator.next();
                    if (!this.ordering.bigger.contains(elem)) break;
                }
                return elem;
            }
            return null;
        }
    }

    private class SetSizeOrdering {
        final Set<E> bigger;
        final Set<E> smaller;
        final int biggerSize;
        final int smallerSize;

        SetSizeOrdering(Set<E> bigger, Set<E> smaller, int biggerSize, int smallerSize) {
            this.bigger = bigger;
            this.smaller = smaller;
            this.biggerSize = biggerSize;
            this.smallerSize = smallerSize;
        }
    }
}

