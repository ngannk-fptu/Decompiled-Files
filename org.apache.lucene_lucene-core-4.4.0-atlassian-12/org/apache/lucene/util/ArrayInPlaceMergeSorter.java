/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.util.Comparator;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.InPlaceMergeSorter;

final class ArrayInPlaceMergeSorter<T>
extends InPlaceMergeSorter {
    private final T[] arr;
    private final Comparator<? super T> comparator;

    public ArrayInPlaceMergeSorter(T[] arr, Comparator<? super T> comparator) {
        this.arr = arr;
        this.comparator = comparator;
    }

    @Override
    protected int compare(int i, int j) {
        return this.comparator.compare(this.arr[i], this.arr[j]);
    }

    @Override
    protected void swap(int i, int j) {
        ArrayUtil.swap(this.arr, i, j);
    }
}

