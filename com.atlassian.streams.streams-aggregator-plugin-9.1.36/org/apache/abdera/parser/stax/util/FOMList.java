/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax.util;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMList<T>
extends AbstractCollection<T>
implements List<T> {
    private final Iterator<T> i;
    private final List<T> buffer = new ArrayList<T>();

    public FOMList(Iterator<T> i) {
        this.i = i;
    }

    public List<T> getAsList() {
        this.buffer(-1);
        return Collections.unmodifiableList(this.buffer);
    }

    private boolean finished() {
        return !this.i.hasNext();
    }

    private int buffered() {
        return this.buffer.size() - 1;
    }

    private int buffer(int n) {
        if (this.i.hasNext()) {
            int read = 0;
            while (this.i.hasNext() && (read++ < n || n == -1)) {
                this.buffer.add(this.i.next());
            }
        }
        return this.buffered();
    }

    @Override
    public T get(int index) {
        int n = this.buffered();
        if (index > n && index > this.buffer(index - n)) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return this.buffer.get(index);
    }

    @Override
    public int size() {
        return this.buffer(-1) + 1;
    }

    @Override
    public Iterator<T> iterator() {
        return new BufferIterator(this);
    }

    private Iterator<T> iterator(int index) {
        return new BufferIterator(this, index);
    }

    @Override
    public boolean add(T o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        this.buffer(-1);
        return this.buffer.contains(o);
    }

    @Override
    public boolean containsAll(Collection c) {
        for (Object o : c) {
            if (!this.contains(o)) continue;
            return true;
        }
        return false;
    }

    @Override
    public int indexOf(Object o) {
        this.buffer(-1);
        return this.buffer.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        this.buffer(-1);
        return this.buffer.isEmpty();
    }

    @Override
    public int lastIndexOf(Object o) {
        this.buffer(-1);
        return this.buffer.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return (ListIterator)this.iterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return (ListIterator)this.iterator(index);
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        this.buffer(-1);
        return Collections.unmodifiableList(this.buffer.subList(fromIndex, toIndex));
    }

    @Override
    public Object[] toArray() {
        this.buffer(-1);
        return this.buffer.toArray();
    }

    @Override
    public Object[] toArray(Object[] a) {
        this.buffer(-1);
        return this.buffer.toArray(a);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class BufferIterator<M>
    implements ListIterator<M> {
        private FOMList set = null;
        private int counter = 0;

        BufferIterator(FOMList set) {
            this.set = set;
        }

        BufferIterator(FOMList set, int index) {
            this.set = set;
            this.counter = index;
        }

        @Override
        public boolean hasNext() {
            return !this.set.finished() || this.set.finished() && this.counter < FOMList.this.buffer.size();
        }

        @Override
        public M next() {
            return (M)this.set.get(this.counter++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(M o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasPrevious() {
            return this.counter > 0;
        }

        @Override
        public int nextIndex() {
            if (this.hasNext()) {
                return this.counter + 1;
            }
            return FOMList.this.buffer.size();
        }

        @Override
        public M previous() {
            return (M)this.set.get(--this.counter);
        }

        @Override
        public int previousIndex() {
            if (this.hasPrevious()) {
                return this.counter - 1;
            }
            return -1;
        }

        @Override
        public void set(M o) {
            throw new UnsupportedOperationException();
        }
    }
}

