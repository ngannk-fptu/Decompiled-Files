/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.array;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.jheaps.AddressableHeap;
import org.jheaps.annotations.LinearTime;
import org.jheaps.array.AbstractArrayAddressableHeap;

public class DaryArrayAddressableHeap<K, V>
extends AbstractArrayAddressableHeap<K, V>
implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String D_ARY_HEAPS_MUST_HAVE_AT_LEAST_2_CHILDREN_PER_NODE = "D-ary heaps must have at least 2 children per node";
    public static final int DEFAULT_HEAP_CAPACITY = 16;
    protected int d;

    public DaryArrayAddressableHeap(int d) {
        this(d, null, 16);
    }

    public DaryArrayAddressableHeap(int d, int capacity) {
        this(d, null, capacity);
    }

    public DaryArrayAddressableHeap(int d, Comparator<? super K> comparator) {
        this(d, comparator, 16);
    }

    public DaryArrayAddressableHeap(int d, Comparator<? super K> comparator, int capacity) {
        super(comparator, capacity);
        if (d < 2) {
            throw new IllegalArgumentException(D_ARY_HEAPS_MUST_HAVE_AT_LEAST_2_CHILDREN_PER_NODE);
        }
        this.d = d;
    }

    @LinearTime
    public static <K, V> DaryArrayAddressableHeap<K, V> heapify(int d, K[] keys, V[] values) {
        int i;
        if (d < 2) {
            throw new IllegalArgumentException(D_ARY_HEAPS_MUST_HAVE_AT_LEAST_2_CHILDREN_PER_NODE);
        }
        if (keys == null) {
            throw new IllegalArgumentException("Key array cannot be null");
        }
        if (values != null && keys.length != values.length) {
            throw new IllegalArgumentException("Values array must have the same length as the keys array");
        }
        if (keys.length == 0) {
            return new DaryArrayAddressableHeap<K, V>(d);
        }
        DaryArrayAddressableHeap<K, V> h = new DaryArrayAddressableHeap<K, V>(d, keys.length);
        for (i = 0; i < keys.length; ++i) {
            K key = keys[i];
            Object value = values == null ? null : (Object)values[i];
            DaryArrayAddressableHeap<K, V> daryArrayAddressableHeap = h;
            Objects.requireNonNull(daryArrayAddressableHeap);
            AbstractArrayAddressableHeap.ArrayHandle ah = new AbstractArrayAddressableHeap.ArrayHandle(daryArrayAddressableHeap, key, value);
            ah.index = i + 1;
            h.array[i + 1] = ah;
        }
        h.size = keys.length;
        for (i = keys.length / d; i > 0; --i) {
            h.fixdown(i);
        }
        return h;
    }

    @LinearTime
    public static <K, V> DaryArrayAddressableHeap<K, V> heapify(int d, K[] keys, V[] values, Comparator<? super K> comparator) {
        int i;
        if (d < 2) {
            throw new IllegalArgumentException(D_ARY_HEAPS_MUST_HAVE_AT_LEAST_2_CHILDREN_PER_NODE);
        }
        if (keys == null) {
            throw new IllegalArgumentException("Keys array cannot be null");
        }
        if (values != null && keys.length != values.length) {
            throw new IllegalArgumentException("Values array must have the same length as the keys array");
        }
        if (keys.length == 0) {
            return new DaryArrayAddressableHeap<K, V>(d, comparator);
        }
        DaryArrayAddressableHeap<K, V> h = new DaryArrayAddressableHeap<K, V>(d, comparator, keys.length);
        for (i = 0; i < keys.length; ++i) {
            K key = keys[i];
            Object value = values == null ? null : (Object)values[i];
            DaryArrayAddressableHeap<K, V> daryArrayAddressableHeap = h;
            Objects.requireNonNull(daryArrayAddressableHeap);
            AbstractArrayAddressableHeap.ArrayHandle ah = new AbstractArrayAddressableHeap.ArrayHandle(daryArrayAddressableHeap, key, value);
            ah.index = i + 1;
            h.array[i + 1] = ah;
        }
        h.size = keys.length;
        for (i = keys.length / d; i > 0; --i) {
            h.fixdownWithComparator(i);
        }
        return h;
    }

    public Iterator<AddressableHeap.Handle<K, V>> handlesIterator() {
        return new Iterator<AddressableHeap.Handle<K, V>>(){
            private int pos = 1;

            @Override
            public boolean hasNext() {
                return this.pos <= DaryArrayAddressableHeap.this.size;
            }

            @Override
            public AddressableHeap.Handle<K, V> next() {
                if (this.pos > DaryArrayAddressableHeap.this.size) {
                    throw new NoSuchElementException();
                }
                return DaryArrayAddressableHeap.this.array[this.pos++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    protected void ensureCapacity(int capacity) {
        this.checkCapacity(capacity);
        AbstractArrayAddressableHeap.ArrayHandle[] newArray = (AbstractArrayAddressableHeap.ArrayHandle[])Array.newInstance(AbstractArrayAddressableHeap.ArrayHandle.class, capacity + 1);
        System.arraycopy(this.array, 1, newArray, 1, this.size);
        this.array = newArray;
    }

    @Override
    protected void forceFixup(int k) {
        AbstractArrayAddressableHeap.ArrayHandle h = this.array[k];
        while (k > 1) {
            int p = (k - 2) / this.d + 1;
            this.array[k] = this.array[p];
            this.array[k].index = k;
            k = p;
        }
        this.array[k] = h;
        h.index = k;
    }

    @Override
    protected void fixup(int k) {
        int p;
        AbstractArrayAddressableHeap.ArrayHandle h = this.array[k];
        while (k > 1 && ((Comparable)this.array[p = (k - 2) / this.d + 1].getKey()).compareTo(h.getKey()) > 0) {
            this.array[k] = this.array[p];
            this.array[k].index = k;
            k = p;
        }
        this.array[k] = h;
        h.index = k;
    }

    @Override
    protected void fixupWithComparator(int k) {
        int p;
        AbstractArrayAddressableHeap.ArrayHandle h = this.array[k];
        while (k > 1 && this.comparator.compare(this.array[p = (k - 2) / this.d + 1].getKey(), h.getKey()) > 0) {
            this.array[k] = this.array[p];
            this.array[k].index = k;
            k = p;
        }
        this.array[k] = h;
        h.index = k;
    }

    @Override
    protected void fixdown(int k) {
        int c;
        AbstractArrayAddressableHeap.ArrayHandle h = this.array[k];
        while ((c = this.d * (k - 1) + 2) <= this.size) {
            int maxc = c;
            for (int i = 1; i < this.d && c + i <= this.size; ++i) {
                if (((Comparable)this.array[maxc].getKey()).compareTo(this.array[c + i].getKey()) <= 0) continue;
                maxc = c + i;
            }
            if (((Comparable)h.getKey()).compareTo(this.array[maxc].getKey()) <= 0) break;
            this.array[k] = this.array[maxc];
            this.array[k].index = k;
            k = maxc;
        }
        this.array[k] = h;
        h.index = k;
    }

    @Override
    protected void fixdownWithComparator(int k) {
        int c;
        AbstractArrayAddressableHeap.ArrayHandle h = this.array[k];
        while ((c = this.d * (k - 1) + 2) <= this.size) {
            int maxc = c;
            for (int i = 1; i < this.d && c + i <= this.size; ++i) {
                if (this.comparator.compare(this.array[maxc].getKey(), this.array[c + i].getKey()) <= 0) continue;
                maxc = c + i;
            }
            if (this.comparator.compare(h.getKey(), this.array[maxc].getKey()) <= 0) break;
            this.array[k] = this.array[maxc];
            this.array[k].index = k;
            k = maxc;
        }
        this.array[k] = h;
        h.index = k;
    }
}

