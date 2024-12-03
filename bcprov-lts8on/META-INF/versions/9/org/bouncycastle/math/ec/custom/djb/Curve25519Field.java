/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.djb;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Pack;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class Curve25519Field {
    private static final long M = 0xFFFFFFFFL;
    static final int[] P = new int[]{-19, -1, -1, -1, -1, -1, -1, Integer.MAX_VALUE};
    private static final int P7 = Integer.MAX_VALUE;
    private static final int[] PExt = new int[]{361, 0, 0, 0, 0, 0, 0, 0, -19, -1, -1, -1, -1, -1, -1, 0x3FFFFFFF};
    private static final int PInv = 19;

    public static void add(int[] x, int[] y, int[] z) {
        Nat256.add(x, y, z);
        if (Nat256.gte(z, P)) {
            Curve25519Field.subPFrom(z);
        }
    }

    public static void addExt(int[] xx, int[] yy, int[] zz) {
        Nat.add(16, xx, yy, zz);
        if (Nat.gte(16, zz, PExt)) {
            Curve25519Field.subPExtFrom(zz);
        }
    }

    public static void addOne(int[] x, int[] z) {
        Nat.inc(8, x, z);
        if (Nat256.gte(z, P)) {
            Curve25519Field.subPFrom(z);
        }
    }

    public static int[] fromBigInteger(BigInteger x) {
        int[] z = Nat256.fromBigInteger(x);
        while (Nat256.gte(z, P)) {
            Nat256.subFrom(P, z);
        }
        return z;
    }

    public static void half(int[] x, int[] z) {
        if ((x[0] & 1) == 0) {
            Nat.shiftDownBit(8, x, 0, z);
        } else {
            Nat256.add(x, P, z);
            Nat.shiftDownBit(8, z, 0);
        }
    }

    public static void inv(int[] x, int[] z) {
        Mod.checkedModOddInverse(P, x, z);
    }

    public static int isZero(int[] x) {
        int d = 0;
        for (int i = 0; i < 8; ++i) {
            d |= x[i];
        }
        d = d >>> 1 | d & 1;
        return d - 1 >> 31;
    }

    public static void multiply(int[] x, int[] y, int[] z) {
        int[] tt = Nat256.createExt();
        Nat256.mul(x, y, tt);
        Curve25519Field.reduce(tt, z);
    }

    public static void multiplyAddToExt(int[] x, int[] y, int[] zz) {
        Nat256.mulAddTo(x, y, zz);
        if (Nat.gte(16, zz, PExt)) {
            Curve25519Field.subPExtFrom(zz);
        }
    }

    public static void negate(int[] x, int[] z) {
        if (0 != Curve25519Field.isZero(x)) {
            Nat256.sub(P, P, z);
        } else {
            Nat256.sub(P, x, z);
        }
    }

    public static void random(SecureRandom r, int[] z) {
        byte[] bb = new byte[32];
        do {
            r.nextBytes(bb);
            Pack.littleEndianToInt(bb, 0, z, 0, 8);
            z[7] = z[7] & Integer.MAX_VALUE;
        } while (0 == Nat.lessThan(8, z, P));
    }

    public static void randomMult(SecureRandom r, int[] z) {
        do {
            Curve25519Field.random(r, z);
        } while (0 != Curve25519Field.isZero(z));
    }

    public static void reduce(int[] xx, int[] z) {
        int xx07 = xx[7];
        Nat.shiftUpBit(8, xx, 8, xx07, z, 0);
        int c = Nat256.mulByWordAddTo(19, xx, z) << 1;
        int z7 = z[7];
        c += (z7 >>> 31) - (xx07 >>> 31);
        z7 &= Integer.MAX_VALUE;
        z[7] = z7 += Nat.addWordTo(7, c * 19, z);
        if (Nat256.gte(z, P)) {
            Curve25519Field.subPFrom(z);
        }
    }

    public static void reduce27(int x, int[] z) {
        int z7 = z[7];
        int c = x << 1 | z7 >>> 31;
        z7 &= Integer.MAX_VALUE;
        z[7] = z7 += Nat.addWordTo(7, c * 19, z);
        if (Nat256.gte(z, P)) {
            Curve25519Field.subPFrom(z);
        }
    }

    public static void square(int[] x, int[] z) {
        int[] tt = Nat256.createExt();
        Nat256.square(x, tt);
        Curve25519Field.reduce(tt, z);
    }

    public static void squareN(int[] x, int n, int[] z) {
        int[] tt = Nat256.createExt();
        Nat256.square(x, tt);
        Curve25519Field.reduce(tt, z);
        while (--n > 0) {
            Nat256.square(z, tt);
            Curve25519Field.reduce(tt, z);
        }
    }

    public static void subtract(int[] x, int[] y, int[] z) {
        int c = Nat256.sub(x, y, z);
        if (c != 0) {
            Curve25519Field.addPTo(z);
        }
    }

    public static void subtractExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.sub(16, xx, yy, zz);
        if (c != 0) {
            Curve25519Field.addPExtTo(zz);
        }
    }

    public static void twice(int[] x, int[] z) {
        Nat.shiftUpBit(8, x, 0, z);
        if (Nat256.gte(z, P)) {
            Curve25519Field.subPFrom(z);
        }
    }

    private static int addPTo(int[] z) {
        long c = ((long)z[0] & 0xFFFFFFFFL) - 19L;
        z[0] = (int)c;
        if ((c >>= 32) != 0L) {
            c = Nat.decAt(7, z, 1);
        }
        z[7] = (int)(c += ((long)z[7] & 0xFFFFFFFFL) + 0x80000000L);
        return (int)(c >>= 32);
    }

    private static int addPExtTo(int[] zz) {
        long c = ((long)zz[0] & 0xFFFFFFFFL) + ((long)PExt[0] & 0xFFFFFFFFL);
        zz[0] = (int)c;
        if ((c >>= 32) != 0L) {
            c = Nat.incAt(8, zz, 1);
        }
        zz[8] = (int)(c += ((long)zz[8] & 0xFFFFFFFFL) - 19L);
        if ((c >>= 32) != 0L) {
            c = Nat.decAt(15, zz, 9);
        }
        zz[15] = (int)(c += ((long)zz[15] & 0xFFFFFFFFL) + ((long)(PExt[15] + 1) & 0xFFFFFFFFL));
        return (int)(c >>= 32);
    }

    private static int subPFrom(int[] z) {
        long c = ((long)z[0] & 0xFFFFFFFFL) + 19L;
        z[0] = (int)c;
        if ((c >>= 32) != 0L) {
            c = Nat.incAt(7, z, 1);
        }
        z[7] = (int)(c += ((long)z[7] & 0xFFFFFFFFL) - 0x80000000L);
        return (int)(c >>= 32);
    }

    private static int subPExtFrom(int[] zz) {
        long c = ((long)zz[0] & 0xFFFFFFFFL) - ((long)PExt[0] & 0xFFFFFFFFL);
        zz[0] = (int)c;
        if ((c >>= 32) != 0L) {
            c = Nat.decAt(8, zz, 1);
        }
        zz[8] = (int)(c += ((long)zz[8] & 0xFFFFFFFFL) + 19L);
        if ((c >>= 32) != 0L) {
            c = Nat.incAt(15, zz, 9);
        }
        zz[15] = (int)(c += ((long)zz[15] & 0xFFFFFFFFL) - ((long)(PExt[15] + 1) & 0xFFFFFFFFL));
        return (int)(c >>= 32);
    }
}

