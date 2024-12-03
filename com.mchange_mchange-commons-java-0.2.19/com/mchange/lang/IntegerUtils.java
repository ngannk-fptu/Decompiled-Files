/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.lang;

import com.mchange.lang.ByteUtils;

public final class IntegerUtils {
    public static final long UNSIGNED_MAX_VALUE = -1L;

    public static int parseInt(String string, int n) {
        if (string == null) {
            return n;
        }
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException numberFormatException) {
            return n;
        }
    }

    public static int parseInt(String string, int n, int n2) {
        if (string == null) {
            return n2;
        }
        try {
            return Integer.parseInt(string, n);
        }
        catch (NumberFormatException numberFormatException) {
            return n2;
        }
    }

    public static int intFromByteArray(byte[] byArray, int n) {
        int n2 = 0;
        n2 |= ByteUtils.unsignedPromote(byArray[n + 0]) << 24;
        n2 |= ByteUtils.unsignedPromote(byArray[n + 1]) << 16;
        n2 |= ByteUtils.unsignedPromote(byArray[n + 2]) << 8;
        return n2 |= ByteUtils.unsignedPromote(byArray[n + 3]) << 0;
    }

    public static byte[] byteArrayFromInt(int n) {
        byte[] byArray = new byte[4];
        IntegerUtils.intIntoByteArray(n, 0, byArray);
        return byArray;
    }

    public static void intIntoByteArray(int n, int n2, byte[] byArray) {
        byArray[n2 + 0] = (byte)(n >>> 24 & 0xFF);
        byArray[n2 + 1] = (byte)(n >>> 16 & 0xFF);
        byArray[n2 + 2] = (byte)(n >>> 8 & 0xFF);
        byArray[n2 + 3] = (byte)(n >>> 0 & 0xFF);
    }

    public static int intFromByteArrayLittleEndian(byte[] byArray, int n) {
        int n2 = 0;
        n2 |= ByteUtils.unsignedPromote(byArray[n + 3]) << 24;
        n2 |= ByteUtils.unsignedPromote(byArray[n + 2]) << 16;
        n2 |= ByteUtils.unsignedPromote(byArray[n + 1]) << 8;
        return n2 |= ByteUtils.unsignedPromote(byArray[n + 0]) << 0;
    }

    public static void intIntoByteArrayLittleEndian(int n, int n2, byte[] byArray) {
        byArray[n2 + 3] = (byte)(n >>> 24 & 0xFF);
        byArray[n2 + 2] = (byte)(n >>> 16 & 0xFF);
        byArray[n2 + 1] = (byte)(n >>> 8 & 0xFF);
        byArray[n2 + 0] = (byte)(n >>> 0 & 0xFF);
    }

    public static long toUnsigned(int n) {
        return 0xFFFFFFFFL & (long)n;
    }

    private IntegerUtils() {
    }
}

