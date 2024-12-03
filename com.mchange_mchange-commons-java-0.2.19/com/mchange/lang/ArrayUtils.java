/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.lang;

public final class ArrayUtils {
    public static int indexOf(Object[] objectArray, Object object) {
        int n = objectArray.length;
        for (int i = 0; i < n; ++i) {
            if (!object.equals(objectArray[i])) continue;
            return i;
        }
        return -1;
    }

    public static int identityIndexOf(Object[] objectArray, Object object) {
        int n = objectArray.length;
        for (int i = 0; i < n; ++i) {
            if (object != objectArray[i]) continue;
            return i;
        }
        return -1;
    }

    public static int hashAll(Object[] objectArray) {
        int n = 0;
        for (Object object : objectArray) {
            if (object == null) continue;
            n ^= object.hashCode();
        }
        return n;
    }

    public static int hashAll(int[] nArray) {
        int n = 0;
        int n2 = nArray.length;
        for (int i = 0; i < n2; ++i) {
            n ^= nArray[i];
        }
        return n;
    }

    public static boolean startsWith(byte[] byArray, byte[] byArray2) {
        int n = byArray.length;
        int n2 = byArray2.length;
        if (n < n2) {
            return false;
        }
        for (int i = 0; i < n2; ++i) {
            if (byArray[i] == byArray2[i]) continue;
            return false;
        }
        return true;
    }

    private ArrayUtils() {
    }
}

