/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.util.Comparator;

public final class Sorting {
    private static final int SMALLSORT = 16;
    private static final Sorting sorting = new Sorting();

    private Sorting() {
    }

    public static Sorting get() {
        return sorting;
    }

    public void insertionSort(Object[] a, Comparator<Object> cmp) {
        Sorting.insertionSort(a, 0, a.length - 1, cmp);
    }

    private static void insertionSort(Object[] a, int start, int end, Comparator<Object> cmp) {
        for (int i = start; i <= end; ++i) {
            Object x = a[i];
            for (int j = i - 1; j >= start && cmp.compare(a[j], x) > 0; --j) {
                a[j + 1] = a[j];
            }
            a[j + 1] = x;
        }
    }

    public void hybridSort(Object[] a, Comparator<Object> cmp) {
        this.hybridSort(a, 0, a.length - 1, cmp, Sorting.log2(a.length) * 2);
    }

    private void hybridSort(Object[] a, int start, int end, Comparator<Object> cmp, int maxdepth) {
        if (start < end) {
            if (maxdepth == 0 || end - start <= 16) {
                Sorting.insertionSort(a, start, end, cmp);
            } else {
                int p = this.partition(a, start, end, cmp);
                this.hybridSort(a, start, p, cmp, maxdepth - 1);
                this.hybridSort(a, p + 1, end, cmp, maxdepth - 1);
            }
        }
    }

    private int partition(Object[] a, int start, int end, Comparator<Object> cmp) {
        int p = this.median(a, start, end, cmp);
        Object pivot = a[p];
        a[p] = a[start];
        a[start] = pivot;
        int i = start;
        int j = end + 1;
        while (true) {
            if (cmp.compare(a[++i], pivot) < 0 && i != end) {
                continue;
            }
            while (cmp.compare(a[--j], pivot) >= 0 && j != start) {
            }
            if (i >= j) break;
            Sorting.swap(a, i, j);
        }
        Sorting.swap(a, start, j);
        return j;
    }

    private static void swap(Object[] a, int l, int h) {
        Object tmp = a[l];
        a[l] = a[h];
        a[h] = tmp;
    }

    private static int log2(int n) {
        return (int)(Math.log10(n) / Math.log10(2.0));
    }

    public int median(Object[] a, int start, int end, Comparator<Object> cmp) {
        int smallest = start;
        int m = start + (end - start) / 2;
        if (cmp.compare(a[smallest], a[m]) > 0) {
            smallest = m;
        }
        if (cmp.compare(a[smallest], a[end]) > 0) {
            smallest = end;
        }
        if (smallest == start) {
            return cmp.compare(a[m], a[end]) < 0 ? m : end;
        }
        if (smallest == m) {
            return cmp.compare(a[start], a[end]) < 0 ? start : end;
        }
        return cmp.compare(a[start], a[m]) < 0 ? start : m;
    }
}

