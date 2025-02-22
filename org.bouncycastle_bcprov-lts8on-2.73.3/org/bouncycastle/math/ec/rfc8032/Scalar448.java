/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.rfc8032;

import org.bouncycastle.math.ec.rfc8032.Codec;
import org.bouncycastle.math.ec.rfc8032.ScalarUtil;
import org.bouncycastle.math.ec.rfc8032.Wnaf;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat448;

abstract class Scalar448 {
    static final int SIZE = 14;
    private static final int SCALAR_BYTES = 57;
    private static final long M26L = 0x3FFFFFFL;
    private static final long M28L = 0xFFFFFFFL;
    private static final long M32L = 0xFFFFFFFFL;
    private static final int TARGET_LENGTH = 447;
    private static final int[] L = new int[]{-1420278541, 595116690, -1916432555, 560775794, -1361693040, -1001465015, 2093622249, -1, -1, -1, -1, -1, -1, 0x3FFFFFFF};
    private static final int[] LSq = new int[]{463601321, -1045562440, 1239460018, -1189350089, -412821483, 1160071467, -1564970643, 1256291574, -1170454588, -240530412, 2118977290, -1845154869, -1618855054, -1019204973, 1437344377, -1849925303, 1189267370, 280387897, -680846520, -500732508, -1100672524, -1, -1, -1, -1, -1, -1, 0xFFFFFFF};
    private static final int L_0 = 78101261;
    private static final int L_1 = 141809365;
    private static final int L_2 = 175155932;
    private static final int L_3 = 64542499;
    private static final int L_4 = 158326419;
    private static final int L_5 = 191173276;
    private static final int L_6 = 104575268;
    private static final int L_7 = 137584065;
    private static final int L4_0 = 43969588;
    private static final int L4_1 = 30366549;
    private static final int L4_2 = 163752818;
    private static final int L4_3 = 258169998;
    private static final int L4_4 = 96434764;
    private static final int L4_5 = 227822194;
    private static final int L4_6 = 149865618;
    private static final int L4_7 = 550336261;

    Scalar448() {
    }

    static boolean checkVar(byte[] s, int[] n) {
        if (s[56] != 0) {
            return false;
        }
        Scalar448.decode(s, n);
        return !Nat.gte(14, n, L);
    }

    static void decode(byte[] k, int[] n) {
        Codec.decode32(k, 0, n, 0, 14);
    }

    static void getOrderWnafVar(int width, byte[] ws) {
        Wnaf.getSignedVar(L, width, ws);
    }

    static void multiply225Var(int[] x, int[] y225, int[] z) {
        int[] tt = new int[22];
        Nat.mul(y225, 0, 8, x, 0, 14, tt, 0);
        if (y225[7] < 0) {
            Nat.addTo(14, L, 0, tt, 8);
            Nat.subFrom(14, x, 0, tt, 8);
        }
        byte[] bytes = new byte[114];
        Codec.encode32(tt, 0, 22, bytes, 0);
        byte[] r = Scalar448.reduce(bytes);
        Scalar448.decode(r, z);
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
        long x18 = (long)Codec.decode32(n, 63) & 0xFFFFFFFFL;
        long x19 = (long)(Codec.decode24(n, 67) << 4) & 0xFFFFFFFFL;
        long x20 = (long)Codec.decode32(n, 70) & 0xFFFFFFFFL;
        long x21 = (long)(Codec.decode24(n, 74) << 4) & 0xFFFFFFFFL;
        long x22 = (long)Codec.decode32(n, 77) & 0xFFFFFFFFL;
        long x23 = (long)(Codec.decode24(n, 81) << 4) & 0xFFFFFFFFL;
        long x24 = (long)Codec.decode32(n, 84) & 0xFFFFFFFFL;
        long x25 = (long)(Codec.decode24(n, 88) << 4) & 0xFFFFFFFFL;
        long x26 = (long)Codec.decode32(n, 91) & 0xFFFFFFFFL;
        long x27 = (long)(Codec.decode24(n, 95) << 4) & 0xFFFFFFFFL;
        long x28 = (long)Codec.decode32(n, 98) & 0xFFFFFFFFL;
        long x29 = (long)(Codec.decode24(n, 102) << 4) & 0xFFFFFFFFL;
        long x30 = (long)Codec.decode32(n, 105) & 0xFFFFFFFFL;
        long x31 = (long)(Codec.decode24(n, 109) << 4) & 0xFFFFFFFFL;
        long x32 = (long)Codec.decode16(n, 112) & 0xFFFFFFFFL;
        x16 += x32 * 43969588L;
        x17 += x32 * 30366549L;
        x18 += x32 * 163752818L;
        x19 += x32 * 258169998L;
        x20 += x32 * 96434764L;
        x21 += x32 * 227822194L;
        x22 += x32 * 149865618L;
        x23 += x32 * 550336261L;
        x31 += x30 >>> 28;
        x30 &= 0xFFFFFFFL;
        x15 += x31 * 43969588L;
        x16 += x31 * 30366549L;
        x17 += x31 * 163752818L;
        x18 += x31 * 258169998L;
        x19 += x31 * 96434764L;
        x20 += x31 * 227822194L;
        x21 += x31 * 149865618L;
        x22 += x31 * 550336261L;
        x14 += x30 * 43969588L;
        x15 += x30 * 30366549L;
        x16 += x30 * 163752818L;
        x17 += x30 * 258169998L;
        x18 += x30 * 96434764L;
        x19 += x30 * 227822194L;
        x20 += x30 * 149865618L;
        x21 += x30 * 550336261L;
        x29 += x28 >>> 28;
        x28 &= 0xFFFFFFFL;
        x13 += x29 * 43969588L;
        x14 += x29 * 30366549L;
        x15 += x29 * 163752818L;
        x16 += x29 * 258169998L;
        x17 += x29 * 96434764L;
        x18 += x29 * 227822194L;
        x19 += x29 * 149865618L;
        x20 += x29 * 550336261L;
        x12 += x28 * 43969588L;
        x13 += x28 * 30366549L;
        x14 += x28 * 163752818L;
        x15 += x28 * 258169998L;
        x16 += x28 * 96434764L;
        x17 += x28 * 227822194L;
        x18 += x28 * 149865618L;
        x19 += x28 * 550336261L;
        x27 += x26 >>> 28;
        x26 &= 0xFFFFFFFL;
        x11 += x27 * 43969588L;
        x12 += x27 * 30366549L;
        x13 += x27 * 163752818L;
        x14 += x27 * 258169998L;
        x15 += x27 * 96434764L;
        x16 += x27 * 227822194L;
        x17 += x27 * 149865618L;
        x18 += x27 * 550336261L;
        x10 += x26 * 43969588L;
        x11 += x26 * 30366549L;
        x12 += x26 * 163752818L;
        x13 += x26 * 258169998L;
        x14 += x26 * 96434764L;
        x15 += x26 * 227822194L;
        x16 += x26 * 149865618L;
        x17 += x26 * 550336261L;
        x25 += x24 >>> 28;
        x24 &= 0xFFFFFFFL;
        x09 += x25 * 43969588L;
        x10 += x25 * 30366549L;
        x11 += x25 * 163752818L;
        x12 += x25 * 258169998L;
        x13 += x25 * 96434764L;
        x14 += x25 * 227822194L;
        x15 += x25 * 149865618L;
        x16 += x25 * 550336261L;
        x21 += x20 >>> 28;
        x20 &= 0xFFFFFFFL;
        x22 += x21 >>> 28;
        x21 &= 0xFFFFFFFL;
        x23 += x22 >>> 28;
        x22 &= 0xFFFFFFFL;
        x24 += x23 >>> 28;
        x23 &= 0xFFFFFFFL;
        x08 += x24 * 43969588L;
        x09 += x24 * 30366549L;
        x10 += x24 * 163752818L;
        x11 += x24 * 258169998L;
        x12 += x24 * 96434764L;
        x13 += x24 * 227822194L;
        x14 += x24 * 149865618L;
        x15 += x24 * 550336261L;
        x07 += x23 * 43969588L;
        x08 += x23 * 30366549L;
        x09 += x23 * 163752818L;
        x10 += x23 * 258169998L;
        x11 += x23 * 96434764L;
        x12 += x23 * 227822194L;
        x13 += x23 * 149865618L;
        x14 += x23 * 550336261L;
        x06 += x22 * 43969588L;
        x07 += x22 * 30366549L;
        x08 += x22 * 163752818L;
        x09 += x22 * 258169998L;
        x10 += x22 * 96434764L;
        x11 += x22 * 227822194L;
        x12 += x22 * 149865618L;
        x13 += x22 * 550336261L;
        x18 += x17 >>> 28;
        x17 &= 0xFFFFFFFL;
        x19 += x18 >>> 28;
        x18 &= 0xFFFFFFFL;
        x20 += x19 >>> 28;
        x19 &= 0xFFFFFFFL;
        x21 += x20 >>> 28;
        x20 &= 0xFFFFFFFL;
        x05 += x21 * 43969588L;
        x06 += x21 * 30366549L;
        x07 += x21 * 163752818L;
        x08 += x21 * 258169998L;
        x09 += x21 * 96434764L;
        x10 += x21 * 227822194L;
        x11 += x21 * 149865618L;
        x12 += x21 * 550336261L;
        x04 += x20 * 43969588L;
        x05 += x20 * 30366549L;
        x06 += x20 * 163752818L;
        x07 += x20 * 258169998L;
        x08 += x20 * 96434764L;
        x09 += x20 * 227822194L;
        x10 += x20 * 149865618L;
        x11 += x20 * 550336261L;
        x03 += x19 * 43969588L;
        x04 += x19 * 30366549L;
        x05 += x19 * 163752818L;
        x06 += x19 * 258169998L;
        x07 += x19 * 96434764L;
        x08 += x19 * 227822194L;
        x09 += x19 * 149865618L;
        x10 += x19 * 550336261L;
        x15 += x14 >>> 28;
        x14 &= 0xFFFFFFFL;
        x16 += x15 >>> 28;
        x15 &= 0xFFFFFFFL;
        x17 += x16 >>> 28;
        x16 &= 0xFFFFFFFL;
        x18 += x17 >>> 28;
        x17 &= 0xFFFFFFFL;
        x02 += x18 * 43969588L;
        x03 += x18 * 30366549L;
        x04 += x18 * 163752818L;
        x05 += x18 * 258169998L;
        x06 += x18 * 96434764L;
        x07 += x18 * 227822194L;
        x08 += x18 * 149865618L;
        x09 += x18 * 550336261L;
        x01 += x17 * 43969588L;
        x02 += x17 * 30366549L;
        x03 += x17 * 163752818L;
        x04 += x17 * 258169998L;
        x05 += x17 * 96434764L;
        x06 += x17 * 227822194L;
        x07 += x17 * 149865618L;
        x08 += x17 * 550336261L;
        x16 *= 4L;
        x16 += x15 >>> 26;
        x15 &= 0x3FFFFFFL;
        x00 += ++x16 * 78101261L;
        x01 += x16 * 141809365L;
        x02 += x16 * 175155932L;
        x03 += x16 * 64542499L;
        x04 += x16 * 158326419L;
        x05 += x16 * 191173276L;
        x06 += x16 * 104575268L;
        x07 += x16 * 137584065L;
        x01 += x00 >>> 28;
        x00 &= 0xFFFFFFFL;
        x02 += x01 >>> 28;
        x01 &= 0xFFFFFFFL;
        x03 += x02 >>> 28;
        x02 &= 0xFFFFFFFL;
        x04 += x03 >>> 28;
        x03 &= 0xFFFFFFFL;
        x05 += x04 >>> 28;
        x04 &= 0xFFFFFFFL;
        x06 += x05 >>> 28;
        x05 &= 0xFFFFFFFL;
        x07 += x06 >>> 28;
        x06 &= 0xFFFFFFFL;
        x08 += x07 >>> 28;
        x07 &= 0xFFFFFFFL;
        x09 += x08 >>> 28;
        x08 &= 0xFFFFFFFL;
        x10 += x09 >>> 28;
        x09 &= 0xFFFFFFFL;
        x11 += x10 >>> 28;
        x10 &= 0xFFFFFFFL;
        x12 += x11 >>> 28;
        x11 &= 0xFFFFFFFL;
        x13 += x12 >>> 28;
        x12 &= 0xFFFFFFFL;
        x14 += x13 >>> 28;
        x13 &= 0xFFFFFFFL;
        x15 += x14 >>> 28;
        x14 &= 0xFFFFFFFL;
        x16 = x15 >>> 26;
        x15 &= 0x3FFFFFFL;
        x00 -= --x16 & 0x4A7BB0DL;
        x01 -= x16 & 0x873D6D5L;
        x02 -= x16 & 0xA70AADCL;
        x03 -= x16 & 0x3D8D723L;
        x04 -= x16 & 0x96FDE93L;
        x05 -= x16 & 0xB65129CL;
        x06 -= x16 & 0x63BB124L;
        x07 -= x16 & 0x8335DC1L;
        x01 += x00 >> 28;
        x00 &= 0xFFFFFFFL;
        x02 += x01 >> 28;
        x01 &= 0xFFFFFFFL;
        x03 += x02 >> 28;
        x02 &= 0xFFFFFFFL;
        x04 += x03 >> 28;
        x03 &= 0xFFFFFFFL;
        x05 += x04 >> 28;
        x04 &= 0xFFFFFFFL;
        x06 += x05 >> 28;
        x05 &= 0xFFFFFFFL;
        x07 += x06 >> 28;
        x06 &= 0xFFFFFFFL;
        x08 += x07 >> 28;
        x07 &= 0xFFFFFFFL;
        x09 += x08 >> 28;
        x08 &= 0xFFFFFFFL;
        x10 += x09 >> 28;
        x09 &= 0xFFFFFFFL;
        x11 += x10 >> 28;
        x10 &= 0xFFFFFFFL;
        x12 += x11 >> 28;
        x11 &= 0xFFFFFFFL;
        x13 += x12 >> 28;
        x12 &= 0xFFFFFFFL;
        x14 += x13 >> 28;
        x13 &= 0xFFFFFFFL;
        x15 += x14 >> 28;
        x14 &= 0xFFFFFFFL;
        byte[] r = new byte[57];
        Codec.encode56(x00 | x01 << 28, r, 0);
        Codec.encode56(x02 | x03 << 28, r, 7);
        Codec.encode56(x04 | x05 << 28, r, 14);
        Codec.encode56(x06 | x07 << 28, r, 21);
        Codec.encode56(x08 | x09 << 28, r, 28);
        Codec.encode56(x10 | x11 << 28, r, 35);
        Codec.encode56(x12 | x13 << 28, r, 42);
        Codec.encode56(x14 | x15 << 28, r, 49);
        return r;
    }

    static void reduceBasisVar(int[] k, int[] z0, int[] z1) {
        int[] Nu = new int[28];
        System.arraycopy(LSq, 0, Nu, 0, 28);
        int[] Nv = new int[28];
        Nat448.square(k, Nv);
        Nv[0] = Nv[0] + 1;
        int[] p = new int[28];
        Nat448.mul(L, k, p);
        int[] u0 = new int[8];
        System.arraycopy(L, 0, u0, 0, 8);
        int[] u1 = new int[8];
        int[] v0 = new int[8];
        System.arraycopy(k, 0, v0, 0, 8);
        int[] v1 = new int[8];
        v1[0] = 1;
        int last = 27;
        int len_Nv = ScalarUtil.getBitLengthPositive(last, Nv);
        while (len_Nv > 447) {
            int len_p = ScalarUtil.getBitLength(last, p);
            int s = len_p - len_Nv;
            s &= ~(s >> 31);
            if (p[last] < 0) {
                ScalarUtil.addShifted_NP(last, s, Nu, Nv, p);
                ScalarUtil.addShifted_UV(7, s, u0, u1, v0, v1);
            } else {
                ScalarUtil.subShifted_NP(last, s, Nu, Nv, p);
                ScalarUtil.subShifted_UV(7, s, u0, u1, v0, v1);
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
        System.arraycopy(v0, 0, z0, 0, 8);
        System.arraycopy(v1, 0, z1, 0, 8);
    }

    static void toSignedDigits(int bits, int[] x, int[] z) {
        z[14] = (1 << bits - 448) + Nat.cadd(14, ~x[0] & 1, x, L, z);
        Nat.shiftDownBit(15, z, 0);
    }
}

