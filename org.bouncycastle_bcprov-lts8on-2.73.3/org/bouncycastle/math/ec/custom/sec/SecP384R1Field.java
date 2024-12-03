/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat384;
import org.bouncycastle.util.Pack;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecP384R1Field {
    private static final long M = 0xFFFFFFFFL;
    static final int[] P = new int[]{-1, 0, 0, -1, -2, -1, -1, -1, -1, -1, -1, -1};
    private static final int[] PExt = new int[]{1, -2, 0, 2, 0, -2, 0, 2, 1, 0, 0, 0, -2, 1, 0, -2, -3, -1, -1, -1, -1, -1, -1, -1};
    private static final int[] PExtInv = new int[]{-1, 1, -1, -3, -1, 1, -1, -3, -2, -1, -1, -1, 1, -2, -1, 1, 2};
    private static final int P11 = -1;
    private static final int PExt23 = -1;

    public static void add(int[] x, int[] y, int[] z) {
        int c = Nat.add(12, x, y, z);
        if (c != 0 || z[11] == -1 && Nat.gte(12, z, P)) {
            SecP384R1Field.addPInvTo(z);
        }
    }

    public static void addExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.add(24, xx, yy, zz);
        if ((c != 0 || zz[23] == -1 && Nat.gte(24, zz, PExt)) && Nat.addTo(PExtInv.length, PExtInv, zz) != 0) {
            Nat.incAt(24, zz, PExtInv.length);
        }
    }

    public static void addOne(int[] x, int[] z) {
        int c = Nat.inc(12, x, z);
        if (c != 0 || z[11] == -1 && Nat.gte(12, z, P)) {
            SecP384R1Field.addPInvTo(z);
        }
    }

    public static int[] fromBigInteger(BigInteger x) {
        int[] z = Nat.fromBigInteger(384, x);
        if (z[11] == -1 && Nat.gte(12, z, P)) {
            Nat.subFrom(12, P, z);
        }
        return z;
    }

    public static void half(int[] x, int[] z) {
        if ((x[0] & 1) == 0) {
            Nat.shiftDownBit(12, x, 0, z);
        } else {
            int c = Nat.add(12, x, P, z);
            Nat.shiftDownBit(12, z, c);
        }
    }

    public static void inv(int[] x, int[] z) {
        Mod.checkedModOddInverse(P, x, z);
    }

    public static int isZero(int[] x) {
        int d = 0;
        for (int i = 0; i < 12; ++i) {
            d |= x[i];
        }
        d = d >>> 1 | d & 1;
        return d - 1 >> 31;
    }

    public static void multiply(int[] x, int[] y, int[] z) {
        int[] tt = Nat.create(24);
        Nat384.mul(x, y, tt);
        SecP384R1Field.reduce(tt, z);
    }

    public static void multiply(int[] x, int[] y, int[] z, int[] tt) {
        Nat384.mul(x, y, tt);
        SecP384R1Field.reduce(tt, z);
    }

    public static void negate(int[] x, int[] z) {
        if (0 != SecP384R1Field.isZero(x)) {
            Nat.sub(12, P, P, z);
        } else {
            Nat.sub(12, P, x, z);
        }
    }

    public static void random(SecureRandom r, int[] z) {
        byte[] bb = new byte[48];
        do {
            r.nextBytes(bb);
            Pack.littleEndianToInt(bb, 0, z, 0, 12);
        } while (0 == Nat.lessThan(12, z, P));
    }

    public static void randomMult(SecureRandom r, int[] z) {
        do {
            SecP384R1Field.random(r, z);
        } while (0 != SecP384R1Field.isZero(z));
    }

    public static void reduce(int[] xx, int[] z) {
        long xx16 = (long)xx[16] & 0xFFFFFFFFL;
        long xx17 = (long)xx[17] & 0xFFFFFFFFL;
        long xx18 = (long)xx[18] & 0xFFFFFFFFL;
        long xx19 = (long)xx[19] & 0xFFFFFFFFL;
        long xx20 = (long)xx[20] & 0xFFFFFFFFL;
        long xx21 = (long)xx[21] & 0xFFFFFFFFL;
        long xx22 = (long)xx[22] & 0xFFFFFFFFL;
        long xx23 = (long)xx[23] & 0xFFFFFFFFL;
        long n = 1L;
        long t0 = ((long)xx[12] & 0xFFFFFFFFL) + xx20 - 1L;
        long t1 = ((long)xx[13] & 0xFFFFFFFFL) + xx22;
        long t2 = ((long)xx[14] & 0xFFFFFFFFL) + xx22 + xx23;
        long t3 = ((long)xx[15] & 0xFFFFFFFFL) + xx23;
        long t4 = xx17 + xx21;
        long t5 = xx21 - xx23;
        long t6 = xx22 - xx23;
        long t7 = t0 + t5;
        long cc = 0L;
        z[0] = (int)(cc += ((long)xx[0] & 0xFFFFFFFFL) + t7);
        cc >>= 32;
        z[1] = (int)(cc += ((long)xx[1] & 0xFFFFFFFFL) + xx23 - t0 + t1);
        cc >>= 32;
        z[2] = (int)(cc += ((long)xx[2] & 0xFFFFFFFFL) - xx21 - t1 + t2);
        cc >>= 32;
        z[3] = (int)(cc += ((long)xx[3] & 0xFFFFFFFFL) - t2 + t3 + t7);
        cc >>= 32;
        z[4] = (int)(cc += ((long)xx[4] & 0xFFFFFFFFL) + xx16 + xx21 + t1 - t3 + t7);
        cc >>= 32;
        z[5] = (int)(cc += ((long)xx[5] & 0xFFFFFFFFL) - xx16 + t1 + t2 + t4);
        cc >>= 32;
        z[6] = (int)(cc += ((long)xx[6] & 0xFFFFFFFFL) + xx18 - xx17 + t2 + t3);
        cc >>= 32;
        z[7] = (int)(cc += ((long)xx[7] & 0xFFFFFFFFL) + xx16 + xx19 - xx18 + t3);
        cc >>= 32;
        z[8] = (int)(cc += ((long)xx[8] & 0xFFFFFFFFL) + xx16 + xx17 + xx20 - xx19);
        cc >>= 32;
        z[9] = (int)(cc += ((long)xx[9] & 0xFFFFFFFFL) + xx18 - xx20 + t4);
        cc >>= 32;
        z[10] = (int)(cc += ((long)xx[10] & 0xFFFFFFFFL) + xx18 + xx19 - t5 + t6);
        cc >>= 32;
        z[11] = (int)(cc += ((long)xx[11] & 0xFFFFFFFFL) + xx19 + xx20 - t6);
        cc >>= 32;
        SecP384R1Field.reduce32((int)(++cc), z);
    }

    public static void reduce32(int x, int[] z) {
        long cc = 0L;
        if (x != 0) {
            long xx12 = (long)x & 0xFFFFFFFFL;
            z[0] = (int)(cc += ((long)z[0] & 0xFFFFFFFFL) + xx12);
            cc >>= 32;
            z[1] = (int)(cc += ((long)z[1] & 0xFFFFFFFFL) - xx12);
            if ((cc >>= 32) != 0L) {
                z[2] = (int)(cc += (long)z[2] & 0xFFFFFFFFL);
                cc >>= 32;
            }
            z[3] = (int)(cc += ((long)z[3] & 0xFFFFFFFFL) + xx12);
            cc >>= 32;
            z[4] = (int)(cc += ((long)z[4] & 0xFFFFFFFFL) + xx12);
            cc >>= 32;
        }
        if (cc != 0L && Nat.incAt(12, z, 5) != 0 || z[11] == -1 && Nat.gte(12, z, P)) {
            SecP384R1Field.addPInvTo(z);
        }
    }

    public static void square(int[] x, int[] z) {
        int[] tt = Nat.create(24);
        Nat384.square(x, tt);
        SecP384R1Field.reduce(tt, z);
    }

    public static void square(int[] x, int[] z, int[] tt) {
        Nat384.square(x, tt);
        SecP384R1Field.reduce(tt, z);
    }

    public static void squareN(int[] x, int n, int[] z) {
        int[] tt = Nat.create(24);
        Nat384.square(x, tt);
        SecP384R1Field.reduce(tt, z);
        while (--n > 0) {
            Nat384.square(z, tt);
            SecP384R1Field.reduce(tt, z);
        }
    }

    public static void squareN(int[] x, int n, int[] z, int[] tt) {
        Nat384.square(x, tt);
        SecP384R1Field.reduce(tt, z);
        while (--n > 0) {
            Nat384.square(z, tt);
            SecP384R1Field.reduce(tt, z);
        }
    }

    public static void subtract(int[] x, int[] y, int[] z) {
        int c = Nat.sub(12, x, y, z);
        if (c != 0) {
            SecP384R1Field.subPInvFrom(z);
        }
    }

    public static void subtractExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.sub(24, xx, yy, zz);
        if (c != 0 && Nat.subFrom(PExtInv.length, PExtInv, zz) != 0) {
            Nat.decAt(24, zz, PExtInv.length);
        }
    }

    public static void twice(int[] x, int[] z) {
        int c = Nat.shiftUpBit(12, x, 0, z);
        if (c != 0 || z[11] == -1 && Nat.gte(12, z, P)) {
            SecP384R1Field.addPInvTo(z);
        }
    }

    private static void addPInvTo(int[] z) {
        long c = ((long)z[0] & 0xFFFFFFFFL) + 1L;
        z[0] = (int)c;
        c >>= 32;
        z[1] = (int)(c += ((long)z[1] & 0xFFFFFFFFL) - 1L);
        if ((c >>= 32) != 0L) {
            z[2] = (int)(c += (long)z[2] & 0xFFFFFFFFL);
            c >>= 32;
        }
        z[3] = (int)(c += ((long)z[3] & 0xFFFFFFFFL) + 1L);
        c >>= 32;
        z[4] = (int)(c += ((long)z[4] & 0xFFFFFFFFL) + 1L);
        if ((c >>= 32) != 0L) {
            Nat.incAt(12, z, 5);
        }
    }

    private static void subPInvFrom(int[] z) {
        long c = ((long)z[0] & 0xFFFFFFFFL) - 1L;
        z[0] = (int)c;
        c >>= 32;
        z[1] = (int)(c += ((long)z[1] & 0xFFFFFFFFL) + 1L);
        if ((c >>= 32) != 0L) {
            z[2] = (int)(c += (long)z[2] & 0xFFFFFFFFL);
            c >>= 32;
        }
        z[3] = (int)(c += ((long)z[3] & 0xFFFFFFFFL) - 1L);
        c >>= 32;
        z[4] = (int)(c += ((long)z[4] & 0xFFFFFFFFL) - 1L);
        if ((c >>= 32) != 0L) {
            Nat.decAt(12, z, 5);
        }
    }
}

