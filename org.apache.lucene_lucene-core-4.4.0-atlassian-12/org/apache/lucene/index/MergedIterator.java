/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.lucene.util.PriorityQueue;

final class MergedIterator<T extends Comparable<T>>
implements Iterator<T> {
    private T current;
    private final TermMergeQueue<T> queue;
    private final SubIterator<T>[] top;
    private int numTop;

    public MergedIterator(Iterator<T> ... iterators) {
        this.queue = new TermMergeQueue(iterators.length);
        this.top = new SubIterator[iterators.length];
        int index = 0;
        for (Iterator<T> iterator : iterators) {
            if (!iterator.hasNext()) continue;
            SubIterator sub = new SubIterator();
            sub.current = (Comparable)iterator.next();
            sub.iterator = iterator;
            sub.index = index++;
            this.queue.add(sub);
        }
    }

    @Override
    public boolean hasNext() {
        if (this.queue.size() > 0) {
            return true;
        }
        for (int i = 0; i < this.numTop; ++i) {
            if (!this.top[i].iterator.hasNext()) continue;
            return true;
        }
        return false;
    }

    @Override
    public T next() {
        this.pushTop();
        if (this.queue.size() > 0) {
            this.pullTop();
        } else {
            this.current = null;
        }
        if (this.current == null) {
            throw new NoSuchElementException();
        }
        return this.current;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private void pullTop() {
        assert (this.numTop == 0);
        do {
            this.top[this.numTop++] = (SubIterator)this.queue.pop();
        } while (this.queue.size() != 0 && ((SubIterator)this.queue.top()).current.equals(this.top[0].current));
        this.current = this.top[0].current;
    }

    private void pushTop() {
        for (int i = 0; i < this.numTop; ++i) {
            if (this.top[i].iterator.hasNext()) {
                this.top[i].current = (Comparable)this.top[i].iterator.next();
                this.queue.add(this.top[i]);
                continue;
            }
            this.top[i].current = null;
        }
        this.numTop = 0;
    }

    private static class TermMergeQueue<C extends Comparable<C>>
    extends PriorityQueue<SubIterator<C>> {
        TermMergeQueue(int size) {
            super(size);
        }

        @Override
        protected boolean lessThan(SubIterator<C> a, SubIterator<C> b) {
            int cmp = a.current.compareTo(b.current);
            if (cmp != 0) {
                return cmp < 0;
            }
            return a.index < b.index;
        }
    }

    private static class SubIterator<I extends Comparable<I>> {
        Iterator<I> iterator;
        I current;
        int index;

        private SubIterator() {
        }
    }
}

