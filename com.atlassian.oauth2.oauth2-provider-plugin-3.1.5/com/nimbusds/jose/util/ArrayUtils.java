/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.util;

import java.util.Arrays;

public class ArrayUtils {
    public static <T> T[] concat(T[] first, T[] ... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    private ArrayUtils() {
    }
}

