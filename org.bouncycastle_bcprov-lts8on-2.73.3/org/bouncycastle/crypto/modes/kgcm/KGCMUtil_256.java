/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes.kgcm;

import org.bouncycastle.math.raw.Interleave;

public class KGCMUtil_256 {
    public static final int SIZE = 4;

    public static void add(long[] x, long[] y, long[] z) {
        z[0] = x[0] ^ y[0];
        z[1] = x[1] ^ y[1];
        z[2] = x[2] ^ y[2];
        z[3] = x[3] ^ y[3];
    }

    public static void copy(long[] x, long[] z) {
        z[0] = x[0];
        z[1] = x[1];
        z[2] = x[2];
        z[3] = x[3];
    }

    public static boolean equal(long[] x, long[] y) {
        long d = 0L;
        d |= x[0] ^ y[0];
        d |= x[1] ^ y[1];
        d |= x[2] ^ y[2];
        return (d |= x[3] ^ y[3]) == 0L;
    }

    public static void multiply(long[] x, long[] y, long[] z) {
        long x0 = x[0];
        long x1 = x[1];
        long x2 = x[2];
        long x3 = x[3];
        long y0 = y[0];
        long y1 = y[1];
        long y2 = y[2];
        long y3 = y[3];
        long z0 = 0L;
        long z1 = 0L;
        long z2 = 0L;
        long z3 = 0L;
        long z4 = 0L;
        for (int j = 0; j < 64; ++j) {
            long m0 = -(x0 & 1L);
            x0 >>>= 1;
            z0 ^= y0 & m0;
            z1 ^= y1 & m0;
            z2 ^= y2 & m0;
            z3 ^= y3 & m0;
            long m1 = -(x1 & 1L);
            x1 >>>= 1;
            z1 ^= y0 & m1;
            z2 ^= y1 & m1;
            z3 ^= y2 & m1;
            z4 ^= y3 & m1;
            long c = y3 >> 63;
            y3 = y3 << 1 | y2 >>> 63;
            y2 = y2 << 1 | y1 >>> 63;
            y1 = y1 << 1 | y0 >>> 63;
            y0 = y0 << 1 ^ c & 0x425L;
        }
        long y4 = y3;
        y3 = y2;
        y2 = y1;
        y1 = y0 ^ y4 >>> 62 ^ y4 >>> 59 ^ y4 >>> 54;
        y0 = y4 ^ y4 << 2 ^ y4 << 5 ^ y4 << 10;
        for (int j = 0; j < 64; ++j) {
            long m2 = -(x2 & 1L);
            x2 >>>= 1;
            z0 ^= y0 & m2;
            z1 ^= y1 & m2;
            z2 ^= y2 & m2;
            z3 ^= y3 & m2;
            long m3 = -(x3 & 1L);
            x3 >>>= 1;
            z1 ^= y0 & m3;
            z2 ^= y1 & m3;
            z3 ^= y2 & m3;
            z4 ^= y3 & m3;
            long c = y3 >> 63;
            y3 = y3 << 1 | y2 >>> 63;
            y2 = y2 << 1 | y1 >>> 63;
            y1 = y1 << 1 | y0 >>> 63;
            y0 = y0 << 1 ^ c & 0x425L;
        }
        z[0] = z0 ^= z4 ^ z4 << 2 ^ z4 << 5 ^ z4 << 10;
        z[1] = z1 ^= z4 >>> 62 ^ z4 >>> 59 ^ z4 >>> 54;
        z[2] = z2;
        z[3] = z3;
    }

    public static void multiplyX(long[] x, long[] z) {
        long x0 = x[0];
        long x1 = x[1];
        long x2 = x[2];
        long x3 = x[3];
        long m = x3 >> 63;
        z[0] = x0 << 1 ^ m & 0x425L;
        z[1] = x1 << 1 | x0 >>> 63;
        z[2] = x2 << 1 | x1 >>> 63;
        z[3] = x3 << 1 | x2 >>> 63;
    }

    public static void multiplyX8(long[] x, long[] z) {
        long x0 = x[0];
        long x1 = x[1];
        long x2 = x[2];
        long x3 = x[3];
        long c = x3 >>> 56;
        z[0] = x0 << 8 ^ c ^ c << 2 ^ c << 5 ^ c << 10;
        z[1] = x1 << 8 | x0 >>> 56;
        z[2] = x2 << 8 | x1 >>> 56;
        z[3] = x3 << 8 | x2 >>> 56;
    }

    public static void one(long[] z) {
        z[0] = 1L;
        z[1] = 0L;
        z[2] = 0L;
        z[3] = 0L;
    }

    public static void square(long[] x, long[] z) {
        long[] t = new long[8];
        for (int i = 0; i < 4; ++i) {
            Interleave.expand64To128(x[i], t, i << 1);
        }
        int j = 8;
        while (--j >= 4) {
            long n = t[j];
            int n2 = j - 4;
            t[n2] = t[n2] ^ (n ^ n << 2 ^ n << 5 ^ n << 10);
            int n3 = j - 4 + 1;
            t[n3] = t[n3] ^ (n >>> 62 ^ n >>> 59 ^ n >>> 54);
        }
        KGCMUtil_256.copy(t, z);
    }

    public static void x(long[] z) {
        z[0] = 2L;
        z[1] = 0L;
        z[2] = 0L;
        z[3] = 0L;
    }

    public static void zero(long[] z) {
        z[0] = 0L;
        z[1] = 0L;
        z[2] = 0L;
        z[3] = 0L;
    }
}

