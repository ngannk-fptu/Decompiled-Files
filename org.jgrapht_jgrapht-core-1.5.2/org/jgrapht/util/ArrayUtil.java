/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.util;

public class ArrayUtil {
    private ArrayUtil() {
    }

    public static final <V> void reverse(V[] arr, int from, int to) {
        int i = from;
        for (int j = to; i < j; ++i, --j) {
            ArrayUtil.swap(arr, i, j);
        }
    }

    public static final void reverse(int[] arr, int from, int to) {
        int i = from;
        for (int j = to; i < j; ++i, --j) {
            int tmp = arr[j];
            arr[j] = arr[i];
            arr[i] = tmp;
        }
    }

    public static final <V> void swap(V[] arr, int i, int j) {
        V tmp = arr[j];
        arr[j] = arr[i];
        arr[i] = tmp;
    }
}

