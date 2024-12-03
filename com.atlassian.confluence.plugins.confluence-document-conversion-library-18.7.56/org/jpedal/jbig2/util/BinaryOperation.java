/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.util;

public class BinaryOperation {
    public static final int LEFT_SHIFT = 0;
    public static final int RIGHT_SHIFT = 1;

    public static int getInt32(short[] sArray) {
        return sArray[0] << 24 | sArray[1] << 16 | sArray[2] << 8 | sArray[3];
    }

    public static int getInt16(short[] sArray) {
        return sArray[0] << 8 | sArray[1];
    }

    public static long bit32Shift(long l, int n, int n2) {
        l = n2 == 0 ? (l <<= n) : (l >>= n);
        long l2 = 0xFFFFFFFFL;
        return l & l2;
    }

    public static int bit8Shift(int n, int n2, int n3) {
        n = n3 == 0 ? (n <<= n2) : (n >>= n2);
        int n4 = 255;
        return n & n4;
    }

    public static int getInt32(byte[] byArray) {
        return byArray[0] << 24 | byArray[1] << 16 | byArray[2] << 8 | byArray[3];
    }
}

