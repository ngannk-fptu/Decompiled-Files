/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Pack;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecP256K1Field {
    static final int[] P = new int[]{-977, -2, -1, -1, -1, -1, -1, -1};
    private static final int[] PExt = new int[]{954529, 1954, 1, 0, 0, 0, 0, 0, -1954, -3, -1, -1, -1, -1, -1, -1};
    private static final int[] PExtInv = new int[]{-954529, -1955, -2, -1, -1, -1, -1, -1, 1953, 2};
    private static final int P7 = -1;
    private static final int PExt15 = -1;
    private static final int PInv33 = 977;

    public static void add(int[] x, int[] y, int[] z) {
        int c = Nat256.add(x, y, z);
        if (c != 0 || z[7] == -1 && Nat256.gte(z, P)) {
            Nat.add33To(8, 977, z);
        }
    }

    public static void addExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.add(16, xx, yy, zz);
        if ((c != 0 || zz[15] == -1 && Nat.gte(16, zz, PExt)) && Nat.addTo(PExtInv.length, PExtInv, zz) != 0) {
            Nat.incAt(16, zz, PExtInv.length);
        }
    }

    public static void addOne(int[] x, int[] z) {
        int c = Nat.inc(8, x, z);
        if (c != 0 || z[7] == -1 && Nat256.gte(z, P)) {
            Nat.add33To(8, 977, z);
        }
    }

    public static int[] fromBigInteger(BigInteger x) {
        int[] z = Nat256.fromBigInteger(x);
        if (z[7] == -1 && Nat256.gte(z, P)) {
            Nat256.subFrom(P, z);
        }
        return z;
    }

    public static void half(int[] x, int[] z) {
        if ((x[0] & 1) == 0) {
            Nat.shiftDownBit(8, x, 0, z);
        } else {
            int c = Nat256.add(x, P, z);
            Nat.shiftDownBit(8, z, c);
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
        SecP256K1Field.reduce(tt, z);
    }

    public static void multiply(int[] x, int[] y, int[] z, int[] tt) {
        Nat256.mul(x, y, tt);
        SecP256K1Field.reduce(tt, z);
    }

    public static void multiplyAddToExt(int[] x, int[] y, int[] zz) {
        int c = Nat256.mulAddTo(x, y, zz);
        if ((c != 0 || zz[15] == -1 && Nat.gte(16, zz, PExt)) && Nat.addTo(PExtInv.length, PExtInv, zz) != 0) {
            Nat.incAt(16, zz, PExtInv.length);
        }
    }

    public static void negate(int[] x, int[] z) {
        if (0 != SecP256K1Field.isZero(x)) {
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
        } while (0 == Nat.lessThan(8, z, P));
    }

    public static void randomMult(SecureRandom r, int[] z) {
        do {
            SecP256K1Field.random(r, z);
        } while (0 != SecP256K1Field.isZero(z));
    }

    public static void reduce(int[] xx, int[] z) {
        long cc = Nat256.mul33Add(977, xx, 8, xx, 0, z, 0);
        int c = Nat256.mul33DWordAdd(977, cc, z, 0);
        if (c != 0 || z[7] == -1 && Nat256.gte(z, P)) {
            Nat.add33To(8, 977, z);
        }
    }

    public static void reduce32(int x, int[] z) {
        if (x != 0 && Nat256.mul33WordAdd(977, x, z, 0) != 0 || z[7] == -1 && Nat256.gte(z, P)) {
            Nat.add33To(8, 977, z);
        }
    }

    public static void square(int[] x, int[] z) {
        int[] tt = Nat256.createExt();
        Nat256.square(x, tt);
        SecP256K1Field.reduce(tt, z);
    }

    public static void square(int[] x, int[] z, int[] tt) {
        Nat256.square(x, tt);
        SecP256K1Field.reduce(tt, z);
    }

    public static void squareN(int[] x, int n, int[] z) {
        int[] tt = Nat256.createExt();
        Nat256.square(x, tt);
        SecP256K1Field.reduce(tt, z);
        while (--n > 0) {
            Nat256.square(z, tt);
            SecP256K1Field.reduce(tt, z);
        }
    }

    public static void squareN(int[] x, int n, int[] z, int[] tt) {
        Nat256.square(x, tt);
        SecP256K1Field.reduce(tt, z);
        while (--n > 0) {
            Nat256.square(z, tt);
            SecP256K1Field.reduce(tt, z);
        }
    }

    public static void subtract(int[] x, int[] y, int[] z) {
        int c = Nat256.sub(x, y, z);
        if (c != 0) {
            Nat.sub33From(8, 977, z);
        }
    }

    public static void subtractExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.sub(16, xx, yy, zz);
        if (c != 0 && Nat.subFrom(PExtInv.length, PExtInv, zz) != 0) {
            Nat.decAt(16, zz, PExtInv.length);
        }
    }

    public static void twice(int[] x, int[] z) {
        int c = Nat.shiftUpBit(8, x, 0, z);
        if (c != 0 || z[7] == -1 && Nat256.gte(z, P)) {
            Nat.add33To(8, 977, z);
        }
    }
}

