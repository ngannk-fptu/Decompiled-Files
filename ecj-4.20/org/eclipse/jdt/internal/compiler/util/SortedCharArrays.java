/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.util;

import java.util.Comparator;

public class SortedCharArrays {
    public static final int BINARY_SEARCH_THRESHOLD = 16;
    public static final Comparator<char[]> CHAR_ARR_COMPARATOR = SortedCharArrays::compareCharArray;
    public static final Comparator<char[][]> CHAR_CHAR_ARR_COMPARATOR = SortedCharArrays::compareCharCharArray;

    public static <T> T[] insertIntoArray(T[] src, T[] target, T entry, int idx, int currentCount) {
        if (src != target) {
            System.arraycopy(src, 0, target, 0, idx);
            System.arraycopy(src, idx, target, idx + 1, currentCount - idx);
        } else if (idx != currentCount) {
            System.arraycopy(src, idx, target, idx + 1, currentCount - idx);
        }
        target[idx] = entry;
        return target;
    }

    public static int compareCharArray(char[] left, char[] right) {
        if (left == right) {
            return 0;
        }
        int l = left.length;
        int diff = right.length - l;
        if (diff == 0) {
            int i = 0;
            while (i < l && (diff = left[i] - right[i]) == 0) {
                ++i;
            }
        }
        return diff;
    }

    public static int compareCharCharArray(char[][] left, char[][] right) {
        if (left == right) {
            return 0;
        }
        int l = left.length;
        int diff = right.length - l;
        if (diff == 0) {
            int i = 0;
            while (i < l && (diff = SortedCharArrays.compareCharArray(left[i], right[i])) == 0) {
                ++i;
            }
        }
        return diff;
    }
}

