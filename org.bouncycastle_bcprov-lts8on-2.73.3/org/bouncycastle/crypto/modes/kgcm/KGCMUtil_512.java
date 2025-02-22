/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes.kgcm;

import org.bouncycastle.math.raw.Interleave;

public class KGCMUtil_512 {
    public static final int SIZE = 8;

    public static void add(long[] x, long[] y, long[] z) {
        z[0] = x[0] ^ y[0];
        z[1] = x[1] ^ y[1];
        z[2] = x[2] ^ y[2];
        z[3] = x[3] ^ y[3];
        z[4] = x[4] ^ y[4];
        z[5] = x[5] ^ y[5];
        z[6] = x[6] ^ y[6];
        z[7] = x[7] ^ y[7];
    }

    public static void copy(long[] x, long[] z) {
        z[0] = x[0];
        z[1] = x[1];
        z[2] = x[2];
        z[3] = x[3];
        z[4] = x[4];
        z[5] = x[5];
        z[6] = x[6];
        z[7] = x[7];
    }

    public static boolean equal(long[] x, long[] y) {
        long d = 0L;
        d |= x[0] ^ y[0];
        d |= x[1] ^ y[1];
        d |= x[2] ^ y[2];
        d |= x[3] ^ y[3];
        d |= x[4] ^ y[4];
        d |= x[5] ^ y[5];
        d |= x[6] ^ y[6];
        return (d |= x[7] ^ y[7]) == 0L;
    }

    public static void multiply(long[] x, long[] y, long[] z) {
        long y0 = y[0];
        long y1 = y[1];
        long y2 = y[2];
        long y3 = y[3];
        long y4 = y[4];
        long y5 = y[5];
        long y6 = y[6];
        long y7 = y[7];
        long z0 = 0L;
        long z1 = 0L;
        long z2 = 0L;
        long z3 = 0L;
        long z4 = 0L;
        long z5 = 0L;
        long z6 = 0L;
        long z7 = 0L;
        long z8 = 0L;
        for (int i = 0; i < 8; i += 2) {
            long x0 = x[i];
            long x1 = x[i + 1];
            for (int j = 0; j < 64; ++j) {
                long m0 = -(x0 & 1L);
                x0 >>>= 1;
                z0 ^= y0 & m0;
                z1 ^= y1 & m0;
                z2 ^= y2 & m0;
                z3 ^= y3 & m0;
                z4 ^= y4 & m0;
                z5 ^= y5 & m0;
                z6 ^= y6 & m0;
                z7 ^= y7 & m0;
                long m1 = -(x1 & 1L);
                x1 >>>= 1;
                z1 ^= y0 & m1;
                z2 ^= y1 & m1;
                z3 ^= y2 & m1;
                z4 ^= y3 & m1;
                z5 ^= y4 & m1;
                z6 ^= y5 & m1;
                z7 ^= y6 & m1;
                z8 ^= y7 & m1;
                long c = y7 >> 63;
                y7 = y7 << 1 | y6 >>> 63;
                y6 = y6 << 1 | y5 >>> 63;
                y5 = y5 << 1 | y4 >>> 63;
                y4 = y4 << 1 | y3 >>> 63;
                y3 = y3 << 1 | y2 >>> 63;
                y2 = y2 << 1 | y1 >>> 63;
                y1 = y1 << 1 | y0 >>> 63;
                y0 = y0 << 1 ^ c & 0x125L;
            }
            long y8 = y7;
            y7 = y6;
            y6 = y5;
            y5 = y4;
            y4 = y3;
            y3 = y2;
            y2 = y1;
            y1 = y0 ^ y8 >>> 62 ^ y8 >>> 59 ^ y8 >>> 56;
            y0 = y8 ^ y8 << 2 ^ y8 << 5 ^ y8 << 8;
        }
        z[0] = z0 ^= z8 ^ z8 << 2 ^ z8 << 5 ^ z8 << 8;
        z[1] = z1 ^= z8 >>> 62 ^ z8 >>> 59 ^ z8 >>> 56;
        z[2] = z2;
        z[3] = z3;
        z[4] = z4;
        z[5] = z5;
        z[6] = z6;
        z[7] = z7;
    }

    public static void multiplyX(long[] x, long[] z) {
        long x0 = x[0];
        long x1 = x[1];
        long x2 = x[2];
        long x3 = x[3];
        long x4 = x[4];
        long x5 = x[5];
        long x6 = x[6];
        long x7 = x[7];
        long m = x7 >> 63;
        z[0] = x0 << 1 ^ m & 0x125L;
        z[1] = x1 << 1 | x0 >>> 63;
        z[2] = x2 << 1 | x1 >>> 63;
        z[3] = x3 << 1 | x2 >>> 63;
        z[4] = x4 << 1 | x3 >>> 63;
        z[5] = x5 << 1 | x4 >>> 63;
        z[6] = x6 << 1 | x5 >>> 63;
        z[7] = x7 << 1 | x6 >>> 63;
    }

    public static void multiplyX8(long[] x, long[] z) {
        long x0 = x[0];
        long x1 = x[1];
        long x2 = x[2];
        long x3 = x[3];
        long x4 = x[4];
        long x5 = x[5];
        long x6 = x[6];
        long x7 = x[7];
        long c = x7 >>> 56;
        z[0] = x0 << 8 ^ c ^ c << 2 ^ c << 5 ^ c << 8;
        z[1] = x1 << 8 | x0 >>> 56;
        z[2] = x2 << 8 | x1 >>> 56;
        z[3] = x3 << 8 | x2 >>> 56;
        z[4] = x4 << 8 | x3 >>> 56;
        z[5] = x5 << 8 | x4 >>> 56;
        z[6] = x6 << 8 | x5 >>> 56;
        z[7] = x7 << 8 | x6 >>> 56;
    }

    public static void one(long[] z) {
        z[0] = 1L;
        z[1] = 0L;
        z[2] = 0L;
        z[3] = 0L;
        z[4] = 0L;
        z[5] = 0L;
        z[6] = 0L;
        z[7] = 0L;
    }

    public static void square(long[] x, long[] z) {
        long[] t = new long[16];
        for (int i = 0; i < 8; ++i) {
            Interleave.expand64To128(x[i], t, i << 1);
        }
        int j = 16;
        while (--j >= 8) {
            long n = t[j];
            int n2 = j - 8;
            t[n2] = t[n2] ^ (n ^ n << 2 ^ n << 5 ^ n << 8);
            int n3 = j - 8 + 1;
            t[n3] = t[n3] ^ (n >>> 62 ^ n >>> 59 ^ n >>> 56);
        }
        KGCMUtil_512.copy(t, z);
    }

    public static void x(long[] z) {
        z[0] = 2L;
        z[1] = 0L;
        z[2] = 0L;
        z[3] = 0L;
        z[4] = 0L;
        z[5] = 0L;
        z[6] = 0L;
        z[7] = 0L;
    }

    public static void zero(long[] z) {
        z[0] = 0L;
        z[1] = 0L;
        z[2] = 0L;
        z[3] = 0L;
        z[4] = 0L;
        z[5] = 0L;
        z[6] = 0L;
        z[7] = 0L;
    }
}

