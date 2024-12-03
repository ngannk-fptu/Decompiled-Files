/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat576;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecT571Field {
    private static final long M59 = 0x7FFFFFFFFFFFFFFL;
    private static final long[] ROOT_Z = new long[]{3161836309350906777L, -7642453882179322845L, -3821226941089661423L, 7312758566309945096L, -556661012383879292L, 8945041530681231562L, -4750851271514160027L, 6847946401097695794L, 541669439031730457L};

    public static void add(long[] x, long[] y, long[] z) {
        for (int i = 0; i < 9; ++i) {
            z[i] = x[i] ^ y[i];
        }
    }

    private static void add(long[] x, int xOff, long[] y, int yOff, long[] z, int zOff) {
        for (int i = 0; i < 9; ++i) {
            z[zOff + i] = x[xOff + i] ^ y[yOff + i];
        }
    }

    public static void addBothTo(long[] x, long[] y, long[] z) {
        for (int i = 0; i < 9; ++i) {
            int n = i;
            z[n] = z[n] ^ (x[i] ^ y[i]);
        }
    }

    private static void addBothTo(long[] x, int xOff, long[] y, int yOff, long[] z, int zOff) {
        for (int i = 0; i < 9; ++i) {
            int n = zOff + i;
            z[n] = z[n] ^ (x[xOff + i] ^ y[yOff + i]);
        }
    }

    public static void addExt(long[] xx, long[] yy, long[] zz) {
        for (int i = 0; i < 18; ++i) {
            zz[i] = xx[i] ^ yy[i];
        }
    }

    public static void addOne(long[] x, long[] z) {
        z[0] = x[0] ^ 1L;
        for (int i = 1; i < 9; ++i) {
            z[i] = x[i];
        }
    }

    private static void addTo(long[] x, long[] z) {
        for (int i = 0; i < 9; ++i) {
            int n = i;
            z[n] = z[n] ^ x[i];
        }
    }

    public static long[] fromBigInteger(BigInteger x) {
        return Nat.fromBigInteger64(571, x);
    }

    public static void halfTrace(long[] x, long[] z) {
        long[] tt = Nat576.createExt64();
        Nat576.copy64(x, z);
        for (int i = 1; i < 571; i += 2) {
            SecT571Field.implSquare(z, tt);
            SecT571Field.reduce(tt, z);
            SecT571Field.implSquare(z, tt);
            SecT571Field.reduce(tt, z);
            SecT571Field.addTo(x, z);
        }
    }

    public static void invert(long[] x, long[] z) {
        if (Nat576.isZero64(x)) {
            throw new IllegalStateException();
        }
        long[] t0 = Nat576.create64();
        long[] t1 = Nat576.create64();
        long[] t2 = Nat576.create64();
        SecT571Field.square(x, t2);
        SecT571Field.square(t2, t0);
        SecT571Field.square(t0, t1);
        SecT571Field.multiply(t0, t1, t0);
        SecT571Field.squareN(t0, 2, t1);
        SecT571Field.multiply(t0, t1, t0);
        SecT571Field.multiply(t0, t2, t0);
        SecT571Field.squareN(t0, 5, t1);
        SecT571Field.multiply(t0, t1, t0);
        SecT571Field.squareN(t1, 5, t1);
        SecT571Field.multiply(t0, t1, t0);
        SecT571Field.squareN(t0, 15, t1);
        SecT571Field.multiply(t0, t1, t2);
        SecT571Field.squareN(t2, 30, t0);
        SecT571Field.squareN(t0, 30, t1);
        SecT571Field.multiply(t0, t1, t0);
        SecT571Field.squareN(t0, 60, t1);
        SecT571Field.multiply(t0, t1, t0);
        SecT571Field.squareN(t1, 60, t1);
        SecT571Field.multiply(t0, t1, t0);
        SecT571Field.squareN(t0, 180, t1);
        SecT571Field.multiply(t0, t1, t0);
        SecT571Field.squareN(t1, 180, t1);
        SecT571Field.multiply(t0, t1, t0);
        SecT571Field.multiply(t0, t2, z);
    }

    public static void multiply(long[] x, long[] y, long[] z) {
        long[] tt = Nat576.createExt64();
        SecT571Field.implMultiply(x, y, tt);
        SecT571Field.reduce(tt, z);
    }

    public static void multiplyAddToExt(long[] x, long[] y, long[] zz) {
        long[] tt = Nat576.createExt64();
        SecT571Field.implMultiply(x, y, tt);
        SecT571Field.addExt(zz, tt, zz);
    }

    public static void multiplyPrecomp(long[] x, long[] precomp, long[] z) {
        long[] tt = Nat576.createExt64();
        SecT571Field.implMultiplyPrecomp(x, precomp, tt);
        SecT571Field.reduce(tt, z);
    }

    public static void multiplyPrecompAddToExt(long[] x, long[] precomp, long[] zz) {
        long[] tt = Nat576.createExt64();
        SecT571Field.implMultiplyPrecomp(x, precomp, tt);
        SecT571Field.addExt(zz, tt, zz);
    }

    public static long[] precompMultiplicand(long[] x) {
        int len = 144;
        long[] t = new long[len << 1];
        System.arraycopy(x, 0, t, 9, 9);
        int tOff = 0;
        for (int i = 7; i > 0; --i) {
            Nat.shiftUpBit64(9, t, (tOff += 18) >>> 1, 0L, t, tOff);
            SecT571Field.reduce5(t, tOff);
            SecT571Field.add(t, 9, t, tOff, t, tOff + 9);
        }
        Nat.shiftUpBits64(len, t, 0, 4, 0L, t, len);
        return t;
    }

    public static void reduce(long[] xx, long[] z) {
        long xx09 = xx[9];
        long u = xx[17];
        long v = xx09;
        xx09 = v ^ u >>> 59 ^ u >>> 57 ^ u >>> 54 ^ u >>> 49;
        v = xx[8] ^ u << 5 ^ u << 7 ^ u << 10 ^ u << 15;
        for (int i = 16; i >= 10; --i) {
            u = xx[i];
            z[i - 8] = v ^ u >>> 59 ^ u >>> 57 ^ u >>> 54 ^ u >>> 49;
            v = xx[i - 9] ^ u << 5 ^ u << 7 ^ u << 10 ^ u << 15;
        }
        u = xx09;
        z[1] = v ^ u >>> 59 ^ u >>> 57 ^ u >>> 54 ^ u >>> 49;
        v = xx[0] ^ u << 5 ^ u << 7 ^ u << 10 ^ u << 15;
        long x08 = z[8];
        long t = x08 >>> 59;
        z[0] = v ^ t ^ t << 2 ^ t << 5 ^ t << 10;
        z[8] = x08 & 0x7FFFFFFFFFFFFFFL;
    }

    public static void reduce5(long[] z, int zOff) {
        long z8 = z[zOff + 8];
        long t = z8 >>> 59;
        int n = zOff;
        z[n] = z[n] ^ (t ^ t << 2 ^ t << 5 ^ t << 10);
        z[zOff + 8] = z8 & 0x7FFFFFFFFFFFFFFL;
    }

    public static void sqrt(long[] x, long[] z) {
        long[] evn = Nat576.create64();
        long[] odd = Nat576.create64();
        int pos = 0;
        for (int i = 0; i < 4; ++i) {
            long u0 = Interleave.unshuffle(x[pos++]);
            long u1 = Interleave.unshuffle(x[pos++]);
            evn[i] = u0 & 0xFFFFFFFFL | u1 << 32;
            odd[i] = u0 >>> 32 | u1 & 0xFFFFFFFF00000000L;
        }
        long u0 = Interleave.unshuffle(x[pos]);
        evn[4] = u0 & 0xFFFFFFFFL;
        odd[4] = u0 >>> 32;
        SecT571Field.multiply(odd, ROOT_Z, z);
        SecT571Field.add(z, evn, z);
    }

    public static void square(long[] x, long[] z) {
        long[] tt = Nat576.createExt64();
        SecT571Field.implSquare(x, tt);
        SecT571Field.reduce(tt, z);
    }

    public static void squareAddToExt(long[] x, long[] zz) {
        long[] tt = Nat576.createExt64();
        SecT571Field.implSquare(x, tt);
        SecT571Field.addExt(zz, tt, zz);
    }

    public static void squareN(long[] x, int n, long[] z) {
        long[] tt = Nat576.createExt64();
        SecT571Field.implSquare(x, tt);
        SecT571Field.reduce(tt, z);
        while (--n > 0) {
            SecT571Field.implSquare(z, tt);
            SecT571Field.reduce(tt, z);
        }
    }

    public static int trace(long[] x) {
        return (int)(x[0] ^ x[8] >>> 49 ^ x[8] >>> 57) & 1;
    }

    protected static void implMultiply(long[] x, long[] y, long[] zz) {
        long[] u = new long[16];
        for (int i = 0; i < 9; ++i) {
            SecT571Field.implMulwAcc(u, x[i], y[i], zz, i << 1);
        }
        long v0 = zz[0];
        long v1 = zz[1];
        zz[1] = (v0 ^= zz[2]) ^ v1;
        zz[2] = (v0 ^= zz[4]) ^ (v1 ^= zz[3]);
        zz[3] = (v0 ^= zz[6]) ^ (v1 ^= zz[5]);
        zz[4] = (v0 ^= zz[8]) ^ (v1 ^= zz[7]);
        zz[5] = (v0 ^= zz[10]) ^ (v1 ^= zz[9]);
        zz[6] = (v0 ^= zz[12]) ^ (v1 ^= zz[11]);
        zz[7] = (v0 ^= zz[14]) ^ (v1 ^= zz[13]);
        zz[8] = (v0 ^= zz[16]) ^ (v1 ^= zz[15]);
        long w = v0 ^ (v1 ^= zz[17]);
        zz[9] = zz[0] ^ w;
        zz[10] = zz[1] ^ w;
        zz[11] = zz[2] ^ w;
        zz[12] = zz[3] ^ w;
        zz[13] = zz[4] ^ w;
        zz[14] = zz[5] ^ w;
        zz[15] = zz[6] ^ w;
        zz[16] = zz[7] ^ w;
        zz[17] = zz[8] ^ w;
        SecT571Field.implMulwAcc(u, x[0] ^ x[1], y[0] ^ y[1], zz, 1);
        SecT571Field.implMulwAcc(u, x[0] ^ x[2], y[0] ^ y[2], zz, 2);
        SecT571Field.implMulwAcc(u, x[0] ^ x[3], y[0] ^ y[3], zz, 3);
        SecT571Field.implMulwAcc(u, x[1] ^ x[2], y[1] ^ y[2], zz, 3);
        SecT571Field.implMulwAcc(u, x[0] ^ x[4], y[0] ^ y[4], zz, 4);
        SecT571Field.implMulwAcc(u, x[1] ^ x[3], y[1] ^ y[3], zz, 4);
        SecT571Field.implMulwAcc(u, x[0] ^ x[5], y[0] ^ y[5], zz, 5);
        SecT571Field.implMulwAcc(u, x[1] ^ x[4], y[1] ^ y[4], zz, 5);
        SecT571Field.implMulwAcc(u, x[2] ^ x[3], y[2] ^ y[3], zz, 5);
        SecT571Field.implMulwAcc(u, x[0] ^ x[6], y[0] ^ y[6], zz, 6);
        SecT571Field.implMulwAcc(u, x[1] ^ x[5], y[1] ^ y[5], zz, 6);
        SecT571Field.implMulwAcc(u, x[2] ^ x[4], y[2] ^ y[4], zz, 6);
        SecT571Field.implMulwAcc(u, x[0] ^ x[7], y[0] ^ y[7], zz, 7);
        SecT571Field.implMulwAcc(u, x[1] ^ x[6], y[1] ^ y[6], zz, 7);
        SecT571Field.implMulwAcc(u, x[2] ^ x[5], y[2] ^ y[5], zz, 7);
        SecT571Field.implMulwAcc(u, x[3] ^ x[4], y[3] ^ y[4], zz, 7);
        SecT571Field.implMulwAcc(u, x[0] ^ x[8], y[0] ^ y[8], zz, 8);
        SecT571Field.implMulwAcc(u, x[1] ^ x[7], y[1] ^ y[7], zz, 8);
        SecT571Field.implMulwAcc(u, x[2] ^ x[6], y[2] ^ y[6], zz, 8);
        SecT571Field.implMulwAcc(u, x[3] ^ x[5], y[3] ^ y[5], zz, 8);
        SecT571Field.implMulwAcc(u, x[1] ^ x[8], y[1] ^ y[8], zz, 9);
        SecT571Field.implMulwAcc(u, x[2] ^ x[7], y[2] ^ y[7], zz, 9);
        SecT571Field.implMulwAcc(u, x[3] ^ x[6], y[3] ^ y[6], zz, 9);
        SecT571Field.implMulwAcc(u, x[4] ^ x[5], y[4] ^ y[5], zz, 9);
        SecT571Field.implMulwAcc(u, x[2] ^ x[8], y[2] ^ y[8], zz, 10);
        SecT571Field.implMulwAcc(u, x[3] ^ x[7], y[3] ^ y[7], zz, 10);
        SecT571Field.implMulwAcc(u, x[4] ^ x[6], y[4] ^ y[6], zz, 10);
        SecT571Field.implMulwAcc(u, x[3] ^ x[8], y[3] ^ y[8], zz, 11);
        SecT571Field.implMulwAcc(u, x[4] ^ x[7], y[4] ^ y[7], zz, 11);
        SecT571Field.implMulwAcc(u, x[5] ^ x[6], y[5] ^ y[6], zz, 11);
        SecT571Field.implMulwAcc(u, x[4] ^ x[8], y[4] ^ y[8], zz, 12);
        SecT571Field.implMulwAcc(u, x[5] ^ x[7], y[5] ^ y[7], zz, 12);
        SecT571Field.implMulwAcc(u, x[5] ^ x[8], y[5] ^ y[8], zz, 13);
        SecT571Field.implMulwAcc(u, x[6] ^ x[7], y[6] ^ y[7], zz, 13);
        SecT571Field.implMulwAcc(u, x[6] ^ x[8], y[6] ^ y[8], zz, 14);
        SecT571Field.implMulwAcc(u, x[7] ^ x[8], y[7] ^ y[8], zz, 15);
    }

    protected static void implMultiplyPrecomp(long[] x, long[] precomp, long[] zz) {
        int v;
        int u;
        int aVal;
        int j;
        int k;
        int MASK = 15;
        for (k = 56; k >= 0; k -= 8) {
            for (j = 1; j < 9; j += 2) {
                aVal = (int)(x[j] >>> k);
                u = aVal & MASK;
                v = aVal >>> 4 & MASK;
                SecT571Field.addBothTo(precomp, 9 * u, precomp, 9 * (v + 16), zz, j - 1);
            }
            Nat.shiftUpBits64(16, zz, 0, 8, 0L);
        }
        for (k = 56; k >= 0; k -= 8) {
            for (j = 0; j < 9; j += 2) {
                aVal = (int)(x[j] >>> k);
                u = aVal & MASK;
                v = aVal >>> 4 & MASK;
                SecT571Field.addBothTo(precomp, 9 * u, precomp, 9 * (v + 16), zz, j);
            }
            if (k <= 0) continue;
            Nat.shiftUpBits64(18, zz, 0, 8, 0L);
        }
    }

    protected static void implMulwAcc(long[] u, long x, long y, long[] z, int zOff) {
        u[1] = y;
        for (int i = 2; i < 16; i += 2) {
            u[i] = u[i >>> 1] << 1;
            u[i + 1] = u[i] ^ y;
        }
        int j = (int)x;
        long h = 0L;
        long l = u[j & 0xF] ^ u[j >>> 4 & 0xF] << 4;
        int k = 56;
        do {
            j = (int)(x >>> k);
            long g = u[j & 0xF] ^ u[j >>> 4 & 0xF] << 4;
            l ^= g << k;
            h ^= g >>> -k;
        } while ((k -= 8) > 0);
        for (int p = 0; p < 7; ++p) {
            x = (x & 0xFEFEFEFEFEFEFEFEL) >>> 1;
            h ^= x & y << p >> 63;
        }
        int n = zOff;
        z[n] = z[n] ^ l;
        int n2 = zOff + 1;
        z[n2] = z[n2] ^ h;
    }

    protected static void implSquare(long[] x, long[] zz) {
        Interleave.expand64To128(x, 0, 9, zz, 0);
    }
}

