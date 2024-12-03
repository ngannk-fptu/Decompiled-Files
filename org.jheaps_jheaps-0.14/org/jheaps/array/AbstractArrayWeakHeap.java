/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.array;

import java.io.Serializable;
import java.util.Comparator;
import org.jheaps.Heap;
import org.jheaps.annotations.ConstantTime;

abstract class AbstractArrayWeakHeap<K>
implements Heap<K>,
Serializable {
    private static final long serialVersionUID = 1L;
    protected static final int MAX_HEAP_CAPACITY = 0x7FFFFFF6;
    protected static final int MIN_HEAP_CAPACITY = 0;
    protected static final int DOWNSIZING_MIN_HEAP_CAPACITY = 16;
    protected final Comparator<? super K> comparator;
    protected K[] array;
    protected int size;
    protected final int minCapacity;

    public AbstractArrayWeakHeap(Comparator<? super K> comparator, int capacity) {
        this.checkCapacity(capacity);
        this.size = 0;
        this.comparator = comparator;
        this.minCapacity = Math.max(capacity, 16);
        this.initCapacity(this.minCapacity);
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
    public Comparator<? super K> comparator() {
        return this.comparator;
    }

    @Override
    @ConstantTime
    public void clear() {
        this.size = 0;
    }

    protected final void checkCapacity(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Heap capacity must be >= 0");
        }
        if (capacity > 0x7FFFFFF6) {
            throw new IllegalArgumentException("Heap capacity too large");
        }
    }

    protected abstract void initCapacity(int var1);

    protected abstract void ensureCapacity(int var1);

    protected abstract void fixup(int var1);

    protected abstract void fixupWithComparator(int var1);

    protected abstract void fixdown(int var1);

    protected abstract void fixdownWithComparator(int var1);
}

