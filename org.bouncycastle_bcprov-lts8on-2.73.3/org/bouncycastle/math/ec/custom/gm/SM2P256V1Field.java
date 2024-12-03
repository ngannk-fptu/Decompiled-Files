/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.gm;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Pack;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SM2P256V1Field {
    private static final long M = 0xFFFFFFFFL;
    static final int[] P = new int[]{-1, -1, 0, -1, -1, -1, -1, -2};
    private static final int[] PExt = new int[]{1, 0, -2, 1, 1, -2, 0, 2, -2, -3, 3, -2, -1, -1, 0, -2};
    private static final int P7s1 = Integer.MAX_VALUE;
    private static final int PExt15s1 = Integer.MAX_VALUE;

    public static void add(int[] x, int[] y, int[] z) {
        int c = Nat256.add(x, y, z);
        if (c != 0 || z[7] >>> 1 >= Integer.MAX_VALUE && Nat256.gte(z, P)) {
            SM2P256V1Field.addPInvTo(z);
        }
    }

    public static void addExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.add(16, xx, yy, zz);
        if (c != 0 || zz[15] >>> 1 >= Integer.MAX_VALUE && Nat.gte(16, zz, PExt)) {
            Nat.subFrom(16, PExt, zz);
        }
    }

    public static void addOne(int[] x, int[] z) {
        int c = Nat.inc(8, x, z);
        if (c != 0 || z[7] >>> 1 >= Integer.MAX_VALUE && Nat256.gte(z, P)) {
            SM2P256V1Field.addPInvTo(z);
        }
    }

    public static int[] fromBigInteger(BigInteger x) {
        int[] z = Nat256.fromBigInteger(x);
        if (z[7] >>> 1 >= Integer.MAX_VALUE && Nat256.gte(z, P)) {
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
        SM2P256V1Field.reduce(tt, z);
    }

    public static void multiplyAddToExt(int[] x, int[] y, int[] zz) {
        int c = Nat256.mulAddTo(x, y, zz);
        if (c != 0 || zz[15] >>> 1 >= Integer.MAX_VALUE && Nat.gte(16, zz, PExt)) {
            Nat.subFrom(16, PExt, zz);
        }
    }

    public static void negate(int[] x, int[] z) {
        if (0 != SM2P256V1Field.isZero(x)) {
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
            SM2P256V1Field.random(r, z);
        } while (0 != SM2P256V1Field.isZero(z));
    }

    public static void reduce(int[] xx, int[] z) {
        long xx08 = (long)xx[8] & 0xFFFFFFFFL;
        long xx09 = (long)xx[9] & 0xFFFFFFFFL;
        long xx10 = (long)xx[10] & 0xFFFFFFFFL;
        long xx11 = (long)xx[11] & 0xFFFFFFFFL;
        long xx12 = (long)xx[12] & 0xFFFFFFFFL;
        long xx13 = (long)xx[13] & 0xFFFFFFFFL;
        long xx14 = (long)xx[14] & 0xFFFFFFFFL;
        long xx15 = (long)xx[15] & 0xFFFFFFFFL;
        long t0 = xx08 + xx09;
        long t1 = xx10 + xx11;
        long t2 = xx12 + xx15;
        long t3 = xx13 + xx14;
        long t4 = t3 + (xx15 << 1);
        long ts = t0 + t3;
        long tt = t1 + t2 + ts;
        long cc = 0L;
        z[0] = (int)(cc += ((long)xx[0] & 0xFFFFFFFFL) + tt + xx13 + xx14 + xx15);
        cc >>= 32;
        z[1] = (int)(cc += ((long)xx[1] & 0xFFFFFFFFL) + tt - xx08 + xx14 + xx15);
        cc >>= 32;
        z[2] = (int)(cc += ((long)xx[2] & 0xFFFFFFFFL) - ts);
        cc >>= 32;
        z[3] = (int)(cc += ((long)xx[3] & 0xFFFFFFFFL) + tt - xx09 - xx10 + xx13);
        cc >>= 32;
        z[4] = (int)(cc += ((long)xx[4] & 0xFFFFFFFFL) + tt - t1 - xx08 + xx14);
        cc >>= 32;
        z[5] = (int)(cc += ((long)xx[5] & 0xFFFFFFFFL) + t4 + xx10);
        cc >>= 32;
        z[6] = (int)(cc += ((long)xx[6] & 0xFFFFFFFFL) + xx11 + xx14 + xx15);
        cc >>= 32;
        z[7] = (int)(cc += ((long)xx[7] & 0xFFFFFFFFL) + tt + t4 + xx12);
        SM2P256V1Field.reduce32((int)(cc >>= 32), z);
    }

    public static void reduce32(int x, int[] z) {
        long cc = 0L;
        if (x != 0) {
            long xx08 = (long)x & 0xFFFFFFFFL;
            z[0] = (int)(cc += ((long)z[0] & 0xFFFFFFFFL) + xx08);
            if ((cc >>= 32) != 0L) {
                z[1] = (int)(cc += (long)z[1] & 0xFFFFFFFFL);
                cc >>= 32;
            }
            z[2] = (int)(cc += ((long)z[2] & 0xFFFFFFFFL) - xx08);
            cc >>= 32;
            z[3] = (int)(cc += ((long)z[3] & 0xFFFFFFFFL) + xx08);
            if ((cc >>= 32) != 0L) {
                z[4] = (int)(cc += (long)z[4] & 0xFFFFFFFFL);
                cc >>= 32;
                z[5] = (int)(cc += (long)z[5] & 0xFFFFFFFFL);
                cc >>= 32;
                z[6] = (int)(cc += (long)z[6] & 0xFFFFFFFFL);
                cc >>= 32;
            }
            z[7] = (int)(cc += ((long)z[7] & 0xFFFFFFFFL) + xx08);
            cc >>= 32;
        }
        if (cc != 0L || z[7] >>> 1 >= Integer.MAX_VALUE && Nat256.gte(z, P)) {
            SM2P256V1Field.addPInvTo(z);
        }
    }

    public static void square(int[] x, int[] z) {
        int[] tt = Nat256.createExt();
        Nat256.square(x, tt);
        SM2P256V1Field.reduce(tt, z);
    }

    public static void squareN(int[] x, int n, int[] z) {
        int[] tt = Nat256.createExt();
        Nat256.square(x, tt);
        SM2P256V1Field.reduce(tt, z);
        while (--n > 0) {
            Nat256.square(z, tt);
            SM2P256V1Field.reduce(tt, z);
        }
    }

    public static void subtract(int[] x, int[] y, int[] z) {
        int c = Nat256.sub(x, y, z);
        if (c != 0) {
            SM2P256V1Field.subPInvFrom(z);
        }
    }

    public static void subtractExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.sub(16, xx, yy, zz);
        if (c != 0) {
            Nat.addTo(16, PExt, zz);
        }
    }

    public static void twice(int[] x, int[] z) {
        int c = Nat.shiftUpBit(8, x, 0, z);
        if (c != 0 || z[7] >>> 1 >= Integer.MAX_VALUE && Nat256.gte(z, P)) {
            SM2P256V1Field.addPInvTo(z);
        }
    }

    private static void addPInvTo(int[] z) {
        long c = ((long)z[0] & 0xFFFFFFFFL) + 1L;
        z[0] = (int)c;
        if ((c >>= 32) != 0L) {
            z[1] = (int)(c += (long)z[1] & 0xFFFFFFFFL);
            c >>= 32;
        }
        z[2] = (int)(c += ((long)z[2] & 0xFFFFFFFFL) - 1L);
        c >>= 32;
        z[3] = (int)(c += ((long)z[3] & 0xFFFFFFFFL) + 1L);
        if ((c >>= 32) != 0L) {
            z[4] = (int)(c += (long)z[4] & 0xFFFFFFFFL);
            c >>= 32;
            z[5] = (int)(c += (long)z[5] & 0xFFFFFFFFL);
            c >>= 32;
            z[6] = (int)(c += (long)z[6] & 0xFFFFFFFFL);
            c >>= 32;
        }
        z[7] = (int)(c += ((long)z[7] & 0xFFFFFFFFL) + 1L);
    }

    private static void subPInvFrom(int[] z) {
        long c = ((long)z[0] & 0xFFFFFFFFL) - 1L;
        z[0] = (int)c;
        if ((c >>= 32) != 0L) {
            z[1] = (int)(c += (long)z[1] & 0xFFFFFFFFL);
            c >>= 32;
        }
        z[2] = (int)(c += ((long)z[2] & 0xFFFFFFFFL) + 1L);
        c >>= 32;
        z[3] = (int)(c += ((long)z[3] & 0xFFFFFFFFL) - 1L);
        if ((c >>= 32) != 0L) {
            z[4] = (int)(c += (long)z[4] & 0xFFFFFFFFL);
            c >>= 32;
            z[5] = (int)(c += (long)z[5] & 0xFFFFFFFFL);
            c >>= 32;
            z[6] = (int)(c += (long)z[6] & 0xFFFFFFFFL);
            c >>= 32;
        }
        z[7] = (int)(c += ((long)z[7] & 0xFFFFFFFFL) - 1L);
    }
}

