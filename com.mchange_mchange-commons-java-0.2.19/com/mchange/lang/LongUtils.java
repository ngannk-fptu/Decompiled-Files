/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.lang;

import com.mchange.lang.ByteUtils;

public class LongUtils {
    private LongUtils() {
    }

    public static long longFromByteArray(byte[] byArray, int n) {
        long l = 0L;
        l |= (long)ByteUtils.toUnsigned(byArray[n + 0]) << 56;
        l |= (long)ByteUtils.toUnsigned(byArray[n + 1]) << 48;
        l |= (long)ByteUtils.toUnsigned(byArray[n + 2]) << 40;
        l |= (long)ByteUtils.toUnsigned(byArray[n + 3]) << 32;
        l |= (long)ByteUtils.toUnsigned(byArray[n + 4]) << 24;
        l |= (long)ByteUtils.toUnsigned(byArray[n + 5]) << 16;
        l |= (long)ByteUtils.toUnsigned(byArray[n + 6]) << 8;
        return l |= (long)ByteUtils.toUnsigned(byArray[n + 7]) << 0;
    }

    public static byte[] byteArrayFromLong(long l) {
        byte[] byArray = new byte[8];
        LongUtils.longIntoByteArray(l, 0, byArray);
        return byArray;
    }

    public static void longIntoByteArray(long l, int n, byte[] byArray) {
        byArray[n + 0] = (byte)(l >>> 56 & 0xFFL);
        byArray[n + 1] = (byte)(l >>> 48 & 0xFFL);
        byArray[n + 2] = (byte)(l >>> 40 & 0xFFL);
        byArray[n + 3] = (byte)(l >>> 32 & 0xFFL);
        byArray[n + 4] = (byte)(l >>> 24 & 0xFFL);
        byArray[n + 5] = (byte)(l >>> 16 & 0xFFL);
        byArray[n + 6] = (byte)(l >>> 8 & 0xFFL);
        byArray[n + 7] = (byte)(l >>> 0 & 0xFFL);
    }

    public static long longFromByteArrayLittleEndian(byte[] byArray, int n) {
        long l = 0L;
        l |= (long)ByteUtils.toUnsigned(byArray[n + 7]) << 56;
        l |= (long)ByteUtils.toUnsigned(byArray[n + 6]) << 48;
        l |= (long)ByteUtils.toUnsigned(byArray[n + 5]) << 40;
        l |= (long)ByteUtils.toUnsigned(byArray[n + 4]) << 32;
        l |= (long)ByteUtils.toUnsigned(byArray[n + 3]) << 24;
        l |= (long)ByteUtils.toUnsigned(byArray[n + 2]) << 16;
        l |= (long)ByteUtils.toUnsigned(byArray[n + 1]) << 8;
        return l |= (long)ByteUtils.toUnsigned(byArray[n + 0]) << 0;
    }

    public static void longIntoByteArrayLittleEndian(long l, int n, byte[] byArray) {
        byArray[n + 7] = (byte)(l >>> 56 & 0xFFL);
        byArray[n + 6] = (byte)(l >>> 48 & 0xFFL);
        byArray[n + 5] = (byte)(l >>> 40 & 0xFFL);
        byArray[n + 4] = (byte)(l >>> 32 & 0xFFL);
        byArray[n + 3] = (byte)(l >>> 24 & 0xFFL);
        byArray[n + 2] = (byte)(l >>> 16 & 0xFFL);
        byArray[n + 1] = (byte)(l >>> 8 & 0xFFL);
        byArray[n + 0] = (byte)(l >>> 0 & 0xFFL);
    }

    public static int fullHashLong(long l) {
        return LongUtils.hashLong(l);
    }

    public static int hashLong(long l) {
        return (int)l ^ (int)(l >>> 32);
    }
}

