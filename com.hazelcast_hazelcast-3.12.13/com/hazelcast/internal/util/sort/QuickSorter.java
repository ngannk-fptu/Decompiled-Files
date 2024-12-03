/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.sort;

public abstract class QuickSorter {
    public final void sort(long startIndex, long length) {
        this.quickSort(startIndex, length - 1L);
    }

    protected abstract void loadPivot(long var1);

    protected abstract boolean isLessThanPivot(long var1);

    protected abstract boolean isGreaterThanPivot(long var1);

    protected abstract void swap(long var1, long var3);

    private void quickSort(long lo, long hi) {
        if (lo >= hi) {
            return;
        }
        long p = this.partition(lo, hi);
        this.quickSort(lo, p);
        this.quickSort(p + 1L, hi);
    }

    private long partition(long lo, long hi) {
        this.loadPivot(lo + hi >>> 1);
        long i = lo - 1L;
        long j = hi + 1L;
        while (true) {
            if (this.isLessThanPivot(++i)) {
                continue;
            }
            while (this.isGreaterThanPivot(--j)) {
            }
            if (i >= j) {
                return j;
            }
            this.swap(i, j);
        }
    }
}

