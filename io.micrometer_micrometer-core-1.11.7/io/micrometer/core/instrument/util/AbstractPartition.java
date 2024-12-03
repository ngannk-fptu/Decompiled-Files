/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.util;

import java.util.AbstractList;
import java.util.List;

public abstract class AbstractPartition<T>
extends AbstractList<List<T>> {
    final List<T> delegate;
    final int partitionSize;
    final int partitionCount;

    protected AbstractPartition(List<T> delegate, int partitionSize) {
        if (delegate == null) {
            throw new NullPointerException("delegate == null");
        }
        this.delegate = delegate;
        if (partitionSize < 1) {
            throw new IllegalArgumentException("partitionSize < 1");
        }
        this.partitionSize = partitionSize;
        this.partitionCount = AbstractPartition.partitionCount(delegate, partitionSize);
    }

    @Override
    public List<T> get(int index) {
        int start = index * this.partitionSize;
        int end = Math.min(start + this.partitionSize, this.delegate.size());
        return this.delegate.subList(start, end);
    }

    @Override
    public int size() {
        return this.partitionCount;
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    static <T> int partitionCount(List<T> delegate, int partitionSize) {
        int result = delegate.size() / partitionSize;
        return delegate.size() % partitionSize == 0 ? result : result + 1;
    }
}

