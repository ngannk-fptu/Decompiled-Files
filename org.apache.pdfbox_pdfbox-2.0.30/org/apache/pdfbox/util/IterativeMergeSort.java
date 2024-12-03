/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.util;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

public final class IterativeMergeSort {
    private IterativeMergeSort() {
    }

    public static <T> void sort(List<T> list, Comparator<? super T> cmp) {
        if (list.size() < 2) {
            return;
        }
        Object[] arr = list.toArray();
        IterativeMergeSort.iterativeMergeSort(arr, cmp);
        ListIterator<T> i = list.listIterator();
        for (Object e : arr) {
            i.next();
            i.set(e);
        }
    }

    private static <T> void iterativeMergeSort(T[] arr, Comparator<? super T> cmp) {
        Object[] aux = (Object[])arr.clone();
        for (int blockSize = 1; blockSize < arr.length; blockSize <<= 1) {
            for (int start = 0; start < arr.length; start += blockSize << 1) {
                IterativeMergeSort.merge(arr, aux, start, start + blockSize, start + (blockSize << 1), cmp);
            }
        }
    }

    private static <T> void merge(T[] arr, T[] aux, int from, int mid, int to, Comparator<? super T> cmp) {
        if (mid >= arr.length) {
            return;
        }
        if (to > arr.length) {
            to = arr.length;
        }
        int i = from;
        int j = mid;
        for (int k = from; k < to; ++k) {
            aux[k] = i == mid ? arr[j++] : (j == to ? arr[i++] : (cmp.compare(arr[j], arr[i]) < 0 ? arr[j++] : arr[i++]));
        }
        System.arraycopy(aux, from, arr, from, to - from);
    }
}

