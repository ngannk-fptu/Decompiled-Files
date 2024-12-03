/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.rfc7748;

import org.bouncycastle.math.raw.Mod;

public abstract class X25519Field {
    public static final int SIZE = 10;
    private static final int M24 = 0xFFFFFF;
    private static final int M25 = 0x1FFFFFF;
    private static final int M26 = 0x3FFFFFF;
    private static final int[] P32 = new int[]{-19, -1, -1, -1, -1, -1, -1, Integer.MAX_VALUE};
    private static final int[] ROOT_NEG_ONE = new int[]{34513072, 59165138, 4688974, 3500415, 6194736, 33281959, 54535759, 32551604, 163342, 5703241};

    protected X25519Field() {
    }

    public static void add(int[] x, int[] y, int[] z) {
        for (int i = 0; i < 10; ++i) {
            z[i] = x[i] + y[i];
        }
    }

    public static void addOne(int[] z) {
        z[0] = z[0] + 1;
    }

    public static void addOne(int[] z, int zOff) {
        int n = zOff;
        z[n] = z[n] + 1;
    }

    public static void apm(int[] x, int[] y, int[] zp, int[] zm) {
        for (int i = 0; i < 10; ++i) {
            int xi = x[i];
            int yi = y[i];
            zp[i] = xi + yi;
            zm[i] = xi - yi;
        }
    }

    public static int areEqual(int[] x, int[] y) {
        int d = 0;
        for (int i = 0; i < 10; ++i) {
            d |= x[i] ^ y[i];
        }
        d = d >>> 1 | d & 1;
        return d - 1 >> 31;
    }

    public static boolean areEqualVar(int[] x, int[] y) {
        return 0 != X25519Field.areEqual(x, y);
    }

    public static void carry(int[] z) {
        int z0 = z[0];
        int z1 = z[1];
        int z2 = z[2];
        int z3 = z[3];
        int z4 = z[4];
        int z5 = z[5];
        int z6 = z[6];
        int z7 = z[7];
        int z8 = z[8];
        int z9 = z[9];
        z2 += z1 >> 26;
        z1 &= 0x3FFFFFF;
        z4 += z3 >> 26;
        z3 &= 0x3FFFFFF;
        z7 += z6 >> 26;
        z6 &= 0x3FFFFFF;
        z9 += z8 >> 26;
        z8 &= 0x3FFFFFF;
        z3 += z2 >> 25;
        z2 &= 0x1FFFFFF;
        z5 += z4 >> 25;
        z4 &= 0x1FFFFFF;
        z8 += z7 >> 25;
        z7 &= 0x1FFFFFF;
        z0 += (z9 >> 25) * 38;
        z9 &= 0x1FFFFFF;
        z1 += z0 >> 26;
        z0 &= 0x3FFFFFF;
        z6 += z5 >> 26;
        z5 &= 0x3FFFFFF;
        z2 += z1 >> 26;
        z1 &= 0x3FFFFFF;
        z4 += z3 >> 26;
        z3 &= 0x3FFFFFF;
        z7 += z6 >> 26;
        z6 &= 0x3FFFFFF;
        z9 += z8 >> 26;
        z8 &= 0x3FFFFFF;
        z[0] = z0;
        z[1] = z1;
        z[2] = z2;
        z[3] = z3;
        z[4] = z4;
        z[5] = z5;
        z[6] = z6;
        z[7] = z7;
        z[8] = z8;
        z[9] = z9;
    }

    public static void cmov(int cond, int[] x, int xOff, int[] z, int zOff) {
        for (int i = 0; i < 10; ++i) {
            int z_i = z[zOff + i];
            int diff = z_i ^ x[xOff + i];
            z[zOff + i] = z_i ^= diff & cond;
        }
    }

    public static void cnegate(int negate, int[] z) {
        int mask = 0 - negate;
        for (int i = 0; i < 10; ++i) {
            z[i] = (z[i] ^ mask) - mask;
        }
    }

    public static void copy(int[] x, int xOff, int[] z, int zOff) {
        for (int i = 0; i < 10; ++i) {
            z[zOff + i] = x[xOff + i];
        }
    }

    public static int[] create() {
        return new int[10];
    }

    public static int[] createTable(int n) {
        return new int[10 * n];
    }

    public static void cswap(int swap, int[] a, int[] b) {
        int mask = 0 - swap;
        for (int i = 0; i < 10; ++i) {
            int ai = a[i];
            int bi = b[i];
            int dummy = mask & (ai ^ bi);
            a[i] = ai ^ dummy;
            b[i] = bi ^ dummy;
        }
    }

    public static void decode(int[] x, int xOff, int[] z) {
        X25519Field.decode128(x, xOff, z, 0);
        X25519Field.decode128(x, xOff + 4, z, 5);
        z[9] = z[9] & 0xFFFFFF;
    }

    public static void decode(byte[] x, int[] z) {
        X25519Field.decode128(x, 0, z, 0);
        X25519Field.decode128(x, 16, z, 5);
        z[9] = z[9] & 0xFFFFFF;
    }

    public static void decode(byte[] x, int xOff, int[] z) {
        X25519Field.decode128(x, xOff, z, 0);
        X25519Field.decode128(x, xOff + 16, z, 5);
        z[9] = z[9] & 0xFFFFFF;
    }

    public static void decode(byte[] x, int xOff, int[] z, int zOff) {
        X25519Field.decode128(x, xOff, z, zOff);
        X25519Field.decode128(x, xOff + 16, z, zOff + 5);
        int n = zOff + 9;
        z[n] = z[n] & 0xFFFFFF;
    }

    private static void decode128(int[] is, int off, int[] z, int zOff) {
        int t0 = is[off + 0];
        int t1 = is[off + 1];
        int t2 = is[off + 2];
        int t3 = is[off + 3];
        z[zOff + 0] = t0 & 0x3FFFFFF;
        z[zOff + 1] = (t1 << 6 | t0 >>> 26) & 0x3FFFFFF;
        z[zOff + 2] = (t2 << 12 | t1 >>> 20) & 0x1FFFFFF;
        z[zOff + 3] = (t3 << 19 | t2 >>> 13) & 0x3FFFFFF;
        z[zOff + 4] = t3 >>> 7;
    }

    private static void decode128(byte[] bs, int off, int[] z, int zOff) {
        int t0 = X25519Field.decode32(bs, off + 0);
        int t1 = X25519Field.decode32(bs, off + 4);
        int t2 = X25519Field.decode32(bs, off + 8);
        int t3 = X25519Field.decode32(bs, off + 12);
        z[zOff + 0] = t0 & 0x3FFFFFF;
        z[zOff + 1] = (t1 << 6 | t0 >>> 26) & 0x3FFFFFF;
        z[zOff + 2] = (t2 << 12 | t1 >>> 20) & 0x1FFFFFF;
        z[zOff + 3] = (t3 << 19 | t2 >>> 13) & 0x3FFFFFF;
        z[zOff + 4] = t3 >>> 7;
    }

    private static int decode32(byte[] bs, int off) {
        int n = bs[off] & 0xFF;
        n |= (bs[++off] & 0xFF) << 8;
        n |= (bs[++off] & 0xFF) << 16;
        return n |= bs[++off] << 24;
    }

    public static void encode(int[] x, int[] z, int zOff) {
        X25519Field.encode128(x, 0, z, zOff);
        X25519Field.encode128(x, 5, z, zOff + 4);
    }

    public static void encode(int[] x, byte[] z) {
        X25519Field.encode128(x, 0, z, 0);
        X25519Field.encode128(x, 5, z, 16);
    }

    public static void encode(int[] x, byte[] z, int zOff) {
        X25519Field.encode128(x, 0, z, zOff);
        X25519Field.encode128(x, 5, z, zOff + 16);
    }

    public static void encode(int[] x, int xOff, byte[] z, int zOff) {
        X25519Field.encode128(x, xOff, z, zOff);
        X25519Field.encode128(x, xOff + 5, z, zOff + 16);
    }

    private static void encode128(int[] x, int xOff, int[] is, int off) {
        int x0 = x[xOff + 0];
        int x1 = x[xOff + 1];
        int x2 = x[xOff + 2];
        int x3 = x[xOff + 3];
        int x4 = x[xOff + 4];
        is[off + 0] = x0 | x1 << 26;
        is[off + 1] = x1 >>> 6 | x2 << 20;
        is[off + 2] = x2 >>> 12 | x3 << 13;
        is[off + 3] = x3 >>> 19 | x4 << 7;
    }

    private static void encode128(int[] x, int xOff, byte[] bs, int off) {
        int x0 = x[xOff + 0];
        int x1 = x[xOff + 1];
        int x2 = x[xOff + 2];
        int x3 = x[xOff + 3];
        int x4 = x[xOff + 4];
        int t0 = x0 | x1 << 26;
        X25519Field.encode32(t0, bs, off + 0);
        int t1 = x1 >>> 6 | x2 << 20;
        X25519Field.encode32(t1, bs, off + 4);
        int t2 = x2 >>> 12 | x3 << 13;
        X25519Field.encode32(t2, bs, off + 8);
        int t3 = x3 >>> 19 | x4 << 7;
        X25519Field.encode32(t3, bs, off + 12);
    }

    private static void encode32(int n, byte[] bs, int off) {
        bs[off] = (byte)n;
        bs[++off] = (byte)(n >>> 8);
        bs[++off] = (byte)(n >>> 16);
        bs[++off] = (byte)(n >>> 24);
    }

    public static void inv(int[] x, int[] z) {
        int[] t = X25519Field.create();
        int[] u = new int[8];
        X25519Field.copy(x, 0, t, 0);
        X25519Field.normalize(t);
        X25519Field.encode(t, u, 0);
        Mod.modOddInverse(P32, u, u);
        X25519Field.decode(u, 0, z);
    }

    public static void invVar(int[] x, int[] z) {
        int[] t = X25519Field.create();
        int[] u = new int[8];
        X25519Field.copy(x, 0, t, 0);
        X25519Field.normalize(t);
        X25519Field.encode(t, u, 0);
        Mod.modOddInverseVar(P32, u, u);
        X25519Field.decode(u, 0, z);
    }

    public static int isOne(int[] x) {
        int d = x[0] ^ 1;
        for (int i = 1; i < 10; ++i) {
            d |= x[i];
        }
        d = d >>> 1 | d & 1;
        return d - 1 >> 31;
    }

    public static boolean isOneVar(int[] x) {
        return 0 != X25519Field.isOne(x);
    }

    public static int isZero(int[] x) {
        int d = 0;
        for (int i = 0; i < 10; ++i) {
            d |= x[i];
        }
        d = d >>> 1 | d & 1;
        return d - 1 >> 31;
    }

    public static boolean isZeroVar(int[] x) {
        return 0 != X25519Field.isZero(x);
    }

    public static void mul(int[] x, int y, int[] z) {
        int x0 = x[0];
        int x1 = x[1];
        int x2 = x[2];
        int x3 = x[3];
        int x4 = x[4];
        int x5 = x[5];
        int x6 = x[6];
        int x7 = x[7];
        int x8 = x[8];
        int x9 = x[9];
        long c0 = (long)x2 * (long)y;
        x2 = (int)c0 & 0x1FFFFFF;
        c0 >>= 25;
        long c1 = (long)x4 * (long)y;
        x4 = (int)c1 & 0x1FFFFFF;
        c1 >>= 25;
        long c2 = (long)x7 * (long)y;
        x7 = (int)c2 & 0x1FFFFFF;
        c2 >>= 25;
        long c3 = (long)x9 * (long)y;
        x9 = (int)c3 & 0x1FFFFFF;
        c3 >>= 25;
        c3 *= 38L;
        z[0] = (int)(c3 += (long)x0 * (long)y) & 0x3FFFFFF;
        c3 >>= 26;
        z[5] = (int)(c1 += (long)x5 * (long)y) & 0x3FFFFFF;
        c1 >>= 26;
        z[1] = (int)(c3 += (long)x1 * (long)y) & 0x3FFFFFF;
        z[3] = (int)(c0 += (long)x3 * (long)y) & 0x3FFFFFF;
        z[6] = (int)(c1 += (long)x6 * (long)y) & 0x3FFFFFF;
        z[8] = (int)(c2 += (long)x8 * (long)y) & 0x3FFFFFF;
        z[2] = x2 + (int)(c3 >>= 26);
        z[4] = x4 + (int)(c0 >>= 26);
        z[7] = x7 + (int)(c1 >>= 26);
        z[9] = x9 + (int)(c2 >>= 26);
    }

    public static void mul(int[] x, int[] y, int[] z) {
        int x0 = x[0];
        int y0 = y[0];
        int x1 = x[1];
        int y1 = y[1];
        int x2 = x[2];
        int y2 = y[2];
        int x3 = x[3];
        int y3 = y[3];
        int x4 = x[4];
        int y4 = y[4];
        int u0 = x[5];
        int v0 = y[5];
        int u1 = x[6];
        int v1 = y[6];
        int u2 = x[7];
        int v2 = y[7];
        int u3 = x[8];
        int v3 = y[8];
        int u4 = x[9];
        int v4 = y[9];
        long a0 = (long)x0 * (long)y0;
        long a1 = (long)x0 * (long)y1 + (long)x1 * (long)y0;
        long a2 = (long)x0 * (long)y2 + (long)x1 * (long)y1 + (long)x2 * (long)y0;
        long a3 = (long)x1 * (long)y2 + (long)x2 * (long)y1;
        a3 <<= 1;
        a3 += (long)x0 * (long)y3 + (long)x3 * (long)y0;
        long a4 = (long)x2 * (long)y2;
        a4 <<= 1;
        a4 += (long)x0 * (long)y4 + (long)x1 * (long)y3 + (long)x3 * (long)y1 + (long)x4 * (long)y0;
        long a5 = (long)x1 * (long)y4 + (long)x2 * (long)y3 + (long)x3 * (long)y2 + (long)x4 * (long)y1;
        a5 <<= 1;
        long a6 = (long)x2 * (long)y4 + (long)x4 * (long)y2;
        a6 <<= 1;
        a6 += (long)x3 * (long)y3;
        long a7 = (long)x3 * (long)y4 + (long)x4 * (long)y3;
        long a8 = (long)x4 * (long)y4;
        a8 <<= 1;
        long b0 = (long)u0 * (long)v0;
        long b1 = (long)u0 * (long)v1 + (long)u1 * (long)v0;
        long b2 = (long)u0 * (long)v2 + (long)u1 * (long)v1 + (long)u2 * (long)v0;
        long b3 = (long)u1 * (long)v2 + (long)u2 * (long)v1;
        b3 <<= 1;
        b3 += (long)u0 * (long)v3 + (long)u3 * (long)v0;
        long b4 = (long)u2 * (long)v2;
        b4 <<= 1;
        b4 += (long)u0 * (long)v4 + (long)u1 * (long)v3 + (long)u3 * (long)v1 + (long)u4 * (long)v0;
        long b5 = (long)u1 * (long)v4 + (long)u2 * (long)v3 + (long)u3 * (long)v2 + (long)u4 * (long)v1;
        long b6 = (long)u2 * (long)v4 + (long)u4 * (long)v2;
        b6 <<= 1;
        long b7 = (long)u3 * (long)v4 + (long)u4 * (long)v3;
        long b8 = (long)u4 * (long)v4;
        a0 -= b5 * 76L;
        a1 -= (b6 += (long)u3 * (long)v3) * 38L;
        a2 -= b7 * 38L;
        a3 -= b8 * 76L;
        a5 -= b0;
        a6 -= b1;
        a7 -= b2;
        a8 -= b3;
        x0 += u0;
        y0 += v0;
        x1 += u1;
        y1 += v1;
        x2 += u2;
        y2 += v2;
        x3 += u3;
        y3 += v3;
        x4 += u4;
        y4 += v4;
        long c0 = (long)x0 * (long)y0;
        long c1 = (long)x0 * (long)y1 + (long)x1 * (long)y0;
        long c2 = (long)x0 * (long)y2 + (long)x1 * (long)y1 + (long)x2 * (long)y0;
        long c3 = (long)x1 * (long)y2 + (long)x2 * (long)y1;
        c3 <<= 1;
        c3 += (long)x0 * (long)y3 + (long)x3 * (long)y0;
        long c4 = (long)x2 * (long)y2;
        c4 <<= 1;
        c4 += (long)x0 * (long)y4 + (long)x1 * (long)y3 + (long)x3 * (long)y1 + (long)x4 * (long)y0;
        long c5 = (long)x1 * (long)y4 + (long)x2 * (long)y3 + (long)x3 * (long)y2 + (long)x4 * (long)y1;
        c5 <<= 1;
        long c6 = (long)x2 * (long)y4 + (long)x4 * (long)y2;
        c6 <<= 1;
        c6 += (long)x3 * (long)y3;
        long c7 = (long)x3 * (long)y4 + (long)x4 * (long)y3;
        long c8 = (long)x4 * (long)y4;
        c8 <<= 1;
        long t = a8 + (c3 - a3);
        int z8 = (int)t & 0x3FFFFFF;
        t >>= 26;
        int z9 = (int)(t += c4 - a4 - b4) & 0x1FFFFFF;
        t >>= 25;
        t = a0 + (t + c5 - a5) * 38L;
        z[0] = (int)t & 0x3FFFFFF;
        t >>= 26;
        z[1] = (int)(t += a1 + (c6 - a6) * 38L) & 0x3FFFFFF;
        t >>= 26;
        z[2] = (int)(t += a2 + (c7 - a7) * 38L) & 0x1FFFFFF;
        t >>= 25;
        z[3] = (int)(t += a3 + (c8 - a8) * 38L) & 0x3FFFFFF;
        t >>= 26;
        z[4] = (int)(t += a4 + b4 * 38L) & 0x1FFFFFF;
        t >>= 25;
        z[5] = (int)(t += a5 + (c0 - a0)) & 0x3FFFFFF;
        t >>= 26;
        z[6] = (int)(t += a6 + (c1 - a1)) & 0x3FFFFFF;
        t >>= 26;
        z[7] = (int)(t += a7 + (c2 - a2)) & 0x1FFFFFF;
        t >>= 25;
        z[8] = (int)(t += (long)z8) & 0x3FFFFFF;
        z[9] = z9 + (int)(t >>= 26);
    }

    public static void negate(int[] x, int[] z) {
        for (int i = 0; i < 10; ++i) {
            z[i] = -x[i];
        }
    }

    public static void normalize(int[] z) {
        int x = z[9] >>> 23 & 1;
        X25519Field.reduce(z, x);
        X25519Field.reduce(z, -x);
    }

    public static void one(int[] z) {
        z[0] = 1;
        for (int i = 1; i < 10; ++i) {
            z[i] = 0;
        }
    }

    private static void powPm5d8(int[] x, int[] rx2, int[] rz) {
        int[] x2 = rx2;
        X25519Field.sqr(x, x2);
        X25519Field.mul(x, x2, x2);
        int[] x3 = X25519Field.create();
        X25519Field.sqr(x2, x3);
        X25519Field.mul(x, x3, x3);
        int[] x5 = x3;
        X25519Field.sqr(x3, 2, x5);
        X25519Field.mul(x2, x5, x5);
        int[] x10 = X25519Field.create();
        X25519Field.sqr(x5, 5, x10);
        X25519Field.mul(x5, x10, x10);
        int[] x15 = X25519Field.create();
        X25519Field.sqr(x10, 5, x15);
        X25519Field.mul(x5, x15, x15);
        int[] x25 = x5;
        X25519Field.sqr(x15, 10, x25);
        X25519Field.mul(x10, x25, x25);
        int[] x50 = x10;
        X25519Field.sqr(x25, 25, x50);
        X25519Field.mul(x25, x50, x50);
        int[] x75 = x15;
        X25519Field.sqr(x50, 25, x75);
        X25519Field.mul(x25, x75, x75);
        int[] x125 = x25;
        X25519Field.sqr(x75, 50, x125);
        X25519Field.mul(x50, x125, x125);
        int[] x250 = x50;
        X25519Field.sqr(x125, 125, x250);
        X25519Field.mul(x125, x250, x250);
        int[] t = x125;
        X25519Field.sqr(x250, 2, t);
        X25519Field.mul(t, x, rz);
    }

    private static void reduce(int[] z, int x) {
        int t = z[9];
        int z9 = t & 0xFFFFFF;
        t = (t >> 24) + x;
        long cc = t * 19;
        z[0] = (int)(cc += (long)z[0]) & 0x3FFFFFF;
        cc >>= 26;
        z[1] = (int)(cc += (long)z[1]) & 0x3FFFFFF;
        cc >>= 26;
        z[2] = (int)(cc += (long)z[2]) & 0x1FFFFFF;
        cc >>= 25;
        z[3] = (int)(cc += (long)z[3]) & 0x3FFFFFF;
        cc >>= 26;
        z[4] = (int)(cc += (long)z[4]) & 0x1FFFFFF;
        cc >>= 25;
        z[5] = (int)(cc += (long)z[5]) & 0x3FFFFFF;
        cc >>= 26;
        z[6] = (int)(cc += (long)z[6]) & 0x3FFFFFF;
        cc >>= 26;
        z[7] = (int)(cc += (long)z[7]) & 0x1FFFFFF;
        cc >>= 25;
        z[8] = (int)(cc += (long)z[8]) & 0x3FFFFFF;
        z[9] = z9 + (int)(cc >>= 26);
    }

    public static void sqr(int[] x, int[] z) {
        int x0 = x[0];
        int x1 = x[1];
        int x2 = x[2];
        int x3 = x[3];
        int x4 = x[4];
        int u0 = x[5];
        int u1 = x[6];
        int u2 = x[7];
        int u3 = x[8];
        int u4 = x[9];
        int x1_2 = x1 * 2;
        int x2_2 = x2 * 2;
        int x3_2 = x3 * 2;
        int x4_2 = x4 * 2;
        long a0 = (long)x0 * (long)x0;
        long a1 = (long)x0 * (long)x1_2;
        long a2 = (long)x0 * (long)x2_2 + (long)x1 * (long)x1;
        long a3 = (long)x1_2 * (long)x2_2 + (long)x0 * (long)x3_2;
        long a4 = (long)x2 * (long)x2_2 + (long)x0 * (long)x4_2 + (long)x1 * (long)x3_2;
        long a5 = (long)x1_2 * (long)x4_2 + (long)x2_2 * (long)x3_2;
        long a6 = (long)x2_2 * (long)x4_2 + (long)x3 * (long)x3;
        long a7 = (long)x3 * (long)x4_2;
        long a8 = (long)x4 * (long)x4_2;
        int u1_2 = u1 * 2;
        int u2_2 = u2 * 2;
        int u3_2 = u3 * 2;
        int u4_2 = u4 * 2;
        long b0 = (long)u0 * (long)u0;
        long b1 = (long)u0 * (long)u1_2;
        long b2 = (long)u0 * (long)u2_2 + (long)u1 * (long)u1;
        long b3 = (long)u1_2 * (long)u2_2 + (long)u0 * (long)u3_2;
        long b4 = (long)u2 * (long)u2_2 + (long)u0 * (long)u4_2 + (long)u1 * (long)u3_2;
        long b5 = (long)u1_2 * (long)u4_2 + (long)u2_2 * (long)u3_2;
        long b6 = (long)u2_2 * (long)u4_2 + (long)u3 * (long)u3;
        long b7 = (long)u3 * (long)u4_2;
        long b8 = (long)u4 * (long)u4_2;
        a0 -= b5 * 38L;
        a1 -= b6 * 38L;
        a2 -= b7 * 38L;
        a3 -= b8 * 38L;
        a5 -= b0;
        a6 -= b1;
        a7 -= b2;
        a8 -= b3;
        x1_2 = (x1 += u1) * 2;
        x2_2 = (x2 += u2) * 2;
        x3_2 = (x3 += u3) * 2;
        x4_2 = (x4 += u4) * 2;
        long c0 = (long)(x0 += u0) * (long)x0;
        long c1 = (long)x0 * (long)x1_2;
        long c2 = (long)x0 * (long)x2_2 + (long)x1 * (long)x1;
        long c3 = (long)x1_2 * (long)x2_2 + (long)x0 * (long)x3_2;
        long c4 = (long)x2 * (long)x2_2 + (long)x0 * (long)x4_2 + (long)x1 * (long)x3_2;
        long c5 = (long)x1_2 * (long)x4_2 + (long)x2_2 * (long)x3_2;
        long c6 = (long)x2_2 * (long)x4_2 + (long)x3 * (long)x3;
        long c7 = (long)x3 * (long)x4_2;
        long c8 = (long)x4 * (long)x4_2;
        long t = a8 + (c3 - a3);
        int z8 = (int)t & 0x3FFFFFF;
        t >>= 26;
        int z9 = (int)(t += c4 - a4 - b4) & 0x1FFFFFF;
        t >>= 25;
        t = a0 + (t + c5 - a5) * 38L;
        z[0] = (int)t & 0x3FFFFFF;
        t >>= 26;
        z[1] = (int)(t += a1 + (c6 - a6) * 38L) & 0x3FFFFFF;
        t >>= 26;
        z[2] = (int)(t += a2 + (c7 - a7) * 38L) & 0x1FFFFFF;
        t >>= 25;
        z[3] = (int)(t += a3 + (c8 - a8) * 38L) & 0x3FFFFFF;
        t >>= 26;
        z[4] = (int)(t += a4 + b4 * 38L) & 0x1FFFFFF;
        t >>= 25;
        z[5] = (int)(t += a5 + (c0 - a0)) & 0x3FFFFFF;
        t >>= 26;
        z[6] = (int)(t += a6 + (c1 - a1)) & 0x3FFFFFF;
        t >>= 26;
        z[7] = (int)(t += a7 + (c2 - a2)) & 0x1FFFFFF;
        t >>= 25;
        z[8] = (int)(t += (long)z8) & 0x3FFFFFF;
        z[9] = z9 + (int)(t >>= 26);
    }

    public static void sqr(int[] x, int n, int[] z) {
        X25519Field.sqr(x, z);
        while (--n > 0) {
            X25519Field.sqr(z, z);
        }
    }

    public static boolean sqrtRatioVar(int[] u, int[] v, int[] z) {
        int[] uv3 = X25519Field.create();
        int[] uv7 = X25519Field.create();
        X25519Field.mul(u, v, uv3);
        X25519Field.sqr(v, uv7);
        X25519Field.mul(uv3, uv7, uv3);
        X25519Field.sqr(uv7, uv7);
        X25519Field.mul(uv7, uv3, uv7);
        int[] t = X25519Field.create();
        int[] x = X25519Field.create();
        X25519Field.powPm5d8(uv7, t, x);
        X25519Field.mul(x, uv3, x);
        int[] vx2 = X25519Field.create();
        X25519Field.sqr(x, vx2);
        X25519Field.mul(vx2, v, vx2);
        X25519Field.sub(vx2, u, t);
        X25519Field.normalize(t);
        if (X25519Field.isZeroVar(t)) {
            X25519Field.copy(x, 0, z, 0);
            return true;
        }
        X25519Field.add(vx2, u, t);
        X25519Field.normalize(t);
        if (X25519Field.isZeroVar(t)) {
            X25519Field.mul(x, ROOT_NEG_ONE, z);
            return true;
        }
        return false;
    }

    public static void sub(int[] x, int[] y, int[] z) {
        for (int i = 0; i < 10; ++i) {
            z[i] = x[i] - y[i];
        }
    }

    public static void subOne(int[] z) {
        z[0] = z[0] - 1;
    }

    public static void zero(int[] z) {
        for (int i = 0; i < 10; ++i) {
            z[i] = 0;
        }
    }
}

