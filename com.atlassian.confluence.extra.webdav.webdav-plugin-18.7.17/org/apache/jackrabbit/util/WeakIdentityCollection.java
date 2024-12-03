/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class WeakIdentityCollection
implements Collection {
    private transient WeakRef[] elementData;
    private int size;
    private final ReferenceQueue refQueue = new ReferenceQueue();
    private final BitSet emptySlots = new BitSet();

    public WeakIdentityCollection(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        this.elementData = new WeakRef[initialCapacity];
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.size; ++i) {
            this.elementData[i] = null;
        }
        this.size = 0;
        this.emptySlots.clear();
    }

    public boolean add(Object o) {
        if (o == null) {
            throw new NullPointerException("Object must not be null");
        }
        WeakRef ref = (WeakRef)this.refQueue.poll();
        if (ref != null) {
            this.elementData[((WeakRef)ref).index] = new WeakRef(o, ref.index);
            this.cleanQueue();
        } else if (!this.emptySlots.isEmpty()) {
            int idx = this.emptySlots.nextSetBit(0);
            this.elementData[idx] = new WeakRef(o, idx);
            this.emptySlots.clear(idx);
        } else {
            this.ensureCapacity(this.size + 1);
            this.elementData[this.size++] = new WeakRef(o, this.size - 1);
        }
        return true;
    }

    @Override
    public boolean contains(Object o) {
        for (int i = 0; i < this.size; ++i) {
            if (this.elementData[i].get() != o) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        for (int i = 0; i < this.size; ++i) {
            if (this.elementData[i].get() != o) continue;
            this.emptySlots.set(i);
            this.elementData[i] = new WeakRef(null, i);
            return true;
        }
        return false;
    }

    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException("addAll");
    }

    public boolean containsAll(Collection c) {
        throw new UnsupportedOperationException("containsAll");
    }

    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException("removeAll");
    }

    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException("retainAll");
    }

    @Override
    public Iterator iterator() {
        return new Iter();
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[this.size];
        for (int i = 0; i < result.length; ++i) {
            result[i] = this.elementData[i].get();
        }
        return result;
    }

    public Object[] toArray(Object[] a) {
        if (a.length < this.size) {
            a = (Object[])Array.newInstance(a.getClass().getComponentType(), this.size);
        }
        for (int i = 0; i < this.size; ++i) {
            a[i] = this.elementData[i].get();
        }
        if (a.length > this.size) {
            a[this.size] = null;
        }
        return a;
    }

    private void ensureCapacity(int minCapacity) {
        int oldCapacity = this.elementData.length;
        if (minCapacity > oldCapacity) {
            WeakRef[] oldData = this.elementData;
            int newCapacity = oldCapacity * 3 / 2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            this.elementData = new WeakRef[newCapacity];
            System.arraycopy(oldData, 0, this.elementData, 0, this.size);
        }
    }

    private void cleanQueue() {
        WeakRef ref;
        while ((ref = (WeakRef)this.refQueue.poll()) != null) {
            this.emptySlots.set(ref.index);
        }
    }

    private final class WeakRef
    extends WeakReference {
        private final int index;

        public WeakRef(Object referent, int index) {
            super(referent, WeakIdentityCollection.this.refQueue);
            this.index = index;
        }
    }

    private final class Iter
    implements Iterator {
        private int index;
        private Reference[] elements;
        private int size;

        private Iter() {
            this.elements = WeakIdentityCollection.this.elementData;
            this.size = WeakIdentityCollection.this.size;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }

        @Override
        public boolean hasNext() {
            return this.index < this.size;
        }

        public Object next() {
            if (this.index >= this.size) {
                throw new NoSuchElementException();
            }
            return this.elements[this.index++].get();
        }
    }
}

