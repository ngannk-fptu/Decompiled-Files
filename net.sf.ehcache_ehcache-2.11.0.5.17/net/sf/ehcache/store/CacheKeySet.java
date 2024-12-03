/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class CacheKeySet<E>
implements Set<E> {
    private static final Iterator EMPTY_ITERATOR = new Iterator(){

        @Override
        public boolean hasNext() {
            return false;
        }

        public Object next() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    };
    private final Collection<E>[] keySets;

    public CacheKeySet(Collection<E> ... keySets) {
        this.keySets = keySets;
    }

    @Override
    public int size() {
        int size = 0;
        for (Collection<E> keySet : this.keySets) {
            size += keySet.size();
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (Collection<E> keySet : this.keySets) {
            if (keySet.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean contains(Object o) {
        for (Collection<E> keySet : this.keySets) {
            if (!keySet.contains(o)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new KeySetIterator();
    }

    @Override
    public Object[] toArray() {
        ArrayList<E> list = new ArrayList<E>();
        for (E e : this) {
            list.add(e);
        }
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        ArrayList<E> list = new ArrayList<E>();
        for (E e : this) {
            list.add(e);
        }
        return list.toArray(a);
    }

    @Override
    public boolean add(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (this.contains(o)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    private final class KeySetIterator
    implements Iterator<E> {
        private Iterator<E> currentIterator;
        private int index = 0;
        private E next;
        private E current;

        private KeySetIterator() {
            this.currentIterator = CacheKeySet.this.keySets.length == 0 ? EMPTY_ITERATOR : CacheKeySet.this.keySets[0].iterator();
            this.advance();
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public E next() {
            this.current = this.next;
            this.advance();
            return this.current;
        }

        private void advance() {
            this.next = null;
            while (this.next == null) {
                if (this.currentIterator.hasNext()) {
                    this.next = this.currentIterator.next();
                    for (int i = 0; i < this.index; ++i) {
                        if (!CacheKeySet.this.keySets[i].contains(this.next)) continue;
                        this.next = null;
                    }
                    continue;
                }
                this.next = null;
                if (++this.index < CacheKeySet.this.keySets.length) {
                    this.currentIterator = CacheKeySet.this.keySets[this.index].iterator();
                    continue;
                }
                return;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

