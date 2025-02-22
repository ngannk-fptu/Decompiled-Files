/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat576;

public class SecT571Field {
    private static final long M59 = 0x7FFFFFFFFFFFFFFL;
    private static final long[] ROOT_Z = new long[]{3161836309350906777L, -7642453882179322845L, -3821226941089661423L, 7312758566309945096L, -556661012383879292L, 8945041530681231562L, -4750851271514160027L, 6847946401097695794L, 541669439031730457L};

    public static void add(long[] lArray, long[] lArray2, long[] lArray3) {
        for (int i = 0; i < 9; ++i) {
            lArray3[i] = lArray[i] ^ lArray2[i];
        }
    }

    private static void add(long[] lArray, int n, long[] lArray2, int n2, long[] lArray3, int n3) {
        for (int i = 0; i < 9; ++i) {
            lArray3[n3 + i] = lArray[n + i] ^ lArray2[n2 + i];
        }
    }

    public static void addBothTo(long[] lArray, long[] lArray2, long[] lArray3) {
        for (int i = 0; i < 9; ++i) {
            int n = i;
            lArray3[n] = lArray3[n] ^ (lArray[i] ^ lArray2[i]);
        }
    }

    private static void addBothTo(long[] lArray, int n, long[] lArray2, int n2, long[] lArray3, int n3) {
        for (int i = 0; i < 9; ++i) {
            int n4 = n3 + i;
            lArray3[n4] = lArray3[n4] ^ (lArray[n + i] ^ lArray2[n2 + i]);
        }
    }

    public static void addExt(long[] lArray, long[] lArray2, long[] lArray3) {
        for (int i = 0; i < 18; ++i) {
            lArray3[i] = lArray[i] ^ lArray2[i];
        }
    }

    public static void addOne(long[] lArray, long[] lArray2) {
        lArray2[0] = lArray[0] ^ 1L;
        for (int i = 1; i < 9; ++i) {
            lArray2[i] = lArray[i];
        }
    }

    private static void addTo(long[] lArray, long[] lArray2) {
        for (int i = 0; i < 9; ++i) {
            int n = i;
            lArray2[n] = lArray2[n] ^ lArray[i];
        }
    }

    public static long[] fromBigInteger(BigInteger bigInteger) {
        return Nat.fromBigInteger64(571, bigInteger);
    }

    public static void halfTrace(long[] lArray, long[] lArray2) {
        long[] lArray3 = Nat576.createExt64();
        Nat576.copy64(lArray, lArray2);
        for (int i = 1; i < 571; i += 2) {
            SecT571Field.implSquare(lArray2, lArray3);
            SecT571Field.reduce(lArray3, lArray2);
            SecT571Field.implSquare(lArray2, lArray3);
            SecT571Field.reduce(lArray3, lArray2);
            SecT571Field.addTo(lArray, lArray2);
        }
    }

    public static void invert(long[] lArray, long[] lArray2) {
        if (Nat576.isZero64(lArray)) {
            throw new IllegalStateException();
        }
        long[] lArray3 = Nat576.create64();
        long[] lArray4 = Nat576.create64();
        long[] lArray5 = Nat576.create64();
        SecT571Field.square(lArray, lArray5);
        SecT571Field.square(lArray5, lArray3);
        SecT571Field.square(lArray3, lArray4);
        SecT571Field.multiply(lArray3, lArray4, lArray3);
        SecT571Field.squareN(lArray3, 2, lArray4);
        SecT571Field.multiply(lArray3, lArray4, lArray3);
        SecT571Field.multiply(lArray3, lArray5, lArray3);
        SecT571Field.squareN(lArray3, 5, lArray4);
        SecT571Field.multiply(lArray3, lArray4, lArray3);
        SecT571Field.squareN(lArray4, 5, lArray4);
        SecT571Field.multiply(lArray3, lArray4, lArray3);
        SecT571Field.squareN(lArray3, 15, lArray4);
        SecT571Field.multiply(lArray3, lArray4, lArray5);
        SecT571Field.squareN(lArray5, 30, lArray3);
        SecT571Field.squareN(lArray3, 30, lArray4);
        SecT571Field.multiply(lArray3, lArray4, lArray3);
        SecT571Field.squareN(lArray3, 60, lArray4);
        SecT571Field.multiply(lArray3, lArray4, lArray3);
        SecT571Field.squareN(lArray4, 60, lArray4);
        SecT571Field.multiply(lArray3, lArray4, lArray3);
        SecT571Field.squareN(lArray3, 180, lArray4);
        SecT571Field.multiply(lArray3, lArray4, lArray3);
        SecT571Field.squareN(lArray4, 180, lArray4);
        SecT571Field.multiply(lArray3, lArray4, lArray3);
        SecT571Field.multiply(lArray3, lArray5, lArray2);
    }

    public static void multiply(long[] lArray, long[] lArray2, long[] lArray3) {
        long[] lArray4 = Nat576.createExt64();
        SecT571Field.implMultiply(lArray, lArray2, lArray4);
        SecT571Field.reduce(lArray4, lArray3);
    }

    public static void multiplyAddToExt(long[] lArray, long[] lArray2, long[] lArray3) {
        long[] lArray4 = Nat576.createExt64();
        SecT571Field.implMultiply(lArray, lArray2, lArray4);
        SecT571Field.addExt(lArray3, lArray4, lArray3);
    }

    public static void multiplyPrecomp(long[] lArray, long[] lArray2, long[] lArray3) {
        long[] lArray4 = Nat576.createExt64();
        SecT571Field.implMultiplyPrecomp(lArray, lArray2, lArray4);
        SecT571Field.reduce(lArray4, lArray3);
    }

    public static void multiplyPrecompAddToExt(long[] lArray, long[] lArray2, long[] lArray3) {
        long[] lArray4 = Nat576.createExt64();
        SecT571Field.implMultiplyPrecomp(lArray, lArray2, lArray4);
        SecT571Field.addExt(lArray3, lArray4, lArray3);
    }

    public static long[] precompMultiplicand(long[] lArray) {
        int n = 144;
        long[] lArray2 = new long[n << 1];
        System.arraycopy(lArray, 0, lArray2, 9, 9);
        int n2 = 0;
        for (int i = 7; i > 0; --i) {
            Nat.shiftUpBit64(9, lArray2, (n2 += 18) >>> 1, 0L, lArray2, n2);
            SecT571Field.reduce5(lArray2, n2);
            SecT571Field.add(lArray2, 9, lArray2, n2, lArray2, n2 + 9);
        }
        Nat.shiftUpBits64(n, lArray2, 0, 4, 0L, lArray2, n);
        return lArray2;
    }

    public static void reduce(long[] lArray, long[] lArray2) {
        long l = lArray[9];
        long l2 = lArray[17];
        long l3 = l;
        l = l3 ^ l2 >>> 59 ^ l2 >>> 57 ^ l2 >>> 54 ^ l2 >>> 49;
        l3 = lArray[8] ^ l2 << 5 ^ l2 << 7 ^ l2 << 10 ^ l2 << 15;
        for (int i = 16; i >= 10; --i) {
            l2 = lArray[i];
            lArray2[i - 8] = l3 ^ l2 >>> 59 ^ l2 >>> 57 ^ l2 >>> 54 ^ l2 >>> 49;
            l3 = lArray[i - 9] ^ l2 << 5 ^ l2 << 7 ^ l2 << 10 ^ l2 << 15;
        }
        l2 = l;
        lArray2[1] = l3 ^ l2 >>> 59 ^ l2 >>> 57 ^ l2 >>> 54 ^ l2 >>> 49;
        l3 = lArray[0] ^ l2 << 5 ^ l2 << 7 ^ l2 << 10 ^ l2 << 15;
        long l4 = lArray2[8];
        long l5 = l4 >>> 59;
        lArray2[0] = l3 ^ l5 ^ l5 << 2 ^ l5 << 5 ^ l5 << 10;
        lArray2[8] = l4 & 0x7FFFFFFFFFFFFFFL;
    }

    public static void reduce5(long[] lArray, int n) {
        long l = lArray[n + 8];
        long l2 = l >>> 59;
        int n2 = n;
        lArray[n2] = lArray[n2] ^ (l2 ^ l2 << 2 ^ l2 << 5 ^ l2 << 10);
        lArray[n + 8] = l & 0x7FFFFFFFFFFFFFFL;
    }

    public static void sqrt(long[] lArray, long[] lArray2) {
        long[] lArray3 = Nat576.create64();
        long[] lArray4 = Nat576.create64();
        int n = 0;
        for (int i = 0; i < 4; ++i) {
            long l = Interleave.unshuffle(lArray[n++]);
            long l2 = Interleave.unshuffle(lArray[n++]);
            lArray3[i] = l & 0xFFFFFFFFL | l2 << 32;
            lArray4[i] = l >>> 32 | l2 & 0xFFFFFFFF00000000L;
        }
        long l = Interleave.unshuffle(lArray[n]);
        lArray3[4] = l & 0xFFFFFFFFL;
        lArray4[4] = l >>> 32;
        SecT571Field.multiply(lArray4, ROOT_Z, lArray2);
        SecT571Field.add(lArray2, lArray3, lArray2);
    }

    public static void square(long[] lArray, long[] lArray2) {
        long[] lArray3 = Nat576.createExt64();
        SecT571Field.implSquare(lArray, lArray3);
        SecT571Field.reduce(lArray3, lArray2);
    }

    public static void squareAddToExt(long[] lArray, long[] lArray2) {
        long[] lArray3 = Nat576.createExt64();
        SecT571Field.implSquare(lArray, lArray3);
        SecT571Field.addExt(lArray2, lArray3, lArray2);
    }

    public static void squareN(long[] lArray, int n, long[] lArray2) {
        long[] lArray3 = Nat576.createExt64();
        SecT571Field.implSquare(lArray, lArray3);
        SecT571Field.reduce(lArray3, lArray2);
        while (--n > 0) {
            SecT571Field.implSquare(lArray2, lArray3);
            SecT571Field.reduce(lArray3, lArray2);
        }
    }

    public static int trace(long[] lArray) {
        return (int)(lArray[0] ^ lArray[8] >>> 49 ^ lArray[8] >>> 57) & 1;
    }

    protected static void implMultiply(long[] lArray, long[] lArray2, long[] lArray3) {
        long[] lArray4 = new long[16];
        for (int i = 0; i < 9; ++i) {
            SecT571Field.implMulwAcc(lArray4, lArray[i], lArray2[i], lArray3, i << 1);
        }
        long l = lArray3[0];
        long l2 = lArray3[1];
        lArray3[1] = (l ^= lArray3[2]) ^ l2;
        lArray3[2] = (l ^= lArray3[4]) ^ (l2 ^= lArray3[3]);
        lArray3[3] = (l ^= lArray3[6]) ^ (l2 ^= lArray3[5]);
        lArray3[4] = (l ^= lArray3[8]) ^ (l2 ^= lArray3[7]);
        lArray3[5] = (l ^= lArray3[10]) ^ (l2 ^= lArray3[9]);
        lArray3[6] = (l ^= lArray3[12]) ^ (l2 ^= lArray3[11]);
        lArray3[7] = (l ^= lArray3[14]) ^ (l2 ^= lArray3[13]);
        lArray3[8] = (l ^= lArray3[16]) ^ (l2 ^= lArray3[15]);
        long l3 = l ^ (l2 ^= lArray3[17]);
        lArray3[9] = lArray3[0] ^ l3;
        lArray3[10] = lArray3[1] ^ l3;
        lArray3[11] = lArray3[2] ^ l3;
        lArray3[12] = lArray3[3] ^ l3;
        lArray3[13] = lArray3[4] ^ l3;
        lArray3[14] = lArray3[5] ^ l3;
        lArray3[15] = lArray3[6] ^ l3;
        lArray3[16] = lArray3[7] ^ l3;
        lArray3[17] = lArray3[8] ^ l3;
        SecT571Field.implMulwAcc(lArray4, lArray[0] ^ lArray[1], lArray2[0] ^ lArray2[1], lArray3, 1);
        SecT571Field.implMulwAcc(lArray4, lArray[0] ^ lArray[2], lArray2[0] ^ lArray2[2], lArray3, 2);
        SecT571Field.implMulwAcc(lArray4, lArray[0] ^ lArray[3], lArray2[0] ^ lArray2[3], lArray3, 3);
        SecT571Field.implMulwAcc(lArray4, lArray[1] ^ lArray[2], lArray2[1] ^ lArray2[2], lArray3, 3);
        SecT571Field.implMulwAcc(lArray4, lArray[0] ^ lArray[4], lArray2[0] ^ lArray2[4], lArray3, 4);
        SecT571Field.implMulwAcc(lArray4, lArray[1] ^ lArray[3], lArray2[1] ^ lArray2[3], lArray3, 4);
        SecT571Field.implMulwAcc(lArray4, lArray[0] ^ lArray[5], lArray2[0] ^ lArray2[5], lArray3, 5);
        SecT571Field.implMulwAcc(lArray4, lArray[1] ^ lArray[4], lArray2[1] ^ lArray2[4], lArray3, 5);
        SecT571Field.implMulwAcc(lArray4, lArray[2] ^ lArray[3], lArray2[2] ^ lArray2[3], lArray3, 5);
        SecT571Field.implMulwAcc(lArray4, lArray[0] ^ lArray[6], lArray2[0] ^ lArray2[6], lArray3, 6);
        SecT571Field.implMulwAcc(lArray4, lArray[1] ^ lArray[5], lArray2[1] ^ lArray2[5], lArray3, 6);
        SecT571Field.implMulwAcc(lArray4, lArray[2] ^ lArray[4], lArray2[2] ^ lArray2[4], lArray3, 6);
        SecT571Field.implMulwAcc(lArray4, lArray[0] ^ lArray[7], lArray2[0] ^ lArray2[7], lArray3, 7);
        SecT571Field.implMulwAcc(lArray4, lArray[1] ^ lArray[6], lArray2[1] ^ lArray2[6], lArray3, 7);
        SecT571Field.implMulwAcc(lArray4, lArray[2] ^ lArray[5], lArray2[2] ^ lArray2[5], lArray3, 7);
        SecT571Field.implMulwAcc(lArray4, lArray[3] ^ lArray[4], lArray2[3] ^ lArray2[4], lArray3, 7);
        SecT571Field.implMulwAcc(lArray4, lArray[0] ^ lArray[8], lArray2[0] ^ lArray2[8], lArray3, 8);
        SecT571Field.implMulwAcc(lArray4, lArray[1] ^ lArray[7], lArray2[1] ^ lArray2[7], lArray3, 8);
        SecT571Field.implMulwAcc(lArray4, lArray[2] ^ lArray[6], lArray2[2] ^ lArray2[6], lArray3, 8);
        SecT571Field.implMulwAcc(lArray4, lArray[3] ^ lArray[5], lArray2[3] ^ lArray2[5], lArray3, 8);
        SecT571Field.implMulwAcc(lArray4, lArray[1] ^ lArray[8], lArray2[1] ^ lArray2[8], lArray3, 9);
        SecT571Field.implMulwAcc(lArray4, lArray[2] ^ lArray[7], lArray2[2] ^ lArray2[7], lArray3, 9);
        SecT571Field.implMulwAcc(lArray4, lArray[3] ^ lArray[6], lArray2[3] ^ lArray2[6], lArray3, 9);
        SecT571Field.implMulwAcc(lArray4, lArray[4] ^ lArray[5], lArray2[4] ^ lArray2[5], lArray3, 9);
        SecT571Field.implMulwAcc(lArray4, lArray[2] ^ lArray[8], lArray2[2] ^ lArray2[8], lArray3, 10);
        SecT571Field.implMulwAcc(lArray4, lArray[3] ^ lArray[7], lArray2[3] ^ lArray2[7], lArray3, 10);
        SecT571Field.implMulwAcc(lArray4, lArray[4] ^ lArray[6], lArray2[4] ^ lArray2[6], lArray3, 10);
        SecT571Field.implMulwAcc(lArray4, lArray[3] ^ lArray[8], lArray2[3] ^ lArray2[8], lArray3, 11);
        SecT571Field.implMulwAcc(lArray4, lArray[4] ^ lArray[7], lArray2[4] ^ lArray2[7], lArray3, 11);
        SecT571Field.implMulwAcc(lArray4, lArray[5] ^ lArray[6], lArray2[5] ^ lArray2[6], lArray3, 11);
        SecT571Field.implMulwAcc(lArray4, lArray[4] ^ lArray[8], lArray2[4] ^ lArray2[8], lArray3, 12);
        SecT571Field.implMulwAcc(lArray4, lArray[5] ^ lArray[7], lArray2[5] ^ lArray2[7], lArray3, 12);
        SecT571Field.implMulwAcc(lArray4, lArray[5] ^ lArray[8], lArray2[5] ^ lArray2[8], lArray3, 13);
        SecT571Field.implMulwAcc(lArray4, lArray[6] ^ lArray[7], lArray2[6] ^ lArray2[7], lArray3, 13);
        SecT571Field.implMulwAcc(lArray4, lArray[6] ^ lArray[8], lArray2[6] ^ lArray2[8], lArray3, 14);
        SecT571Field.implMulwAcc(lArray4, lArray[7] ^ lArray[8], lArray2[7] ^ lArray2[8], lArray3, 15);
    }

    protected static void implMultiplyPrecomp(long[] lArray, long[] lArray2, long[] lArray3) {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        int n6 = 15;
        for (n5 = 56; n5 >= 0; n5 -= 8) {
            for (n4 = 1; n4 < 9; n4 += 2) {
                n3 = (int)(lArray[n4] >>> n5);
                n2 = n3 & n6;
                n = n3 >>> 4 & n6;
                SecT571Field.addBothTo(lArray2, 9 * n2, lArray2, 9 * (n + 16), lArray3, n4 - 1);
            }
            Nat.shiftUpBits64(16, lArray3, 0, 8, 0L);
        }
        for (n5 = 56; n5 >= 0; n5 -= 8) {
            for (n4 = 0; n4 < 9; n4 += 2) {
                n3 = (int)(lArray[n4] >>> n5);
                n2 = n3 & n6;
                n = n3 >>> 4 & n6;
                SecT571Field.addBothTo(lArray2, 9 * n2, lArray2, 9 * (n + 16), lArray3, n4);
            }
            if (n5 <= 0) continue;
            Nat.shiftUpBits64(18, lArray3, 0, 8, 0L);
        }
    }

    protected static void implMulwAcc(long[] lArray, long l, long l2, long[] lArray2, int n) {
        int n2;
        lArray[1] = l2;
        for (n2 = 2; n2 < 16; n2 += 2) {
            lArray[n2] = lArray[n2 >>> 1] << 1;
            lArray[n2 + 1] = lArray[n2] ^ l2;
        }
        n2 = (int)l;
        long l3 = 0L;
        long l4 = lArray[n2 & 0xF] ^ lArray[n2 >>> 4 & 0xF] << 4;
        int n3 = 56;
        do {
            n2 = (int)(l >>> n3);
            long l5 = lArray[n2 & 0xF] ^ lArray[n2 >>> 4 & 0xF] << 4;
            l4 ^= l5 << n3;
            l3 ^= l5 >>> -n3;
        } while ((n3 -= 8) > 0);
        for (int i = 0; i < 7; ++i) {
            l = (l & 0xFEFEFEFEFEFEFEFEL) >>> 1;
            l3 ^= l & l2 << i >> 63;
        }
        int n4 = n;
        lArray2[n4] = lArray2[n4] ^ l4;
        int n5 = n + 1;
        lArray2[n5] = lArray2[n5] ^ l3;
    }

    protected static void implSquare(long[] lArray, long[] lArray2) {
        Interleave.expand64To128(lArray, 0, 9, lArray2, 0);
    }
}

