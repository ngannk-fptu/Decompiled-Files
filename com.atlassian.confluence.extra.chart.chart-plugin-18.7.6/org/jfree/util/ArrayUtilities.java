/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.util;

import java.util.Arrays;

public class ArrayUtilities {
    private ArrayUtilities() {
    }

    public static float[][] clone(float[][] array) {
        if (array == null) {
            return null;
        }
        float[][] result = new float[array.length][];
        System.arraycopy(array, 0, result, 0, array.length);
        for (int i = 0; i < array.length; ++i) {
            float[] child = array[i];
            float[] copychild = new float[child.length];
            System.arraycopy(child, 0, copychild, 0, child.length);
            result[i] = copychild;
        }
        return result;
    }

    public static boolean equalReferencesInArrays(Object[] array1, Object[] array2) {
        if (array1 == null) {
            return array2 == null;
        }
        if (array2 == null) {
            return false;
        }
        if (array1.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array1.length; ++i) {
            if (array1[i] == null && array2[i] != null) {
                return false;
            }
            if (array2[i] == null && array1[i] != null) {
                return false;
            }
            if (array1[i] == array2[i]) continue;
            return false;
        }
        return true;
    }

    public static boolean equal(float[][] array1, float[][] array2) {
        if (array1 == null) {
            return array2 == null;
        }
        if (array2 == null) {
            return false;
        }
        if (array1.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array1.length; ++i) {
            if (Arrays.equals(array1[i], array2[i])) continue;
            return false;
        }
        return true;
    }

    public static boolean hasDuplicateItems(Object[] array) {
        for (int i = 0; i < array.length; ++i) {
            for (int j = 0; j < i; ++j) {
                Object o1 = array[i];
                Object o2 = array[j];
                if (o1 == null || o2 == null || !o1.equals(o2)) continue;
                return true;
            }
        }
        return false;
    }

    public static int compareVersionArrays(Comparable[] a1, Comparable[] a2) {
        int length = Math.min(a1.length, a2.length);
        for (int i = 0; i < length; ++i) {
            Comparable o1 = a1[i];
            Comparable o2 = a2[i];
            if (o1 == null && o2 == null) continue;
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
            int retval = o1.compareTo(o2);
            if (retval == 0) continue;
            return retval;
        }
        return 0;
    }
}

