/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.array;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.NoSuchElementException;
import org.jheaps.ValueHeap;
import org.jheaps.annotations.ConstantTime;
import org.jheaps.annotations.LogarithmicTime;

public class BinaryArrayIntegerValueHeap<V>
implements ValueHeap<Integer, V>,
Serializable {
    private static final long serialVersionUID = 1L;
    public static final int DEFAULT_HEAP_CAPACITY = 16;
    private static final int SUP_KEY = Integer.MAX_VALUE;
    private static final int INF_KEY = Integer.MIN_VALUE;
    private static final int MAX_HEAP_CAPACITY = 0x7FFFFFF6;
    private static final int MIN_HEAP_CAPACITY = 0;
    private Elem<V>[] array;
    private int size;
    private int minCapacity;

    public BinaryArrayIntegerValueHeap() {
        this(16);
    }

    public BinaryArrayIntegerValueHeap(int capacity) {
        this.checkCapacity(capacity);
        this.minCapacity = Math.max(capacity, 16);
        this.array = (Elem[])Array.newInstance(Elem.class, this.minCapacity + 2);
        this.array[0] = new Elem<Object>(Integer.MIN_VALUE, null);
        for (int i = 1; i < this.minCapacity + 2; ++i) {
            this.array[i] = new Elem<Object>(Integer.MAX_VALUE, null);
        }
        this.size = 0;
    }

    @Override
    @ConstantTime
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    @ConstantTime
    public long size() {
        return this.size;
    }

    @Override
    @ConstantTime
    public void clear() {
        this.size = 0;
    }

    @Override
    public Comparator<? super Integer> comparator() {
        return null;
    }

    @Override
    @ConstantTime
    public Integer findMin() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.array[1].key;
    }

    @Override
    @ConstantTime
    public V findMinValue() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.array[1].value;
    }

    @Override
    @LogarithmicTime
    public void insert(Integer key, V value) {
        if (key == null) {
            throw new NullPointerException("Null keys not permitted");
        }
        if (this.size == this.array.length - 2) {
            if (this.array.length == 2) {
                this.ensureCapacity(1);
            } else {
                this.ensureCapacity(2 * (this.array.length - 2));
            }
        }
        ++this.size;
        int hole = this.size;
        int pred = hole >> 1;
        Elem<V> predElem = this.array[pred];
        while (predElem.key > key) {
            this.array[hole].key = predElem.key;
            this.array[hole].value = predElem.value;
            hole = pred;
            predElem = this.array[pred >>= 1];
        }
        this.array[hole].key = key;
        this.array[hole].value = value;
    }

    @Override
    @LogarithmicTime
    public void insert(Integer key) {
        this.insert(key, (V)null);
    }

    @Override
    @LogarithmicTime
    public Integer deleteMin() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        Integer result = this.array[1].key;
        int hole = 1;
        int sz = this.size;
        for (int succ = 2; succ < sz; succ <<= 1) {
            int key1 = this.array[succ].key;
            int key2 = this.array[succ + 1].key;
            if (key1 > key2) {
                this.array[hole].key = key2;
                this.array[hole].value = this.array[++succ].value;
            } else {
                this.array[hole].key = key1;
                this.array[hole].value = this.array[succ].value;
            }
            hole = succ;
        }
        int bubble = this.array[sz].key;
        int pred = hole >> 1;
        while (this.array[pred].key > bubble) {
            this.array[hole].key = this.array[pred].key;
            this.array[hole].value = this.array[pred].value;
            hole = pred;
            pred >>= 1;
        }
        this.array[hole].key = bubble;
        this.array[hole].value = this.array[sz].value;
        this.array[this.size].key = Integer.MAX_VALUE;
        this.array[this.size].value = null;
        this.size = sz - 1;
        int currentCapacity = this.array.length - 2;
        if (2 * this.minCapacity <= currentCapacity && 4 * this.size < currentCapacity) {
            this.ensureCapacity(currentCapacity / 2);
        }
        return result;
    }

    private void ensureCapacity(int capacity) {
        this.checkCapacity(capacity);
        Elem[] newArray = (Elem[])Array.newInstance(Elem.class, capacity + 2);
        if (newArray.length >= this.array.length) {
            System.arraycopy(this.array, 0, newArray, 0, this.array.length);
            for (int i = this.array.length; i < newArray.length; ++i) {
                newArray[i] = new Elem<Object>(Integer.MAX_VALUE, null);
            }
        } else {
            System.arraycopy(this.array, 0, newArray, 0, newArray.length);
        }
        this.array = newArray;
    }

    private void checkCapacity(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Heap capacity must be >= 0");
        }
        if (capacity > 0x7FFFFFF6) {
            throw new IllegalArgumentException("Heap capacity too large");
        }
    }

    private static class Elem<V>
    implements Serializable {
        private static final long serialVersionUID = 1L;
        int key;
        V value;

        public Elem(Integer key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}

