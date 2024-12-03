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

public class BinaryArrayAddressableHeap<K, V>
extends AbstractArrayAddressableHeap<K, V>
implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int DEFAULT_HEAP_CAPACITY = 16;

    public BinaryArrayAddressableHeap() {
        this(null, 16);
    }

    public BinaryArrayAddressableHeap(int capacity) {
        this(null, capacity);
    }

    public BinaryArrayAddressableHeap(Comparator<? super K> comparator) {
        this(comparator, 16);
    }

    public BinaryArrayAddressableHeap(Comparator<? super K> comparator, int capacity) {
        super(comparator, capacity);
    }

    @LinearTime
    public static <K, V> BinaryArrayAddressableHeap<K, V> heapify(K[] keys, V[] values) {
        int i;
        if (keys == null) {
            throw new IllegalArgumentException("Key array cannot be null");
        }
        if (values != null && keys.length != values.length) {
            throw new IllegalArgumentException("Values array must have the same length as the keys array");
        }
        if (keys.length == 0) {
            return new BinaryArrayAddressableHeap<K, V>();
        }
        BinaryArrayAddressableHeap<K, V> h = new BinaryArrayAddressableHeap<K, V>(keys.length);
        for (i = 0; i < keys.length; ++i) {
            K key = keys[i];
            Object value = values == null ? null : (Object)values[i];
            BinaryArrayAddressableHeap<K, V> binaryArrayAddressableHeap = h;
            Objects.requireNonNull(binaryArrayAddressableHeap);
            AbstractArrayAddressableHeap.ArrayHandle ah = new AbstractArrayAddressableHeap.ArrayHandle(binaryArrayAddressableHeap, key, value);
            ah.index = i + 1;
            h.array[i + 1] = ah;
        }
        h.size = keys.length;
        for (i = keys.length / 2; i > 0; --i) {
            h.fixdown(i);
        }
        return h;
    }

    @LinearTime
    public static <K, V> BinaryArrayAddressableHeap<K, V> heapify(K[] keys, V[] values, Comparator<? super K> comparator) {
        int i;
        if (keys == null) {
            throw new IllegalArgumentException("Keys array cannot be null");
        }
        if (values != null && keys.length != values.length) {
            throw new IllegalArgumentException("Values array must have the same length as the keys array");
        }
        if (keys.length == 0) {
            return new BinaryArrayAddressableHeap<K, V>(comparator);
        }
        BinaryArrayAddressableHeap<K, V> h = new BinaryArrayAddressableHeap<K, V>(comparator, keys.length);
        for (i = 0; i < keys.length; ++i) {
            K key = keys[i];
            Object value = values == null ? null : (Object)values[i];
            BinaryArrayAddressableHeap<K, V> binaryArrayAddressableHeap = h;
            Objects.requireNonNull(binaryArrayAddressableHeap);
            AbstractArrayAddressableHeap.ArrayHandle ah = new AbstractArrayAddressableHeap.ArrayHandle(binaryArrayAddressableHeap, key, value);
            ah.index = i + 1;
            h.array[i + 1] = ah;
        }
        h.size = keys.length;
        for (i = keys.length / 2; i > 0; --i) {
            h.fixdownWithComparator(i);
        }
        return h;
    }

    public Iterator<AddressableHeap.Handle<K, V>> handlesIterator() {
        return new Iterator<AddressableHeap.Handle<K, V>>(){
            private int pos = 1;

            @Override
            public boolean hasNext() {
                return this.pos <= BinaryArrayAddressableHeap.this.size;
            }

            @Override
            public AddressableHeap.Handle<K, V> next() {
                if (this.pos > BinaryArrayAddressableHeap.this.size) {
                    throw new NoSuchElementException();
                }
                return BinaryArrayAddressableHeap.this.array[this.pos++];
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
            this.array[k] = this.array[k / 2];
            this.array[k].index = k;
            k /= 2;
        }
        this.array[k] = h;
        h.index = k;
    }

    @Override
    protected void fixup(int k) {
        AbstractArrayAddressableHeap.ArrayHandle h = this.array[k];
        while (k > 1 && ((Comparable)this.array[k / 2].getKey()).compareTo(h.getKey()) > 0) {
            this.array[k] = this.array[k / 2];
            this.array[k].index = k;
            k /= 2;
        }
        this.array[k] = h;
        h.index = k;
    }

    @Override
    protected void fixupWithComparator(int k) {
        AbstractArrayAddressableHeap.ArrayHandle h = this.array[k];
        while (k > 1 && this.comparator.compare(this.array[k / 2].getKey(), h.getKey()) > 0) {
            this.array[k] = this.array[k / 2];
            this.array[k].index = k;
            k /= 2;
        }
        this.array[k] = h;
        h.index = k;
    }

    @Override
    protected void fixdown(int k) {
        AbstractArrayAddressableHeap.ArrayHandle h = this.array[k];
        while (2 * k <= this.size) {
            int j = 2 * k;
            if (j < this.size && ((Comparable)this.array[j].getKey()).compareTo(this.array[j + 1].getKey()) > 0) {
                ++j;
            }
            if (((Comparable)h.getKey()).compareTo(this.array[j].getKey()) <= 0) break;
            this.array[k] = this.array[j];
            this.array[k].index = k;
            k = j;
        }
        this.array[k] = h;
        h.index = k;
    }

    @Override
    protected void fixdownWithComparator(int k) {
        AbstractArrayAddressableHeap.ArrayHandle h = this.array[k];
        while (2 * k <= this.size) {
            int j = 2 * k;
            if (j < this.size && this.comparator.compare(this.array[j].getKey(), this.array[j + 1].getKey()) > 0) {
                ++j;
            }
            if (this.comparator.compare(h.getKey(), this.array[j].getKey()) <= 0) break;
            this.array[k] = this.array[j];
            this.array[k].index = k;
            k = j;
        }
        this.array[k] = h;
        h.index = k;
    }
}

