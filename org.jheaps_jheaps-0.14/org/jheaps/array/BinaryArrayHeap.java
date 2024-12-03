/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.array;

import java.util.Comparator;
import org.jheaps.annotations.LinearTime;
import org.jheaps.array.AbstractArrayHeap;

public class BinaryArrayHeap<K>
extends AbstractArrayHeap<K> {
    private static final long serialVersionUID = 1L;
    public static final int DEFAULT_HEAP_CAPACITY = 16;

    public BinaryArrayHeap() {
        super(null, 16);
    }

    public BinaryArrayHeap(int capacity) {
        super(null, capacity);
    }

    public BinaryArrayHeap(Comparator<? super K> comparator) {
        super(comparator, 16);
    }

    public BinaryArrayHeap(Comparator<? super K> comparator, int capacity) {
        super(comparator, capacity);
    }

    @LinearTime
    public static <K> BinaryArrayHeap<K> heapify(K[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (array.length == 0) {
            return new BinaryArrayHeap<K>();
        }
        BinaryArrayHeap<K> h = new BinaryArrayHeap<K>(array.length);
        System.arraycopy(array, 0, h.array, 1, array.length);
        h.size = array.length;
        for (int i = array.length / 2; i > 0; --i) {
            h.fixdown(i);
        }
        return h;
    }

    @LinearTime
    public static <K> BinaryArrayHeap<K> heapify(K[] array, Comparator<? super K> comparator) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (array.length == 0) {
            return new BinaryArrayHeap<K>(comparator);
        }
        BinaryArrayHeap<K> h = new BinaryArrayHeap<K>(comparator, array.length);
        System.arraycopy(array, 0, h.array, 1, array.length);
        h.size = array.length;
        for (int i = array.length / 2; i > 0; --i) {
            h.fixdownWithComparator(i);
        }
        return h;
    }

    @Override
    protected void ensureCapacity(int capacity) {
        this.checkCapacity(capacity);
        Object[] newArray = new Object[capacity + 1];
        System.arraycopy(this.array, 1, newArray, 1, this.size);
        this.array = newArray;
    }

    @Override
    protected void fixup(int k) {
        Object key = this.array[k];
        while (k > 1 && ((Comparable)this.array[k >> 1]).compareTo(key) > 0) {
            this.array[k] = this.array[k >> 1];
            k >>= 1;
        }
        this.array[k] = key;
    }

    @Override
    protected void fixupWithComparator(int k) {
        Object key = this.array[k];
        while (k > 1 && this.comparator.compare(this.array[k >> 1], key) > 0) {
            this.array[k] = this.array[k >> 1];
            k >>= 1;
        }
        this.array[k] = key;
    }

    @Override
    protected void fixdown(int k) {
        Object key = this.array[k];
        while (2 * k <= this.size) {
            int j = 2 * k;
            if (j < this.size && ((Comparable)this.array[j]).compareTo(this.array[j + 1]) > 0) {
                ++j;
            }
            if (((Comparable)key).compareTo(this.array[j]) <= 0) break;
            this.array[k] = this.array[j];
            k = j;
        }
        this.array[k] = key;
    }

    @Override
    protected void fixdownWithComparator(int k) {
        Object key = this.array[k];
        while (2 * k <= this.size) {
            int j = 2 * k;
            if (j < this.size && this.comparator.compare(this.array[j], this.array[j + 1]) > 0) {
                ++j;
            }
            if (this.comparator.compare(key, this.array[j]) <= 0) break;
            this.array[k] = this.array[j];
            k = j;
        }
        this.array[k] = key;
    }
}

