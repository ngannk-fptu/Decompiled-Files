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
public class SecP160R2Field {
    static final int[] P = new int[]{-21389, -2, -1, -1, -1};
    private static final int[] PExt = new int[]{457489321, 42778, 1, 0, 0, -42778, -3, -1, -1, -1};
    private static final int[] PExtInv = new int[]{-457489321, -42779, -2, -1, -1, 42777, 2};
    private static final int P4 = -1;
    private static final int PExt9 = -1;
    private static final int PInv33 = 21389;

    public static void add(int[] x, int[] y, int[] z) {
        int c = Nat160.add(x, y, z);
        if (c != 0 || z[4] == -1 && Nat160.gte(z, P)) {
            Nat.add33To(5, 21389, z);
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
            Nat.add33To(5, 21389, z);
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
        SecP160R2Field.reduce(tt, z);
    }

    public static void multiplyAddToExt(int[] x, int[] y, int[] zz) {
        int c = Nat160.mulAddTo(x, y, zz);
        if ((c != 0 || zz[9] == -1 && Nat.gte(10, zz, PExt)) && Nat.addTo(PExtInv.length, PExtInv, zz) != 0) {
            Nat.incAt(10, zz, PExtInv.length);
        }
    }

    public static void negate(int[] x, int[] z) {
        if (0 != SecP160R2Field.isZero(x)) {
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
            SecP160R2Field.random(r, z);
        } while (0 != SecP160R2Field.isZero(z));
    }

    public static void reduce(int[] xx, int[] z) {
        long cc = Nat160.mul33Add(21389, xx, 5, xx, 0, z, 0);
        int c = Nat160.mul33DWordAdd(21389, cc, z, 0);
        if (c != 0 || z[4] == -1 && Nat160.gte(z, P)) {
            Nat.add33To(5, 21389, z);
        }
    }

    public static void reduce32(int x, int[] z) {
        if (x != 0 && Nat160.mul33WordAdd(21389, x, z, 0) != 0 || z[4] == -1 && Nat160.gte(z, P)) {
            Nat.add33To(5, 21389, z);
        }
    }

    public static void square(int[] x, int[] z) {
        int[] tt = Nat160.createExt();
        Nat160.square(x, tt);
        SecP160R2Field.reduce(tt, z);
    }

    public static void squareN(int[] x, int n, int[] z) {
        int[] tt = Nat160.createExt();
        Nat160.square(x, tt);
        SecP160R2Field.reduce(tt, z);
        while (--n > 0) {
            Nat160.square(z, tt);
            SecP160R2Field.reduce(tt, z);
        }
    }

    public static void subtract(int[] x, int[] y, int[] z) {
        int c = Nat160.sub(x, y, z);
        if (c != 0) {
            Nat.sub33From(5, 21389, z);
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
            Nat.add33To(5, 21389, z);
        }
    }
}

