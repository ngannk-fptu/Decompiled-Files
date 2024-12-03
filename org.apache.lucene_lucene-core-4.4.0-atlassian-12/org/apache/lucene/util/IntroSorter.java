/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import org.apache.lucene.util.Sorter;

public abstract class IntroSorter
extends Sorter {
    static int ceilLog2(int n) {
        return 32 - Integer.numberOfLeadingZeros(n - 1);
    }

    @Override
    public final void sort(int from, int to) {
        this.checkRange(from, to);
        this.quicksort(from, to, IntroSorter.ceilLog2(to - from));
    }

    void quicksort(int from, int to, int maxDepth) {
        if (to - from < 20) {
            this.insertionSort(from, to);
            return;
        }
        if (--maxDepth < 0) {
            this.heapSort(from, to);
            return;
        }
        int mid = from + to >>> 1;
        if (this.compare(from, mid) > 0) {
            this.swap(from, mid);
        }
        if (this.compare(mid, to - 1) > 0) {
            this.swap(mid, to - 1);
            if (this.compare(from, mid) > 0) {
                this.swap(from, mid);
            }
        }
        int left = from + 1;
        int right = to - 2;
        this.setPivot(mid);
        while (true) {
            if (this.comparePivot(right) < 0) {
                --right;
                continue;
            }
            while (left < right && this.comparePivot(left) >= 0) {
                ++left;
            }
            if (left >= right) break;
            this.swap(left, right);
            --right;
        }
        this.quicksort(from, left + 1, maxDepth);
        this.quicksort(left + 1, to, maxDepth);
    }

    protected abstract void setPivot(int var1);

    protected abstract int comparePivot(int var1);
}

