/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

public abstract class SorterTemplate {
    private static final int MERGESORT_THRESHOLD = 12;
    private static final int QUICKSORT_THRESHOLD = 7;

    protected abstract void swap(int var1, int var2);

    protected abstract int compare(int var1, int var2);

    protected abstract void setPivot(int var1);

    protected abstract int comparePivot(int var1);

    public final void insertionSort(int lo, int hi) {
        for (int i = lo + 1; i <= hi; ++i) {
            for (int j = i; j > lo && this.compare(j - 1, j) > 0; --j) {
                this.swap(j - 1, j);
            }
        }
    }

    public final void quickSort(int lo, int hi) {
        if (hi <= lo) {
            return;
        }
        this.quickSort(lo, hi, 32 - Integer.numberOfLeadingZeros(hi - lo) << 1);
    }

    private void quickSort(int lo, int hi, int maxDepth) {
        int diff = hi - lo;
        if (diff <= 7) {
            this.insertionSort(lo, hi);
            return;
        }
        if (--maxDepth == 0) {
            this.mergeSort(lo, hi);
            return;
        }
        int mid = lo + (diff >>> 1);
        if (this.compare(lo, mid) > 0) {
            this.swap(lo, mid);
        }
        if (this.compare(mid, hi) > 0) {
            this.swap(mid, hi);
            if (this.compare(lo, mid) > 0) {
                this.swap(lo, mid);
            }
        }
        int left = lo + 1;
        int right = hi - 1;
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
        this.quickSort(lo, left, maxDepth);
        this.quickSort(left + 1, hi, maxDepth);
    }

    public final void mergeSort(int lo, int hi) {
        int diff = hi - lo;
        if (diff <= 12) {
            this.insertionSort(lo, hi);
            return;
        }
        int mid = lo + (diff >>> 1);
        this.mergeSort(lo, mid);
        this.mergeSort(mid, hi);
        this.merge(lo, mid, hi, mid - lo, hi - mid);
    }

    private void merge(int lo, int pivot, int hi, int len1, int len2) {
        int len22;
        int second_cut;
        int first_cut;
        int len11;
        if (len1 == 0 || len2 == 0) {
            return;
        }
        if (len1 + len2 == 2) {
            if (this.compare(pivot, lo) < 0) {
                this.swap(pivot, lo);
            }
            return;
        }
        if (len1 > len2) {
            len11 = len1 >>> 1;
            first_cut = lo + len11;
            second_cut = this.lower(pivot, hi, first_cut);
            len22 = second_cut - pivot;
        } else {
            len22 = len2 >>> 1;
            second_cut = pivot + len22;
            first_cut = this.upper(lo, pivot, second_cut);
            len11 = first_cut - lo;
        }
        this.rotate(first_cut, pivot, second_cut);
        int new_mid = first_cut + len22;
        this.merge(lo, first_cut, new_mid, len11, len22);
        this.merge(new_mid, second_cut, hi, len1 - len11, len2 - len22);
    }

    private void rotate(int lo, int mid, int hi) {
        int lot = lo;
        int hit = mid - 1;
        while (lot < hit) {
            this.swap(lot++, hit--);
        }
        lot = mid;
        hit = hi - 1;
        while (lot < hit) {
            this.swap(lot++, hit--);
        }
        lot = lo;
        hit = hi - 1;
        while (lot < hit) {
            this.swap(lot++, hit--);
        }
    }

    private int lower(int lo, int hi, int val) {
        int len = hi - lo;
        while (len > 0) {
            int half = len >>> 1;
            int mid = lo + half;
            if (this.compare(mid, val) < 0) {
                lo = mid + 1;
                len = len - half - 1;
                continue;
            }
            len = half;
        }
        return lo;
    }

    private int upper(int lo, int hi, int val) {
        int len = hi - lo;
        while (len > 0) {
            int half = len >>> 1;
            int mid = lo + half;
            if (this.compare(val, mid) < 0) {
                len = half;
                continue;
            }
            lo = mid + 1;
            len = len - half - 1;
        }
        return lo;
    }
}

