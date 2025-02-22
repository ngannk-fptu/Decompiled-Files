/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat128;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecT113Field {
    private static final long M49 = 0x1FFFFFFFFFFFFL;
    private static final long M57 = 0x1FFFFFFFFFFFFFFL;

    public static void add(long[] x, long[] y, long[] z) {
        z[0] = x[0] ^ y[0];
        z[1] = x[1] ^ y[1];
    }

    public static void addExt(long[] xx, long[] yy, long[] zz) {
        zz[0] = xx[0] ^ yy[0];
        zz[1] = xx[1] ^ yy[1];
        zz[2] = xx[2] ^ yy[2];
        zz[3] = xx[3] ^ yy[3];
    }

    public static void addOne(long[] x, long[] z) {
        z[0] = x[0] ^ 1L;
        z[1] = x[1];
    }

    private static void addTo(long[] x, long[] z) {
        z[0] = z[0] ^ x[0];
        z[1] = z[1] ^ x[1];
    }

    public static long[] fromBigInteger(BigInteger x) {
        return Nat.fromBigInteger64(113, x);
    }

    public static void halfTrace(long[] x, long[] z) {
        long[] tt = Nat128.createExt64();
        Nat128.copy64(x, z);
        for (int i = 1; i < 113; i += 2) {
            SecT113Field.implSquare(z, tt);
            SecT113Field.reduce(tt, z);
            SecT113Field.implSquare(z, tt);
            SecT113Field.reduce(tt, z);
            SecT113Field.addTo(x, z);
        }
    }

    public static void invert(long[] x, long[] z) {
        if (Nat128.isZero64(x)) {
            throw new IllegalStateException();
        }
        long[] t0 = Nat128.create64();
        long[] t1 = Nat128.create64();
        SecT113Field.square(x, t0);
        SecT113Field.multiply(t0, x, t0);
        SecT113Field.square(t0, t0);
        SecT113Field.multiply(t0, x, t0);
        SecT113Field.squareN(t0, 3, t1);
        SecT113Field.multiply(t1, t0, t1);
        SecT113Field.square(t1, t1);
        SecT113Field.multiply(t1, x, t1);
        SecT113Field.squareN(t1, 7, t0);
        SecT113Field.multiply(t0, t1, t0);
        SecT113Field.squareN(t0, 14, t1);
        SecT113Field.multiply(t1, t0, t1);
        SecT113Field.squareN(t1, 28, t0);
        SecT113Field.multiply(t0, t1, t0);
        SecT113Field.squareN(t0, 56, t1);
        SecT113Field.multiply(t1, t0, t1);
        SecT113Field.square(t1, z);
    }

    public static void multiply(long[] x, long[] y, long[] z) {
        long[] tt = new long[8];
        SecT113Field.implMultiply(x, y, tt);
        SecT113Field.reduce(tt, z);
    }

    public static void multiplyAddToExt(long[] x, long[] y, long[] zz) {
        long[] tt = new long[8];
        SecT113Field.implMultiply(x, y, tt);
        SecT113Field.addExt(zz, tt, zz);
    }

    public static void reduce(long[] xx, long[] z) {
        long x0 = xx[0];
        long x1 = xx[1];
        long x2 = xx[2];
        long x3 = xx[3];
        x1 ^= x3 << 15 ^ x3 << 24;
        long t = (x1 ^= x2 >>> 49 ^ x2 >>> 40) >>> 49;
        z[0] = (x0 ^= (x2 ^= x3 >>> 49 ^ x3 >>> 40) << 15 ^ x2 << 24) ^ t ^ t << 9;
        z[1] = x1 & 0x1FFFFFFFFFFFFL;
    }

    public static void reduce15(long[] z, int zOff) {
        long z1 = z[zOff + 1];
        long t = z1 >>> 49;
        int n = zOff;
        z[n] = z[n] ^ (t ^ t << 9);
        z[zOff + 1] = z1 & 0x1FFFFFFFFFFFFL;
    }

    public static void sqrt(long[] x, long[] z) {
        long u0 = Interleave.unshuffle(x[0]);
        long u1 = Interleave.unshuffle(x[1]);
        long e0 = u0 & 0xFFFFFFFFL | u1 << 32;
        long c0 = u0 >>> 32 | u1 & 0xFFFFFFFF00000000L;
        z[0] = e0 ^ c0 << 57 ^ c0 << 5;
        z[1] = c0 >>> 7 ^ c0 >>> 59;
    }

    public static void square(long[] x, long[] z) {
        long[] tt = Nat128.createExt64();
        SecT113Field.implSquare(x, tt);
        SecT113Field.reduce(tt, z);
    }

    public static void squareAddToExt(long[] x, long[] zz) {
        long[] tt = Nat128.createExt64();
        SecT113Field.implSquare(x, tt);
        SecT113Field.addExt(zz, tt, zz);
    }

    public static void squareN(long[] x, int n, long[] z) {
        long[] tt = Nat128.createExt64();
        SecT113Field.implSquare(x, tt);
        SecT113Field.reduce(tt, z);
        while (--n > 0) {
            SecT113Field.implSquare(z, tt);
            SecT113Field.reduce(tt, z);
        }
    }

    public static int trace(long[] x) {
        return (int)x[0] & 1;
    }

    protected static void implMultiply(long[] x, long[] y, long[] zz) {
        long f0 = x[0];
        long f1 = x[1];
        f1 = (f0 >>> 57 ^ f1 << 7) & 0x1FFFFFFFFFFFFFFL;
        f0 &= 0x1FFFFFFFFFFFFFFL;
        long g0 = y[0];
        long g1 = y[1];
        g1 = (g0 >>> 57 ^ g1 << 7) & 0x1FFFFFFFFFFFFFFL;
        long[] u = zz;
        long[] H = new long[6];
        SecT113Field.implMulw(u, f0, g0 &= 0x1FFFFFFFFFFFFFFL, H, 0);
        SecT113Field.implMulw(u, f1, g1, H, 2);
        SecT113Field.implMulw(u, f0 ^ f1, g0 ^ g1, H, 4);
        long r = H[1] ^ H[2];
        long z0 = H[0];
        long z3 = H[3];
        long z1 = H[4] ^ z0 ^ r;
        long z2 = H[5] ^ z3 ^ r;
        zz[0] = z0 ^ z1 << 57;
        zz[1] = z1 >>> 7 ^ z2 << 50;
        zz[2] = z2 >>> 14 ^ z3 << 43;
        zz[3] = z3 >>> 21;
    }

    protected static void implMulw(long[] u, long x, long y, long[] z, int zOff) {
        u[1] = y;
        u[2] = u[1] << 1;
        u[3] = u[2] ^ y;
        u[4] = u[2] << 1;
        u[5] = u[4] ^ y;
        u[6] = u[3] << 1;
        u[7] = u[6] ^ y;
        int j = (int)x;
        long h = 0L;
        long l = u[j & 7];
        int k = 48;
        do {
            j = (int)(x >>> k);
            long g = u[j & 7] ^ u[j >>> 3 & 7] << 3 ^ u[j >>> 6 & 7] << 6;
            l ^= g << k;
            h ^= g >>> -k;
        } while ((k -= 9) > 0);
        z[zOff] = l & 0x1FFFFFFFFFFFFFFL;
        z[zOff + 1] = l >>> 57 ^ (h ^= (x & 0x100804020100800L & y << 7 >> 63) >>> 8) << 7;
    }

    protected static void implSquare(long[] x, long[] zz) {
        Interleave.expand64To128(x, 0, 2, zz, 0);
    }
}

