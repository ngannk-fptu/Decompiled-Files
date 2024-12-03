/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat160;
import org.bouncycastle.util.Pack;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecP160R1Field {
    private static final long M = 0xFFFFFFFFL;
    static final int[] P = new int[]{Integer.MAX_VALUE, -1, -1, -1, -1};
    private static final int[] PExt = new int[]{1, 0x40000001, 0, 0, 0, -2, -2, -1, -1, -1};
    private static final int[] PExtInv = new int[]{-1, -1073741826, -1, -1, -1, 1, 1};
    private static final int P4 = -1;
    private static final int PExt9 = -1;
    private static final int PInv = -2147483647;

    public static void add(int[] x, int[] y, int[] z) {
        int c = Nat160.add(x, y, z);
        if (c != 0 || z[4] == -1 && Nat160.gte(z, P)) {
            Nat.addWordTo(5, -2147483647, z);
        }
    }

    public static void addExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.add(10, xx, yy, zz);
        if ((c != 0 || zz[9] == -1 && Nat.gte(10, zz, PExt)) && Nat.addTo(PExtInv.length, PExtInv, zz) != 0) {
            Nat.incAt(10, zz, PExtInv.length);
        }
    }

    public static void addOne(int[] x, int[] z) {
        int c = Nat.inc(5, x, z);
        if (c != 0 || z[4] == -1 && Nat160.gte(z, P)) {
            Nat.addWordTo(5, -2147483647, z);
        }
    }

    public static int[] fromBigInteger(BigInteger x) {
        int[] z = Nat160.fromBigInteger(x);
        if (z[4] == -1 && Nat160.gte(z, P)) {
            Nat160.subFrom(P, z);
        }
        return z;
    }

    public static void half(int[] x, int[] z) {
        if ((x[0] & 1) == 0) {
            Nat.shiftDownBit(5, x, 0, z);
        } else {
            int c = Nat160.add(x, P, z);
            Nat.shiftDownBit(5, z, c);
        }
    }

    public static void inv(int[] x, int[] z) {
        Mod.checkedModOddInverse(P, x, z);
    }

    public static int isZero(int[] x) {
        int d = 0;
        for (int i = 0; i < 5; ++i) {
            d |= x[i];
        }
        d = d >>> 1 | d & 1;
        return d - 1 >> 31;
    }

    public static void multiply(int[] x, int[] y, int[] z) {
        int[] tt = Nat160.createExt();
        Nat160.mul(x, y, tt);
        SecP160R1Field.reduce(tt, z);
    }

    public static void multiplyAddToExt(int[] x, int[] y, int[] zz) {
        int c = Nat160.mulAddTo(x, y, zz);
        if ((c != 0 || zz[9] == -1 && Nat.gte(10, zz, PExt)) && Nat.addTo(PExtInv.length, PExtInv, zz) != 0) {
            Nat.incAt(10, zz, PExtInv.length);
        }
    }

    public static void negate(int[] x, int[] z) {
        if (0 != SecP160R1Field.isZero(x)) {
            Nat160.sub(P, P, z);
        } else {
            Nat160.sub(P, x, z);
        }
    }

    public static void random(SecureRandom r, int[] z) {
        byte[] bb = new byte[20];
        do {
            r.nextBytes(bb);
            Pack.littleEndianToInt(bb, 0, z, 0, 5);
        } while (0 == Nat.lessThan(5, z, P));
    }

    public static void randomMult(SecureRandom r, int[] z) {
        do {
            SecP160R1Field.random(r, z);
        } while (0 != SecP160R1Field.isZero(z));
    }

    public static void reduce(int[] xx, int[] z) {
        long x5 = (long)xx[5] & 0xFFFFFFFFL;
        long x6 = (long)xx[6] & 0xFFFFFFFFL;
        long x7 = (long)xx[7] & 0xFFFFFFFFL;
        long x8 = (long)xx[8] & 0xFFFFFFFFL;
        long x9 = (long)xx[9] & 0xFFFFFFFFL;
        long c = 0L;
        z[0] = (int)(c += ((long)xx[0] & 0xFFFFFFFFL) + x5 + (x5 << 31));
        c >>>= 32;
        z[1] = (int)(c += ((long)xx[1] & 0xFFFFFFFFL) + x6 + (x6 << 31));
        c >>>= 32;
        z[2] = (int)(c += ((long)xx[2] & 0xFFFFFFFFL) + x7 + (x7 << 31));
        c >>>= 32;
        z[3] = (int)(c += ((long)xx[3] & 0xFFFFFFFFL) + x8 + (x8 << 31));
        c >>>= 32;
        z[4] = (int)(c += ((long)xx[4] & 0xFFFFFFFFL) + x9 + (x9 << 31));
        SecP160R1Field.reduce32((int)(c >>>= 32), z);
    }

    public static void reduce32(int x, int[] z) {
        if (x != 0 && Nat160.mulWordsAdd(-2147483647, x, z, 0) != 0 || z[4] == -1 && Nat160.gte(z, P)) {
            Nat.addWordTo(5, -2147483647, z);
        }
    }

    public static void square(int[] x, int[] z) {
        int[] tt = Nat160.createExt();
        Nat160.square(x, tt);
        SecP160R1Field.reduce(tt, z);
    }

    public static void squareN(int[] x, int n, int[] z) {
        int[] tt = Nat160.createExt();
        Nat160.square(x, tt);
        SecP160R1Field.reduce(tt, z);
        while (--n > 0) {
            Nat160.square(z, tt);
            SecP160R1Field.reduce(tt, z);
        }
    }

    public static void subtract(int[] x, int[] y, int[] z) {
        int c = Nat160.sub(x, y, z);
        if (c != 0) {
            Nat.subWordFrom(5, -2147483647, z);
        }
    }

    public static void subtractExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.sub(10, xx, yy, zz);
        if (c != 0 && Nat.subFrom(PExtInv.length, PExtInv, zz) != 0) {
            Nat.decAt(10, zz, PExtInv.length);
        }
    }

    public static void twice(int[] x, int[] z) {
        int c = Nat.shiftUpBit(5, x, 0, z);
        if (c != 0 || z[4] == -1 && Nat160.gte(z, P)) {
            Nat.addWordTo(5, -2147483647, z);
        }
    }
}

