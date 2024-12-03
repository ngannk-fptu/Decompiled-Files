/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.raw;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.Pack;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class Nat192 {
    private static final long M = 0xFFFFFFFFL;

    public static int add(int[] x, int[] y, int[] z) {
        long c = 0L;
        z[0] = (int)(c += ((long)x[0] & 0xFFFFFFFFL) + ((long)y[0] & 0xFFFFFFFFL));
        c >>>= 32;
        z[1] = (int)(c += ((long)x[1] & 0xFFFFFFFFL) + ((long)y[1] & 0xFFFFFFFFL));
        c >>>= 32;
        z[2] = (int)(c += ((long)x[2] & 0xFFFFFFFFL) + ((long)y[2] & 0xFFFFFFFFL));
        c >>>= 32;
        z[3] = (int)(c += ((long)x[3] & 0xFFFFFFFFL) + ((long)y[3] & 0xFFFFFFFFL));
        c >>>= 32;
        z[4] = (int)(c += ((long)x[4] & 0xFFFFFFFFL) + ((long)y[4] & 0xFFFFFFFFL));
        c >>>= 32;
        z[5] = (int)(c += ((long)x[5] & 0xFFFFFFFFL) + ((long)y[5] & 0xFFFFFFFFL));
        return (int)(c >>>= 32);
    }

    public static int addBothTo(int[] x, int[] y, int[] z) {
        long c = 0L;
        z[0] = (int)(c += ((long)x[0] & 0xFFFFFFFFL) + ((long)y[0] & 0xFFFFFFFFL) + ((long)z[0] & 0xFFFFFFFFL));
        c >>>= 32;
        z[1] = (int)(c += ((long)x[1] & 0xFFFFFFFFL) + ((long)y[1] & 0xFFFFFFFFL) + ((long)z[1] & 0xFFFFFFFFL));
        c >>>= 32;
        z[2] = (int)(c += ((long)x[2] & 0xFFFFFFFFL) + ((long)y[2] & 0xFFFFFFFFL) + ((long)z[2] & 0xFFFFFFFFL));
        c >>>= 32;
        z[3] = (int)(c += ((long)x[3] & 0xFFFFFFFFL) + ((long)y[3] & 0xFFFFFFFFL) + ((long)z[3] & 0xFFFFFFFFL));
        c >>>= 32;
        z[4] = (int)(c += ((long)x[4] & 0xFFFFFFFFL) + ((long)y[4] & 0xFFFFFFFFL) + ((long)z[4] & 0xFFFFFFFFL));
        c >>>= 32;
        z[5] = (int)(c += ((long)x[5] & 0xFFFFFFFFL) + ((long)y[5] & 0xFFFFFFFFL) + ((long)z[5] & 0xFFFFFFFFL));
        return (int)(c >>>= 32);
    }

    public static int addTo(int[] x, int[] z) {
        long c = 0L;
        z[0] = (int)(c += ((long)x[0] & 0xFFFFFFFFL) + ((long)z[0] & 0xFFFFFFFFL));
        c >>>= 32;
        z[1] = (int)(c += ((long)x[1] & 0xFFFFFFFFL) + ((long)z[1] & 0xFFFFFFFFL));
        c >>>= 32;
        z[2] = (int)(c += ((long)x[2] & 0xFFFFFFFFL) + ((long)z[2] & 0xFFFFFFFFL));
        c >>>= 32;
        z[3] = (int)(c += ((long)x[3] & 0xFFFFFFFFL) + ((long)z[3] & 0xFFFFFFFFL));
        c >>>= 32;
        z[4] = (int)(c += ((long)x[4] & 0xFFFFFFFFL) + ((long)z[4] & 0xFFFFFFFFL));
        c >>>= 32;
        z[5] = (int)(c += ((long)x[5] & 0xFFFFFFFFL) + ((long)z[5] & 0xFFFFFFFFL));
        return (int)(c >>>= 32);
    }

    public static int addTo(int[] x, int xOff, int[] z, int zOff, int cIn) {
        long c = (long)cIn & 0xFFFFFFFFL;
        z[zOff + 0] = (int)(c += ((long)x[xOff + 0] & 0xFFFFFFFFL) + ((long)z[zOff + 0] & 0xFFFFFFFFL));
        c >>>= 32;
        z[zOff + 1] = (int)(c += ((long)x[xOff + 1] & 0xFFFFFFFFL) + ((long)z[zOff + 1] & 0xFFFFFFFFL));
        c >>>= 32;
        z[zOff + 2] = (int)(c += ((long)x[xOff + 2] & 0xFFFFFFFFL) + ((long)z[zOff + 2] & 0xFFFFFFFFL));
        c >>>= 32;
        z[zOff + 3] = (int)(c += ((long)x[xOff + 3] & 0xFFFFFFFFL) + ((long)z[zOff + 3] & 0xFFFFFFFFL));
        c >>>= 32;
        z[zOff + 4] = (int)(c += ((long)x[xOff + 4] & 0xFFFFFFFFL) + ((long)z[zOff + 4] & 0xFFFFFFFFL));
        c >>>= 32;
        z[zOff + 5] = (int)(c += ((long)x[xOff + 5] & 0xFFFFFFFFL) + ((long)z[zOff + 5] & 0xFFFFFFFFL));
        return (int)(c >>>= 32);
    }

    public static int addToEachOther(int[] u, int uOff, int[] v, int vOff) {
        long c = 0L;
        u[uOff + 0] = (int)(c += ((long)u[uOff + 0] & 0xFFFFFFFFL) + ((long)v[vOff + 0] & 0xFFFFFFFFL));
        v[vOff + 0] = (int)c;
        c >>>= 32;
        u[uOff + 1] = (int)(c += ((long)u[uOff + 1] & 0xFFFFFFFFL) + ((long)v[vOff + 1] & 0xFFFFFFFFL));
        v[vOff + 1] = (int)c;
        c >>>= 32;
        u[uOff + 2] = (int)(c += ((long)u[uOff + 2] & 0xFFFFFFFFL) + ((long)v[vOff + 2] & 0xFFFFFFFFL));
        v[vOff + 2] = (int)c;
        c >>>= 32;
        u[uOff + 3] = (int)(c += ((long)u[uOff + 3] & 0xFFFFFFFFL) + ((long)v[vOff + 3] & 0xFFFFFFFFL));
        v[vOff + 3] = (int)c;
        c >>>= 32;
        u[uOff + 4] = (int)(c += ((long)u[uOff + 4] & 0xFFFFFFFFL) + ((long)v[vOff + 4] & 0xFFFFFFFFL));
        v[vOff + 4] = (int)c;
        c >>>= 32;
        u[uOff + 5] = (int)(c += ((long)u[uOff + 5] & 0xFFFFFFFFL) + ((long)v[vOff + 5] & 0xFFFFFFFFL));
        v[vOff + 5] = (int)c;
        return (int)(c >>>= 32);
    }

    public static void copy(int[] x, int[] z) {
        z[0] = x[0];
        z[1] = x[1];
        z[2] = x[2];
        z[3] = x[3];
        z[4] = x[4];
        z[5] = x[5];
    }

    public static void copy(int[] x, int xOff, int[] z, int zOff) {
        z[zOff + 0] = x[xOff + 0];
        z[zOff + 1] = x[xOff + 1];
        z[zOff + 2] = x[xOff + 2];
        z[zOff + 3] = x[xOff + 3];
        z[zOff + 4] = x[xOff + 4];
        z[zOff + 5] = x[xOff + 5];
    }

    public static void copy64(long[] x, long[] z) {
        z[0] = x[0];
        z[1] = x[1];
        z[2] = x[2];
    }

    public static void copy64(long[] x, int xOff, long[] z, int zOff) {
        z[zOff + 0] = x[xOff + 0];
        z[zOff + 1] = x[xOff + 1];
        z[zOff + 2] = x[xOff + 2];
    }

    public static int[] create() {
        return new int[6];
    }

    public static long[] create64() {
        return new long[3];
    }

    public static int[] createExt() {
        return new int[12];
    }

    public static long[] createExt64() {
        return new long[6];
    }

    public static boolean diff(int[] x, int xOff, int[] y, int yOff, int[] z, int zOff) {
        boolean pos = Nat192.gte(x, xOff, y, yOff);
        if (pos) {
            Nat192.sub(x, xOff, y, yOff, z, zOff);
        } else {
            Nat192.sub(y, yOff, x, xOff, z, zOff);
        }
        return pos;
    }

    public static boolean eq(int[] x, int[] y) {
        for (int i = 5; i >= 0; --i) {
            if (x[i] == y[i]) continue;
            return false;
        }
        return true;
    }

    public static boolean eq64(long[] x, long[] y) {
        for (int i = 2; i >= 0; --i) {
            if (x[i] == y[i]) continue;
            return false;
        }
        return true;
    }

    public static int[] fromBigInteger(BigInteger x) {
        if (x.signum() < 0 || x.bitLength() > 192) {
            throw new IllegalArgumentException();
        }
        int[] z = Nat192.create();
        for (int i = 0; i < 6; ++i) {
            z[i] = x.intValue();
            x = x.shiftRight(32);
        }
        return z;
    }

    public static long[] fromBigInteger64(BigInteger x) {
        if (x.signum() < 0 || x.bitLength() > 192) {
            throw new IllegalArgumentException();
        }
        long[] z = Nat192.create64();
        for (int i = 0; i < 3; ++i) {
            z[i] = x.longValue();
            x = x.shiftRight(64);
        }
        return z;
    }

    public static int getBit(int[] x, int bit) {
        if (bit == 0) {
            return x[0] & 1;
        }
        int w = bit >> 5;
        if (w < 0 || w >= 6) {
            return 0;
        }
        int b = bit & 0x1F;
        return x[w] >>> b & 1;
    }

    public static boolean gte(int[] x, int[] y) {
        for (int i = 5; i >= 0; --i) {
            int x_i = x[i] ^ Integer.MIN_VALUE;
            int y_i = y[i] ^ Integer.MIN_VALUE;
            if (x_i < y_i) {
                return false;
            }
            if (x_i <= y_i) continue;
            return true;
        }
        return true;
    }

    public static boolean gte(int[] x, int xOff, int[] y, int yOff) {
        for (int i = 5; i >= 0; --i) {
            int x_i = x[xOff + i] ^ Integer.MIN_VALUE;
            int y_i = y[yOff + i] ^ Integer.MIN_VALUE;
            if (x_i < y_i) {
                return false;
            }
            if (x_i <= y_i) continue;
            return true;
        }
        return true;
    }

    public static boolean isOne(int[] x) {
        if (x[0] != 1) {
            return false;
        }
        for (int i = 1; i < 6; ++i) {
            if (x[i] == 0) continue;
            return false;
        }
        return true;
    }

    public static boolean isOne64(long[] x) {
        if (x[0] != 1L) {
            return false;
        }
        for (int i = 1; i < 3; ++i) {
            if (x[i] == 0L) continue;
            return false;
        }
        return true;
    }

    public static boolean isZero(int[] x) {
        for (int i = 0; i < 6; ++i) {
            if (x[i] == 0) continue;
            return false;
        }
        return true;
    }

    public static boolean isZero64(long[] x) {
        for (int i = 0; i < 3; ++i) {
            if (x[i] == 0L) continue;
            return false;
        }
        return true;
    }

    public static void mul(int[] x, int[] y, int[] zz) {
        long y_0 = (long)y[0] & 0xFFFFFFFFL;
        long y_1 = (long)y[1] & 0xFFFFFFFFL;
        long y_2 = (long)y[2] & 0xFFFFFFFFL;
        long y_3 = (long)y[3] & 0xFFFFFFFFL;
        long y_4 = (long)y[4] & 0xFFFFFFFFL;
        long y_5 = (long)y[5] & 0xFFFFFFFFL;
        long c = 0L;
        long x_0 = (long)x[0] & 0xFFFFFFFFL;
        zz[0] = (int)(c += x_0 * y_0);
        c >>>= 32;
        zz[1] = (int)(c += x_0 * y_1);
        c >>>= 32;
        zz[2] = (int)(c += x_0 * y_2);
        c >>>= 32;
        zz[3] = (int)(c += x_0 * y_3);
        c >>>= 32;
        zz[4] = (int)(c += x_0 * y_4);
        c >>>= 32;
        zz[5] = (int)(c += x_0 * y_5);
        zz[6] = (int)(c >>>= 32);
        for (int i = 1; i < 6; ++i) {
            long c2 = 0L;
            long x_i = (long)x[i] & 0xFFFFFFFFL;
            zz[i + 0] = (int)(c2 += x_i * y_0 + ((long)zz[i + 0] & 0xFFFFFFFFL));
            c2 >>>= 32;
            zz[i + 1] = (int)(c2 += x_i * y_1 + ((long)zz[i + 1] & 0xFFFFFFFFL));
            c2 >>>= 32;
            zz[i + 2] = (int)(c2 += x_i * y_2 + ((long)zz[i + 2] & 0xFFFFFFFFL));
            c2 >>>= 32;
            zz[i + 3] = (int)(c2 += x_i * y_3 + ((long)zz[i + 3] & 0xFFFFFFFFL));
            c2 >>>= 32;
            zz[i + 4] = (int)(c2 += x_i * y_4 + ((long)zz[i + 4] & 0xFFFFFFFFL));
            c2 >>>= 32;
            zz[i + 5] = (int)(c2 += x_i * y_5 + ((long)zz[i + 5] & 0xFFFFFFFFL));
            zz[i + 6] = (int)(c2 >>>= 32);
        }
    }

    public static void mul(int[] x, int xOff, int[] y, int yOff, int[] zz, int zzOff) {
        long y_0 = (long)y[yOff + 0] & 0xFFFFFFFFL;
        long y_1 = (long)y[yOff + 1] & 0xFFFFFFFFL;
        long y_2 = (long)y[yOff + 2] & 0xFFFFFFFFL;
        long y_3 = (long)y[yOff + 3] & 0xFFFFFFFFL;
        long y_4 = (long)y[yOff + 4] & 0xFFFFFFFFL;
        long y_5 = (long)y[yOff + 5] & 0xFFFFFFFFL;
        long c = 0L;
        long x_0 = (long)x[xOff + 0] & 0xFFFFFFFFL;
        zz[zzOff + 0] = (int)(c += x_0 * y_0);
        c >>>= 32;
        zz[zzOff + 1] = (int)(c += x_0 * y_1);
        c >>>= 32;
        zz[zzOff + 2] = (int)(c += x_0 * y_2);
        c >>>= 32;
        zz[zzOff + 3] = (int)(c += x_0 * y_3);
        c >>>= 32;
        zz[zzOff + 4] = (int)(c += x_0 * y_4);
        c >>>= 32;
        zz[zzOff + 5] = (int)(c += x_0 * y_5);
        zz[zzOff + 6] = (int)(c >>>= 32);
        for (int i = 1; i < 6; ++i) {
            long c2 = 0L;
            long x_i = (long)x[xOff + i] & 0xFFFFFFFFL;
            zz[zzOff + 0] = (int)(c2 += x_i * y_0 + ((long)zz[++zzOff + 0] & 0xFFFFFFFFL));
            c2 >>>= 32;
            zz[zzOff + 1] = (int)(c2 += x_i * y_1 + ((long)zz[zzOff + 1] & 0xFFFFFFFFL));
            c2 >>>= 32;
            zz[zzOff + 2] = (int)(c2 += x_i * y_2 + ((long)zz[zzOff + 2] & 0xFFFFFFFFL));
            c2 >>>= 32;
            zz[zzOff + 3] = (int)(c2 += x_i * y_3 + ((long)zz[zzOff + 3] & 0xFFFFFFFFL));
            c2 >>>= 32;
            zz[zzOff + 4] = (int)(c2 += x_i * y_4 + ((long)zz[zzOff + 4] & 0xFFFFFFFFL));
            c2 >>>= 32;
            zz[zzOff + 5] = (int)(c2 += x_i * y_5 + ((long)zz[zzOff + 5] & 0xFFFFFFFFL));
            zz[zzOff + 6] = (int)(c2 >>>= 32);
        }
    }

    public static int mulAddTo(int[] x, int[] y, int[] zz) {
        long y_0 = (long)y[0] & 0xFFFFFFFFL;
        long y_1 = (long)y[1] & 0xFFFFFFFFL;
        long y_2 = (long)y[2] & 0xFFFFFFFFL;
        long y_3 = (long)y[3] & 0xFFFFFFFFL;
        long y_4 = (long)y[4] & 0xFFFFFFFFL;
        long y_5 = (long)y[5] & 0xFFFFFFFFL;
        long zc = 0L;
        for (int i = 0; i < 6; ++i) {
            long c = 0L;
            long x_i = (long)x[i] & 0xFFFFFFFFL;
            zz[i + 0] = (int)(c += x_i * y_0 + ((long)zz[i + 0] & 0xFFFFFFFFL));
            c >>>= 32;
            zz[i + 1] = (int)(c += x_i * y_1 + ((long)zz[i + 1] & 0xFFFFFFFFL));
            c >>>= 32;
            zz[i + 2] = (int)(c += x_i * y_2 + ((long)zz[i + 2] & 0xFFFFFFFFL));
            c >>>= 32;
            zz[i + 3] = (int)(c += x_i * y_3 + ((long)zz[i + 3] & 0xFFFFFFFFL));
            c >>>= 32;
            zz[i + 4] = (int)(c += x_i * y_4 + ((long)zz[i + 4] & 0xFFFFFFFFL));
            c >>>= 32;
            zz[i + 5] = (int)(c += x_i * y_5 + ((long)zz[i + 5] & 0xFFFFFFFFL));
            zz[i + 6] = (int)(zc += (c >>>= 32) + ((long)zz[i + 6] & 0xFFFFFFFFL));
            zc >>>= 32;
        }
        return (int)zc;
    }

    public static int mulAddTo(int[] x, int xOff, int[] y, int yOff, int[] zz, int zzOff) {
        long y_0 = (long)y[yOff + 0] & 0xFFFFFFFFL;
        long y_1 = (long)y[yOff + 1] & 0xFFFFFFFFL;
        long y_2 = (long)y[yOff + 2] & 0xFFFFFFFFL;
        long y_3 = (long)y[yOff + 3] & 0xFFFFFFFFL;
        long y_4 = (long)y[yOff + 4] & 0xFFFFFFFFL;
        long y_5 = (long)y[yOff + 5] & 0xFFFFFFFFL;
        long zc = 0L;
        for (int i = 0; i < 6; ++i) {
            long c = 0L;
            long x_i = (long)x[xOff + i] & 0xFFFFFFFFL;
            zz[zzOff + 0] = (int)(c += x_i * y_0 + ((long)zz[zzOff + 0] & 0xFFFFFFFFL));
            c >>>= 32;
            zz[zzOff + 1] = (int)(c += x_i * y_1 + ((long)zz[zzOff + 1] & 0xFFFFFFFFL));
            c >>>= 32;
            zz[zzOff + 2] = (int)(c += x_i * y_2 + ((long)zz[zzOff + 2] & 0xFFFFFFFFL));
            c >>>= 32;
            zz[zzOff + 3] = (int)(c += x_i * y_3 + ((long)zz[zzOff + 3] & 0xFFFFFFFFL));
            c >>>= 32;
            zz[zzOff + 4] = (int)(c += x_i * y_4 + ((long)zz[zzOff + 4] & 0xFFFFFFFFL));
            c >>>= 32;
            zz[zzOff + 5] = (int)(c += x_i * y_5 + ((long)zz[zzOff + 5] & 0xFFFFFFFFL));
            zz[zzOff + 6] = (int)(zc += (c >>>= 32) + ((long)zz[zzOff + 6] & 0xFFFFFFFFL));
            zc >>>= 32;
            ++zzOff;
        }
        return (int)zc;
    }

    public static long mul33Add(int w, int[] x, int xOff, int[] y, int yOff, int[] z, int zOff) {
        long c = 0L;
        long wVal = (long)w & 0xFFFFFFFFL;
        long x0 = (long)x[xOff + 0] & 0xFFFFFFFFL;
        z[zOff + 0] = (int)(c += wVal * x0 + ((long)y[yOff + 0] & 0xFFFFFFFFL));
        c >>>= 32;
        long x1 = (long)x[xOff + 1] & 0xFFFFFFFFL;
        z[zOff + 1] = (int)(c += wVal * x1 + x0 + ((long)y[yOff + 1] & 0xFFFFFFFFL));
        c >>>= 32;
        long x2 = (long)x[xOff + 2] & 0xFFFFFFFFL;
        z[zOff + 2] = (int)(c += wVal * x2 + x1 + ((long)y[yOff + 2] & 0xFFFFFFFFL));
        c >>>= 32;
        long x3 = (long)x[xOff + 3] & 0xFFFFFFFFL;
        z[zOff + 3] = (int)(c += wVal * x3 + x2 + ((long)y[yOff + 3] & 0xFFFFFFFFL));
        c >>>= 32;
        long x4 = (long)x[xOff + 4] & 0xFFFFFFFFL;
        z[zOff + 4] = (int)(c += wVal * x4 + x3 + ((long)y[yOff + 4] & 0xFFFFFFFFL));
        c >>>= 32;
        long x5 = (long)x[xOff + 5] & 0xFFFFFFFFL;
        z[zOff + 5] = (int)(c += wVal * x5 + x4 + ((long)y[yOff + 5] & 0xFFFFFFFFL));
        c >>>= 32;
        return c += x5;
    }

    public static int mulWordAddExt(int x, int[] yy, int yyOff, int[] zz, int zzOff) {
        long c = 0L;
        long xVal = (long)x & 0xFFFFFFFFL;
        zz[zzOff + 0] = (int)(c += xVal * ((long)yy[yyOff + 0] & 0xFFFFFFFFL) + ((long)zz[zzOff + 0] & 0xFFFFFFFFL));
        c >>>= 32;
        zz[zzOff + 1] = (int)(c += xVal * ((long)yy[yyOff + 1] & 0xFFFFFFFFL) + ((long)zz[zzOff + 1] & 0xFFFFFFFFL));
        c >>>= 32;
        zz[zzOff + 2] = (int)(c += xVal * ((long)yy[yyOff + 2] & 0xFFFFFFFFL) + ((long)zz[zzOff + 2] & 0xFFFFFFFFL));
        c >>>= 32;
        zz[zzOff + 3] = (int)(c += xVal * ((long)yy[yyOff + 3] & 0xFFFFFFFFL) + ((long)zz[zzOff + 3] & 0xFFFFFFFFL));
        c >>>= 32;
        zz[zzOff + 4] = (int)(c += xVal * ((long)yy[yyOff + 4] & 0xFFFFFFFFL) + ((long)zz[zzOff + 4] & 0xFFFFFFFFL));
        c >>>= 32;
        zz[zzOff + 5] = (int)(c += xVal * ((long)yy[yyOff + 5] & 0xFFFFFFFFL) + ((long)zz[zzOff + 5] & 0xFFFFFFFFL));
        return (int)(c >>>= 32);
    }

    public static int mul33DWordAdd(int x, long y, int[] z, int zOff) {
        long c = 0L;
        long xVal = (long)x & 0xFFFFFFFFL;
        long y00 = y & 0xFFFFFFFFL;
        z[zOff + 0] = (int)(c += xVal * y00 + ((long)z[zOff + 0] & 0xFFFFFFFFL));
        c >>>= 32;
        long y01 = y >>> 32;
        z[zOff + 1] = (int)(c += xVal * y01 + y00 + ((long)z[zOff + 1] & 0xFFFFFFFFL));
        c >>>= 32;
        z[zOff + 2] = (int)(c += y01 + ((long)z[zOff + 2] & 0xFFFFFFFFL));
        c >>>= 32;
        z[zOff + 3] = (int)(c += (long)z[zOff + 3] & 0xFFFFFFFFL);
        return (c >>>= 32) == 0L ? 0 : Nat.incAt(6, z, zOff, 4);
    }

    public static int mul33WordAdd(int x, int y, int[] z, int zOff) {
        long c = 0L;
        long xVal = (long)x & 0xFFFFFFFFL;
        long yVal = (long)y & 0xFFFFFFFFL;
        z[zOff + 0] = (int)(c += yVal * xVal + ((long)z[zOff + 0] & 0xFFFFFFFFL));
        c >>>= 32;
        z[zOff + 1] = (int)(c += yVal + ((long)z[zOff + 1] & 0xFFFFFFFFL));
        c >>>= 32;
        z[zOff + 2] = (int)(c += (long)z[zOff + 2] & 0xFFFFFFFFL);
        return (c >>>= 32) == 0L ? 0 : Nat.incAt(6, z, zOff, 3);
    }

    public static int mulWordDwordAdd(int x, long y, int[] z, int zOff) {
        long c = 0L;
        long xVal = (long)x & 0xFFFFFFFFL;
        z[zOff + 0] = (int)(c += xVal * (y & 0xFFFFFFFFL) + ((long)z[zOff + 0] & 0xFFFFFFFFL));
        c >>>= 32;
        z[zOff + 1] = (int)(c += xVal * (y >>> 32) + ((long)z[zOff + 1] & 0xFFFFFFFFL));
        c >>>= 32;
        z[zOff + 2] = (int)(c += (long)z[zOff + 2] & 0xFFFFFFFFL);
        return (c >>>= 32) == 0L ? 0 : Nat.incAt(6, z, zOff, 3);
    }

    public static int mulWord(int x, int[] y, int[] z, int zOff) {
        long c = 0L;
        long xVal = (long)x & 0xFFFFFFFFL;
        int i = 0;
        do {
            z[zOff + i] = (int)(c += xVal * ((long)y[i] & 0xFFFFFFFFL));
            c >>>= 32;
        } while (++i < 6);
        return (int)c;
    }

    public static void square(int[] x, int[] zz) {
        long x_0 = (long)x[0] & 0xFFFFFFFFL;
        int c = 0;
        int i = 5;
        int j = 12;
        do {
            long xVal = (long)x[i--] & 0xFFFFFFFFL;
            long p = xVal * xVal;
            zz[--j] = c << 31 | (int)(p >>> 33);
            zz[--j] = (int)(p >>> 1);
            c = (int)p;
        } while (i > 0);
        long p = x_0 * x_0;
        long zz_1 = (long)(c << 31) & 0xFFFFFFFFL | p >>> 33;
        zz[0] = (int)p;
        c = (int)(p >>> 32) & 1;
        long x_1 = (long)x[1] & 0xFFFFFFFFL;
        long zz_2 = (long)zz[2] & 0xFFFFFFFFL;
        int w = (int)(zz_1 += x_1 * x_0);
        zz[1] = w << 1 | c;
        c = w >>> 31;
        zz_2 += zz_1 >>> 32;
        long x_2 = (long)x[2] & 0xFFFFFFFFL;
        long zz_3 = (long)zz[3] & 0xFFFFFFFFL;
        long zz_4 = (long)zz[4] & 0xFFFFFFFFL;
        w = (int)(zz_2 += x_2 * x_0);
        zz[2] = w << 1 | c;
        c = w >>> 31;
        zz_3 &= 0xFFFFFFFFL;
        long x_3 = (long)x[3] & 0xFFFFFFFFL;
        long zz_5 = ((long)zz[5] & 0xFFFFFFFFL) + ((zz_4 += (zz_3 += (zz_2 >>> 32) + x_2 * x_1) >>> 32) >>> 32);
        zz_4 &= 0xFFFFFFFFL;
        long zz_6 = ((long)zz[6] & 0xFFFFFFFFL) + (zz_5 >>> 32);
        zz_5 &= 0xFFFFFFFFL;
        w = (int)(zz_3 += x_3 * x_0);
        zz[3] = w << 1 | c;
        c = w >>> 31;
        zz_4 &= 0xFFFFFFFFL;
        zz_5 &= 0xFFFFFFFFL;
        long x_4 = (long)x[4] & 0xFFFFFFFFL;
        long zz_7 = ((long)zz[7] & 0xFFFFFFFFL) + ((zz_6 += (zz_5 += ((zz_4 += (zz_3 >>> 32) + x_3 * x_1) >>> 32) + x_3 * x_2) >>> 32) >>> 32);
        zz_6 &= 0xFFFFFFFFL;
        long zz_8 = ((long)zz[8] & 0xFFFFFFFFL) + (zz_7 >>> 32);
        zz_7 &= 0xFFFFFFFFL;
        w = (int)(zz_4 += x_4 * x_0);
        zz[4] = w << 1 | c;
        c = w >>> 31;
        zz_5 &= 0xFFFFFFFFL;
        zz_6 &= 0xFFFFFFFFL;
        zz_7 &= 0xFFFFFFFFL;
        long x_5 = (long)x[5] & 0xFFFFFFFFL;
        long zz_9 = ((long)zz[9] & 0xFFFFFFFFL) + ((zz_8 += (zz_7 += ((zz_6 += ((zz_5 += (zz_4 >>> 32) + x_4 * x_1) >>> 32) + x_4 * x_2) >>> 32) + x_4 * x_3) >>> 32) >>> 32);
        zz_8 &= 0xFFFFFFFFL;
        long zz_10 = ((long)zz[10] & 0xFFFFFFFFL) + (zz_9 >>> 32);
        zz_9 &= 0xFFFFFFFFL;
        w = (int)(zz_5 += x_5 * x_0);
        zz[5] = w << 1 | c;
        c = w >>> 31;
        zz_10 += (zz_9 += ((zz_8 += ((zz_7 += ((zz_6 += (zz_5 >>> 32) + x_5 * x_1) >>> 32) + x_5 * x_2) >>> 32) + x_5 * x_3) >>> 32) + x_5 * x_4) >>> 32;
        w = (int)zz_6;
        zz[6] = w << 1 | c;
        c = w >>> 31;
        w = (int)zz_7;
        zz[7] = w << 1 | c;
        c = w >>> 31;
        w = (int)zz_8;
        zz[8] = w << 1 | c;
        c = w >>> 31;
        w = (int)zz_9;
        zz[9] = w << 1 | c;
        c = w >>> 31;
        w = (int)zz_10;
        zz[10] = w << 1 | c;
        c = w >>> 31;
        w = zz[11] + (int)(zz_10 >>> 32);
        zz[11] = w << 1 | c;
    }

    public static void square(int[] x, int xOff, int[] zz, int zzOff) {
        long x_0 = (long)x[xOff + 0] & 0xFFFFFFFFL;
        int c = 0;
        int i = 5;
        int j = 12;
        do {
            long xVal = (long)x[xOff + i--] & 0xFFFFFFFFL;
            long p = xVal * xVal;
            zz[zzOff + --j] = c << 31 | (int)(p >>> 33);
            zz[zzOff + --j] = (int)(p >>> 1);
            c = (int)p;
        } while (i > 0);
        long p = x_0 * x_0;
        long zz_1 = (long)(c << 31) & 0xFFFFFFFFL | p >>> 33;
        zz[zzOff + 0] = (int)p;
        c = (int)(p >>> 32) & 1;
        long x_1 = (long)x[xOff + 1] & 0xFFFFFFFFL;
        long zz_2 = (long)zz[zzOff + 2] & 0xFFFFFFFFL;
        int w = (int)(zz_1 += x_1 * x_0);
        zz[zzOff + 1] = w << 1 | c;
        c = w >>> 31;
        zz_2 += zz_1 >>> 32;
        long x_2 = (long)x[xOff + 2] & 0xFFFFFFFFL;
        long zz_3 = (long)zz[zzOff + 3] & 0xFFFFFFFFL;
        long zz_4 = (long)zz[zzOff + 4] & 0xFFFFFFFFL;
        w = (int)(zz_2 += x_2 * x_0);
        zz[zzOff + 2] = w << 1 | c;
        c = w >>> 31;
        zz_3 &= 0xFFFFFFFFL;
        long x_3 = (long)x[xOff + 3] & 0xFFFFFFFFL;
        long zz_5 = ((long)zz[zzOff + 5] & 0xFFFFFFFFL) + ((zz_4 += (zz_3 += (zz_2 >>> 32) + x_2 * x_1) >>> 32) >>> 32);
        zz_4 &= 0xFFFFFFFFL;
        long zz_6 = ((long)zz[zzOff + 6] & 0xFFFFFFFFL) + (zz_5 >>> 32);
        zz_5 &= 0xFFFFFFFFL;
        w = (int)(zz_3 += x_3 * x_0);
        zz[zzOff + 3] = w << 1 | c;
        c = w >>> 31;
        zz_4 &= 0xFFFFFFFFL;
        zz_5 &= 0xFFFFFFFFL;
        long x_4 = (long)x[xOff + 4] & 0xFFFFFFFFL;
        long zz_7 = ((long)zz[zzOff + 7] & 0xFFFFFFFFL) + ((zz_6 += (zz_5 += ((zz_4 += (zz_3 >>> 32) + x_3 * x_1) >>> 32) + x_3 * x_2) >>> 32) >>> 32);
        zz_6 &= 0xFFFFFFFFL;
        long zz_8 = ((long)zz[zzOff + 8] & 0xFFFFFFFFL) + (zz_7 >>> 32);
        zz_7 &= 0xFFFFFFFFL;
        w = (int)(zz_4 += x_4 * x_0);
        zz[zzOff + 4] = w << 1 | c;
        c = w >>> 31;
        zz_5 &= 0xFFFFFFFFL;
        zz_6 &= 0xFFFFFFFFL;
        zz_7 &= 0xFFFFFFFFL;
        long x_5 = (long)x[xOff + 5] & 0xFFFFFFFFL;
        long zz_9 = ((long)zz[zzOff + 9] & 0xFFFFFFFFL) + ((zz_8 += (zz_7 += ((zz_6 += ((zz_5 += (zz_4 >>> 32) + x_4 * x_1) >>> 32) + x_4 * x_2) >>> 32) + x_4 * x_3) >>> 32) >>> 32);
        zz_8 &= 0xFFFFFFFFL;
        long zz_10 = ((long)zz[zzOff + 10] & 0xFFFFFFFFL) + (zz_9 >>> 32);
        zz_9 &= 0xFFFFFFFFL;
        w = (int)(zz_5 += x_5 * x_0);
        zz[zzOff + 5] = w << 1 | c;
        c = w >>> 31;
        zz_10 += (zz_9 += ((zz_8 += ((zz_7 += ((zz_6 += (zz_5 >>> 32) + x_5 * x_1) >>> 32) + x_5 * x_2) >>> 32) + x_5 * x_3) >>> 32) + x_5 * x_4) >>> 32;
        w = (int)zz_6;
        zz[zzOff + 6] = w << 1 | c;
        c = w >>> 31;
        w = (int)zz_7;
        zz[zzOff + 7] = w << 1 | c;
        c = w >>> 31;
        w = (int)zz_8;
        zz[zzOff + 8] = w << 1 | c;
        c = w >>> 31;
        w = (int)zz_9;
        zz[zzOff + 9] = w << 1 | c;
        c = w >>> 31;
        w = (int)zz_10;
        zz[zzOff + 10] = w << 1 | c;
        c = w >>> 31;
        w = zz[zzOff + 11] + (int)(zz_10 >>> 32);
        zz[zzOff + 11] = w << 1 | c;
    }

    public static int sub(int[] x, int[] y, int[] z) {
        long c = 0L;
        z[0] = (int)(c += ((long)x[0] & 0xFFFFFFFFL) - ((long)y[0] & 0xFFFFFFFFL));
        c >>= 32;
        z[1] = (int)(c += ((long)x[1] & 0xFFFFFFFFL) - ((long)y[1] & 0xFFFFFFFFL));
        c >>= 32;
        z[2] = (int)(c += ((long)x[2] & 0xFFFFFFFFL) - ((long)y[2] & 0xFFFFFFFFL));
        c >>= 32;
        z[3] = (int)(c += ((long)x[3] & 0xFFFFFFFFL) - ((long)y[3] & 0xFFFFFFFFL));
        c >>= 32;
        z[4] = (int)(c += ((long)x[4] & 0xFFFFFFFFL) - ((long)y[4] & 0xFFFFFFFFL));
        c >>= 32;
        z[5] = (int)(c += ((long)x[5] & 0xFFFFFFFFL) - ((long)y[5] & 0xFFFFFFFFL));
        return (int)(c >>= 32);
    }

    public static int sub(int[] x, int xOff, int[] y, int yOff, int[] z, int zOff) {
        long c = 0L;
        z[zOff + 0] = (int)(c += ((long)x[xOff + 0] & 0xFFFFFFFFL) - ((long)y[yOff + 0] & 0xFFFFFFFFL));
        c >>= 32;
        z[zOff + 1] = (int)(c += ((long)x[xOff + 1] & 0xFFFFFFFFL) - ((long)y[yOff + 1] & 0xFFFFFFFFL));
        c >>= 32;
        z[zOff + 2] = (int)(c += ((long)x[xOff + 2] & 0xFFFFFFFFL) - ((long)y[yOff + 2] & 0xFFFFFFFFL));
        c >>= 32;
        z[zOff + 3] = (int)(c += ((long)x[xOff + 3] & 0xFFFFFFFFL) - ((long)y[yOff + 3] & 0xFFFFFFFFL));
        c >>= 32;
        z[zOff + 4] = (int)(c += ((long)x[xOff + 4] & 0xFFFFFFFFL) - ((long)y[yOff + 4] & 0xFFFFFFFFL));
        c >>= 32;
        z[zOff + 5] = (int)(c += ((long)x[xOff + 5] & 0xFFFFFFFFL) - ((long)y[yOff + 5] & 0xFFFFFFFFL));
        return (int)(c >>= 32);
    }

    public static int subBothFrom(int[] x, int[] y, int[] z) {
        long c = 0L;
        z[0] = (int)(c += ((long)z[0] & 0xFFFFFFFFL) - ((long)x[0] & 0xFFFFFFFFL) - ((long)y[0] & 0xFFFFFFFFL));
        c >>= 32;
        z[1] = (int)(c += ((long)z[1] & 0xFFFFFFFFL) - ((long)x[1] & 0xFFFFFFFFL) - ((long)y[1] & 0xFFFFFFFFL));
        c >>= 32;
        z[2] = (int)(c += ((long)z[2] & 0xFFFFFFFFL) - ((long)x[2] & 0xFFFFFFFFL) - ((long)y[2] & 0xFFFFFFFFL));
        c >>= 32;
        z[3] = (int)(c += ((long)z[3] & 0xFFFFFFFFL) - ((long)x[3] & 0xFFFFFFFFL) - ((long)y[3] & 0xFFFFFFFFL));
        c >>= 32;
        z[4] = (int)(c += ((long)z[4] & 0xFFFFFFFFL) - ((long)x[4] & 0xFFFFFFFFL) - ((long)y[4] & 0xFFFFFFFFL));
        c >>= 32;
        z[5] = (int)(c += ((long)z[5] & 0xFFFFFFFFL) - ((long)x[5] & 0xFFFFFFFFL) - ((long)y[5] & 0xFFFFFFFFL));
        return (int)(c >>= 32);
    }

    public static int subFrom(int[] x, int[] z) {
        long c = 0L;
        z[0] = (int)(c += ((long)z[0] & 0xFFFFFFFFL) - ((long)x[0] & 0xFFFFFFFFL));
        c >>= 32;
        z[1] = (int)(c += ((long)z[1] & 0xFFFFFFFFL) - ((long)x[1] & 0xFFFFFFFFL));
        c >>= 32;
        z[2] = (int)(c += ((long)z[2] & 0xFFFFFFFFL) - ((long)x[2] & 0xFFFFFFFFL));
        c >>= 32;
        z[3] = (int)(c += ((long)z[3] & 0xFFFFFFFFL) - ((long)x[3] & 0xFFFFFFFFL));
        c >>= 32;
        z[4] = (int)(c += ((long)z[4] & 0xFFFFFFFFL) - ((long)x[4] & 0xFFFFFFFFL));
        c >>= 32;
        z[5] = (int)(c += ((long)z[5] & 0xFFFFFFFFL) - ((long)x[5] & 0xFFFFFFFFL));
        return (int)(c >>= 32);
    }

    public static int subFrom(int[] x, int xOff, int[] z, int zOff) {
        long c = 0L;
        z[zOff + 0] = (int)(c += ((long)z[zOff + 0] & 0xFFFFFFFFL) - ((long)x[xOff + 0] & 0xFFFFFFFFL));
        c >>= 32;
        z[zOff + 1] = (int)(c += ((long)z[zOff + 1] & 0xFFFFFFFFL) - ((long)x[xOff + 1] & 0xFFFFFFFFL));
        c >>= 32;
        z[zOff + 2] = (int)(c += ((long)z[zOff + 2] & 0xFFFFFFFFL) - ((long)x[xOff + 2] & 0xFFFFFFFFL));
        c >>= 32;
        z[zOff + 3] = (int)(c += ((long)z[zOff + 3] & 0xFFFFFFFFL) - ((long)x[xOff + 3] & 0xFFFFFFFFL));
        c >>= 32;
        z[zOff + 4] = (int)(c += ((long)z[zOff + 4] & 0xFFFFFFFFL) - ((long)x[xOff + 4] & 0xFFFFFFFFL));
        c >>= 32;
        z[zOff + 5] = (int)(c += ((long)z[zOff + 5] & 0xFFFFFFFFL) - ((long)x[xOff + 5] & 0xFFFFFFFFL));
        return (int)(c >>= 32);
    }

    public static BigInteger toBigInteger(int[] x) {
        byte[] bs = new byte[24];
        for (int i = 0; i < 6; ++i) {
            int x_i = x[i];
            if (x_i == 0) continue;
            Pack.intToBigEndian(x_i, bs, 5 - i << 2);
        }
        return new BigInteger(1, bs);
    }

    public static BigInteger toBigInteger64(long[] x) {
        byte[] bs = new byte[24];
        for (int i = 0; i < 3; ++i) {
            long x_i = x[i];
            if (x_i == 0L) continue;
            Pack.longToBigEndian(x_i, bs, 2 - i << 3);
        }
        return new BigInteger(1, bs);
    }

    public static void zero(int[] z) {
        z[0] = 0;
        z[1] = 0;
        z[2] = 0;
        z[3] = 0;
        z[4] = 0;
        z[5] = 0;
    }
}

