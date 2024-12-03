/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.collections;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class JoinedIterable<T>
implements Iterable<T> {
    private final TypeSafeJoinedIterator<T> iterator;

    public JoinedIterable(List<Iterable<T>> iterables) {
        if (iterables == null) {
            throw new NullPointerException("Unexpected null iterables argument");
        }
        this.iterator = new TypeSafeJoinedIterator<T>(iterables);
    }

    @Override
    public Iterator<T> iterator() {
        return this.iterator;
    }

    private static class TypeSafeJoinedIterator<T>
    implements Iterator<T> {
        private List<Iterable<T>> iterables;
        private int currentIterableIndex;
        private Iterator<T> currentIterator;
        private Iterator<T> lastUsedIterator;

        public TypeSafeJoinedIterator(List<Iterable<T>> iterables) {
            this.iterables = iterables;
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
                this.currentIterator = this.iterables.size() == 0 ? Collections.emptyIterator() : this.iterables.get(0).iterator();
                this.lastUsedIterator = this.currentIterator;
            }
            while (!this.currentIterator.hasNext() && this.currentIterableIndex < this.iterables.size() - 1) {
                ++this.currentIterableIndex;
                this.currentIterator = this.iterables.get(this.currentIterableIndex).iterator();
            }
        }
    }
}

