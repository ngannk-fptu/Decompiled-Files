/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

public final class BitUtil {
    private BitUtil() {
    }

    public static long pop_array(long[] arr, int wordOffset, int numWords) {
        long popCount = 0L;
        int end = wordOffset + numWords;
        for (int i = wordOffset; i < end; ++i) {
            popCount += (long)Long.bitCount(arr[i]);
        }
        return popCount;
    }

    public static long pop_intersect(long[] arr1, long[] arr2, int wordOffset, int numWords) {
        long popCount = 0L;
        int end = wordOffset + numWords;
        for (int i = wordOffset; i < end; ++i) {
            popCount += (long)Long.bitCount(arr1[i] & arr2[i]);
        }
        return popCount;
    }

    public static long pop_union(long[] arr1, long[] arr2, int wordOffset, int numWords) {
        long popCount = 0L;
        int end = wordOffset + numWords;
        for (int i = wordOffset; i < end; ++i) {
            popCount += (long)Long.bitCount(arr1[i] | arr2[i]);
        }
        return popCount;
    }

    public static long pop_andnot(long[] arr1, long[] arr2, int wordOffset, int numWords) {
        long popCount = 0L;
        int end = wordOffset + numWords;
        for (int i = wordOffset; i < end; ++i) {
            popCount += (long)Long.bitCount(arr1[i] & (arr2[i] ^ 0xFFFFFFFFFFFFFFFFL));
        }
        return popCount;
    }

    public static long pop_xor(long[] arr1, long[] arr2, int wordOffset, int numWords) {
        long popCount = 0L;
        int end = wordOffset + numWords;
        for (int i = wordOffset; i < end; ++i) {
            popCount += (long)Long.bitCount(arr1[i] ^ arr2[i]);
        }
        return popCount;
    }

    public static int nextHighestPowerOfTwo(int v) {
        --v;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        return ++v;
    }

    public static long nextHighestPowerOfTwo(long v) {
        --v;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        v |= v >> 32;
        return ++v;
    }
}

