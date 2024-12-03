/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.rfc7748;

import org.bouncycastle.math.raw.Mod;

public abstract class X448Field {
    public static final int SIZE = 16;
    private static final int M28 = 0xFFFFFFF;
    private static final long U32 = 0xFFFFFFFFL;
    private static final int[] P32 = new int[]{-1, -1, -1, -1, -1, -1, -1, -2, -1, -1, -1, -1, -1, -1};

    protected X448Field() {
    }

    public static void add(int[] x, int[] y, int[] z) {
        for (int i = 0; i < 16; ++i) {
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

    public static int areEqual(int[] x, int[] y) {
        int d = 0;
        for (int i = 0; i < 16; ++i) {
            d |= x[i] ^ y[i];
        }
        d = d >>> 1 | d & 1;
        return d - 1 >> 31;
    }

    public static boolean areEqualVar(int[] x, int[] y) {
        return 0 != X448Field.areEqual(x, y);
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
        int z10 = z[10];
        int z11 = z[11];
        int z12 = z[12];
        int z13 = z[13];
        int z14 = z[14];
        int z15 = z[15];
        z1 += z0 >>> 28;
        z0 &= 0xFFFFFFF;
        z5 += z4 >>> 28;
        z4 &= 0xFFFFFFF;
        z9 += z8 >>> 28;
        z8 &= 0xFFFFFFF;
        z13 += z12 >>> 28;
        z12 &= 0xFFFFFFF;
        z2 += z1 >>> 28;
        z1 &= 0xFFFFFFF;
        z6 += z5 >>> 28;
        z5 &= 0xFFFFFFF;
        z10 += z9 >>> 28;
        z9 &= 0xFFFFFFF;
        z14 += z13 >>> 28;
        z13 &= 0xFFFFFFF;
        z3 += z2 >>> 28;
        z2 &= 0xFFFFFFF;
        z7 += z6 >>> 28;
        z6 &= 0xFFFFFFF;
        z11 += z10 >>> 28;
        z10 &= 0xFFFFFFF;
        z15 += z14 >>> 28;
        z14 &= 0xFFFFFFF;
        int t = z15 >>> 28;
        z15 &= 0xFFFFFFF;
        z0 += t;
        z8 += t;
        z4 += z3 >>> 28;
        z3 &= 0xFFFFFFF;
        z8 += z7 >>> 28;
        z7 &= 0xFFFFFFF;
        z12 += z11 >>> 28;
        z11 &= 0xFFFFFFF;
        z1 += z0 >>> 28;
        z0 &= 0xFFFFFFF;
        z5 += z4 >>> 28;
        z4 &= 0xFFFFFFF;
        z9 += z8 >>> 28;
        z8 &= 0xFFFFFFF;
        z13 += z12 >>> 28;
        z12 &= 0xFFFFFFF;
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
        z[10] = z10;
        z[11] = z11;
        z[12] = z12;
        z[13] = z13;
        z[14] = z14;
        z[15] = z15;
    }

    public static void cmov(int cond, int[] x, int xOff, int[] z, int zOff) {
        for (int i = 0; i < 16; ++i) {
            int z_i = z[zOff + i];
            int diff = z_i ^ x[xOff + i];
            z[zOff + i] = z_i ^= diff & cond;
        }
    }

    public static void cnegate(int negate, int[] z) {
        int[] t = X448Field.create();
        X448Field.sub(t, z, t);
        X448Field.cmov(-negate, t, 0, z, 0);
    }

    public static void copy(int[] x, int xOff, int[] z, int zOff) {
        for (int i = 0; i < 16; ++i) {
            z[zOff + i] = x[xOff + i];
        }
    }

    public static int[] create() {
        return new int[16];
    }

    public static int[] createTable(int n) {
        return new int[16 * n];
    }

    public static void cswap(int swap, int[] a, int[] b) {
        int mask = 0 - swap;
        for (int i = 0; i < 16; ++i) {
            int ai = a[i];
            int bi = b[i];
            int dummy = mask & (ai ^ bi);
            a[i] = ai ^ dummy;
            b[i] = bi ^ dummy;
        }
    }

    public static void decode(int[] x, int xOff, int[] z) {
        X448Field.decode224(x, xOff, z, 0);
        X448Field.decode224(x, xOff + 7, z, 8);
    }

    public static void decode(byte[] x, int[] z) {
        X448Field.decode56(x, 0, z, 0);
        X448Field.decode56(x, 7, z, 2);
        X448Field.decode56(x, 14, z, 4);
        X448Field.decode56(x, 21, z, 6);
        X448Field.decode56(x, 28, z, 8);
        X448Field.decode56(x, 35, z, 10);
        X448Field.decode56(x, 42, z, 12);
        X448Field.decode56(x, 49, z, 14);
    }

    public static void decode(byte[] x, int xOff, int[] z) {
        X448Field.decode56(x, xOff, z, 0);
        X448Field.decode56(x, xOff + 7, z, 2);
        X448Field.decode56(x, xOff + 14, z, 4);
        X448Field.decode56(x, xOff + 21, z, 6);
        X448Field.decode56(x, xOff + 28, z, 8);
        X448Field.decode56(x, xOff + 35, z, 10);
        X448Field.decode56(x, xOff + 42, z, 12);
        X448Field.decode56(x, xOff + 49, z, 14);
    }

    public static void decode(byte[] x, int xOff, int[] z, int zOff) {
        X448Field.decode56(x, xOff, z, zOff);
        X448Field.decode56(x, xOff + 7, z, zOff + 2);
        X448Field.decode56(x, xOff + 14, z, zOff + 4);
        X448Field.decode56(x, xOff + 21, z, zOff + 6);
        X448Field.decode56(x, xOff + 28, z, zOff + 8);
        X448Field.decode56(x, xOff + 35, z, zOff + 10);
        X448Field.decode56(x, xOff + 42, z, zOff + 12);
        X448Field.decode56(x, xOff + 49, z, zOff + 14);
    }

    private static void decode224(int[] x, int xOff, int[] z, int zOff) {
        int x0 = x[xOff + 0];
        int x1 = x[xOff + 1];
        int x2 = x[xOff + 2];
        int x3 = x[xOff + 3];
        int x4 = x[xOff + 4];
        int x5 = x[xOff + 5];
        int x6 = x[xOff + 6];
        z[zOff + 0] = x0 & 0xFFFFFFF;
        z[zOff + 1] = (x0 >>> 28 | x1 << 4) & 0xFFFFFFF;
        z[zOff + 2] = (x1 >>> 24 | x2 << 8) & 0xFFFFFFF;
        z[zOff + 3] = (x2 >>> 20 | x3 << 12) & 0xFFFFFFF;
        z[zOff + 4] = (x3 >>> 16 | x4 << 16) & 0xFFFFFFF;
        z[zOff + 5] = (x4 >>> 12 | x5 << 20) & 0xFFFFFFF;
        z[zOff + 6] = (x5 >>> 8 | x6 << 24) & 0xFFFFFFF;
        z[zOff + 7] = x6 >>> 4;
    }

    private static int decode24(byte[] bs, int off) {
        int n = bs[off] & 0xFF;
        n |= (bs[++off] & 0xFF) << 8;
        return n |= (bs[++off] & 0xFF) << 16;
    }

    private static int decode32(byte[] bs, int off) {
        int n = bs[off] & 0xFF;
        n |= (bs[++off] & 0xFF) << 8;
        n |= (bs[++off] & 0xFF) << 16;
        return n |= bs[++off] << 24;
    }

    private static void decode56(byte[] bs, int off, int[] z, int zOff) {
        int lo = X448Field.decode32(bs, off);
        int hi = X448Field.decode24(bs, off + 4);
        z[zOff] = lo & 0xFFFFFFF;
        z[zOff + 1] = lo >>> 28 | hi << 4;
    }

    public static void encode(int[] x, int[] z, int zOff) {
        X448Field.encode224(x, 0, z, zOff);
        X448Field.encode224(x, 8, z, zOff + 7);
    }

    public static void encode(int[] x, byte[] z) {
        X448Field.encode56(x, 0, z, 0);
        X448Field.encode56(x, 2, z, 7);
        X448Field.encode56(x, 4, z, 14);
        X448Field.encode56(x, 6, z, 21);
        X448Field.encode56(x, 8, z, 28);
        X448Field.encode56(x, 10, z, 35);
        X448Field.encode56(x, 12, z, 42);
        X448Field.encode56(x, 14, z, 49);
    }

    public static void encode(int[] x, byte[] z, int zOff) {
        X448Field.encode56(x, 0, z, zOff);
        X448Field.encode56(x, 2, z, zOff + 7);
        X448Field.encode56(x, 4, z, zOff + 14);
        X448Field.encode56(x, 6, z, zOff + 21);
        X448Field.encode56(x, 8, z, zOff + 28);
        X448Field.encode56(x, 10, z, zOff + 35);
        X448Field.encode56(x, 12, z, zOff + 42);
        X448Field.encode56(x, 14, z, zOff + 49);
    }

    public static void encode(int[] x, int xOff, byte[] z, int zOff) {
        X448Field.encode56(x, xOff, z, zOff);
        X448Field.encode56(x, xOff + 2, z, zOff + 7);
        X448Field.encode56(x, xOff + 4, z, zOff + 14);
        X448Field.encode56(x, xOff + 6, z, zOff + 21);
        X448Field.encode56(x, xOff + 8, z, zOff + 28);
        X448Field.encode56(x, xOff + 10, z, zOff + 35);
        X448Field.encode56(x, xOff + 12, z, zOff + 42);
        X448Field.encode56(x, xOff + 14, z, zOff + 49);
    }

    private static void encode224(int[] x, int xOff, int[] is, int off) {
        int x0 = x[xOff + 0];
        int x1 = x[xOff + 1];
        int x2 = x[xOff + 2];
        int x3 = x[xOff + 3];
        int x4 = x[xOff + 4];
        int x5 = x[xOff + 5];
        int x6 = x[xOff + 6];
        int x7 = x[xOff + 7];
        is[off + 0] = x0 | x1 << 28;
        is[off + 1] = x1 >>> 4 | x2 << 24;
        is[off + 2] = x2 >>> 8 | x3 << 20;
        is[off + 3] = x3 >>> 12 | x4 << 16;
        is[off + 4] = x4 >>> 16 | x5 << 12;
        is[off + 5] = x5 >>> 20 | x6 << 8;
        is[off + 6] = x6 >>> 24 | x7 << 4;
    }

    private static void encode24(int n, byte[] bs, int off) {
        bs[off] = (byte)n;
        bs[++off] = (byte)(n >>> 8);
        bs[++off] = (byte)(n >>> 16);
    }

    private static void encode32(int n, byte[] bs, int off) {
        bs[off] = (byte)n;
        bs[++off] = (byte)(n >>> 8);
        bs[++off] = (byte)(n >>> 16);
        bs[++off] = (byte)(n >>> 24);
    }

    private static void encode56(int[] x, int xOff, byte[] bs, int off) {
        int lo = x[xOff];
        int hi = x[xOff + 1];
        X448Field.encode32(lo | hi << 28, bs, off);
        X448Field.encode24(hi >>> 4, bs, off + 4);
    }

    public static void inv(int[] x, int[] z) {
        int[] t = X448Field.create();
        int[] u = new int[14];
        X448Field.copy(x, 0, t, 0);
        X448Field.normalize(t);
        X448Field.encode(t, u, 0);
        Mod.modOddInverse(P32, u, u);
        X448Field.decode(u, 0, z);
    }

    public static void invVar(int[] x, int[] z) {
        int[] t = X448Field.create();
        int[] u = new int[14];
        X448Field.copy(x, 0, t, 0);
        X448Field.normalize(t);
        X448Field.encode(t, u, 0);
        Mod.modOddInverseVar(P32, u, u);
        X448Field.decode(u, 0, z);
    }

    public static int isOne(int[] x) {
        int d = x[0] ^ 1;
        for (int i = 1; i < 16; ++i) {
            d |= x[i];
        }
        d = d >>> 1 | d & 1;
        return d - 1 >> 31;
    }

    public static boolean isOneVar(int[] x) {
        return 0 != X448Field.isOne(x);
    }

    public static int isZero(int[] x) {
        int d = 0;
        for (int i = 0; i < 16; ++i) {
            d |= x[i];
        }
        d = d >>> 1 | d & 1;
        return d - 1 >> 31;
    }

    public static boolean isZeroVar(int[] x) {
        return 0 != X448Field.isZero(x);
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
        int x10 = x[10];
        int x11 = x[11];
        int x12 = x[12];
        int x13 = x[13];
        int x14 = x[14];
        int x15 = x[15];
        long c = (long)x1 * (long)y;
        int z1 = (int)c & 0xFFFFFFF;
        c >>>= 28;
        long d = (long)x5 * (long)y;
        int z5 = (int)d & 0xFFFFFFF;
        d >>>= 28;
        long e = (long)x9 * (long)y;
        int z9 = (int)e & 0xFFFFFFF;
        e >>>= 28;
        long f = (long)x13 * (long)y;
        int z13 = (int)f & 0xFFFFFFF;
        f >>>= 28;
        z[2] = (int)(c += (long)x2 * (long)y) & 0xFFFFFFF;
        c >>>= 28;
        z[6] = (int)(d += (long)x6 * (long)y) & 0xFFFFFFF;
        d >>>= 28;
        z[10] = (int)(e += (long)x10 * (long)y) & 0xFFFFFFF;
        e >>>= 28;
        z[14] = (int)(f += (long)x14 * (long)y) & 0xFFFFFFF;
        f >>>= 28;
        z[3] = (int)(c += (long)x3 * (long)y) & 0xFFFFFFF;
        c >>>= 28;
        z[7] = (int)(d += (long)x7 * (long)y) & 0xFFFFFFF;
        d >>>= 28;
        z[11] = (int)(e += (long)x11 * (long)y) & 0xFFFFFFF;
        e >>>= 28;
        z[15] = (int)(f += (long)x15 * (long)y) & 0xFFFFFFF;
        d += (f >>>= 28);
        z[4] = (int)(c += (long)x4 * (long)y) & 0xFFFFFFF;
        c >>>= 28;
        z[8] = (int)(d += (long)x8 * (long)y) & 0xFFFFFFF;
        z[12] = (int)(e += (long)x12 * (long)y) & 0xFFFFFFF;
        z[0] = (int)(f += (long)x0 * (long)y) & 0xFFFFFFF;
        z[1] = z1 + (int)(f >>>= 28);
        z[5] = z5 + (int)c;
        z[9] = z9 + (int)(d >>>= 28);
        z[13] = z13 + (int)(e >>>= 28);
    }

    public static void mul(int[] x, int[] y, int[] z) {
        int x0 = x[0];
        int x1 = x[1];
        int x2 = x[2];
        int x3 = x[3];
        int x4 = x[4];
        int x5 = x[5];
        int x6 = x[6];
        int x7 = x[7];
        int u0 = x[8];
        int u1 = x[9];
        int u2 = x[10];
        int u3 = x[11];
        int u4 = x[12];
        int u5 = x[13];
        int u6 = x[14];
        int u7 = x[15];
        int y0 = y[0];
        int y1 = y[1];
        int y2 = y[2];
        int y3 = y[3];
        int y4 = y[4];
        int y5 = y[5];
        int y6 = y[6];
        int y7 = y[7];
        int v0 = y[8];
        int v1 = y[9];
        int v2 = y[10];
        int v3 = y[11];
        int v4 = y[12];
        int v5 = y[13];
        int v6 = y[14];
        int v7 = y[15];
        int s0 = x0 + u0;
        int s1 = x1 + u1;
        int s2 = x2 + u2;
        int s3 = x3 + u3;
        int s4 = x4 + u4;
        int s5 = x5 + u5;
        int s6 = x6 + u6;
        int s7 = x7 + u7;
        int t0 = y0 + v0;
        int t1 = y1 + v1;
        int t2 = y2 + v2;
        int t3 = y3 + v3;
        int t4 = y4 + v4;
        int t5 = y5 + v5;
        int t6 = y6 + v6;
        int t7 = y7 + v7;
        long f0 = (long)x0 * (long)y0;
        long f8 = (long)x7 * (long)y1 + (long)x6 * (long)y2 + (long)x5 * (long)y3 + (long)x4 * (long)y4 + (long)x3 * (long)y5 + (long)x2 * (long)y6 + (long)x1 * (long)y7;
        long g0 = (long)u0 * (long)v0;
        long g8 = (long)u7 * (long)v1 + (long)u6 * (long)v2 + (long)u5 * (long)v3 + (long)u4 * (long)v4 + (long)u3 * (long)v5 + (long)u2 * (long)v6 + (long)u1 * (long)v7;
        long h0 = (long)s0 * (long)t0;
        long h8 = (long)s7 * (long)t1 + (long)s6 * (long)t2 + (long)s5 * (long)t3 + (long)s4 * (long)t4 + (long)s3 * (long)t5 + (long)s2 * (long)t6 + (long)s1 * (long)t7;
        long c = f0 + g0 + h8 - f8;
        int z0 = (int)c & 0xFFFFFFF;
        c >>>= 28;
        long d = g8 + h0 - f0 + h8;
        int z8 = (int)d & 0xFFFFFFF;
        d >>>= 28;
        long f1 = (long)x1 * (long)y0 + (long)x0 * (long)y1;
        long f9 = (long)x7 * (long)y2 + (long)x6 * (long)y3 + (long)x5 * (long)y4 + (long)x4 * (long)y5 + (long)x3 * (long)y6 + (long)x2 * (long)y7;
        long g1 = (long)u1 * (long)v0 + (long)u0 * (long)v1;
        long g9 = (long)u7 * (long)v2 + (long)u6 * (long)v3 + (long)u5 * (long)v4 + (long)u4 * (long)v5 + (long)u3 * (long)v6 + (long)u2 * (long)v7;
        long h1 = (long)s1 * (long)t0 + (long)s0 * (long)t1;
        long h9 = (long)s7 * (long)t2 + (long)s6 * (long)t3 + (long)s5 * (long)t4 + (long)s4 * (long)t5 + (long)s3 * (long)t6 + (long)s2 * (long)t7;
        int z1 = (int)(c += f1 + g1 + h9 - f9) & 0xFFFFFFF;
        c >>>= 28;
        int z9 = (int)(d += g9 + h1 - f1 + h9) & 0xFFFFFFF;
        d >>>= 28;
        long f2 = (long)x2 * (long)y0 + (long)x1 * (long)y1 + (long)x0 * (long)y2;
        long f10 = (long)x7 * (long)y3 + (long)x6 * (long)y4 + (long)x5 * (long)y5 + (long)x4 * (long)y6 + (long)x3 * (long)y7;
        long g2 = (long)u2 * (long)v0 + (long)u1 * (long)v1 + (long)u0 * (long)v2;
        long g10 = (long)u7 * (long)v3 + (long)u6 * (long)v4 + (long)u5 * (long)v5 + (long)u4 * (long)v6 + (long)u3 * (long)v7;
        long h2 = (long)s2 * (long)t0 + (long)s1 * (long)t1 + (long)s0 * (long)t2;
        long h10 = (long)s7 * (long)t3 + (long)s6 * (long)t4 + (long)s5 * (long)t5 + (long)s4 * (long)t6 + (long)s3 * (long)t7;
        int z2 = (int)(c += f2 + g2 + h10 - f10) & 0xFFFFFFF;
        c >>>= 28;
        int z10 = (int)(d += g10 + h2 - f2 + h10) & 0xFFFFFFF;
        d >>>= 28;
        long f3 = (long)x3 * (long)y0 + (long)x2 * (long)y1 + (long)x1 * (long)y2 + (long)x0 * (long)y3;
        long f11 = (long)x7 * (long)y4 + (long)x6 * (long)y5 + (long)x5 * (long)y6 + (long)x4 * (long)y7;
        long g3 = (long)u3 * (long)v0 + (long)u2 * (long)v1 + (long)u1 * (long)v2 + (long)u0 * (long)v3;
        long g11 = (long)u7 * (long)v4 + (long)u6 * (long)v5 + (long)u5 * (long)v6 + (long)u4 * (long)v7;
        long h3 = (long)s3 * (long)t0 + (long)s2 * (long)t1 + (long)s1 * (long)t2 + (long)s0 * (long)t3;
        long h11 = (long)s7 * (long)t4 + (long)s6 * (long)t5 + (long)s5 * (long)t6 + (long)s4 * (long)t7;
        int z3 = (int)(c += f3 + g3 + h11 - f11) & 0xFFFFFFF;
        c >>>= 28;
        int z11 = (int)(d += g11 + h3 - f3 + h11) & 0xFFFFFFF;
        d >>>= 28;
        long f4 = (long)x4 * (long)y0 + (long)x3 * (long)y1 + (long)x2 * (long)y2 + (long)x1 * (long)y3 + (long)x0 * (long)y4;
        long f12 = (long)x7 * (long)y5 + (long)x6 * (long)y6 + (long)x5 * (long)y7;
        long g4 = (long)u4 * (long)v0 + (long)u3 * (long)v1 + (long)u2 * (long)v2 + (long)u1 * (long)v3 + (long)u0 * (long)v4;
        long g12 = (long)u7 * (long)v5 + (long)u6 * (long)v6 + (long)u5 * (long)v7;
        long h4 = (long)s4 * (long)t0 + (long)s3 * (long)t1 + (long)s2 * (long)t2 + (long)s1 * (long)t3 + (long)s0 * (long)t4;
        long h12 = (long)s7 * (long)t5 + (long)s6 * (long)t6 + (long)s5 * (long)t7;
        int z4 = (int)(c += f4 + g4 + h12 - f12) & 0xFFFFFFF;
        c >>>= 28;
        int z12 = (int)(d += g12 + h4 - f4 + h12) & 0xFFFFFFF;
        d >>>= 28;
        long f5 = (long)x5 * (long)y0 + (long)x4 * (long)y1 + (long)x3 * (long)y2 + (long)x2 * (long)y3 + (long)x1 * (long)y4 + (long)x0 * (long)y5;
        long f13 = (long)x7 * (long)y6 + (long)x6 * (long)y7;
        long g5 = (long)u5 * (long)v0 + (long)u4 * (long)v1 + (long)u3 * (long)v2 + (long)u2 * (long)v3 + (long)u1 * (long)v4 + (long)u0 * (long)v5;
        long g13 = (long)u7 * (long)v6 + (long)u6 * (long)v7;
        long h5 = (long)s5 * (long)t0 + (long)s4 * (long)t1 + (long)s3 * (long)t2 + (long)s2 * (long)t3 + (long)s1 * (long)t4 + (long)s0 * (long)t5;
        long h13 = (long)s7 * (long)t6 + (long)s6 * (long)t7;
        int z5 = (int)(c += f5 + g5 + h13 - f13) & 0xFFFFFFF;
        c >>>= 28;
        int z13 = (int)(d += g13 + h5 - f5 + h13) & 0xFFFFFFF;
        d >>>= 28;
        long f6 = (long)x6 * (long)y0 + (long)x5 * (long)y1 + (long)x4 * (long)y2 + (long)x3 * (long)y3 + (long)x2 * (long)y4 + (long)x1 * (long)y5 + (long)x0 * (long)y6;
        long f14 = (long)x7 * (long)y7;
        long g6 = (long)u6 * (long)v0 + (long)u5 * (long)v1 + (long)u4 * (long)v2 + (long)u3 * (long)v3 + (long)u2 * (long)v4 + (long)u1 * (long)v5 + (long)u0 * (long)v6;
        long g14 = (long)u7 * (long)v7;
        long h6 = (long)s6 * (long)t0 + (long)s5 * (long)t1 + (long)s4 * (long)t2 + (long)s3 * (long)t3 + (long)s2 * (long)t4 + (long)s1 * (long)t5 + (long)s0 * (long)t6;
        long h14 = (long)s7 * (long)t7;
        int z6 = (int)(c += f6 + g6 + h14 - f14) & 0xFFFFFFF;
        c >>>= 28;
        int z14 = (int)(d += g14 + h6 - f6 + h14) & 0xFFFFFFF;
        d >>>= 28;
        long f7 = (long)x7 * (long)y0 + (long)x6 * (long)y1 + (long)x5 * (long)y2 + (long)x4 * (long)y3 + (long)x3 * (long)y4 + (long)x2 * (long)y5 + (long)x1 * (long)y6 + (long)x0 * (long)y7;
        long g7 = (long)u7 * (long)v0 + (long)u6 * (long)v1 + (long)u5 * (long)v2 + (long)u4 * (long)v3 + (long)u3 * (long)v4 + (long)u2 * (long)v5 + (long)u1 * (long)v6 + (long)u0 * (long)v7;
        long h7 = (long)s7 * (long)t0 + (long)s6 * (long)t1 + (long)s5 * (long)t2 + (long)s4 * (long)t3 + (long)s3 * (long)t4 + (long)s2 * (long)t5 + (long)s1 * (long)t6 + (long)s0 * (long)t7;
        int z7 = (int)(c += f7 + g7) & 0xFFFFFFF;
        c >>>= 28;
        int z15 = (int)(d += h7 - f7) & 0xFFFFFFF;
        c += (d >>>= 28);
        c += (long)z8;
        z8 = (int)c & 0xFFFFFFF;
        d += (long)z0;
        z0 = (int)d & 0xFFFFFFF;
        z9 += (int)(c >>>= 28);
        z[0] = z0;
        z[1] = z1 += (int)(d >>>= 28);
        z[2] = z2;
        z[3] = z3;
        z[4] = z4;
        z[5] = z5;
        z[6] = z6;
        z[7] = z7;
        z[8] = z8;
        z[9] = z9;
        z[10] = z10;
        z[11] = z11;
        z[12] = z12;
        z[13] = z13;
        z[14] = z14;
        z[15] = z15;
    }

    public static void negate(int[] x, int[] z) {
        int[] zero = X448Field.create();
        X448Field.sub(zero, x, z);
    }

    public static void normalize(int[] z) {
        X448Field.reduce(z, 1);
        X448Field.reduce(z, -1);
    }

    public static void one(int[] z) {
        z[0] = 1;
        for (int i = 1; i < 16; ++i) {
            z[i] = 0;
        }
    }

    private static void powPm3d4(int[] x, int[] z) {
        int[] x2 = X448Field.create();
        X448Field.sqr(x, x2);
        X448Field.mul(x, x2, x2);
        int[] x3 = X448Field.create();
        X448Field.sqr(x2, x3);
        X448Field.mul(x, x3, x3);
        int[] x6 = X448Field.create();
        X448Field.sqr(x3, 3, x6);
        X448Field.mul(x3, x6, x6);
        int[] x9 = X448Field.create();
        X448Field.sqr(x6, 3, x9);
        X448Field.mul(x3, x9, x9);
        int[] x18 = X448Field.create();
        X448Field.sqr(x9, 9, x18);
        X448Field.mul(x9, x18, x18);
        int[] x19 = X448Field.create();
        X448Field.sqr(x18, x19);
        X448Field.mul(x, x19, x19);
        int[] x37 = X448Field.create();
        X448Field.sqr(x19, 18, x37);
        X448Field.mul(x18, x37, x37);
        int[] x74 = X448Field.create();
        X448Field.sqr(x37, 37, x74);
        X448Field.mul(x37, x74, x74);
        int[] x111 = X448Field.create();
        X448Field.sqr(x74, 37, x111);
        X448Field.mul(x37, x111, x111);
        int[] x222 = X448Field.create();
        X448Field.sqr(x111, 111, x222);
        X448Field.mul(x111, x222, x222);
        int[] x223 = X448Field.create();
        X448Field.sqr(x222, x223);
        X448Field.mul(x, x223, x223);
        int[] t = X448Field.create();
        X448Field.sqr(x223, 223, t);
        X448Field.mul(t, x222, z);
    }

    private static void reduce(int[] z, int x) {
        int i;
        int t = z[15];
        int z15 = t & 0xFFFFFFF;
        t = (t >>> 28) + x;
        long cc = t;
        for (i = 0; i < 8; ++i) {
            z[i] = (int)(cc += (long)z[i] & 0xFFFFFFFFL) & 0xFFFFFFF;
            cc >>= 28;
        }
        cc += (long)t;
        for (i = 8; i < 15; ++i) {
            z[i] = (int)(cc += (long)z[i] & 0xFFFFFFFFL) & 0xFFFFFFF;
            cc >>= 28;
        }
        z[15] = z15 + (int)cc;
    }

    public static void sqr(int[] x, int[] z) {
        int x0 = x[0];
        int x1 = x[1];
        int x2 = x[2];
        int x3 = x[3];
        int x4 = x[4];
        int x5 = x[5];
        int x6 = x[6];
        int x7 = x[7];
        int u0 = x[8];
        int u1 = x[9];
        int u2 = x[10];
        int u3 = x[11];
        int u4 = x[12];
        int u5 = x[13];
        int u6 = x[14];
        int u7 = x[15];
        int x0_2 = x0 * 2;
        int x1_2 = x1 * 2;
        int x2_2 = x2 * 2;
        int x3_2 = x3 * 2;
        int x4_2 = x4 * 2;
        int x5_2 = x5 * 2;
        int x6_2 = x6 * 2;
        int u0_2 = u0 * 2;
        int u1_2 = u1 * 2;
        int u2_2 = u2 * 2;
        int u3_2 = u3 * 2;
        int u4_2 = u4 * 2;
        int u5_2 = u5 * 2;
        int u6_2 = u6 * 2;
        int s0 = x0 + u0;
        int s1 = x1 + u1;
        int s2 = x2 + u2;
        int s3 = x3 + u3;
        int s4 = x4 + u4;
        int s5 = x5 + u5;
        int s6 = x6 + u6;
        int s7 = x7 + u7;
        int s0_2 = s0 * 2;
        int s1_2 = s1 * 2;
        int s2_2 = s2 * 2;
        int s3_2 = s3 * 2;
        int s4_2 = s4 * 2;
        int s5_2 = s5 * 2;
        int s6_2 = s6 * 2;
        long f0 = (long)x0 * (long)x0;
        long f8 = (long)x7 * (long)x1_2 + (long)x6 * (long)x2_2 + (long)x5 * (long)x3_2 + (long)x4 * (long)x4;
        long g0 = (long)u0 * (long)u0;
        long g8 = (long)u7 * (long)u1_2 + (long)u6 * (long)u2_2 + (long)u5 * (long)u3_2 + (long)u4 * (long)u4;
        long h0 = (long)s0 * (long)s0;
        long h8 = (long)s7 * ((long)s1_2 & 0xFFFFFFFFL) + (long)s6 * ((long)s2_2 & 0xFFFFFFFFL) + (long)s5 * ((long)s3_2 & 0xFFFFFFFFL) + (long)s4 * (long)s4;
        long c = f0 + g0 + h8 - f8;
        int z0 = (int)c & 0xFFFFFFF;
        c >>>= 28;
        long d = g8 + h0 - f0 + h8;
        int z8 = (int)d & 0xFFFFFFF;
        d >>>= 28;
        long f1 = (long)x1 * (long)x0_2;
        long f9 = (long)x7 * (long)x2_2 + (long)x6 * (long)x3_2 + (long)x5 * (long)x4_2;
        long g1 = (long)u1 * (long)u0_2;
        long g9 = (long)u7 * (long)u2_2 + (long)u6 * (long)u3_2 + (long)u5 * (long)u4_2;
        long h1 = (long)s1 * ((long)s0_2 & 0xFFFFFFFFL);
        long h9 = (long)s7 * ((long)s2_2 & 0xFFFFFFFFL) + (long)s6 * ((long)s3_2 & 0xFFFFFFFFL) + (long)s5 * ((long)s4_2 & 0xFFFFFFFFL);
        int z1 = (int)(c += f1 + g1 + h9 - f9) & 0xFFFFFFF;
        c >>>= 28;
        int z9 = (int)(d += g9 + h1 - f1 + h9) & 0xFFFFFFF;
        d >>>= 28;
        long f2 = (long)x2 * (long)x0_2 + (long)x1 * (long)x1;
        long f10 = (long)x7 * (long)x3_2 + (long)x6 * (long)x4_2 + (long)x5 * (long)x5;
        long g2 = (long)u2 * (long)u0_2 + (long)u1 * (long)u1;
        long g10 = (long)u7 * (long)u3_2 + (long)u6 * (long)u4_2 + (long)u5 * (long)u5;
        long h2 = (long)s2 * ((long)s0_2 & 0xFFFFFFFFL) + (long)s1 * (long)s1;
        long h10 = (long)s7 * ((long)s3_2 & 0xFFFFFFFFL) + (long)s6 * ((long)s4_2 & 0xFFFFFFFFL) + (long)s5 * (long)s5;
        int z2 = (int)(c += f2 + g2 + h10 - f10) & 0xFFFFFFF;
        c >>>= 28;
        int z10 = (int)(d += g10 + h2 - f2 + h10) & 0xFFFFFFF;
        d >>>= 28;
        long f3 = (long)x3 * (long)x0_2 + (long)x2 * (long)x1_2;
        long f11 = (long)x7 * (long)x4_2 + (long)x6 * (long)x5_2;
        long g3 = (long)u3 * (long)u0_2 + (long)u2 * (long)u1_2;
        long g11 = (long)u7 * (long)u4_2 + (long)u6 * (long)u5_2;
        long h3 = (long)s3 * ((long)s0_2 & 0xFFFFFFFFL) + (long)s2 * ((long)s1_2 & 0xFFFFFFFFL);
        long h11 = (long)s7 * ((long)s4_2 & 0xFFFFFFFFL) + (long)s6 * ((long)s5_2 & 0xFFFFFFFFL);
        int z3 = (int)(c += f3 + g3 + h11 - f11) & 0xFFFFFFF;
        c >>>= 28;
        int z11 = (int)(d += g11 + h3 - f3 + h11) & 0xFFFFFFF;
        d >>>= 28;
        long f4 = (long)x4 * (long)x0_2 + (long)x3 * (long)x1_2 + (long)x2 * (long)x2;
        long f12 = (long)x7 * (long)x5_2 + (long)x6 * (long)x6;
        long g4 = (long)u4 * (long)u0_2 + (long)u3 * (long)u1_2 + (long)u2 * (long)u2;
        long g12 = (long)u7 * (long)u5_2 + (long)u6 * (long)u6;
        long h4 = (long)s4 * ((long)s0_2 & 0xFFFFFFFFL) + (long)s3 * ((long)s1_2 & 0xFFFFFFFFL) + (long)s2 * (long)s2;
        long h12 = (long)s7 * ((long)s5_2 & 0xFFFFFFFFL) + (long)s6 * (long)s6;
        int z4 = (int)(c += f4 + g4 + h12 - f12) & 0xFFFFFFF;
        c >>>= 28;
        int z12 = (int)(d += g12 + h4 - f4 + h12) & 0xFFFFFFF;
        d >>>= 28;
        long f5 = (long)x5 * (long)x0_2 + (long)x4 * (long)x1_2 + (long)x3 * (long)x2_2;
        long f13 = (long)x7 * (long)x6_2;
        long g5 = (long)u5 * (long)u0_2 + (long)u4 * (long)u1_2 + (long)u3 * (long)u2_2;
        long g13 = (long)u7 * (long)u6_2;
        long h5 = (long)s5 * ((long)s0_2 & 0xFFFFFFFFL) + (long)s4 * ((long)s1_2 & 0xFFFFFFFFL) + (long)s3 * ((long)s2_2 & 0xFFFFFFFFL);
        long h13 = (long)s7 * ((long)s6_2 & 0xFFFFFFFFL);
        int z5 = (int)(c += f5 + g5 + h13 - f13) & 0xFFFFFFF;
        c >>>= 28;
        int z13 = (int)(d += g13 + h5 - f5 + h13) & 0xFFFFFFF;
        d >>>= 28;
        long f6 = (long)x6 * (long)x0_2 + (long)x5 * (long)x1_2 + (long)x4 * (long)x2_2 + (long)x3 * (long)x3;
        long f14 = (long)x7 * (long)x7;
        long g6 = (long)u6 * (long)u0_2 + (long)u5 * (long)u1_2 + (long)u4 * (long)u2_2 + (long)u3 * (long)u3;
        long g14 = (long)u7 * (long)u7;
        long h6 = (long)s6 * ((long)s0_2 & 0xFFFFFFFFL) + (long)s5 * ((long)s1_2 & 0xFFFFFFFFL) + (long)s4 * ((long)s2_2 & 0xFFFFFFFFL) + (long)s3 * (long)s3;
        long h14 = (long)s7 * (long)s7;
        int z6 = (int)(c += f6 + g6 + h14 - f14) & 0xFFFFFFF;
        c >>>= 28;
        int z14 = (int)(d += g14 + h6 - f6 + h14) & 0xFFFFFFF;
        d >>>= 28;
        long f7 = (long)x7 * (long)x0_2 + (long)x6 * (long)x1_2 + (long)x5 * (long)x2_2 + (long)x4 * (long)x3_2;
        long g7 = (long)u7 * (long)u0_2 + (long)u6 * (long)u1_2 + (long)u5 * (long)u2_2 + (long)u4 * (long)u3_2;
        long h7 = (long)s7 * ((long)s0_2 & 0xFFFFFFFFL) + (long)s6 * ((long)s1_2 & 0xFFFFFFFFL) + (long)s5 * ((long)s2_2 & 0xFFFFFFFFL) + (long)s4 * ((long)s3_2 & 0xFFFFFFFFL);
        int z7 = (int)(c += f7 + g7) & 0xFFFFFFF;
        c >>>= 28;
        int z15 = (int)(d += h7 - f7) & 0xFFFFFFF;
        c += (d >>>= 28);
        c += (long)z8;
        z8 = (int)c & 0xFFFFFFF;
        d += (long)z0;
        z0 = (int)d & 0xFFFFFFF;
        z9 += (int)(c >>>= 28);
        z[0] = z0;
        z[1] = z1 += (int)(d >>>= 28);
        z[2] = z2;
        z[3] = z3;
        z[4] = z4;
        z[5] = z5;
        z[6] = z6;
        z[7] = z7;
        z[8] = z8;
        z[9] = z9;
        z[10] = z10;
        z[11] = z11;
        z[12] = z12;
        z[13] = z13;
        z[14] = z14;
        z[15] = z15;
    }

    public static void sqr(int[] x, int n, int[] z) {
        X448Field.sqr(x, z);
        while (--n > 0) {
            X448Field.sqr(z, z);
        }
    }

    public static boolean sqrtRatioVar(int[] u, int[] v, int[] z) {
        int[] u3v = X448Field.create();
        int[] u5v3 = X448Field.create();
        X448Field.sqr(u, u3v);
        X448Field.mul(u3v, v, u3v);
        X448Field.sqr(u3v, u5v3);
        X448Field.mul(u3v, u, u3v);
        X448Field.mul(u5v3, u, u5v3);
        X448Field.mul(u5v3, v, u5v3);
        int[] x = X448Field.create();
        X448Field.powPm3d4(u5v3, x);
        X448Field.mul(x, u3v, x);
        int[] t = X448Field.create();
        X448Field.sqr(x, t);
        X448Field.mul(t, v, t);
        X448Field.sub(u, t, t);
        X448Field.normalize(t);
        if (X448Field.isZeroVar(t)) {
            X448Field.copy(x, 0, z, 0);
            return true;
        }
        return false;
    }

    public static void sub(int[] x, int[] y, int[] z) {
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
        int x10 = x[10];
        int x11 = x[11];
        int x12 = x[12];
        int x13 = x[13];
        int x14 = x[14];
        int x15 = x[15];
        int y0 = y[0];
        int y1 = y[1];
        int y2 = y[2];
        int y3 = y[3];
        int y4 = y[4];
        int y5 = y[5];
        int y6 = y[6];
        int y7 = y[7];
        int y8 = y[8];
        int y9 = y[9];
        int y10 = y[10];
        int y11 = y[11];
        int y12 = y[12];
        int y13 = y[13];
        int y14 = y[14];
        int y15 = y[15];
        int z0 = x0 + 0x1FFFFFFE - y0;
        int z1 = x1 + 0x1FFFFFFE - y1;
        int z2 = x2 + 0x1FFFFFFE - y2;
        int z3 = x3 + 0x1FFFFFFE - y3;
        int z4 = x4 + 0x1FFFFFFE - y4;
        int z5 = x5 + 0x1FFFFFFE - y5;
        int z6 = x6 + 0x1FFFFFFE - y6;
        int z7 = x7 + 0x1FFFFFFE - y7;
        int z8 = x8 + 0x1FFFFFFC - y8;
        int z9 = x9 + 0x1FFFFFFE - y9;
        int z10 = x10 + 0x1FFFFFFE - y10;
        int z11 = x11 + 0x1FFFFFFE - y11;
        int z12 = x12 + 0x1FFFFFFE - y12;
        int z13 = x13 + 0x1FFFFFFE - y13;
        int z14 = x14 + 0x1FFFFFFE - y14;
        int z15 = x15 + 0x1FFFFFFE - y15;
        z2 += z1 >>> 28;
        z1 &= 0xFFFFFFF;
        z6 += z5 >>> 28;
        z5 &= 0xFFFFFFF;
        z10 += z9 >>> 28;
        z9 &= 0xFFFFFFF;
        z14 += z13 >>> 28;
        z13 &= 0xFFFFFFF;
        z3 += z2 >>> 28;
        z2 &= 0xFFFFFFF;
        z7 += z6 >>> 28;
        z6 &= 0xFFFFFFF;
        z11 += z10 >>> 28;
        z10 &= 0xFFFFFFF;
        z15 += z14 >>> 28;
        z14 &= 0xFFFFFFF;
        int t = z15 >>> 28;
        z15 &= 0xFFFFFFF;
        z0 += t;
        z8 += t;
        z4 += z3 >>> 28;
        z3 &= 0xFFFFFFF;
        z8 += z7 >>> 28;
        z7 &= 0xFFFFFFF;
        z12 += z11 >>> 28;
        z11 &= 0xFFFFFFF;
        z1 += z0 >>> 28;
        z0 &= 0xFFFFFFF;
        z5 += z4 >>> 28;
        z4 &= 0xFFFFFFF;
        z9 += z8 >>> 28;
        z8 &= 0xFFFFFFF;
        z13 += z12 >>> 28;
        z12 &= 0xFFFFFFF;
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
        z[10] = z10;
        z[11] = z11;
        z[12] = z12;
        z[13] = z13;
        z[14] = z14;
        z[15] = z15;
    }

    public static void subOne(int[] z) {
        int[] one = X448Field.create();
        one[0] = 1;
        X448Field.sub(z, one, z);
    }

    public static void zero(int[] z) {
        for (int i = 0; i < 16; ++i) {
            z[i] = 0;
        }
    }
}

