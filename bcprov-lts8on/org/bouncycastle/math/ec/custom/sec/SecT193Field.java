/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecT193Field {
    private static final long M01 = 1L;
    private static final long M49 = 0x1FFFFFFFFFFFFL;

    public static void add(long[] x, long[] y, long[] z) {
        z[0] = x[0] ^ y[0];
        z[1] = x[1] ^ y[1];
        z[2] = x[2] ^ y[2];
        z[3] = x[3] ^ y[3];
    }

    public static void addExt(long[] xx, long[] yy, long[] zz) {
        zz[0] = xx[0] ^ yy[0];
        zz[1] = xx[1] ^ yy[1];
        zz[2] = xx[2] ^ yy[2];
        zz[3] = xx[3] ^ yy[3];
        zz[4] = xx[4] ^ yy[4];
        zz[5] = xx[5] ^ yy[5];
        zz[6] = xx[6] ^ yy[6];
    }

    public static void addOne(long[] x, long[] z) {
        z[0] = x[0] ^ 1L;
        z[1] = x[1];
        z[2] = x[2];
        z[3] = x[3];
    }

    private static void addTo(long[] x, long[] z) {
        z[0] = z[0] ^ x[0];
        z[1] = z[1] ^ x[1];
        z[2] = z[2] ^ x[2];
        z[3] = z[3] ^ x[3];
    }

    public static long[] fromBigInteger(BigInteger x) {
        return Nat.fromBigInteger64(193, x);
    }

    public static void halfTrace(long[] x, long[] z) {
        long[] tt = Nat256.createExt64();
        Nat256.copy64(x, z);
        for (int i = 1; i < 193; i += 2) {
            SecT193Field.implSquare(z, tt);
            SecT193Field.reduce(tt, z);
            SecT193Field.implSquare(z, tt);
            SecT193Field.reduce(tt, z);
            SecT193Field.addTo(x, z);
        }
    }

    public static void invert(long[] x, long[] z) {
        if (Nat256.isZero64(x)) {
            throw new IllegalStateException();
        }
        long[] t0 = Nat256.create64();
        long[] t1 = Nat256.create64();
        SecT193Field.square(x, t0);
        SecT193Field.squareN(t0, 1, t1);
        SecT193Field.multiply(t0, t1, t0);
        SecT193Field.squareN(t1, 1, t1);
        SecT193Field.multiply(t0, t1, t0);
        SecT193Field.squareN(t0, 3, t1);
        SecT193Field.multiply(t0, t1, t0);
        SecT193Field.squareN(t0, 6, t1);
        SecT193Field.multiply(t0, t1, t0);
        SecT193Field.squareN(t0, 12, t1);
        SecT193Field.multiply(t0, t1, t0);
        SecT193Field.squareN(t0, 24, t1);
        SecT193Field.multiply(t0, t1, t0);
        SecT193Field.squareN(t0, 48, t1);
        SecT193Field.multiply(t0, t1, t0);
        SecT193Field.squareN(t0, 96, t1);
        SecT193Field.multiply(t0, t1, z);
    }

    public static void multiply(long[] x, long[] y, long[] z) {
        long[] tt = Nat256.createExt64();
        SecT193Field.implMultiply(x, y, tt);
        SecT193Field.reduce(tt, z);
    }

    public static void multiplyAddToExt(long[] x, long[] y, long[] zz) {
        long[] tt = Nat256.createExt64();
        SecT193Field.implMultiply(x, y, tt);
        SecT193Field.addExt(zz, tt, zz);
    }

    public static void reduce(long[] xx, long[] z) {
        long x0 = xx[0];
        long x1 = xx[1];
        long x2 = xx[2];
        long x3 = xx[3];
        long x4 = xx[4];
        long x5 = xx[5];
        long x6 = xx[6];
        x2 ^= x6 << 63;
        x3 ^= x6 >>> 1 ^ x6 << 14;
        x1 ^= x5 << 63;
        x2 ^= x5 >>> 1 ^ x5 << 14;
        long t = (x3 ^= x5 >>> 50) >>> 1;
        z[0] = (x0 ^= (x4 ^= x6 >>> 50) << 63) ^ t ^ t << 15;
        z[1] = (x1 ^= x4 >>> 1 ^ x4 << 14) ^ t >>> 49;
        z[2] = x2 ^= x4 >>> 50;
        z[3] = x3 & 1L;
    }

    public static void reduce63(long[] z, int zOff) {
        long z3 = z[zOff + 3];
        long t = z3 >>> 1;
        int n = zOff;
        z[n] = z[n] ^ (t ^ t << 15);
        int n2 = zOff + 1;
        z[n2] = z[n2] ^ t >>> 49;
        z[zOff + 3] = z3 & 1L;
    }

    public static void sqrt(long[] x, long[] z) {
        long u0 = Interleave.unshuffle(x[0]);
        long u1 = Interleave.unshuffle(x[1]);
        long e0 = u0 & 0xFFFFFFFFL | u1 << 32;
        long c0 = u0 >>> 32 | u1 & 0xFFFFFFFF00000000L;
        u0 = Interleave.unshuffle(x[2]);
        long e1 = u0 & 0xFFFFFFFFL ^ x[3] << 32;
        long c1 = u0 >>> 32;
        z[0] = e0 ^ c0 << 8;
        z[1] = e1 ^ c1 << 8 ^ c0 >>> 56 ^ c0 << 33;
        z[2] = c1 >>> 56 ^ c1 << 33 ^ c0 >>> 31;
        z[3] = c1 >>> 31;
    }

    public static void square(long[] x, long[] z) {
        long[] tt = Nat256.createExt64();
        SecT193Field.implSquare(x, tt);
        SecT193Field.reduce(tt, z);
    }

    public static void squareAddToExt(long[] x, long[] zz) {
        long[] tt = Nat256.createExt64();
        SecT193Field.implSquare(x, tt);
        SecT193Field.addExt(zz, tt, zz);
    }

    public static void squareN(long[] x, int n, long[] z) {
        long[] tt = Nat256.createExt64();
        SecT193Field.implSquare(x, tt);
        SecT193Field.reduce(tt, z);
        while (--n > 0) {
            SecT193Field.implSquare(z, tt);
            SecT193Field.reduce(tt, z);
        }
    }

    public static int trace(long[] x) {
        return (int)x[0] & 1;
    }

    protected static void implCompactExt(long[] zz) {
        long z0 = zz[0];
        long z1 = zz[1];
        long z2 = zz[2];
        long z3 = zz[3];
        long z4 = zz[4];
        long z5 = zz[5];
        long z6 = zz[6];
        long z7 = zz[7];
        zz[0] = z0 ^ z1 << 49;
        zz[1] = z1 >>> 15 ^ z2 << 34;
        zz[2] = z2 >>> 30 ^ z3 << 19;
        zz[3] = z3 >>> 45 ^ z4 << 4 ^ z5 << 53;
        zz[4] = z4 >>> 60 ^ z6 << 38 ^ z5 >>> 11;
        zz[5] = z6 >>> 26 ^ z7 << 23;
        zz[6] = z7 >>> 41;
        zz[7] = 0L;
    }

    protected static void implExpand(long[] x, long[] z) {
        long x0 = x[0];
        long x1 = x[1];
        long x2 = x[2];
        long x3 = x[3];
        z[0] = x0 & 0x1FFFFFFFFFFFFL;
        z[1] = (x0 >>> 49 ^ x1 << 15) & 0x1FFFFFFFFFFFFL;
        z[2] = (x1 >>> 34 ^ x2 << 30) & 0x1FFFFFFFFFFFFL;
        z[3] = x2 >>> 19 ^ x3 << 45;
    }

    protected static void implMultiply(long[] x, long[] y, long[] zz) {
        int i;
        long[] f = new long[4];
        long[] g = new long[4];
        SecT193Field.implExpand(x, f);
        SecT193Field.implExpand(y, g);
        long[] u = new long[8];
        SecT193Field.implMulwAcc(u, f[0], g[0], zz, 0);
        SecT193Field.implMulwAcc(u, f[1], g[1], zz, 1);
        SecT193Field.implMulwAcc(u, f[2], g[2], zz, 2);
        SecT193Field.implMulwAcc(u, f[3], g[3], zz, 3);
        for (i = 5; i > 0; --i) {
            int n = i;
            zz[n] = zz[n] ^ zz[i - 1];
        }
        SecT193Field.implMulwAcc(u, f[0] ^ f[1], g[0] ^ g[1], zz, 1);
        SecT193Field.implMulwAcc(u, f[2] ^ f[3], g[2] ^ g[3], zz, 3);
        for (i = 7; i > 1; --i) {
            int n = i;
            zz[n] = zz[n] ^ zz[i - 2];
        }
        long c0 = f[0] ^ f[2];
        long c1 = f[1] ^ f[3];
        long d0 = g[0] ^ g[2];
        long d1 = g[1] ^ g[3];
        SecT193Field.implMulwAcc(u, c0 ^ c1, d0 ^ d1, zz, 3);
        long[] t = new long[3];
        SecT193Field.implMulwAcc(u, c0, d0, t, 0);
        SecT193Field.implMulwAcc(u, c1, d1, t, 1);
        long t0 = t[0];
        long t1 = t[1];
        long t2 = t[2];
        zz[2] = zz[2] ^ t0;
        zz[3] = zz[3] ^ (t0 ^ t1);
        zz[4] = zz[4] ^ (t2 ^ t1);
        zz[5] = zz[5] ^ t2;
        SecT193Field.implCompactExt(zz);
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
        int k = 36;
        do {
            j = (int)(x >>> k);
            long g = u[j & 7] ^ u[j >>> 3 & 7] << 3 ^ u[j >>> 6 & 7] << 6 ^ u[j >>> 9 & 7] << 9 ^ u[j >>> 12 & 7] << 12;
            l ^= g << k;
            h ^= g >>> -k;
        } while ((k -= 15) > 0);
        int n = zOff;
        z[n] = z[n] ^ l & 0x1FFFFFFFFFFFFL;
        int n2 = zOff + 1;
        z[n2] = z[n2] ^ (l >>> 49 ^ h << 15);
    }

    protected static void implSquare(long[] x, long[] zz) {
        Interleave.expand64To128(x, 0, 3, zz, 0);
        zz[6] = x[3] & 1L;
    }
}

