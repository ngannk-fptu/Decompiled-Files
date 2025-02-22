/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

public abstract class Pack {
    public static short bigEndianToShort(byte[] byArray, int n) {
        int n2 = (byArray[n] & 0xFF) << 8;
        return (short)(n2 |= byArray[++n] & 0xFF);
    }

    public static int bigEndianToInt(byte[] byArray, int n) {
        int n2 = byArray[n] << 24;
        n2 |= (byArray[++n] & 0xFF) << 16;
        n2 |= (byArray[++n] & 0xFF) << 8;
        return n2 |= byArray[++n] & 0xFF;
    }

    public static void bigEndianToInt(byte[] byArray, int n, int[] nArray) {
        for (int i = 0; i < nArray.length; ++i) {
            nArray[i] = Pack.bigEndianToInt(byArray, n);
            n += 4;
        }
    }

    public static void bigEndianToInt(byte[] byArray, int n, int[] nArray, int n2, int n3) {
        for (int i = 0; i < n3; ++i) {
            nArray[n2 + i] = Pack.bigEndianToInt(byArray, n);
            n += 4;
        }
    }

    public static byte[] intToBigEndian(int n) {
        byte[] byArray = new byte[4];
        Pack.intToBigEndian(n, byArray, 0);
        return byArray;
    }

    public static void intToBigEndian(int n, byte[] byArray, int n2) {
        byArray[n2] = (byte)(n >>> 24);
        byArray[++n2] = (byte)(n >>> 16);
        byArray[++n2] = (byte)(n >>> 8);
        byArray[++n2] = (byte)n;
    }

    public static byte[] intToBigEndian(int[] nArray) {
        byte[] byArray = new byte[4 * nArray.length];
        Pack.intToBigEndian(nArray, byArray, 0);
        return byArray;
    }

    public static void intToBigEndian(int[] nArray, byte[] byArray, int n) {
        for (int i = 0; i < nArray.length; ++i) {
            Pack.intToBigEndian(nArray[i], byArray, n);
            n += 4;
        }
    }

    public static void intToBigEndian(int[] nArray, int n, int n2, byte[] byArray, int n3) {
        for (int i = 0; i < n2; ++i) {
            Pack.intToBigEndian(nArray[n + i], byArray, n3);
            n3 += 4;
        }
    }

    public static long bigEndianToLong(byte[] byArray, int n) {
        int n2 = Pack.bigEndianToInt(byArray, n);
        int n3 = Pack.bigEndianToInt(byArray, n + 4);
        return ((long)n2 & 0xFFFFFFFFL) << 32 | (long)n3 & 0xFFFFFFFFL;
    }

    public static void bigEndianToLong(byte[] byArray, int n, long[] lArray) {
        for (int i = 0; i < lArray.length; ++i) {
            lArray[i] = Pack.bigEndianToLong(byArray, n);
            n += 8;
        }
    }

    public static void bigEndianToLong(byte[] byArray, int n, long[] lArray, int n2, int n3) {
        for (int i = 0; i < n3; ++i) {
            lArray[n2 + i] = Pack.bigEndianToLong(byArray, n);
            n += 8;
        }
    }

    public static byte[] longToBigEndian(long l) {
        byte[] byArray = new byte[8];
        Pack.longToBigEndian(l, byArray, 0);
        return byArray;
    }

    public static void longToBigEndian(long l, byte[] byArray, int n) {
        Pack.intToBigEndian((int)(l >>> 32), byArray, n);
        Pack.intToBigEndian((int)(l & 0xFFFFFFFFL), byArray, n + 4);
    }

    public static byte[] longToBigEndian(long[] lArray) {
        byte[] byArray = new byte[8 * lArray.length];
        Pack.longToBigEndian(lArray, byArray, 0);
        return byArray;
    }

    public static void longToBigEndian(long[] lArray, byte[] byArray, int n) {
        for (int i = 0; i < lArray.length; ++i) {
            Pack.longToBigEndian(lArray[i], byArray, n);
            n += 8;
        }
    }

    public static void longToBigEndian(long[] lArray, int n, int n2, byte[] byArray, int n3) {
        for (int i = 0; i < n2; ++i) {
            Pack.longToBigEndian(lArray[n + i], byArray, n3);
            n3 += 8;
        }
    }

    public static void longToBigEndian(long l, byte[] byArray, int n, int n2) {
        for (int i = n2 - 1; i >= 0; --i) {
            byArray[i + n] = (byte)(l & 0xFFL);
            l >>>= 8;
        }
    }

    public static short littleEndianToShort(byte[] byArray, int n) {
        int n2 = byArray[n] & 0xFF;
        return (short)(n2 |= (byArray[++n] & 0xFF) << 8);
    }

    public static int littleEndianToInt(byte[] byArray, int n) {
        int n2 = byArray[n] & 0xFF;
        n2 |= (byArray[++n] & 0xFF) << 8;
        n2 |= (byArray[++n] & 0xFF) << 16;
        return n2 |= byArray[++n] << 24;
    }

    public static void littleEndianToInt(byte[] byArray, int n, int[] nArray) {
        for (int i = 0; i < nArray.length; ++i) {
            nArray[i] = Pack.littleEndianToInt(byArray, n);
            n += 4;
        }
    }

    public static void littleEndianToInt(byte[] byArray, int n, int[] nArray, int n2, int n3) {
        for (int i = 0; i < n3; ++i) {
            nArray[n2 + i] = Pack.littleEndianToInt(byArray, n);
            n += 4;
        }
    }

    public static int[] littleEndianToInt(byte[] byArray, int n, int n2) {
        int[] nArray = new int[n2];
        for (int i = 0; i < nArray.length; ++i) {
            nArray[i] = Pack.littleEndianToInt(byArray, n);
            n += 4;
        }
        return nArray;
    }

    public static byte[] shortToLittleEndian(short s) {
        byte[] byArray = new byte[2];
        Pack.shortToLittleEndian(s, byArray, 0);
        return byArray;
    }

    public static void shortToLittleEndian(short s, byte[] byArray, int n) {
        byArray[n] = (byte)s;
        byArray[++n] = (byte)(s >>> 8);
    }

    public static byte[] shortToBigEndian(short s) {
        byte[] byArray = new byte[2];
        Pack.shortToBigEndian(s, byArray, 0);
        return byArray;
    }

    public static void shortToBigEndian(short s, byte[] byArray, int n) {
        byArray[n] = (byte)(s >>> 8);
        byArray[++n] = (byte)s;
    }

    public static byte[] intToLittleEndian(int n) {
        byte[] byArray = new byte[4];
        Pack.intToLittleEndian(n, byArray, 0);
        return byArray;
    }

    public static void intToLittleEndian(int n, byte[] byArray, int n2) {
        byArray[n2] = (byte)n;
        byArray[++n2] = (byte)(n >>> 8);
        byArray[++n2] = (byte)(n >>> 16);
        byArray[++n2] = (byte)(n >>> 24);
    }

    public static byte[] intToLittleEndian(int[] nArray) {
        byte[] byArray = new byte[4 * nArray.length];
        Pack.intToLittleEndian(nArray, byArray, 0);
        return byArray;
    }

    public static void intToLittleEndian(int[] nArray, byte[] byArray, int n) {
        for (int i = 0; i < nArray.length; ++i) {
            Pack.intToLittleEndian(nArray[i], byArray, n);
            n += 4;
        }
    }

    public static void intToLittleEndian(int[] nArray, int n, int n2, byte[] byArray, int n3) {
        for (int i = 0; i < n2; ++i) {
            Pack.intToLittleEndian(nArray[n + i], byArray, n3);
            n3 += 4;
        }
    }

    public static long littleEndianToLong(byte[] byArray, int n) {
        int n2 = Pack.littleEndianToInt(byArray, n);
        int n3 = Pack.littleEndianToInt(byArray, n + 4);
        return ((long)n3 & 0xFFFFFFFFL) << 32 | (long)n2 & 0xFFFFFFFFL;
    }

    public static void littleEndianToLong(byte[] byArray, int n, long[] lArray) {
        for (int i = 0; i < lArray.length; ++i) {
            lArray[i] = Pack.littleEndianToLong(byArray, n);
            n += 8;
        }
    }

    public static void littleEndianToLong(byte[] byArray, int n, long[] lArray, int n2, int n3) {
        for (int i = 0; i < n3; ++i) {
            lArray[n2 + i] = Pack.littleEndianToLong(byArray, n);
            n += 8;
        }
    }

    public static byte[] longToLittleEndian(long l) {
        byte[] byArray = new byte[8];
        Pack.longToLittleEndian(l, byArray, 0);
        return byArray;
    }

    public static void longToLittleEndian(long l, byte[] byArray, int n) {
        Pack.intToLittleEndian((int)(l & 0xFFFFFFFFL), byArray, n);
        Pack.intToLittleEndian((int)(l >>> 32), byArray, n + 4);
    }

    public static byte[] longToLittleEndian(long[] lArray) {
        byte[] byArray = new byte[8 * lArray.length];
        Pack.longToLittleEndian(lArray, byArray, 0);
        return byArray;
    }

    public static void longToLittleEndian(long[] lArray, byte[] byArray, int n) {
        for (int i = 0; i < lArray.length; ++i) {
            Pack.longToLittleEndian(lArray[i], byArray, n);
            n += 8;
        }
    }

    public static void longToLittleEndian(long[] lArray, int n, int n2, byte[] byArray, int n3) {
        for (int i = 0; i < n2; ++i) {
            Pack.longToLittleEndian(lArray[n + i], byArray, n3);
            n3 += 8;
        }
    }
}

