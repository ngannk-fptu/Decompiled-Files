/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat224;
import org.bouncycastle.util.Pack;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecP224R1Field {
    private static final long M = 0xFFFFFFFFL;
    static final int[] P = new int[]{1, 0, 0, -1, -1, -1, -1};
    private static final int[] PExt = new int[]{1, 0, 0, -2, -1, -1, 0, 2, 0, 0, -2, -1, -1, -1};
    private static final int[] PExtInv = new int[]{-1, -1, -1, 1, 0, 0, -1, -3, -1, -1, 1};
    private static final int P6 = -1;
    private static final int PExt13 = -1;

    public static void add(int[] x, int[] y, int[] z) {
        int c = Nat224.add(x, y, z);
        if (c != 0 || z[6] == -1 && Nat224.gte(z, P)) {
            SecP224R1Field.addPInvTo(z);
        }
    }

    public static void addExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.add(14, xx, yy, zz);
        if ((c != 0 || zz[13] == -1 && Nat.gte(14, zz, PExt)) && Nat.addTo(PExtInv.length, PExtInv, zz) != 0) {
            Nat.incAt(14, zz, PExtInv.length);
        }
    }

    public static void addOne(int[] x, int[] z) {
        int c = Nat.inc(7, x, z);
        if (c != 0 || z[6] == -1 && Nat224.gte(z, P)) {
            SecP224R1Field.addPInvTo(z);
        }
    }

    public static int[] fromBigInteger(BigInteger x) {
        int[] z = Nat224.fromBigInteger(x);
        if (z[6] == -1 && Nat224.gte(z, P)) {
            Nat224.subFrom(P, z);
        }
        return z;
    }

    public static void half(int[] x, int[] z) {
        if ((x[0] & 1) == 0) {
            Nat.shiftDownBit(7, x, 0, z);
        } else {
            int c = Nat224.add(x, P, z);
            Nat.shiftDownBit(7, z, c);
        }
    }

    public static void inv(int[] x, int[] z) {
        Mod.checkedModOddInverse(P, x, z);
    }

    public static int isZero(int[] x) {
        int d = 0;
        for (int i = 0; i < 7; ++i) {
            d |= x[i];
        }
        d = d >>> 1 | d & 1;
        return d - 1 >> 31;
    }

    public static void multiply(int[] x, int[] y, int[] z) {
        int[] tt = Nat224.createExt();
        Nat224.mul(x, y, tt);
        SecP224R1Field.reduce(tt, z);
    }

    public static void multiplyAddToExt(int[] x, int[] y, int[] zz) {
        int c = Nat224.mulAddTo(x, y, zz);
        if ((c != 0 || zz[13] == -1 && Nat.gte(14, zz, PExt)) && Nat.addTo(PExtInv.length, PExtInv, zz) != 0) {
            Nat.incAt(14, zz, PExtInv.length);
        }
    }

    public static void negate(int[] x, int[] z) {
        if (0 != SecP224R1Field.isZero(x)) {
            Nat224.sub(P, P, z);
        } else {
            Nat224.sub(P, x, z);
        }
    }

    public static void random(SecureRandom r, int[] z) {
        byte[] bb = new byte[28];
        do {
            r.nextBytes(bb);
            Pack.littleEndianToInt(bb, 0, z, 0, 7);
        } while (0 == Nat.lessThan(7, z, P));
    }

    public static void randomMult(SecureRandom r, int[] z) {
        do {
            SecP224R1Field.random(r, z);
        } while (0 != SecP224R1Field.isZero(z));
    }

    public static void reduce(int[] xx, int[] z) {
        long xx10 = (long)xx[10] & 0xFFFFFFFFL;
        long xx11 = (long)xx[11] & 0xFFFFFFFFL;
        long xx12 = (long)xx[12] & 0xFFFFFFFFL;
        long xx13 = (long)xx[13] & 0xFFFFFFFFL;
        long n = 1L;
        long t0 = ((long)xx[7] & 0xFFFFFFFFL) + xx11 - 1L;
        long t1 = ((long)xx[8] & 0xFFFFFFFFL) + xx12;
        long t2 = ((long)xx[9] & 0xFFFFFFFFL) + xx13;
        long cc = 0L;
        long z0 = (cc += ((long)xx[0] & 0xFFFFFFFFL) - t0) & 0xFFFFFFFFL;
        cc >>= 32;
        z[1] = (int)(cc += ((long)xx[1] & 0xFFFFFFFFL) - t1);
        cc >>= 32;
        z[2] = (int)(cc += ((long)xx[2] & 0xFFFFFFFFL) - t2);
        cc >>= 32;
        long z3 = (cc += ((long)xx[3] & 0xFFFFFFFFL) + t0 - xx10) & 0xFFFFFFFFL;
        cc >>= 32;
        z[4] = (int)(cc += ((long)xx[4] & 0xFFFFFFFFL) + t1 - xx11);
        cc >>= 32;
        z[5] = (int)(cc += ((long)xx[5] & 0xFFFFFFFFL) + t2 - xx12);
        cc >>= 32;
        z[6] = (int)(cc += ((long)xx[6] & 0xFFFFFFFFL) + xx10 - xx13);
        cc >>= 32;
        z3 += ++cc;
        z[0] = (int)(z0 -= cc);
        cc = z0 >> 32;
        if (cc != 0L) {
            z[1] = (int)(cc += (long)z[1] & 0xFFFFFFFFL);
            cc >>= 32;
            z[2] = (int)(cc += (long)z[2] & 0xFFFFFFFFL);
            z3 += cc >> 32;
        }
        z[3] = (int)z3;
        cc = z3 >> 32;
        if (cc != 0L && Nat.incAt(7, z, 4) != 0 || z[6] == -1 && Nat224.gte(z, P)) {
            SecP224R1Field.addPInvTo(z);
        }
    }

    public static void reduce32(int x, int[] z) {
        long cc = 0L;
        if (x != 0) {
            long xx07 = (long)x & 0xFFFFFFFFL;
            z[0] = (int)(cc += ((long)z[0] & 0xFFFFFFFFL) - xx07);
            if ((cc >>= 32) != 0L) {
                z[1] = (int)(cc += (long)z[1] & 0xFFFFFFFFL);
                cc >>= 32;
                z[2] = (int)(cc += (long)z[2] & 0xFFFFFFFFL);
                cc >>= 32;
            }
            z[3] = (int)(cc += ((long)z[3] & 0xFFFFFFFFL) + xx07);
            cc >>= 32;
        }
        if (cc != 0L && Nat.incAt(7, z, 4) != 0 || z[6] == -1 && Nat224.gte(z, P)) {
            SecP224R1Field.addPInvTo(z);
        }
    }

    public static void square(int[] x, int[] z) {
        int[] tt = Nat224.createExt();
        Nat224.square(x, tt);
        SecP224R1Field.reduce(tt, z);
    }

    public static void squareN(int[] x, int n, int[] z) {
        int[] tt = Nat224.createExt();
        Nat224.square(x, tt);
        SecP224R1Field.reduce(tt, z);
        while (--n > 0) {
            Nat224.square(z, tt);
            SecP224R1Field.reduce(tt, z);
        }
    }

    public static void subtract(int[] x, int[] y, int[] z) {
        int c = Nat224.sub(x, y, z);
        if (c != 0) {
            SecP224R1Field.subPInvFrom(z);
        }
    }

    public static void subtractExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.sub(14, xx, yy, zz);
        if (c != 0 && Nat.subFrom(PExtInv.length, PExtInv, zz) != 0) {
            Nat.decAt(14, zz, PExtInv.length);
        }
    }

    public static void twice(int[] x, int[] z) {
        int c = Nat.shiftUpBit(7, x, 0, z);
        if (c != 0 || z[6] == -1 && Nat224.gte(z, P)) {
            SecP224R1Field.addPInvTo(z);
        }
    }

    private static void addPInvTo(int[] z) {
        long c = ((long)z[0] & 0xFFFFFFFFL) - 1L;
        z[0] = (int)c;
        if ((c >>= 32) != 0L) {
            z[1] = (int)(c += (long)z[1] & 0xFFFFFFFFL);
            c >>= 32;
            z[2] = (int)(c += (long)z[2] & 0xFFFFFFFFL);
            c >>= 32;
        }
        z[3] = (int)(c += ((long)z[3] & 0xFFFFFFFFL) + 1L);
        if ((c >>= 32) != 0L) {
            Nat.incAt(7, z, 4);
        }
    }

    private static void subPInvFrom(int[] z) {
        long c = ((long)z[0] & 0xFFFFFFFFL) + 1L;
        z[0] = (int)c;
        if ((c >>= 32) != 0L) {
            z[1] = (int)(c += (long)z[1] & 0xFFFFFFFFL);
            c >>= 32;
            z[2] = (int)(c += (long)z[2] & 0xFFFFFFFFL);
            c >>= 32;
        }
        z[3] = (int)(c += ((long)z[3] & 0xFFFFFFFFL) - 1L);
        if ((c >>= 32) != 0L) {
            Nat.decAt(7, z, 4);
        }
    }
}

