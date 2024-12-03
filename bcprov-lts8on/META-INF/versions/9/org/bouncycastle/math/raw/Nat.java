/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.raw;

import java.math.BigInteger;
import org.bouncycastle.util.Pack;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class Nat {
    private static final long M = 0xFFFFFFFFL;

    public static int add(int len, int[] x, int[] y, int[] z) {
        long c = 0L;
        for (int i = 0; i < len; ++i) {
            z[i] = (int)(c += ((long)x[i] & 0xFFFFFFFFL) + ((long)y[i] & 0xFFFFFFFFL));
            c >>>= 32;
        }
        return (int)c;
    }

    public static int add33At(int len, int x, int[] z, int zPos) {
        long c = ((long)z[zPos + 0] & 0xFFFFFFFFL) + ((long)x & 0xFFFFFFFFL);
        z[zPos + 0] = (int)c;
        c >>>= 32;
        z[zPos + 1] = (int)(c += ((long)z[zPos + 1] & 0xFFFFFFFFL) + 1L);
        return (c >>>= 32) == 0L ? 0 : Nat.incAt(len, z, zPos + 2);
    }

    public static int add33At(int len, int x, int[] z, int zOff, int zPos) {
        long c = ((long)z[zOff + zPos] & 0xFFFFFFFFL) + ((long)x & 0xFFFFFFFFL);
        z[zOff + zPos] = (int)c;
        c >>>= 32;
        z[zOff + zPos + 1] = (int)(c += ((long)z[zOff + zPos + 1] & 0xFFFFFFFFL) + 1L);
        return (c >>>= 32) == 0L ? 0 : Nat.incAt(len, z, zOff, zPos + 2);
    }

    public static int add33To(int len, int x, int[] z) {
        long c = ((long)z[0] & 0xFFFFFFFFL) + ((long)x & 0xFFFFFFFFL);
        z[0] = (int)c;
        c >>>= 32;
        z[1] = (int)(c += ((long)z[1] & 0xFFFFFFFFL) + 1L);
        return (c >>>= 32) == 0L ? 0 : Nat.incAt(len, z, 2);
    }

    public static int add33To(int len, int x, int[] z, int zOff) {
        long c = ((long)z[zOff + 0] & 0xFFFFFFFFL) + ((long)x & 0xFFFFFFFFL);
        z[zOff + 0] = (int)c;
        c >>>= 32;
        z[zOff + 1] = (int)(c += ((long)z[zOff + 1] & 0xFFFFFFFFL) + 1L);
        return (c >>>= 32) == 0L ? 0 : Nat.incAt(len, z, zOff, 2);
    }

    public static int addBothTo(int len, int[] x, int[] y, int[] z) {
        long c = 0L;
        for (int i = 0; i < len; ++i) {
            z[i] = (int)(c += ((long)x[i] & 0xFFFFFFFFL) + ((long)y[i] & 0xFFFFFFFFL) + ((long)z[i] & 0xFFFFFFFFL));
            c >>>= 32;
        }
        return (int)c;
    }

    public static int addBothTo(int len, int[] x, int xOff, int[] y, int yOff, int[] z, int zOff) {
        long c = 0L;
        for (int i = 0; i < len; ++i) {
            z[zOff + i] = (int)(c += ((long)x[xOff + i] & 0xFFFFFFFFL) + ((long)y[yOff + i] & 0xFFFFFFFFL) + ((long)z[zOff + i] & 0xFFFFFFFFL));
            c >>>= 32;
        }
        return (int)c;
    }

    public static int addDWordAt(int len, long x, int[] z, int zPos) {
        long c = ((long)z[zPos + 0] & 0xFFFFFFFFL) + (x & 0xFFFFFFFFL);
        z[zPos + 0] = (int)c;
        c >>>= 32;
        z[zPos + 1] = (int)(c += ((long)z[zPos + 1] & 0xFFFFFFFFL) + (x >>> 32));
        return (c >>>= 32) == 0L ? 0 : Nat.incAt(len, z, zPos + 2);
    }

    public static int addDWordAt(int len, long x, int[] z, int zOff, int zPos) {
        long c = ((long)z[zOff + zPos] & 0xFFFFFFFFL) + (x & 0xFFFFFFFFL);
        z[zOff + zPos] = (int)c;
        c >>>= 32;
        z[zOff + zPos + 1] = (int)(c += ((long)z[zOff + zPos + 1] & 0xFFFFFFFFL) + (x >>> 32));
        return (c >>>= 32) == 0L ? 0 : Nat.incAt(len, z, zOff, zPos + 2);
    }

    public static int addDWordTo(int len, long x, int[] z) {
        long c = ((long)z[0] & 0xFFFFFFFFL) + (x & 0xFFFFFFFFL);
        z[0] = (int)c;
        c >>>= 32;
        z[1] = (int)(c += ((long)z[1] & 0xFFFFFFFFL) + (x >>> 32));
        return (c >>>= 32) == 0L ? 0 : Nat.incAt(len, z, 2);
    }

    public static int addDWordTo(int len, long x, int[] z, int zOff) {
        long c = ((long)z[zOff + 0] & 0xFFFFFFFFL) + (x & 0xFFFFFFFFL);
        z[zOff + 0] = (int)c;
        c >>>= 32;
        z[zOff + 1] = (int)(c += ((long)z[zOff + 1] & 0xFFFFFFFFL) + (x >>> 32));
        return (c >>>= 32) == 0L ? 0 : Nat.incAt(len, z, zOff, 2);
    }

    public static int addTo(int len, int[] x, int[] z) {
        long c = 0L;
        for (int i = 0; i < len; ++i) {
            z[i] = (int)(c += ((long)x[i] & 0xFFFFFFFFL) + ((long)z[i] & 0xFFFFFFFFL));
            c >>>= 32;
        }
        return (int)c;
    }

    public static int addTo(int len, int[] x, int xOff, int[] z, int zOff) {
        long c = 0L;
        for (int i = 0; i < len; ++i) {
            z[zOff + i] = (int)(c += ((long)x[xOff + i] & 0xFFFFFFFFL) + ((long)z[zOff + i] & 0xFFFFFFFFL));
            c >>>= 32;
        }
        return (int)c;
    }

    public static int addTo(int len, int[] x, int xOff, int[] z, int zOff, int cIn) {
        long c = (long)cIn & 0xFFFFFFFFL;
        for (int i = 0; i < len; ++i) {
            z[zOff + i] = (int)(c += ((long)x[xOff + i] & 0xFFFFFFFFL) + ((long)z[zOff + i] & 0xFFFFFFFFL));
            c >>>= 32;
        }
        return (int)c;
    }

    public static int addToEachOther(int len, int[] u, int uOff, int[] v, int vOff) {
        long c = 0L;
        for (int i = 0; i < len; ++i) {
            u[uOff + i] = (int)(c += ((long)u[uOff + i] & 0xFFFFFFFFL) + ((long)v[vOff + i] & 0xFFFFFFFFL));
            v[vOff + i] = (int)c;
            c >>>= 32;
        }
        return (int)c;
    }

    public static int addWordAt(int len, int x, int[] z, int zPos) {
        long c = ((long)x & 0xFFFFFFFFL) + ((long)z[zPos] & 0xFFFFFFFFL);
        z[zPos] = (int)c;
        return (c >>>= 32) == 0L ? 0 : Nat.incAt(len, z, zPos + 1);
    }

    public static int addWordAt(int len, int x, int[] z, int zOff, int zPos) {
        long c = ((long)x & 0xFFFFFFFFL) + ((long)z[zOff + zPos] & 0xFFFFFFFFL);
        z[zOff + zPos] = (int)c;
        return (c >>>= 32) == 0L ? 0 : Nat.incAt(len, z, zOff, zPos + 1);
    }

    public static int addWordTo(int len, int x, int[] z) {
        long c = ((long)x & 0xFFFFFFFFL) + ((long)z[0] & 0xFFFFFFFFL);
        z[0] = (int)c;
        return (c >>>= 32) == 0L ? 0 : Nat.incAt(len, z, 1);
    }

    public static int addWordTo(int len, int x, int[] z, int zOff) {
        long c = ((long)x & 0xFFFFFFFFL) + ((long)z[zOff] & 0xFFFFFFFFL);
        z[zOff] = (int)c;
        return (c >>>= 32) == 0L ? 0 : Nat.incAt(len, z, zOff, 1);
    }

    public static int cadd(int len, int mask, int[] x, int[] y, int[] z) {
        long MASK = (long)(-(mask & 1)) & 0xFFFFFFFFL;
        long c = 0L;
        for (int i = 0; i < len; ++i) {
            z[i] = (int)(c += ((long)x[i] & 0xFFFFFFFFL) + ((long)y[i] & MASK));
            c >>>= 32;
        }
        return (int)c;
    }

    public static void cmov(int len, int mask, int[] x, int xOff, int[] z, int zOff) {
        mask = -(mask & 1);
        for (int i = 0; i < len; ++i) {
            int z_i = z[zOff + i];
            int diff = z_i ^ x[xOff + i];
            z[zOff + i] = z_i ^= diff & mask;
        }
    }

    public static int compare(int len, int[] x, int[] y) {
        for (int i = len - 1; i >= 0; --i) {
            int x_i = x[i] ^ Integer.MIN_VALUE;
            int y_i = y[i] ^ Integer.MIN_VALUE;
            if (x_i < y_i) {
                return -1;
            }
            if (x_i <= y_i) continue;
            return 1;
        }
        return 0;
    }

    public static int compare(int len, int[] x, int xOff, int[] y, int yOff) {
        for (int i = len - 1; i >= 0; --i) {
            int x_i = x[xOff + i] ^ Integer.MIN_VALUE;
            int y_i = y[yOff + i] ^ Integer.MIN_VALUE;
            if (x_i < y_i) {
                return -1;
            }
            if (x_i <= y_i) continue;
            return 1;
        }
        return 0;
    }

    public static int[] copy(int len, int[] x) {
        int[] z = new int[len];
        System.arraycopy(x, 0, z, 0, len);
        return z;
    }

    public static void copy(int len, int[] x, int[] z) {
        System.arraycopy(x, 0, z, 0, len);
    }

    public static void copy(int len, int[] x, int xOff, int[] z, int zOff) {
        System.arraycopy(x, xOff, z, zOff, len);
    }

    public static long[] copy64(int len, long[] x) {
        long[] z = new long[len];
        System.arraycopy(x, 0, z, 0, len);
        return z;
    }

    public static void copy64(int len, long[] x, long[] z) {
        System.arraycopy(x, 0, z, 0, len);
    }

    public static void copy64(int len, long[] x, int xOff, long[] z, int zOff) {
        System.arraycopy(x, xOff, z, zOff, len);
    }

    public static int[] create(int len) {
        return new int[len];
    }

    public static long[] create64(int len) {
        return new long[len];
    }

    public static int csub(int len, int mask, int[] x, int[] y, int[] z) {
        long MASK = (long)(-(mask & 1)) & 0xFFFFFFFFL;
        long c = 0L;
        for (int i = 0; i < len; ++i) {
            z[i] = (int)(c += ((long)x[i] & 0xFFFFFFFFL) - ((long)y[i] & MASK));
            c >>= 32;
        }
        return (int)c;
    }

    public static int csub(int len, int mask, int[] x, int xOff, int[] y, int yOff, int[] z, int zOff) {
        long MASK = (long)(-(mask & 1)) & 0xFFFFFFFFL;
        long c = 0L;
        for (int i = 0; i < len; ++i) {
            z[zOff + i] = (int)(c += ((long)x[xOff + i] & 0xFFFFFFFFL) - ((long)y[yOff + i] & MASK));
            c >>= 32;
        }
        return (int)c;
    }

    public static int dec(int len, int[] z) {
        int i = 0;
        while (i < len) {
            int n = i++;
            z[n] = z[n] - 1;
            if (z[n] == -1) continue;
            return 0;
        }
        return -1;
    }

    public static int dec(int len, int[] x, int[] z) {
        for (int i = 0; i < len; ++i) {
            int c;
            z[i] = c = x[i] - 1;
            if (c == -1) continue;
            while (i < len) {
                z[i] = x[i];
                ++i;
            }
            return 0;
        }
        return -1;
    }

    public static int decAt(int len, int[] z, int zPos) {
        int i = zPos;
        while (i < len) {
            int n = i++;
            z[n] = z[n] - 1;
            if (z[n] == -1) continue;
            return 0;
        }
        return -1;
    }

    public static int decAt(int len, int[] z, int zOff, int zPos) {
        for (int i = zPos; i < len; ++i) {
            int n = zOff + i;
            z[n] = z[n] - 1;
            if (z[n] == -1) continue;
            return 0;
        }
        return -1;
    }

    public static boolean diff(int len, int[] x, int xOff, int[] y, int yOff, int[] z, int zOff) {
        boolean pos = Nat.gte(len, x, xOff, y, yOff);
        if (pos) {
            Nat.sub(len, x, xOff, y, yOff, z, zOff);
        } else {
            Nat.sub(len, y, yOff, x, xOff, z, zOff);
        }
        return pos;
    }

    public static boolean eq(int len, int[] x, int[] y) {
        for (int i = len - 1; i >= 0; --i) {
            if (x[i] == y[i]) continue;
            return false;
        }
        return true;
    }

    public static int equalTo(int len, int[] x, int y) {
        int d = x[0] ^ y;
        for (int i = 1; i < len; ++i) {
            d |= x[i];
        }
        d = d >>> 1 | d & 1;
        return d - 1 >> 31;
    }

    public static int equalTo(int len, int[] x, int xOff, int y) {
        int d = x[xOff] ^ y;
        for (int i = 1; i < len; ++i) {
            d |= x[xOff + i];
        }
        d = d >>> 1 | d & 1;
        return d - 1 >> 31;
    }

    public static int equalTo(int len, int[] x, int[] y) {
        int d = 0;
        for (int i = 0; i < len; ++i) {
            d |= x[i] ^ y[i];
        }
        d = d >>> 1 | d & 1;
        return d - 1 >> 31;
    }

    public static int equalTo(int len, int[] x, int xOff, int[] y, int yOff) {
        int d = 0;
        for (int i = 0; i < len; ++i) {
            d |= x[xOff + i] ^ y[yOff + i];
        }
        d = d >>> 1 | d & 1;
        return d - 1 >> 31;
    }

    public static int equalToZero(int len, int[] x) {
        int d = 0;
        for (int i = 0; i < len; ++i) {
            d |= x[i];
        }
        d = d >>> 1 | d & 1;
        return d - 1 >> 31;
    }

    public static int equalToZero(int len, int[] x, int xOff) {
        int d = 0;
        for (int i = 0; i < len; ++i) {
            d |= x[xOff + i];
        }
        d = d >>> 1 | d & 1;
        return d - 1 >> 31;
    }

    public static int[] fromBigInteger(int bits, BigInteger x) {
        if (x.signum() < 0 || x.bitLength() > bits) {
            throw new IllegalArgumentException();
        }
        int len = bits + 31 >> 5;
        int[] z = Nat.create(len);
        for (int i = 0; i < len; ++i) {
            z[i] = x.intValue();
            x = x.shiftRight(32);
        }
        return z;
    }

    public static long[] fromBigInteger64(int bits, BigInteger x) {
        if (x.signum() < 0 || x.bitLength() > bits) {
            throw new IllegalArgumentException();
        }
        int len = bits + 63 >> 6;
        long[] z = Nat.create64(len);
        for (int i = 0; i < len; ++i) {
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
        if (w < 0 || w >= x.length) {
            return 0;
        }
        int b = bit & 0x1F;
        return x[w] >>> b & 1;
    }

    public static boolean gte(int len, int[] x, int[] y) {
        for (int i = len - 1; i >= 0; --i) {
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

    public static boolean gte(int len, int[] x, int xOff, int[] y, int yOff) {
        for (int i = len - 1; i >= 0; --i) {
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

    public static int inc(int len, int[] z) {
        int i = 0;
        while (i < len) {
            int n = i++;
            z[n] = z[n] + 1;
            if (z[n] == 0) continue;
            return 0;
        }
        return 1;
    }

    public static int inc(int len, int[] x, int[] z) {
        for (int i = 0; i < len; ++i) {
            int c;
            z[i] = c = x[i] + 1;
            if (c == 0) continue;
            while (i < len) {
                z[i] = x[i];
                ++i;
            }
            return 0;
        }
        return 1;
    }

    public static int incAt(int len, int[] z, int zPos) {
        int i = zPos;
        while (i < len) {
            int n = i++;
            z[n] = z[n] + 1;
            if (z[n] == 0) continue;
            return 0;
        }
        return 1;
    }

    public static int incAt(int len, int[] z, int zOff, int zPos) {
        for (int i = zPos; i < len; ++i) {
            int n = zOff + i;
            z[n] = z[n] + 1;
            if (z[n] == 0) continue;
            return 0;
        }
        return 1;
    }

    public static boolean isOne(int len, int[] x) {
        if (x[0] != 1) {
            return false;
        }
        for (int i = 1; i < len; ++i) {
            if (x[i] == 0) continue;
            return false;
        }
        return true;
    }

    public static boolean isZero(int len, int[] x) {
        for (int i = 0; i < len; ++i) {
            if (x[i] == 0) continue;
            return false;
        }
        return true;
    }

    public static int lessThan(int len, int[] x, int[] y) {
        long c = 0L;
        for (int i = 0; i < len; ++i) {
            c += ((long)x[i] & 0xFFFFFFFFL) - ((long)y[i] & 0xFFFFFFFFL);
            c >>= 32;
        }
        return (int)c;
    }

    public static int lessThan(int len, int[] x, int xOff, int[] y, int yOff) {
        long c = 0L;
        for (int i = 0; i < len; ++i) {
            c += ((long)x[xOff + i] & 0xFFFFFFFFL) - ((long)y[yOff + i] & 0xFFFFFFFFL);
            c >>= 32;
        }
        return (int)c;
    }

    public static void mul(int len, int[] x, int[] y, int[] zz) {
        zz[len] = Nat.mulWord(len, x[0], y, zz);
        for (int i = 1; i < len; ++i) {
            zz[i + len] = Nat.mulWordAddTo(len, x[i], y, 0, zz, i);
        }
    }

    public static void mul(int len, int[] x, int xOff, int[] y, int yOff, int[] zz, int zzOff) {
        zz[zzOff + len] = Nat.mulWord(len, x[xOff], y, yOff, zz, zzOff);
        for (int i = 1; i < len; ++i) {
            zz[zzOff + i + len] = Nat.mulWordAddTo(len, x[xOff + i], y, yOff, zz, zzOff + i);
        }
    }

    public static void mul(int[] x, int xOff, int xLen, int[] y, int yOff, int yLen, int[] zz, int zzOff) {
        zz[zzOff + yLen] = Nat.mulWord(yLen, x[xOff], y, yOff, zz, zzOff);
        for (int i = 1; i < xLen; ++i) {
            zz[zzOff + i + yLen] = Nat.mulWordAddTo(yLen, x[xOff + i], y, yOff, zz, zzOff + i);
        }
    }

    public static int mulAddTo(int len, int[] x, int[] y, int[] zz) {
        long zc = 0L;
        for (int i = 0; i < len; ++i) {
            zc += (long)Nat.mulWordAddTo(len, x[i], y, 0, zz, i) & 0xFFFFFFFFL;
            zz[i + len] = (int)(zc += (long)zz[i + len] & 0xFFFFFFFFL);
            zc >>>= 32;
        }
        return (int)zc;
    }

    public static int mulAddTo(int len, int[] x, int xOff, int[] y, int yOff, int[] zz, int zzOff) {
        long zc = 0L;
        for (int i = 0; i < len; ++i) {
            zc += (long)Nat.mulWordAddTo(len, x[xOff + i], y, yOff, zz, zzOff) & 0xFFFFFFFFL;
            zz[zzOff + len] = (int)(zc += (long)zz[zzOff + len] & 0xFFFFFFFFL);
            zc >>>= 32;
            ++zzOff;
        }
        return (int)zc;
    }

    public static int mul31BothAdd(int len, int a, int[] x, int b, int[] y, int[] z, int zOff) {
        long c = 0L;
        long aVal = (long)a & 0xFFFFFFFFL;
        long bVal = (long)b & 0xFFFFFFFFL;
        int i = 0;
        do {
            z[zOff + i] = (int)(c += aVal * ((long)x[i] & 0xFFFFFFFFL) + bVal * ((long)y[i] & 0xFFFFFFFFL) + ((long)z[zOff + i] & 0xFFFFFFFFL));
            c >>>= 32;
        } while (++i < len);
        return (int)c;
    }

    public static int mulWord(int len, int x, int[] y, int[] z) {
        long c = 0L;
        long xVal = (long)x & 0xFFFFFFFFL;
        int i = 0;
        do {
            z[i] = (int)(c += xVal * ((long)y[i] & 0xFFFFFFFFL));
            c >>>= 32;
        } while (++i < len);
        return (int)c;
    }

    public static int mulWord(int len, int x, int[] y, int yOff, int[] z, int zOff) {
        long c = 0L;
        long xVal = (long)x & 0xFFFFFFFFL;
        int i = 0;
        do {
            z[zOff + i] = (int)(c += xVal * ((long)y[yOff + i] & 0xFFFFFFFFL));
            c >>>= 32;
        } while (++i < len);
        return (int)c;
    }

    public static int mulWordAddTo(int len, int x, int[] y, int yOff, int[] z, int zOff) {
        long c = 0L;
        long xVal = (long)x & 0xFFFFFFFFL;
        int i = 0;
        do {
            z[zOff + i] = (int)(c += xVal * ((long)y[yOff + i] & 0xFFFFFFFFL) + ((long)z[zOff + i] & 0xFFFFFFFFL));
            c >>>= 32;
        } while (++i < len);
        return (int)c;
    }

    public static int mulWordDwordAddAt(int len, int x, long y, int[] z, int zPos) {
        long c = 0L;
        long xVal = (long)x & 0xFFFFFFFFL;
        z[zPos + 0] = (int)(c += xVal * (y & 0xFFFFFFFFL) + ((long)z[zPos + 0] & 0xFFFFFFFFL));
        c >>>= 32;
        z[zPos + 1] = (int)(c += xVal * (y >>> 32) + ((long)z[zPos + 1] & 0xFFFFFFFFL));
        c >>>= 32;
        z[zPos + 2] = (int)(c += (long)z[zPos + 2] & 0xFFFFFFFFL);
        return (c >>>= 32) == 0L ? 0 : Nat.incAt(len, z, zPos + 3);
    }

    public static int shiftDownBit(int len, int[] z, int c) {
        int i = len;
        while (--i >= 0) {
            int next = z[i];
            z[i] = next >>> 1 | c << 31;
            c = next;
        }
        return c << 31;
    }

    public static int shiftDownBit(int len, int[] z, int zOff, int c) {
        int i = len;
        while (--i >= 0) {
            int next = z[zOff + i];
            z[zOff + i] = next >>> 1 | c << 31;
            c = next;
        }
        return c << 31;
    }

    public static int shiftDownBit(int len, int[] x, int c, int[] z) {
        int i = len;
        while (--i >= 0) {
            int next = x[i];
            z[i] = next >>> 1 | c << 31;
            c = next;
        }
        return c << 31;
    }

    public static int shiftDownBit(int len, int[] x, int xOff, int c, int[] z, int zOff) {
        int i = len;
        while (--i >= 0) {
            int next = x[xOff + i];
            z[zOff + i] = next >>> 1 | c << 31;
            c = next;
        }
        return c << 31;
    }

    public static int shiftDownBits(int len, int[] z, int bits, int c) {
        int i = len;
        while (--i >= 0) {
            int next = z[i];
            z[i] = next >>> bits | c << -bits;
            c = next;
        }
        return c << -bits;
    }

    public static int shiftDownBits(int len, int[] z, int zOff, int bits, int c) {
        int i = len;
        while (--i >= 0) {
            int next = z[zOff + i];
            z[zOff + i] = next >>> bits | c << -bits;
            c = next;
        }
        return c << -bits;
    }

    public static int shiftDownBits(int len, int[] x, int bits, int c, int[] z) {
        int i = len;
        while (--i >= 0) {
            int next = x[i];
            z[i] = next >>> bits | c << -bits;
            c = next;
        }
        return c << -bits;
    }

    public static int shiftDownBits(int len, int[] x, int xOff, int bits, int c, int[] z, int zOff) {
        int i = len;
        while (--i >= 0) {
            int next = x[xOff + i];
            z[zOff + i] = next >>> bits | c << -bits;
            c = next;
        }
        return c << -bits;
    }

    public static int shiftDownWord(int len, int[] z, int c) {
        int i = len;
        while (--i >= 0) {
            int next = z[i];
            z[i] = c;
            c = next;
        }
        return c;
    }

    public static int shiftUpBit(int len, int[] z, int c) {
        for (int i = 0; i < len; ++i) {
            int next = z[i];
            z[i] = next << 1 | c >>> 31;
            c = next;
        }
        return c >>> 31;
    }

    public static int shiftUpBit(int len, int[] z, int zOff, int c) {
        for (int i = 0; i < len; ++i) {
            int next = z[zOff + i];
            z[zOff + i] = next << 1 | c >>> 31;
            c = next;
        }
        return c >>> 31;
    }

    public static int shiftUpBit(int len, int[] x, int c, int[] z) {
        for (int i = 0; i < len; ++i) {
            int next = x[i];
            z[i] = next << 1 | c >>> 31;
            c = next;
        }
        return c >>> 31;
    }

    public static int shiftUpBit(int len, int[] x, int xOff, int c, int[] z, int zOff) {
        for (int i = 0; i < len; ++i) {
            int next = x[xOff + i];
            z[zOff + i] = next << 1 | c >>> 31;
            c = next;
        }
        return c >>> 31;
    }

    public static long shiftUpBit64(int len, long[] x, int xOff, long c, long[] z, int zOff) {
        for (int i = 0; i < len; ++i) {
            long next = x[xOff + i];
            z[zOff + i] = next << 1 | c >>> 63;
            c = next;
        }
        return c >>> 63;
    }

    public static int shiftUpBits(int len, int[] z, int bits, int c) {
        for (int i = 0; i < len; ++i) {
            int next = z[i];
            z[i] = next << bits | c >>> -bits;
            c = next;
        }
        return c >>> -bits;
    }

    public static int shiftUpBits(int len, int[] z, int zOff, int bits, int c) {
        for (int i = 0; i < len; ++i) {
            int next = z[zOff + i];
            z[zOff + i] = next << bits | c >>> -bits;
            c = next;
        }
        return c >>> -bits;
    }

    public static long shiftUpBits64(int len, long[] z, int zOff, int bits, long c) {
        for (int i = 0; i < len; ++i) {
            long next = z[zOff + i];
            z[zOff + i] = next << bits | c >>> -bits;
            c = next;
        }
        return c >>> -bits;
    }

    public static int shiftUpBits(int len, int[] x, int bits, int c, int[] z) {
        for (int i = 0; i < len; ++i) {
            int next = x[i];
            z[i] = next << bits | c >>> -bits;
            c = next;
        }
        return c >>> -bits;
    }

    public static int shiftUpBits(int len, int[] x, int xOff, int bits, int c, int[] z, int zOff) {
        for (int i = 0; i < len; ++i) {
            int next = x[xOff + i];
            z[zOff + i] = next << bits | c >>> -bits;
            c = next;
        }
        return c >>> -bits;
    }

    public static long shiftUpBits64(int len, long[] x, int xOff, int bits, long c, long[] z, int zOff) {
        for (int i = 0; i < len; ++i) {
            long next = x[xOff + i];
            z[zOff + i] = next << bits | c >>> -bits;
            c = next;
        }
        return c >>> -bits;
    }

    public static void square(int len, int[] x, int[] zz) {
        int extLen = len << 1;
        int c = 0;
        int j = len;
        int k = extLen;
        do {
            long xVal = (long)x[--j] & 0xFFFFFFFFL;
            long p = xVal * xVal;
            zz[--k] = c << 31 | (int)(p >>> 33);
            zz[--k] = (int)(p >>> 1);
            c = (int)p;
        } while (j > 0);
        long d = 0L;
        int zzPos = 2;
        for (int i = 1; i < len; ++i) {
            d += (long)Nat.squareWordAddTo(x, i, zz) & 0xFFFFFFFFL;
            d += (long)zz[zzPos] & 0xFFFFFFFFL;
            zz[zzPos++] = (int)d;
            d >>>= 32;
            d += (long)zz[zzPos] & 0xFFFFFFFFL;
            zz[zzPos++] = (int)d;
            d >>>= 32;
        }
        Nat.shiftUpBit(extLen, zz, x[0] << 31);
    }

    public static void square(int len, int[] x, int xOff, int[] zz, int zzOff) {
        int extLen = len << 1;
        int c = 0;
        int j = len;
        int k = extLen;
        do {
            long xVal = (long)x[xOff + --j] & 0xFFFFFFFFL;
            long p = xVal * xVal;
            zz[zzOff + --k] = c << 31 | (int)(p >>> 33);
            zz[zzOff + --k] = (int)(p >>> 1);
            c = (int)p;
        } while (j > 0);
        long d = 0L;
        int zzPos = zzOff + 2;
        for (int i = 1; i < len; ++i) {
            d += (long)Nat.squareWordAddTo(x, xOff, i, zz, zzOff) & 0xFFFFFFFFL;
            d += (long)zz[zzPos] & 0xFFFFFFFFL;
            zz[zzPos++] = (int)d;
            d >>>= 32;
            d += (long)zz[zzPos] & 0xFFFFFFFFL;
            zz[zzPos++] = (int)d;
            d >>>= 32;
        }
        Nat.shiftUpBit(extLen, zz, zzOff, x[xOff] << 31);
    }

    public static int squareWordAddTo(int[] x, int xPos, int[] z) {
        long c = 0L;
        long xVal = (long)x[xPos] & 0xFFFFFFFFL;
        int i = 0;
        do {
            z[xPos + i] = (int)(c += xVal * ((long)x[i] & 0xFFFFFFFFL) + ((long)z[xPos + i] & 0xFFFFFFFFL));
            c >>>= 32;
        } while (++i < xPos);
        return (int)c;
    }

    public static int squareWordAddTo(int[] x, int xOff, int xPos, int[] z, int zOff) {
        long c = 0L;
        long xVal = (long)x[xOff + xPos] & 0xFFFFFFFFL;
        int i = 0;
        do {
            z[xPos + zOff] = (int)(c += xVal * ((long)x[xOff + i] & 0xFFFFFFFFL) + ((long)z[xPos + zOff] & 0xFFFFFFFFL));
            c >>>= 32;
            ++zOff;
        } while (++i < xPos);
        return (int)c;
    }

    public static int sub(int len, int[] x, int[] y, int[] z) {
        long c = 0L;
        for (int i = 0; i < len; ++i) {
            z[i] = (int)(c += ((long)x[i] & 0xFFFFFFFFL) - ((long)y[i] & 0xFFFFFFFFL));
            c >>= 32;
        }
        return (int)c;
    }

    public static int sub(int len, int[] x, int xOff, int[] y, int yOff, int[] z, int zOff) {
        long c = 0L;
        for (int i = 0; i < len; ++i) {
            z[zOff + i] = (int)(c += ((long)x[xOff + i] & 0xFFFFFFFFL) - ((long)y[yOff + i] & 0xFFFFFFFFL));
            c >>= 32;
        }
        return (int)c;
    }

    public static int sub33At(int len, int x, int[] z, int zPos) {
        long c = ((long)z[zPos + 0] & 0xFFFFFFFFL) - ((long)x & 0xFFFFFFFFL);
        z[zPos + 0] = (int)c;
        c >>= 32;
        z[zPos + 1] = (int)(c += ((long)z[zPos + 1] & 0xFFFFFFFFL) - 1L);
        return (c >>= 32) == 0L ? 0 : Nat.decAt(len, z, zPos + 2);
    }

    public static int sub33At(int len, int x, int[] z, int zOff, int zPos) {
        long c = ((long)z[zOff + zPos] & 0xFFFFFFFFL) - ((long)x & 0xFFFFFFFFL);
        z[zOff + zPos] = (int)c;
        c >>= 32;
        z[zOff + zPos + 1] = (int)(c += ((long)z[zOff + zPos + 1] & 0xFFFFFFFFL) - 1L);
        return (c >>= 32) == 0L ? 0 : Nat.decAt(len, z, zOff, zPos + 2);
    }

    public static int sub33From(int len, int x, int[] z) {
        long c = ((long)z[0] & 0xFFFFFFFFL) - ((long)x & 0xFFFFFFFFL);
        z[0] = (int)c;
        c >>= 32;
        z[1] = (int)(c += ((long)z[1] & 0xFFFFFFFFL) - 1L);
        return (c >>= 32) == 0L ? 0 : Nat.decAt(len, z, 2);
    }

    public static int sub33From(int len, int x, int[] z, int zOff) {
        long c = ((long)z[zOff + 0] & 0xFFFFFFFFL) - ((long)x & 0xFFFFFFFFL);
        z[zOff + 0] = (int)c;
        c >>= 32;
        z[zOff + 1] = (int)(c += ((long)z[zOff + 1] & 0xFFFFFFFFL) - 1L);
        return (c >>= 32) == 0L ? 0 : Nat.decAt(len, z, zOff, 2);
    }

    public static int subBothFrom(int len, int[] x, int[] y, int[] z) {
        long c = 0L;
        for (int i = 0; i < len; ++i) {
            z[i] = (int)(c += ((long)z[i] & 0xFFFFFFFFL) - ((long)x[i] & 0xFFFFFFFFL) - ((long)y[i] & 0xFFFFFFFFL));
            c >>= 32;
        }
        return (int)c;
    }

    public static int subBothFrom(int len, int[] x, int xOff, int[] y, int yOff, int[] z, int zOff) {
        long c = 0L;
        for (int i = 0; i < len; ++i) {
            z[zOff + i] = (int)(c += ((long)z[zOff + i] & 0xFFFFFFFFL) - ((long)x[xOff + i] & 0xFFFFFFFFL) - ((long)y[yOff + i] & 0xFFFFFFFFL));
            c >>= 32;
        }
        return (int)c;
    }

    public static int subDWordAt(int len, long x, int[] z, int zPos) {
        long c = ((long)z[zPos + 0] & 0xFFFFFFFFL) - (x & 0xFFFFFFFFL);
        z[zPos + 0] = (int)c;
        c >>= 32;
        z[zPos + 1] = (int)(c += ((long)z[zPos + 1] & 0xFFFFFFFFL) - (x >>> 32));
        return (c >>= 32) == 0L ? 0 : Nat.decAt(len, z, zPos + 2);
    }

    public static int subDWordAt(int len, long x, int[] z, int zOff, int zPos) {
        long c = ((long)z[zOff + zPos] & 0xFFFFFFFFL) - (x & 0xFFFFFFFFL);
        z[zOff + zPos] = (int)c;
        c >>= 32;
        z[zOff + zPos + 1] = (int)(c += ((long)z[zOff + zPos + 1] & 0xFFFFFFFFL) - (x >>> 32));
        return (c >>= 32) == 0L ? 0 : Nat.decAt(len, z, zOff, zPos + 2);
    }

    public static int subDWordFrom(int len, long x, int[] z) {
        long c = ((long)z[0] & 0xFFFFFFFFL) - (x & 0xFFFFFFFFL);
        z[0] = (int)c;
        c >>= 32;
        z[1] = (int)(c += ((long)z[1] & 0xFFFFFFFFL) - (x >>> 32));
        return (c >>= 32) == 0L ? 0 : Nat.decAt(len, z, 2);
    }

    public static int subDWordFrom(int len, long x, int[] z, int zOff) {
        long c = ((long)z[zOff + 0] & 0xFFFFFFFFL) - (x & 0xFFFFFFFFL);
        z[zOff + 0] = (int)c;
        c >>= 32;
        z[zOff + 1] = (int)(c += ((long)z[zOff + 1] & 0xFFFFFFFFL) - (x >>> 32));
        return (c >>= 32) == 0L ? 0 : Nat.decAt(len, z, zOff, 2);
    }

    public static int subFrom(int len, int[] x, int[] z) {
        long c = 0L;
        for (int i = 0; i < len; ++i) {
            z[i] = (int)(c += ((long)z[i] & 0xFFFFFFFFL) - ((long)x[i] & 0xFFFFFFFFL));
            c >>= 32;
        }
        return (int)c;
    }

    public static int subFrom(int len, int[] x, int xOff, int[] z, int zOff) {
        long c = 0L;
        for (int i = 0; i < len; ++i) {
            z[zOff + i] = (int)(c += ((long)z[zOff + i] & 0xFFFFFFFFL) - ((long)x[xOff + i] & 0xFFFFFFFFL));
            c >>= 32;
        }
        return (int)c;
    }

    public static int subWordAt(int len, int x, int[] z, int zPos) {
        long c = ((long)z[zPos] & 0xFFFFFFFFL) - ((long)x & 0xFFFFFFFFL);
        z[zPos] = (int)c;
        return (c >>= 32) == 0L ? 0 : Nat.decAt(len, z, zPos + 1);
    }

    public static int subWordAt(int len, int x, int[] z, int zOff, int zPos) {
        long c = ((long)z[zOff + zPos] & 0xFFFFFFFFL) - ((long)x & 0xFFFFFFFFL);
        z[zOff + zPos] = (int)c;
        return (c >>= 32) == 0L ? 0 : Nat.decAt(len, z, zOff, zPos + 1);
    }

    public static int subWordFrom(int len, int x, int[] z) {
        long c = ((long)z[0] & 0xFFFFFFFFL) - ((long)x & 0xFFFFFFFFL);
        z[0] = (int)c;
        return (c >>= 32) == 0L ? 0 : Nat.decAt(len, z, 1);
    }

    public static int subWordFrom(int len, int x, int[] z, int zOff) {
        long c = ((long)z[zOff + 0] & 0xFFFFFFFFL) - ((long)x & 0xFFFFFFFFL);
        z[zOff + 0] = (int)c;
        return (c >>= 32) == 0L ? 0 : Nat.decAt(len, z, zOff, 1);
    }

    public static BigInteger toBigInteger(int len, int[] x) {
        byte[] bs = new byte[len << 2];
        for (int i = 0; i < len; ++i) {
            int x_i = x[i];
            if (x_i == 0) continue;
            Pack.intToBigEndian(x_i, bs, len - 1 - i << 2);
        }
        return new BigInteger(1, bs);
    }

    public static void zero(int len, int[] z) {
        for (int i = 0; i < len; ++i) {
            z[i] = 0;
        }
    }

    public static void zero(int len, int[] z, int zOff) {
        for (int i = 0; i < len; ++i) {
            z[zOff + i] = 0;
        }
    }

    public static void zero64(int len, long[] z) {
        for (int i = 0; i < len; ++i) {
            z[i] = 0L;
        }
    }
}

