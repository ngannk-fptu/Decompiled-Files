/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.array;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Comparator;
import java.util.NoSuchElementException;
import org.jheaps.annotations.ConstantTime;
import org.jheaps.annotations.LinearTime;
import org.jheaps.annotations.LogarithmicTime;
import org.jheaps.array.AbstractArrayWeakHeap;

public class BinaryArrayWeakHeap<K>
extends AbstractArrayWeakHeap<K>
implements Serializable {
    private static final long serialVersionUID = 7721391024028836146L;
    public static final int DEFAULT_HEAP_CAPACITY = 16;
    protected BitSet reverse;

    public BinaryArrayWeakHeap() {
        super(null, 16);
    }

    public BinaryArrayWeakHeap(int capacity) {
        super(null, capacity);
    }

    public BinaryArrayWeakHeap(Comparator<? super K> comparator) {
        super(comparator, 16);
    }

    public BinaryArrayWeakHeap(Comparator<? super K> comparator, int capacity) {
        super(comparator, capacity);
    }

    @LinearTime
    public static <K> BinaryArrayWeakHeap<K> heapify(K[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (array.length == 0) {
            return new BinaryArrayWeakHeap<K>();
        }
        BinaryArrayWeakHeap<K> h = new BinaryArrayWeakHeap<K>(array.length);
        System.arraycopy(array, 0, h.array, 0, array.length);
        h.size = array.length;
        for (int j = h.size - 1; j > 0; --j) {
            h.join(h.dancestor(j), j);
        }
        return h;
    }

    @LinearTime
    public static <K> BinaryArrayWeakHeap<K> heapify(K[] array, Comparator<? super K> comparator) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (array.length == 0) {
            return new BinaryArrayWeakHeap<K>(comparator);
        }
        BinaryArrayWeakHeap<K> h = new BinaryArrayWeakHeap<K>(comparator, array.length);
        System.arraycopy(array, 0, h.array, 0, array.length);
        h.size = array.length;
        for (int j = h.size - 1; j > 0; --j) {
            h.joinWithComparator(h.dancestor(j), j);
        }
        return h;
    }

    @Override
    @ConstantTime
    public K findMin() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return (K)this.array[0];
    }

    @Override
    @LogarithmicTime(amortized=true)
    public void insert(K key) {
        if (key == null) {
            throw new NullPointerException("Null keys not permitted");
        }
        if (this.size == this.array.length) {
            if (this.size == 0) {
                this.ensureCapacity(1);
            } else {
                this.ensureCapacity(2 * this.array.length);
            }
        }
        this.array[this.size] = key;
        this.reverse.clear(this.size);
        if (this.size % 2 == 0) {
            this.reverse.clear(this.size / 2);
        }
        if (this.comparator == null) {
            this.fixup(this.size);
        } else {
            this.fixupWithComparator(this.size);
        }
        ++this.size;
    }

    @Override
    @LogarithmicTime(amortized=true)
    public K deleteMin() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        Object result = this.array[0];
        --this.size;
        this.array[0] = this.array[this.size];
        this.array[this.size] = null;
        if (this.size > 1) {
            if (this.comparator == null) {
                this.fixdown(0);
            } else {
                this.fixdownWithComparator(0);
            }
        }
        if (2 * this.minCapacity <= this.array.length && 4 * this.size < this.array.length) {
            this.ensureCapacity(this.array.length / 2);
        }
        return (K)result;
    }

    @Override
    protected void initCapacity(int capacity) {
        this.array = new Object[capacity];
        this.reverse = new BitSet(capacity);
    }

    @Override
    protected void ensureCapacity(int capacity) {
        this.checkCapacity(capacity);
        Object[] newArray = new Object[capacity];
        System.arraycopy(this.array, 0, newArray, 0, this.size);
        this.array = newArray;
        BitSet newBitSet = new BitSet(capacity);
        newBitSet.or(this.reverse);
        this.reverse = newBitSet;
    }

    protected int dancestor(int j) {
        while (j % 2 == 1 == this.reverse.get(j / 2)) {
            j /= 2;
        }
        return j / 2;
    }

    protected boolean join(int i, int j) {
        if (((Comparable)this.array[j]).compareTo(this.array[i]) < 0) {
            Object tmp = this.array[i];
            this.array[i] = this.array[j];
            this.array[j] = tmp;
            this.reverse.flip(j);
            return false;
        }
        return true;
    }

    protected boolean joinWithComparator(int i, int j) {
        if (this.comparator.compare(this.array[j], this.array[i]) < 0) {
            Object tmp = this.array[i];
            this.array[i] = this.array[j];
            this.array[j] = tmp;
            this.reverse.flip(j);
            return false;
        }
        return true;
    }

    @Override
    protected void fixup(int j) {
        int i;
        while (j > 0 && !this.join(i = this.dancestor(j), j)) {
            j = i;
        }
    }

    @Override
    protected void fixupWithComparator(int j) {
        int i;
        while (j > 0 && !this.joinWithComparator(i = this.dancestor(j), j)) {
            j = i;
        }
    }

    @Override
    protected void fixdown(int j) {
        int c;
        int k = 2 * j + (this.reverse.get(j) ? 0 : 1);
        while ((c = 2 * k + (this.reverse.get(k) ? 1 : 0)) < this.size) {
            k = c;
        }
        while (k != j) {
            this.join(j, k);
            k /= 2;
        }
    }

    @Override
    protected void fixdownWithComparator(int j) {
        int c;
        int k = 2 * j + (this.reverse.get(j) ? 0 : 1);
        while ((c = 2 * k + (this.reverse.get(k) ? 1 : 0)) < this.size) {
            k = c;
        }
        while (k != j) {
            this.joinWithComparator(j, k);
            k /= 2;
        }
    }
}

