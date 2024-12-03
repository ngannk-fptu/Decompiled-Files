/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import org.apache.lucene.util.Sorter;

public abstract class InPlaceMergeSorter
extends Sorter {
    @Override
    public final void sort(int from, int to) {
        this.checkRange(from, to);
        this.mergeSort(from, to);
    }

    void mergeSort(int from, int to) {
        if (to - from < 20) {
            this.insertionSort(from, to);
        } else {
            int mid = from + to >>> 1;
            this.mergeSort(from, mid);
            this.mergeSort(mid, to);
            this.mergeInPlace(from, mid, to);
        }
    }
}

