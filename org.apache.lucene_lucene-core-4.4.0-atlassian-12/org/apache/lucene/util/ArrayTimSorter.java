/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.util.Comparator;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.TimSorter;

final class ArrayTimSorter<T>
extends TimSorter {
    private final Comparator<? super T> comparator;
    private final T[] arr;
    private final T[] tmp;

    public ArrayTimSorter(T[] arr, Comparator<? super T> comparator, int maxTempSlots) {
        super(maxTempSlots);
        this.arr = arr;
        this.comparator = comparator;
        if (maxTempSlots > 0) {
            Object[] tmp = new Object[maxTempSlots];
            this.tmp = tmp;
        } else {
            this.tmp = null;
        }
    }

    @Override
    protected int compare(int i, int j) {
        return this.comparator.compare(this.arr[i], this.arr[j]);
    }

    @Override
    protected void swap(int i, int j) {
        ArrayUtil.swap(this.arr, i, j);
    }

    @Override
    protected void copy(int src, int dest) {
        this.arr[dest] = this.arr[src];
    }

    @Override
    protected void save(int start, int len) {
        System.arraycopy(this.arr, start, this.tmp, 0, len);
    }

    @Override
    protected void restore(int src, int dest) {
        this.arr[dest] = this.tmp[src];
    }

    @Override
    protected int compareSaved(int i, int j) {
        return this.comparator.compare(this.tmp[i], this.arr[j]);
    }
}

