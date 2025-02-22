/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.rfc8032;

import org.bouncycastle.math.ec.rfc8032.Codec;
import org.bouncycastle.math.ec.rfc8032.ScalarUtil;
import org.bouncycastle.math.ec.rfc8032.Wnaf;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;

abstract class Scalar25519 {
    static final int SIZE = 8;
    private static final long M08L = 255L;
    private static final long M28L = 0xFFFFFFFL;
    private static final long M32L = 0xFFFFFFFFL;
    private static final int TARGET_LENGTH = 254;
    private static final int[] L = new int[]{1559614445, 1477600026, -1560830762, 350157278, 0, 0, 0, 0x10000000};
    private static final int[] LSq = new int[]{-1424848535, -487721339, 580428573, 1745064566, -770181698, 1036971123, 461123738, -1582065343, 1268693629, -889041821, -731974758, 43769659, 0, 0, 0, 0x1000000};
    private static final int L0 = -50998291;
    private static final int L1 = 19280294;
    private static final int L2 = 127719000;
    private static final int L3 = -6428113;
    private static final int L4 = 5343;

    Scalar25519() {
    }

    static boolean checkVar(byte[] s, int[] n) {
        Scalar25519.decode(s, n);
        return !Nat256.gte(n, L);
    }

    static void decode(byte[] k, int[] n) {
        Codec.decode32(k, 0, n, 0, 8);
    }

    static void getOrderWnafVar(int width, byte[] ws) {
        Wnaf.getSignedVar(L, width, ws);
    }

    static void multiply128Var(int[] x, int[] y128, int[] z) {
        int[] tt = new int[12];
        Nat256.mul128(x, y128, tt);
        if (y128[3] < 0) {
            Nat256.addTo(L, 0, tt, 4, 0);
            Nat256.subFrom(x, 0, tt, 4, 0);
        }
        byte[] bytes = new byte[64];
        Codec.encode32(tt, 0, 12, bytes, 0);
        byte[] r = Scalar25519.reduce(bytes);
        Scalar25519.decode(r, z);
    }

    static byte[] reduce(byte[] n) {
        long x00 = (long)Codec.decode32(n, 0) & 0xFFFFFFFFL;
        long x01 = (long)(Codec.decode24(n, 4) << 4) & 0xFFFFFFFFL;
        long x02 = (long)Codec.decode32(n, 7) & 0xFFFFFFFFL;
        long x03 = (long)(Codec.decode24(n, 11) << 4) & 0xFFFFFFFFL;
        long x04 = (long)Codec.decode32(n, 14) & 0xFFFFFFFFL;
        long x05 = (long)(Codec.decode24(n, 18) << 4) & 0xFFFFFFFFL;
        long x06 = (long)Codec.decode32(n, 21) & 0xFFFFFFFFL;
        long x07 = (long)(Codec.decode24(n, 25) << 4) & 0xFFFFFFFFL;
        long x08 = (long)Codec.decode32(n, 28) & 0xFFFFFFFFL;
        long x09 = (long)(Codec.decode24(n, 32) << 4) & 0xFFFFFFFFL;
        long x10 = (long)Codec.decode32(n, 35) & 0xFFFFFFFFL;
        long x11 = (long)(Codec.decode24(n, 39) << 4) & 0xFFFFFFFFL;
        long x12 = (long)Codec.decode32(n, 42) & 0xFFFFFFFFL;
        long x13 = (long)(Codec.decode24(n, 46) << 4) & 0xFFFFFFFFL;
        long x14 = (long)Codec.decode32(n, 49) & 0xFFFFFFFFL;
        long x15 = (long)(Codec.decode24(n, 53) << 4) & 0xFFFFFFFFL;
        long x16 = (long)Codec.decode32(n, 56) & 0xFFFFFFFFL;
        long x17 = (long)(Codec.decode24(n, 60) << 4) & 0xFFFFFFFFL;
        long x18 = (long)n[63] & 0xFFL;
        x09 -= x18 * -50998291L;
        x10 -= x18 * 19280294L;
        x11 -= x18 * 127719000L;
        x12 -= x18 * -6428113L;
        x13 -= x18 * 5343L;
        x17 += x16 >> 28;
        x16 &= 0xFFFFFFFL;
        x08 -= x17 * -50998291L;
        x09 -= x17 * 19280294L;
        x10 -= x17 * 127719000L;
        x11 -= x17 * -6428113L;
        x12 -= x17 * 5343L;
        x07 -= x16 * -50998291L;
        x08 -= x16 * 19280294L;
        x09 -= x16 * 127719000L;
        x10 -= x16 * -6428113L;
        x11 -= x16 * 5343L;
        x15 += x14 >> 28;
        x14 &= 0xFFFFFFFL;
        x06 -= x15 * -50998291L;
        x07 -= x15 * 19280294L;
        x08 -= x15 * 127719000L;
        x09 -= x15 * -6428113L;
        x10 -= x15 * 5343L;
        x05 -= x14 * -50998291L;
        x06 -= x14 * 19280294L;
        x07 -= x14 * 127719000L;
        x08 -= x14 * -6428113L;
        x09 -= x14 * 5343L;
        x13 += x12 >> 28;
        x12 &= 0xFFFFFFFL;
        x04 -= x13 * -50998291L;
        x05 -= x13 * 19280294L;
        x06 -= x13 * 127719000L;
        x07 -= x13 * -6428113L;
        x08 -= x13 * 5343L;
        x12 += x11 >> 28;
        x11 &= 0xFFFFFFFL;
        x03 -= x12 * -50998291L;
        x04 -= x12 * 19280294L;
        x05 -= x12 * 127719000L;
        x06 -= x12 * -6428113L;
        x07 -= x12 * 5343L;
        x11 += x10 >> 28;
        x10 &= 0xFFFFFFFL;
        x02 -= x11 * -50998291L;
        x03 -= x11 * 19280294L;
        x04 -= x11 * 127719000L;
        x05 -= x11 * -6428113L;
        x06 -= x11 * 5343L;
        x10 += x09 >> 28;
        x09 &= 0xFFFFFFFL;
        x01 -= x10 * -50998291L;
        x02 -= x10 * 19280294L;
        x03 -= x10 * 127719000L;
        x04 -= x10 * -6428113L;
        x05 -= x10 * 5343L;
        x08 += x07 >> 28;
        x07 &= 0xFFFFFFFL;
        x09 += x08 >> 28;
        long t = (x08 &= 0xFFFFFFFL) >>> 27;
        x01 -= x09 * 19280294L;
        x02 -= x09 * 127719000L;
        x03 -= x09 * -6428113L;
        x04 -= x09 * 5343L;
        x00 &= 0xFFFFFFFL;
        x01 &= 0xFFFFFFFL;
        x02 &= 0xFFFFFFFL;
        x03 &= 0xFFFFFFFL;
        x04 &= 0xFFFFFFFL;
        x05 &= 0xFFFFFFFL;
        x06 &= 0xFFFFFFFL;
        x07 &= 0xFFFFFFFL;
        x09 = (x08 += (x07 += (x06 += (x05 += (x04 += (x03 += (x02 += (x01 += (x00 -= (x09 += t) * -50998291L) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28;
        x08 &= 0xFFFFFFFL;
        x01 += x09 & 0x12631A6L;
        x02 += x09 & 0x79CD658L;
        x03 += x09 & 0xFFFFFFFFFF9DEA2FL;
        x04 += x09 & 0x14DFL;
        x00 &= 0xFFFFFFFL;
        x01 &= 0xFFFFFFFL;
        x02 &= 0xFFFFFFFL;
        x03 &= 0xFFFFFFFL;
        x04 &= 0xFFFFFFFL;
        x05 &= 0xFFFFFFFL;
        x06 &= 0xFFFFFFFL;
        x08 += (x07 += (x06 += (x05 += (x04 += (x03 += (x02 += (x01 += (x00 += (x09 -= t) & 0xFFFFFFFFFCF5D3EDL) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28) >> 28;
        byte[] r = new byte[64];
        Codec.encode56(x00 | x01 << 28, r, 0);
        Codec.encode56(x02 | x03 << 28, r, 7);
        Codec.encode56(x04 | x05 << 28, r, 14);
        Codec.encode56(x06 | (x07 &= 0xFFFFFFFL) << 28, r, 21);
        Codec.encode32((int)x08, r, 28);
        return r;
    }

    static void reduceBasisVar(int[] k, int[] z0, int[] z1) {
        int[] Nu = new int[16];
        System.arraycopy(LSq, 0, Nu, 0, 16);
        int[] Nv = new int[16];
        Nat256.square(k, Nv);
        Nv[0] = Nv[0] + 1;
        int[] p = new int[16];
        Nat256.mul(L, k, p);
        int[] u0 = new int[4];
        System.arraycopy(L, 0, u0, 0, 4);
        int[] u1 = new int[4];
        int[] v0 = new int[4];
        System.arraycopy(k, 0, v0, 0, 4);
        int[] v1 = new int[4];
        v1[0] = 1;
        int last = 15;
        int len_Nv = ScalarUtil.getBitLengthPositive(last, Nv);
        while (len_Nv > 254) {
            int len_p = ScalarUtil.getBitLength(last, p);
            int s = len_p - len_Nv;
            s &= ~(s >> 31);
            if (p[last] < 0) {
                ScalarUtil.addShifted_NP(last, s, Nu, Nv, p);
                ScalarUtil.addShifted_UV(3, s, u0, u1, v0, v1);
            } else {
                ScalarUtil.subShifted_NP(last, s, Nu, Nv, p);
                ScalarUtil.subShifted_UV(3, s, u0, u1, v0, v1);
            }
            if (!ScalarUtil.lessThan(last, Nu, Nv)) continue;
            int[] t0 = u0;
            u0 = v0;
            v0 = t0;
            int[] t1 = u1;
            u1 = v1;
            v1 = t1;
            int[] tN = Nu;
            Nu = Nv;
            Nv = tN;
            last = len_Nv >>> 5;
            len_Nv = ScalarUtil.getBitLengthPositive(last, Nv);
        }
        System.arraycopy(v0, 0, z0, 0, 4);
        System.arraycopy(v1, 0, z1, 0, 4);
    }

    static void toSignedDigits(int bits, int[] x, int[] z) {
        Nat.cadd(8, ~x[0] & 1, x, L, z);
        Nat.shiftDownBit(8, z, 1);
    }
}

