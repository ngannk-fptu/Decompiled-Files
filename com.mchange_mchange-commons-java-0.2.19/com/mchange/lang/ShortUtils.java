/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.lang;

import com.mchange.lang.ByteUtils;

public class ShortUtils {
    public static final int UNSIGNED_MAX_VALUE = 65535;

    public static short shortFromByteArray(byte[] byArray, int n) {
        int n2 = 0;
        n2 |= ByteUtils.toUnsigned(byArray[n + 0]) << 8;
        return (short)(n2 |= ByteUtils.toUnsigned(byArray[n + 1]) << 0);
    }

    public static byte[] byteArrayFromShort(short s) {
        byte[] byArray = new byte[2];
        ShortUtils.shortIntoByteArray(s, 0, byArray);
        return byArray;
    }

    public static void shortIntoByteArray(short s, int n, byte[] byArray) {
        byArray[n + 0] = (byte)(s >>> 8 & 0xFF);
        byArray[n + 1] = (byte)(s >>> 0 & 0xFF);
    }

    public static int toUnsigned(short n) {
        return n < 0 ? 65536 + n : n;
    }

    private ShortUtils() {
    }
}

