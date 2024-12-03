/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.util;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class RadixSort {
    @Deprecated(since="1.5.2", forRemoval=true)
    public static int CUT_OFF = 40;
    private static final int MAX_DIGITS = 32;
    private static final int MAX_D = 4;
    private static final int SIZE_RADIX = 256;
    private static final int MASK = 255;
    private static int[] count = new int[256];

    public static void setCutOff(int cutOff) {
        CUT_OFF = cutOff;
    }

    private RadixSort() {
    }

    private static void radixSort(int[] array, int n, int[] tempArray, int[] cnt) {
        int d = 0;
        int shift = 0;
        while (d < 4) {
            int i;
            Arrays.fill(cnt, 0);
            for (i = 0; i < n; ++i) {
                int n2 = array[i] >> shift & 0xFF;
                cnt[n2] = cnt[n2] + 1;
            }
            for (i = 1; i < 256; ++i) {
                int n3 = i;
                cnt[n3] = cnt[n3] + cnt[i - 1];
            }
            for (i = n - 1; i >= 0; --i) {
                int n4 = array[i] >> shift & 0xFF;
                int n5 = cnt[n4] - 1;
                cnt[n4] = n5;
                tempArray[n5] = array[i];
            }
            System.arraycopy(tempArray, 0, array, 0, n);
            ++d;
            shift += 8;
        }
    }

    public static void sort(List<Integer> list) {
        if (list == null) {
            return;
        }
        int n = list.size();
        if (n <= CUT_OFF) {
            list.sort(null);
            return;
        }
        int[] array = new int[n];
        ListIterator<Integer> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            array[listIterator.nextIndex()] = listIterator.next();
        }
        RadixSort.radixSort(array, n, new int[n], count);
        listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            listIterator.next();
            listIterator.set(array[listIterator.previousIndex()]);
        }
    }
}

