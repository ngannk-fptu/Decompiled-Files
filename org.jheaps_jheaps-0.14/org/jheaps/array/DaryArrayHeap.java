/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.array;

import java.util.Comparator;
import org.jheaps.annotations.LinearTime;
import org.jheaps.array.AbstractArrayHeap;

public class DaryArrayHeap<K>
extends AbstractArrayHeap<K> {
    private static final long serialVersionUID = 1L;
    public static final int DEFAULT_HEAP_CAPACITY = 16;
    protected int d;

    public DaryArrayHeap(int d) {
        this(d, null, 16);
    }

    public DaryArrayHeap(int d, int capacity) {
        this(d, null, capacity);
    }

    public DaryArrayHeap(int d, Comparator<? super K> comparator) {
        this(d, comparator, 16);
    }

    public DaryArrayHeap(int d, Comparator<? super K> comparator, int capacity) {
        super(comparator, capacity);
        if (d < 2) {
            throw new IllegalArgumentException("D-ary heaps must have at least 2 children per node");
        }
        this.d = d;
    }

    @LinearTime
    public static <K> DaryArrayHeap<K> heapify(int d, K[] array) {
        if (d < 2) {
            throw new IllegalArgumentException("D-ary heaps must have at least 2 children per node");
        }
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (array.length == 0) {
            return new DaryArrayHeap<K>(d);
        }
        DaryArrayHeap<K> h = new DaryArrayHeap<K>(d, array.length);
        System.arraycopy(array, 0, h.array, 1, array.length);
        h.size = array.length;
        for (int i = array.length / d; i > 0; --i) {
            h.fixdown(i);
        }
        return h;
    }

    @LinearTime
    public static <K> DaryArrayHeap<K> heapify(int d, K[] array, Comparator<? super K> comparator) {
        if (d < 2) {
            throw new IllegalArgumentException("D-ary heaps must have at least 2 children per node");
        }
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (array.length == 0) {
            return new DaryArrayHeap<K>(d, comparator);
        }
        DaryArrayHeap<K> h = new DaryArrayHeap<K>(d, comparator, array.length);
        System.arraycopy(array, 0, h.array, 1, array.length);
        h.size = array.length;
        for (int i = array.length / d; i > 0; --i) {
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
        int p;
        Object key = this.array[k];
        while (k > 1 && ((Comparable)this.array[p = (k - 2) / this.d + 1]).compareTo(key) > 0) {
            this.array[k] = this.array[p];
            k = p;
        }
        this.array[k] = key;
    }

    @Override
    protected void fixupWithComparator(int k) {
        int p;
        Object key = this.array[k];
        while (k > 1 && this.comparator.compare(this.array[p = (k - 2) / this.d + 1], key) > 0) {
            this.array[k] = this.array[p];
            k = p;
        }
        this.array[k] = key;
    }

    @Override
    protected void fixdown(int k) {
        int c;
        Object key = this.array[k];
        while ((c = this.d * (k - 1) + 2) <= this.size) {
            int maxc = c;
            for (int i = 1; i < this.d; ++i) {
                if (c + i > this.size || ((Comparable)this.array[maxc]).compareTo(this.array[c + i]) <= 0) continue;
                maxc = c + i;
            }
            if (((Comparable)key).compareTo(this.array[maxc]) <= 0) break;
            this.array[k] = this.array[maxc];
            k = maxc;
        }
        this.array[k] = key;
    }

    @Override
    protected void fixdownWithComparator(int k) {
        int c;
        Object key = this.array[k];
        while ((c = this.d * (k - 1) + 2) <= this.size) {
            int maxc = c;
            for (int i = 1; i < this.d; ++i) {
                if (c + i > this.size || this.comparator.compare(this.array[maxc], this.array[c + i]) <= 0) continue;
                maxc = c + i;
            }
            if (this.comparator.compare(key, this.array[maxc]) <= 0) break;
            this.array[k] = this.array[maxc];
            k = maxc;
        }
        this.array[k] = key;
    }
}

