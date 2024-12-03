/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.util.Pack;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecP192R1Field {
    private static final long M = 0xFFFFFFFFL;
    static final int[] P = new int[]{-1, -1, -2, -1, -1, -1};
    private static final int[] PExt = new int[]{1, 0, 2, 0, 1, 0, -2, -1, -3, -1, -1, -1};
    private static final int[] PExtInv = new int[]{-1, -1, -3, -1, -2, -1, 1, 0, 2};
    private static final int P5 = -1;
    private static final int PExt11 = -1;

    public static void add(int[] x, int[] y, int[] z) {
        int c = Nat192.add(x, y, z);
        if (c != 0 || z[5] == -1 && Nat192.gte(z, P)) {
            SecP192R1Field.addPInvTo(z);
        }
    }

    public static void addExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.add(12, xx, yy, zz);
        if ((c != 0 || zz[11] == -1 && Nat.gte(12, zz, PExt)) && Nat.addTo(PExtInv.length, PExtInv, zz) != 0) {
            Nat.incAt(12, zz, PExtInv.length);
        }
    }

    public static void addOne(int[] x, int[] z) {
        int c = Nat.inc(6, x, z);
        if (c != 0 || z[5] == -1 && Nat192.gte(z, P)) {
            SecP192R1Field.addPInvTo(z);
        }
    }

    public static int[] fromBigInteger(BigInteger x) {
        int[] z = Nat192.fromBigInteger(x);
        if (z[5] == -1 && Nat192.gte(z, P)) {
            Nat192.subFrom(P, z);
        }
        return z;
    }

    public static void half(int[] x, int[] z) {
        if ((x[0] & 1) == 0) {
            Nat.shiftDownBit(6, x, 0, z);
        } else {
            int c = Nat192.add(x, P, z);
            Nat.shiftDownBit(6, z, c);
        }
    }

    public static void inv(int[] x, int[] z) {
        Mod.checkedModOddInverse(P, x, z);
    }

    public static int isZero(int[] x) {
        int d = 0;
        for (int i = 0; i < 6; ++i) {
            d |= x[i];
        }
        d = d >>> 1 | d & 1;
        return d - 1 >> 31;
    }

    public static void multiply(int[] x, int[] y, int[] z) {
        int[] tt = Nat192.createExt();
        Nat192.mul(x, y, tt);
        SecP192R1Field.reduce(tt, z);
    }

    public static void multiplyAddToExt(int[] x, int[] y, int[] zz) {
        int c = Nat192.mulAddTo(x, y, zz);
        if ((c != 0 || zz[11] == -1 && Nat.gte(12, zz, PExt)) && Nat.addTo(PExtInv.length, PExtInv, zz) != 0) {
            Nat.incAt(12, zz, PExtInv.length);
        }
    }

    public static void negate(int[] x, int[] z) {
        if (0 != SecP192R1Field.isZero(x)) {
            Nat192.sub(P, P, z);
        } else {
            Nat192.sub(P, x, z);
        }
    }

    public static void random(SecureRandom r, int[] z) {
        byte[] bb = new byte[24];
        do {
            r.nextBytes(bb);
            Pack.littleEndianToInt(bb, 0, z, 0, 6);
        } while (0 == Nat.lessThan(6, z, P));
    }

    public static void randomMult(SecureRandom r, int[] z) {
        do {
            SecP192R1Field.random(r, z);
        } while (0 != SecP192R1Field.isZero(z));
    }

    public static void reduce(int[] xx, int[] z) {
        long xx06 = (long)xx[6] & 0xFFFFFFFFL;
        long xx07 = (long)xx[7] & 0xFFFFFFFFL;
        long xx08 = (long)xx[8] & 0xFFFFFFFFL;
        long xx09 = (long)xx[9] & 0xFFFFFFFFL;
        long xx10 = (long)xx[10] & 0xFFFFFFFFL;
        long xx11 = (long)xx[11] & 0xFFFFFFFFL;
        long t0 = xx06 + xx10;
        long t1 = xx07 + xx11;
        long cc = 0L;
        int z0 = (int)(cc += ((long)xx[0] & 0xFFFFFFFFL) + t0);
        cc >>= 32;
        z[1] = (int)(cc += ((long)xx[1] & 0xFFFFFFFFL) + t1);
        cc >>= 32;
        long z2 = (cc += ((long)xx[2] & 0xFFFFFFFFL) + (t0 += xx08)) & 0xFFFFFFFFL;
        cc >>= 32;
        z[3] = (int)(cc += ((long)xx[3] & 0xFFFFFFFFL) + (t1 += xx09));
        cc >>= 32;
        z[4] = (int)(cc += ((long)xx[4] & 0xFFFFFFFFL) + (t0 -= xx06));
        cc >>= 32;
        z[5] = (int)(cc += ((long)xx[5] & 0xFFFFFFFFL) + (t1 -= xx07));
        z2 += (cc >>= 32);
        z[0] = (int)(cc += (long)z0 & 0xFFFFFFFFL);
        if ((cc >>= 32) != 0L) {
            z[1] = (int)(cc += (long)z[1] & 0xFFFFFFFFL);
            z2 += cc >> 32;
        }
        z[2] = (int)z2;
        cc = z2 >> 32;
        if (cc != 0L && Nat.incAt(6, z, 3) != 0 || z[5] == -1 && Nat192.gte(z, P)) {
            SecP192R1Field.addPInvTo(z);
        }
    }

    public static void reduce32(int x, int[] z) {
        long cc = 0L;
        if (x != 0) {
            long xx06 = (long)x & 0xFFFFFFFFL;
            z[0] = (int)(cc += ((long)z[0] & 0xFFFFFFFFL) + xx06);
            if ((cc >>= 32) != 0L) {
                z[1] = (int)(cc += (long)z[1] & 0xFFFFFFFFL);
                cc >>= 32;
            }
            z[2] = (int)(cc += ((long)z[2] & 0xFFFFFFFFL) + xx06);
            cc >>= 32;
        }
        if (cc != 0L && Nat.incAt(6, z, 3) != 0 || z[5] == -1 && Nat192.gte(z, P)) {
            SecP192R1Field.addPInvTo(z);
        }
    }

    public static void square(int[] x, int[] z) {
        int[] tt = Nat192.createExt();
        Nat192.square(x, tt);
        SecP192R1Field.reduce(tt, z);
    }

    public static void squareN(int[] x, int n, int[] z) {
        int[] tt = Nat192.createExt();
        Nat192.square(x, tt);
        SecP192R1Field.reduce(tt, z);
        while (--n > 0) {
            Nat192.square(z, tt);
            SecP192R1Field.reduce(tt, z);
        }
    }

    public static void subtract(int[] x, int[] y, int[] z) {
        int c = Nat192.sub(x, y, z);
        if (c != 0) {
            SecP192R1Field.subPInvFrom(z);
        }
    }

    public static void subtractExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.sub(12, xx, yy, zz);
        if (c != 0 && Nat.subFrom(PExtInv.length, PExtInv, zz) != 0) {
            Nat.decAt(12, zz, PExtInv.length);
        }
    }

    public static void twice(int[] x, int[] z) {
        int c = Nat.shiftUpBit(6, x, 0, z);
        if (c != 0 || z[5] == -1 && Nat192.gte(z, P)) {
            SecP192R1Field.addPInvTo(z);
        }
    }

    private static void addPInvTo(int[] z) {
        long c = ((long)z[0] & 0xFFFFFFFFL) + 1L;
        z[0] = (int)c;
        if ((c >>= 32) != 0L) {
            z[1] = (int)(c += (long)z[1] & 0xFFFFFFFFL);
            c >>= 32;
        }
        z[2] = (int)(c += ((long)z[2] & 0xFFFFFFFFL) + 1L);
        if ((c >>= 32) != 0L) {
            Nat.incAt(6, z, 3);
        }
    }

    private static void subPInvFrom(int[] z) {
        long c = ((long)z[0] & 0xFFFFFFFFL) - 1L;
        z[0] = (int)c;
        if ((c >>= 32) != 0L) {
            z[1] = (int)(c += (long)z[1] & 0xFFFFFFFFL);
            c >>= 32;
        }
        z[2] = (int)(c += ((long)z[2] & 0xFFFFFFFFL) - 1L);
        if ((c >>= 32) != 0L) {
            Nat.decAt(6, z, 3);
        }
    }
}

