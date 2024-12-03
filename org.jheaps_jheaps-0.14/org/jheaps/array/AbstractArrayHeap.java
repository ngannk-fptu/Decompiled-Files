/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.array;

import java.util.Comparator;
import java.util.NoSuchElementException;
import org.jheaps.annotations.ConstantTime;
import org.jheaps.annotations.LogarithmicTime;
import org.jheaps.array.AbstractArrayWeakHeap;

abstract class AbstractArrayHeap<K>
extends AbstractArrayWeakHeap<K> {
    private static final long serialVersionUID = 1L;

    public AbstractArrayHeap(Comparator<? super K> comparator, int capacity) {
        super(comparator, capacity);
    }

    @Override
    protected void initCapacity(int capacity) {
        this.array = new Object[capacity + 1];
    }

    @Override
    @ConstantTime
    public K findMin() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return (K)this.array[1];
    }

    @Override
    @LogarithmicTime(amortized=true)
    public void insert(K key) {
        if (key == null) {
            throw new NullPointerException("Null keys not permitted");
        }
        if (this.size == this.array.length - 1) {
            if (this.array.length == 1) {
                this.ensureCapacity(1);
            } else {
                this.ensureCapacity(2 * (this.array.length - 1));
            }
        }
        this.array[++this.size] = key;
        if (this.comparator == null) {
            this.fixup(this.size);
        } else {
            this.fixupWithComparator(this.size);
        }
    }

    @Override
    @LogarithmicTime(amortized=true)
    public K deleteMin() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        Object result = this.array[1];
        if (this.size == 1) {
            this.array[1] = null;
            this.size = 0;
        } else {
            this.array[1] = this.array[this.size];
            this.array[this.size] = null;
            --this.size;
            if (this.comparator == null) {
                this.fixdown(1);
            } else {
                this.fixdownWithComparator(1);
            }
        }
        int currentCapacity = this.array.length - 1;
        if (2 * this.minCapacity <= currentCapacity && 4 * this.size < currentCapacity) {
            this.ensureCapacity(currentCapacity / 2);
        }
        return (K)result;
    }
}

