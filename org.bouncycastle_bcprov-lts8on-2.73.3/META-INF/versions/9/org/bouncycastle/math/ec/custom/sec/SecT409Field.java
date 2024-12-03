/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat448;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecT409Field {
    private static final long M25 = 0x1FFFFFFL;
    private static final long M59 = 0x7FFFFFFFFFFFFFFL;

    public static void add(long[] x, long[] y, long[] z) {
        z[0] = x[0] ^ y[0];
        z[1] = x[1] ^ y[1];
        z[2] = x[2] ^ y[2];
        z[3] = x[3] ^ y[3];
        z[4] = x[4] ^ y[4];
        z[5] = x[5] ^ y[5];
        z[6] = x[6] ^ y[6];
    }

    public static void addExt(long[] xx, long[] yy, long[] zz) {
        for (int i = 0; i < 13; ++i) {
            zz[i] = xx[i] ^ yy[i];
        }
    }

    public static void addOne(long[] x, long[] z) {
        z[0] = x[0] ^ 1L;
        z[1] = x[1];
        z[2] = x[2];
        z[3] = x[3];
        z[4] = x[4];
        z[5] = x[5];
        z[6] = x[6];
    }

    private static void addTo(long[] x, long[] z) {
        z[0] = z[0] ^ x[0];
        z[1] = z[1] ^ x[1];
        z[2] = z[2] ^ x[2];
        z[3] = z[3] ^ x[3];
        z[4] = z[4] ^ x[4];
        z[5] = z[5] ^ x[5];
        z[6] = z[6] ^ x[6];
    }

    public static long[] fromBigInteger(BigInteger x) {
        return Nat.fromBigInteger64(409, x);
    }

    public static void halfTrace(long[] x, long[] z) {
        long[] tt = Nat.create64(13);
        Nat448.copy64(x, z);
        for (int i = 1; i < 409; i += 2) {
            SecT409Field.implSquare(z, tt);
            SecT409Field.reduce(tt, z);
            SecT409Field.implSquare(z, tt);
            SecT409Field.reduce(tt, z);
            SecT409Field.addTo(x, z);
        }
    }

    public static void invert(long[] x, long[] z) {
        if (Nat448.isZero64(x)) {
            throw new IllegalStateException();
        }
        long[] t0 = Nat448.create64();
        long[] t1 = Nat448.create64();
        long[] t2 = Nat448.create64();
        SecT409Field.square(x, t0);
        SecT409Field.squareN(t0, 1, t1);
        SecT409Field.multiply(t0, t1, t0);
        SecT409Field.squareN(t1, 1, t1);
        SecT409Field.multiply(t0, t1, t0);
        SecT409Field.squareN(t0, 3, t1);
        SecT409Field.multiply(t0, t1, t0);
        SecT409Field.squareN(t0, 6, t1);
        SecT409Field.multiply(t0, t1, t0);
        SecT409Field.squareN(t0, 12, t1);
        SecT409Field.multiply(t0, t1, t2);
        SecT409Field.squareN(t2, 24, t0);
        SecT409Field.squareN(t0, 24, t1);
        SecT409Field.multiply(t0, t1, t0);
        SecT409Field.squareN(t0, 48, t1);
        SecT409Field.multiply(t0, t1, t0);
        SecT409Field.squareN(t0, 96, t1);
        SecT409Field.multiply(t0, t1, t0);
        SecT409Field.squareN(t0, 192, t1);
        SecT409Field.multiply(t0, t1, t0);
        SecT409Field.multiply(t0, t2, z);
    }

    public static void multiply(long[] x, long[] y, long[] z) {
        long[] tt = Nat448.createExt64();
        SecT409Field.implMultiply(x, y, tt);
        SecT409Field.reduce(tt, z);
    }

    public static void multiplyAddToExt(long[] x, long[] y, long[] zz) {
        long[] tt = Nat448.createExt64();
        SecT409Field.implMultiply(x, y, tt);
        SecT409Field.addExt(zz, tt, zz);
    }

    public static void reduce(long[] xx, long[] z) {
        long x00 = xx[0];
        long x01 = xx[1];
        long x02 = xx[2];
        long x03 = xx[3];
        long x04 = xx[4];
        long x05 = xx[5];
        long x06 = xx[6];
        long x07 = xx[7];
        long u = xx[12];
        x05 ^= u << 39;
        x06 ^= u >>> 25 ^ u << 62;
        x07 ^= u >>> 2;
        u = xx[11];
        x04 ^= u << 39;
        x05 ^= u >>> 25 ^ u << 62;
        x06 ^= u >>> 2;
        u = xx[10];
        x03 ^= u << 39;
        x04 ^= u >>> 25 ^ u << 62;
        x05 ^= u >>> 2;
        u = xx[9];
        x02 ^= u << 39;
        x03 ^= u >>> 25 ^ u << 62;
        x04 ^= u >>> 2;
        u = xx[8];
        x01 ^= u << 39;
        x02 ^= u >>> 25 ^ u << 62;
        x03 ^= u >>> 2;
        u = x07;
        long t = x06 >>> 25;
        z[0] = (x00 ^= u << 39) ^ t;
        z[1] = (x01 ^= u >>> 25 ^ u << 62) ^ t << 23;
        z[2] = x02 ^= u >>> 2;
        z[3] = x03;
        z[4] = x04;
        z[5] = x05;
        z[6] = x06 & 0x1FFFFFFL;
    }

    public static void reduce39(long[] z, int zOff) {
        long z6 = z[zOff + 6];
        long t = z6 >>> 25;
        int n = zOff;
        z[n] = z[n] ^ t;
        int n2 = zOff + 1;
        z[n2] = z[n2] ^ t << 23;
        z[zOff + 6] = z6 & 0x1FFFFFFL;
    }

    public static void sqrt(long[] x, long[] z) {
        long u0 = Interleave.unshuffle(x[0]);
        long u1 = Interleave.unshuffle(x[1]);
        long e0 = u0 & 0xFFFFFFFFL | u1 << 32;
        long c0 = u0 >>> 32 | u1 & 0xFFFFFFFF00000000L;
        u0 = Interleave.unshuffle(x[2]);
        u1 = Interleave.unshuffle(x[3]);
        long e1 = u0 & 0xFFFFFFFFL | u1 << 32;
        long c1 = u0 >>> 32 | u1 & 0xFFFFFFFF00000000L;
        u0 = Interleave.unshuffle(x[4]);
        u1 = Interleave.unshuffle(x[5]);
        long e2 = u0 & 0xFFFFFFFFL | u1 << 32;
        long c2 = u0 >>> 32 | u1 & 0xFFFFFFFF00000000L;
        u0 = Interleave.unshuffle(x[6]);
        long e3 = u0 & 0xFFFFFFFFL;
        long c3 = u0 >>> 32;
        z[0] = e0 ^ c0 << 44;
        z[1] = e1 ^ c1 << 44 ^ c0 >>> 20;
        z[2] = e2 ^ c2 << 44 ^ c1 >>> 20;
        z[3] = e3 ^ c3 << 44 ^ c2 >>> 20 ^ c0 << 13;
        z[4] = c3 >>> 20 ^ c1 << 13 ^ c0 >>> 51;
        z[5] = c2 << 13 ^ c1 >>> 51;
        z[6] = c3 << 13 ^ c2 >>> 51;
    }

    public static void square(long[] x, long[] z) {
        long[] tt = Nat.create64(13);
        SecT409Field.implSquare(x, tt);
        SecT409Field.reduce(tt, z);
    }

    public static void squareAddToExt(long[] x, long[] zz) {
        long[] tt = Nat.create64(13);
        SecT409Field.implSquare(x, tt);
        SecT409Field.addExt(zz, tt, zz);
    }

    public static void squareN(long[] x, int n, long[] z) {
        long[] tt = Nat.create64(13);
        SecT409Field.implSquare(x, tt);
        SecT409Field.reduce(tt, z);
        while (--n > 0) {
            SecT409Field.implSquare(z, tt);
            SecT409Field.reduce(tt, z);
        }
    }

    public static int trace(long[] x) {
        return (int)x[0] & 1;
    }

    protected static void implCompactExt(long[] zz) {
        long z00 = zz[0];
        long z01 = zz[1];
        long z02 = zz[2];
        long z03 = zz[3];
        long z04 = zz[4];
        long z05 = zz[5];
        long z06 = zz[6];
        long z07 = zz[7];
        long z08 = zz[8];
        long z09 = zz[9];
        long z10 = zz[10];
        long z11 = zz[11];
        long z12 = zz[12];
        long z13 = zz[13];
        zz[0] = z00 ^ z01 << 59;
        zz[1] = z01 >>> 5 ^ z02 << 54;
        zz[2] = z02 >>> 10 ^ z03 << 49;
        zz[3] = z03 >>> 15 ^ z04 << 44;
        zz[4] = z04 >>> 20 ^ z05 << 39;
        zz[5] = z05 >>> 25 ^ z06 << 34;
        zz[6] = z06 >>> 30 ^ z07 << 29;
        zz[7] = z07 >>> 35 ^ z08 << 24;
        zz[8] = z08 >>> 40 ^ z09 << 19;
        zz[9] = z09 >>> 45 ^ z10 << 14;
        zz[10] = z10 >>> 50 ^ z11 << 9;
        zz[11] = z11 >>> 55 ^ z12 << 4 ^ z13 << 63;
        zz[12] = z13 >>> 1;
    }

    protected static void implExpand(long[] x, long[] z) {
        long x0 = x[0];
        long x1 = x[1];
        long x2 = x[2];
        long x3 = x[3];
        long x4 = x[4];
        long x5 = x[5];
        long x6 = x[6];
        z[0] = x0 & 0x7FFFFFFFFFFFFFFL;
        z[1] = (x0 >>> 59 ^ x1 << 5) & 0x7FFFFFFFFFFFFFFL;
        z[2] = (x1 >>> 54 ^ x2 << 10) & 0x7FFFFFFFFFFFFFFL;
        z[3] = (x2 >>> 49 ^ x3 << 15) & 0x7FFFFFFFFFFFFFFL;
        z[4] = (x3 >>> 44 ^ x4 << 20) & 0x7FFFFFFFFFFFFFFL;
        z[5] = (x4 >>> 39 ^ x5 << 25) & 0x7FFFFFFFFFFFFFFL;
        z[6] = x5 >>> 34 ^ x6 << 30;
    }

    protected static void implMultiply(long[] x, long[] y, long[] zz) {
        long[] a = new long[7];
        long[] b = new long[7];
        SecT409Field.implExpand(x, a);
        SecT409Field.implExpand(y, b);
        long[] u = new long[8];
        for (int i = 0; i < 7; ++i) {
            SecT409Field.implMulwAcc(u, a[i], b[i], zz, i << 1);
        }
        long v0 = zz[0];
        long v1 = zz[1];
        zz[1] = (v0 ^= zz[2]) ^ v1;
        zz[2] = (v0 ^= zz[4]) ^ (v1 ^= zz[3]);
        zz[3] = (v0 ^= zz[6]) ^ (v1 ^= zz[5]);
        zz[4] = (v0 ^= zz[8]) ^ (v1 ^= zz[7]);
        zz[5] = (v0 ^= zz[10]) ^ (v1 ^= zz[9]);
        zz[6] = (v0 ^= zz[12]) ^ (v1 ^= zz[11]);
        long w = v0 ^ (v1 ^= zz[13]);
        zz[7] = zz[0] ^ w;
        zz[8] = zz[1] ^ w;
        zz[9] = zz[2] ^ w;
        zz[10] = zz[3] ^ w;
        zz[11] = zz[4] ^ w;
        zz[12] = zz[5] ^ w;
        zz[13] = zz[6] ^ w;
        SecT409Field.implMulwAcc(u, a[0] ^ a[1], b[0] ^ b[1], zz, 1);
        SecT409Field.implMulwAcc(u, a[0] ^ a[2], b[0] ^ b[2], zz, 2);
        SecT409Field.implMulwAcc(u, a[0] ^ a[3], b[0] ^ b[3], zz, 3);
        SecT409Field.implMulwAcc(u, a[1] ^ a[2], b[1] ^ b[2], zz, 3);
        SecT409Field.implMulwAcc(u, a[0] ^ a[4], b[0] ^ b[4], zz, 4);
        SecT409Field.implMulwAcc(u, a[1] ^ a[3], b[1] ^ b[3], zz, 4);
        SecT409Field.implMulwAcc(u, a[0] ^ a[5], b[0] ^ b[5], zz, 5);
        SecT409Field.implMulwAcc(u, a[1] ^ a[4], b[1] ^ b[4], zz, 5);
        SecT409Field.implMulwAcc(u, a[2] ^ a[3], b[2] ^ b[3], zz, 5);
        SecT409Field.implMulwAcc(u, a[0] ^ a[6], b[0] ^ b[6], zz, 6);
        SecT409Field.implMulwAcc(u, a[1] ^ a[5], b[1] ^ b[5], zz, 6);
        SecT409Field.implMulwAcc(u, a[2] ^ a[4], b[2] ^ b[4], zz, 6);
        SecT409Field.implMulwAcc(u, a[1] ^ a[6], b[1] ^ b[6], zz, 7);
        SecT409Field.implMulwAcc(u, a[2] ^ a[5], b[2] ^ b[5], zz, 7);
        SecT409Field.implMulwAcc(u, a[3] ^ a[4], b[3] ^ b[4], zz, 7);
        SecT409Field.implMulwAcc(u, a[2] ^ a[6], b[2] ^ b[6], zz, 8);
        SecT409Field.implMulwAcc(u, a[3] ^ a[5], b[3] ^ b[5], zz, 8);
        SecT409Field.implMulwAcc(u, a[3] ^ a[6], b[3] ^ b[6], zz, 9);
        SecT409Field.implMulwAcc(u, a[4] ^ a[5], b[4] ^ b[5], zz, 9);
        SecT409Field.implMulwAcc(u, a[4] ^ a[6], b[4] ^ b[6], zz, 10);
        SecT409Field.implMulwAcc(u, a[5] ^ a[6], b[5] ^ b[6], zz, 11);
        SecT409Field.implCompactExt(zz);
    }

    protected static void implMulwAcc(long[] u, long x, long y, long[] z, int zOff) {
        u[1] = y;
        u[2] = u[1] << 1;
        u[3] = u[2] ^ y;
        u[4] = u[2] << 1;
        u[5] = u[4] ^ y;
        u[6] = u[3] << 1;
        u[7] = u[6] ^ y;
        int j = (int)x;
        long h = 0L;
        long l = u[j & 7] ^ u[j >>> 3 & 7] << 3;
        int k = 54;
        do {
            j = (int)(x >>> k);
            long g = u[j & 7] ^ u[j >>> 3 & 7] << 3;
            l ^= g << k;
            h ^= g >>> -k;
        } while ((k -= 6) > 0);
        int n = zOff;
        z[n] = z[n] ^ l & 0x7FFFFFFFFFFFFFFL;
        int n2 = zOff + 1;
        z[n2] = z[n2] ^ (l >>> 59 ^ h << 5);
    }

    protected static void implSquare(long[] x, long[] zz) {
        Interleave.expand64To128(x, 0, 6, zz, 0);
        zz[12] = Interleave.expand32to64((int)x[6]);
    }
}

