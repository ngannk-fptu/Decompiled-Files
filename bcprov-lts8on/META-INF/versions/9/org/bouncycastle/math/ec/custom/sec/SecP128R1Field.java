/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat128;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Pack;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecP128R1Field {
    private static final long M = 0xFFFFFFFFL;
    static final int[] P = new int[]{-1, -1, -1, -3};
    private static final int[] PExt = new int[]{1, 0, 0, 4, -2, -1, 3, -4};
    private static final int[] PExtInv = new int[]{-1, -1, -1, -5, 1, 0, -4, 3};
    private static final int P3s1 = 0x7FFFFFFE;
    private static final int PExt7s1 = 0x7FFFFFFE;

    public static void add(int[] x, int[] y, int[] z) {
        int c = Nat128.add(x, y, z);
        if (c != 0 || z[3] >>> 1 >= 0x7FFFFFFE && Nat128.gte(z, P)) {
            SecP128R1Field.addPInvTo(z);
        }
    }

    public static void addExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat256.add(xx, yy, zz);
        if (c != 0 || zz[7] >>> 1 >= 0x7FFFFFFE && Nat256.gte(zz, PExt)) {
            Nat.addTo(PExtInv.length, PExtInv, zz);
        }
    }

    public static void addOne(int[] x, int[] z) {
        int c = Nat.inc(4, x, z);
        if (c != 0 || z[3] >>> 1 >= 0x7FFFFFFE && Nat128.gte(z, P)) {
            SecP128R1Field.addPInvTo(z);
        }
    }

    public static int[] fromBigInteger(BigInteger x) {
        int[] z = Nat128.fromBigInteger(x);
        if (z[3] >>> 1 >= 0x7FFFFFFE && Nat128.gte(z, P)) {
            Nat128.subFrom(P, z);
        }
        return z;
    }

    public static void half(int[] x, int[] z) {
        if ((x[0] & 1) == 0) {
            Nat.shiftDownBit(4, x, 0, z);
        } else {
            int c = Nat128.add(x, P, z);
            Nat.shiftDownBit(4, z, c);
        }
    }

    public static void inv(int[] x, int[] z) {
        Mod.checkedModOddInverse(P, x, z);
    }

    public static int isZero(int[] x) {
        int d = 0;
        for (int i = 0; i < 4; ++i) {
            d |= x[i];
        }
        d = d >>> 1 | d & 1;
        return d - 1 >> 31;
    }

    public static void multiply(int[] x, int[] y, int[] z) {
        int[] tt = Nat128.createExt();
        Nat128.mul(x, y, tt);
        SecP128R1Field.reduce(tt, z);
    }

    public static void multiplyAddToExt(int[] x, int[] y, int[] zz) {
        int c = Nat128.mulAddTo(x, y, zz);
        if (c != 0 || zz[7] >>> 1 >= 0x7FFFFFFE && Nat256.gte(zz, PExt)) {
            Nat.addTo(PExtInv.length, PExtInv, zz);
        }
    }

    public static void negate(int[] x, int[] z) {
        if (0 != SecP128R1Field.isZero(x)) {
            Nat128.sub(P, P, z);
        } else {
            Nat128.sub(P, x, z);
        }
    }

    public static void random(SecureRandom r, int[] z) {
        byte[] bb = new byte[16];
        do {
            r.nextBytes(bb);
            Pack.littleEndianToInt(bb, 0, z, 0, 4);
        } while (0 == Nat.lessThan(4, z, P));
    }

    public static void randomMult(SecureRandom r, int[] z) {
        do {
            SecP128R1Field.random(r, z);
        } while (0 != SecP128R1Field.isZero(z));
    }

    public static void reduce(int[] xx, int[] z) {
        long x0 = (long)xx[0] & 0xFFFFFFFFL;
        long x1 = (long)xx[1] & 0xFFFFFFFFL;
        long x2 = (long)xx[2] & 0xFFFFFFFFL;
        long x3 = (long)xx[3] & 0xFFFFFFFFL;
        long x4 = (long)xx[4] & 0xFFFFFFFFL;
        long x5 = (long)xx[5] & 0xFFFFFFFFL;
        long x6 = (long)xx[6] & 0xFFFFFFFFL;
        long x7 = (long)xx[7] & 0xFFFFFFFFL;
        x3 += x7;
        x2 += (x6 += x7 << 1);
        x1 += (x5 += x6 << 1);
        x3 += x4 << 1;
        z[0] = (int)(x0 += (x4 += x5 << 1));
        z[1] = (int)(x1 += x0 >>> 32);
        z[2] = (int)(x2 += x1 >>> 32);
        z[3] = (int)(x3 += x2 >>> 32);
        SecP128R1Field.reduce32((int)(x3 >>> 32), z);
    }

    public static void reduce32(int x, int[] z) {
        while (x != 0) {
            long x4 = (long)x & 0xFFFFFFFFL;
            long c = ((long)z[0] & 0xFFFFFFFFL) + x4;
            z[0] = (int)c;
            if ((c >>= 32) != 0L) {
                z[1] = (int)(c += (long)z[1] & 0xFFFFFFFFL);
                c >>= 32;
                z[2] = (int)(c += (long)z[2] & 0xFFFFFFFFL);
                c >>= 32;
            }
            z[3] = (int)(c += ((long)z[3] & 0xFFFFFFFFL) + (x4 << 1));
            x = (int)(c >>= 32);
        }
        if (z[3] >>> 1 >= 0x7FFFFFFE && Nat128.gte(z, P)) {
            SecP128R1Field.addPInvTo(z);
        }
    }

    public static void square(int[] x, int[] z) {
        int[] tt = Nat128.createExt();
        Nat128.square(x, tt);
        SecP128R1Field.reduce(tt, z);
    }

    public static void squareN(int[] x, int n, int[] z) {
        int[] tt = Nat128.createExt();
        Nat128.square(x, tt);
        SecP128R1Field.reduce(tt, z);
        while (--n > 0) {
            Nat128.square(z, tt);
            SecP128R1Field.reduce(tt, z);
        }
    }

    public static void subtract(int[] x, int[] y, int[] z) {
        int c = Nat128.sub(x, y, z);
        if (c != 0) {
            SecP128R1Field.subPInvFrom(z);
        }
    }

    public static void subtractExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.sub(10, xx, yy, zz);
        if (c != 0) {
            Nat.subFrom(PExtInv.length, PExtInv, zz);
        }
    }

    public static void twice(int[] x, int[] z) {
        int c = Nat.shiftUpBit(4, x, 0, z);
        if (c != 0 || z[3] >>> 1 >= 0x7FFFFFFE && Nat128.gte(z, P)) {
            SecP128R1Field.addPInvTo(z);
        }
    }

    private static void addPInvTo(int[] z) {
        long c = ((long)z[0] & 0xFFFFFFFFL) + 1L;
        z[0] = (int)c;
        if ((c >>= 32) != 0L) {
            z[1] = (int)(c += (long)z[1] & 0xFFFFFFFFL);
            c >>= 32;
            z[2] = (int)(c += (long)z[2] & 0xFFFFFFFFL);
            c >>= 32;
        }
        z[3] = (int)(c += ((long)z[3] & 0xFFFFFFFFL) + 2L);
    }

    private static void subPInvFrom(int[] z) {
        long c = ((long)z[0] & 0xFFFFFFFFL) - 1L;
        z[0] = (int)c;
        if ((c >>= 32) != 0L) {
            z[1] = (int)(c += (long)z[1] & 0xFFFFFFFFL);
            c >>= 32;
            z[2] = (int)(c += (long)z[2] & 0xFFFFFFFFL);
            c >>= 32;
        }
        z[3] = (int)(c += ((long)z[3] & 0xFFFFFFFFL) - 2L);
    }
}

