/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.collection;

import java.lang.reflect.Array;
import java.util.Arrays;

public final class ArrayUtils {
    private ArrayUtils() {
    }

    public static <T> T[] createCopy(T[] src) {
        return Arrays.copyOf(src, src.length);
    }

    public static <T> T[] remove(T[] src, T object) {
        int index = ArrayUtils.indexOf(src, object);
        if (index == -1) {
            return src;
        }
        Object[] dst = (Object[])Array.newInstance(src.getClass().getComponentType(), src.length - 1);
        System.arraycopy(src, 0, dst, 0, index);
        if (index < src.length - 1) {
            System.arraycopy(src, index + 1, dst, index, src.length - index - 1);
        }
        return dst;
    }

    public static <T> T[] append(T[] array1, T[] array2) {
        Object[] dst = (Object[])Array.newInstance(array1.getClass().getComponentType(), array1.length + array2.length);
        System.arraycopy(array1, 0, dst, 0, array1.length);
        System.arraycopy(array2, 0, dst, array1.length, array2.length);
        return dst;
    }

    public static <T> T[] replaceFirst(T[] src, T oldValue, T[] newValues) {
        int index = ArrayUtils.indexOf(src, oldValue);
        if (index == -1) {
            return src;
        }
        Object[] dst = (Object[])Array.newInstance(src.getClass().getComponentType(), src.length - 1 + newValues.length);
        System.arraycopy(src, 0, dst, 0, index);
        System.arraycopy(src, index + 1, dst, index + newValues.length, src.length - index - 1);
        System.arraycopy(newValues, 0, dst, index, newValues.length);
        return dst;
    }

    private static <T> int indexOf(T[] array, T object) {
        for (int k = 0; k < array.length; ++k) {
            if (array[k] != object) continue;
            return k;
        }
        return -1;
    }

    public static <T> void copyWithoutNulls(T[] src, T[] dst) {
        int skipped = 0;
        for (int i = 0; i < src.length; ++i) {
            T object = src[i];
            if (object == null) {
                ++skipped;
                continue;
            }
            dst[i - skipped] = object;
        }
    }

    public static <T> boolean contains(T[] array, T item) {
        for (T o : array) {
            if (!(o == null ? item == null : o.equals(item))) continue;
            return true;
        }
        return false;
    }

    public static <T> T getItemAtPositionOrNull(T[] array, int position) {
        if (array.length > position) {
            return array[position];
        }
        return null;
    }

    public static <T> void concat(T[] sourceFirst, T[] sourceSecond, T[] dest) {
        System.arraycopy(sourceFirst, 0, dest, 0, sourceFirst.length);
        System.arraycopy(sourceSecond, 0, dest, sourceFirst.length, sourceSecond.length);
    }

    public static void boundsCheck(int capacity, int index, int length) {
        if (capacity < 0 || index < 0 || length < 0 || index > capacity - length) {
            throw new IndexOutOfBoundsException(String.format("index=%d, length=%d, capacity=%d", index, length, capacity));
        }
    }
}

