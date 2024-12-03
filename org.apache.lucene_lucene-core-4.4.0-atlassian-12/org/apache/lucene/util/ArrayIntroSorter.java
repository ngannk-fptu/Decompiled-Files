/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.util.Comparator;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.IntroSorter;

final class ArrayIntroSorter<T>
extends IntroSorter {
    private final T[] arr;
    private final Comparator<? super T> comparator;
    private T pivot;

    public ArrayIntroSorter(T[] arr, Comparator<? super T> comparator) {
        this.arr = arr;
        this.comparator = comparator;
        this.pivot = null;
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
    protected void setPivot(int i) {
        this.pivot = this.arr[i];
    }

    @Override
    protected int comparePivot(int i) {
        return this.comparator.compare(this.pivot, this.arr[i]);
    }
}

