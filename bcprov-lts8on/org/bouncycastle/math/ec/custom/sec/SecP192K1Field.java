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
public class SecP192K1Field {
    static final int[] P = new int[]{-4553, -2, -1, -1, -1, -1};
    private static final int[] PExt = new int[]{20729809, 9106, 1, 0, 0, 0, -9106, -3, -1, -1, -1, -1};
    private static final int[] PExtInv = new int[]{-20729809, -9107, -2, -1, -1, -1, 9105, 2};
    private static final int P5 = -1;
    private static final int PExt11 = -1;
    private static final int PInv33 = 4553;

    public static void add(int[] x, int[] y, int[] z) {
        int c = Nat192.add(x, y, z);
        if (c != 0 || z[5] == -1 && Nat192.gte(z, P)) {
            Nat.add33To(6, 4553, z);
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
            Nat.add33To(6, 4553, z);
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
        SecP192K1Field.reduce(tt, z);
    }

    public static void multiplyAddToExt(int[] x, int[] y, int[] zz) {
        int c = Nat192.mulAddTo(x, y, zz);
        if ((c != 0 || zz[11] == -1 && Nat.gte(12, zz, PExt)) && Nat.addTo(PExtInv.length, PExtInv, zz) != 0) {
            Nat.incAt(12, zz, PExtInv.length);
        }
    }

    public static void negate(int[] x, int[] z) {
        if (0 != SecP192K1Field.isZero(x)) {
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
            SecP192K1Field.random(r, z);
        } while (0 != SecP192K1Field.isZero(z));
    }

    public static void reduce(int[] xx, int[] z) {
        long cc = Nat192.mul33Add(4553, xx, 6, xx, 0, z, 0);
        int c = Nat192.mul33DWordAdd(4553, cc, z, 0);
        if (c != 0 || z[5] == -1 && Nat192.gte(z, P)) {
            Nat.add33To(6, 4553, z);
        }
    }

    public static void reduce32(int x, int[] z) {
        if (x != 0 && Nat192.mul33WordAdd(4553, x, z, 0) != 0 || z[5] == -1 && Nat192.gte(z, P)) {
            Nat.add33To(6, 4553, z);
        }
    }

    public static void square(int[] x, int[] z) {
        int[] tt = Nat192.createExt();
        Nat192.square(x, tt);
        SecP192K1Field.reduce(tt, z);
    }

    public static void squareN(int[] x, int n, int[] z) {
        int[] tt = Nat192.createExt();
        Nat192.square(x, tt);
        SecP192K1Field.reduce(tt, z);
        while (--n > 0) {
            Nat192.square(z, tt);
            SecP192K1Field.reduce(tt, z);
        }
    }

    public static void subtract(int[] x, int[] y, int[] z) {
        int c = Nat192.sub(x, y, z);
        if (c != 0) {
            Nat.sub33From(6, 4553, z);
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
            Nat.add33To(6, 4553, z);
        }
    }
}

