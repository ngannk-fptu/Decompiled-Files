/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.util;

import java.lang.reflect.Array;

public final class ArrayUtils {
    public static <T> T[] combine(Class<T> type, T first, T second, T ... rest) {
        Object[] array = (Object[])Array.newInstance(type, rest.length + 2);
        array[0] = first;
        array[1] = second;
        System.arraycopy(rest, 0, array, 2, rest.length);
        return array;
    }

    public static Object[] combine(int size, Object[] ... arrays) {
        int offset = 0;
        Object[] target = new Object[size];
        for (Object[] arr : arrays) {
            System.arraycopy(arr, 0, target, offset, arr.length);
            offset += arr.length;
        }
        return target;
    }

    public static Object[] subarray(Object[] array, int startIndexInclusive, int endIndexExclusive) {
        int newSize = endIndexExclusive - startIndexInclusive;
        Class<?> type = array.getClass().getComponentType();
        if (newSize <= 0) {
            return (Object[])Array.newInstance(type, 0);
        }
        Object[] subarray = (Object[])Array.newInstance(type, newSize);
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    private ArrayUtils() {
    }
}

