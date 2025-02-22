/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

public final class QuickMath {
    private QuickMath() {
    }

    public static boolean isPowerOfTwo(long x) {
        return (x & x - 1L) == 0L;
    }

    public static int modPowerOfTwo(int a, int b) {
        return a & b - 1;
    }

    public static long modPowerOfTwo(long a, int b) {
        return a & (long)(b - 1);
    }

    public static int nextPowerOfTwo(int value) {
        return 1 << 32 - Integer.numberOfLeadingZeros(value - 1);
    }

    public static long nextPowerOfTwo(long value) {
        return 1L << 64 - Long.numberOfLeadingZeros(value - 1L);
    }

    public static int log2(int value) {
        return 31 - Integer.numberOfLeadingZeros(value);
    }

    public static int log2(long value) {
        return 63 - Long.numberOfLeadingZeros(value);
    }

    public static int divideByAndCeilToInt(double d, int k) {
        return (int)Math.ceil(d / (double)k);
    }

    public static long divideByAndCeilToLong(double d, int k) {
        return (long)Math.ceil(d / (double)k);
    }

    public static int divideByAndRoundToInt(double d, int k) {
        return (int)Math.rint(d / (double)k);
    }

    public static long divideByAndRoundToLong(double d, int k) {
        return (long)Math.rint(d / (double)k);
    }

    public static int normalize(int value, int factor) {
        return QuickMath.divideByAndCeilToInt(value, factor) * factor;
    }

    public static long normalize(long value, int factor) {
        return QuickMath.divideByAndCeilToLong(value, factor) * (long)factor;
    }

    public static String bytesToHex(byte[] in) {
        char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[in.length * 2];
        for (int j = 0; j < in.length; ++j) {
            int v = in[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0xF];
        }
        return new String(hexChars);
    }

    public static int compareIntegers(int i1, int i2) {
        if (i1 > i2) {
            return 1;
        }
        if (i2 > i1) {
            return -1;
        }
        return 0;
    }

    public static int compareLongs(long l1, long l2) {
        if (l1 > l2) {
            return 1;
        }
        if (l2 > l1) {
            return -1;
        }
        return 0;
    }
}

