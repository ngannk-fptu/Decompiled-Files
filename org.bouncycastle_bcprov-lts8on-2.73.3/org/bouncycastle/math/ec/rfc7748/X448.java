/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.rfc7748;

import java.security.SecureRandom;
import org.bouncycastle.math.ec.rfc7748.X448Field;
import org.bouncycastle.math.ec.rfc8032.Ed448;
import org.bouncycastle.util.Arrays;

public abstract class X448 {
    public static final int POINT_SIZE = 56;
    public static final int SCALAR_SIZE = 56;
    private static final int C_A = 156326;
    private static final int C_A24 = 39082;

    public static boolean calculateAgreement(byte[] k, int kOff, byte[] u, int uOff, byte[] r, int rOff) {
        X448.scalarMult(k, kOff, u, uOff, r, rOff);
        return !Arrays.areAllZeroes(r, rOff, 56);
    }

    private static int decode32(byte[] bs, int off) {
        int n = bs[off] & 0xFF;
        n |= (bs[++off] & 0xFF) << 8;
        n |= (bs[++off] & 0xFF) << 16;
        return n |= bs[++off] << 24;
    }

    private static void decodeScalar(byte[] k, int kOff, int[] n) {
        for (int i = 0; i < 14; ++i) {
            n[i] = X448.decode32(k, kOff + i * 4);
        }
        n[0] = n[0] & 0xFFFFFFFC;
        n[13] = n[13] | Integer.MIN_VALUE;
    }

    public static void generatePrivateKey(SecureRandom random, byte[] k) {
        if (k.length != 56) {
            throw new IllegalArgumentException("k");
        }
        random.nextBytes(k);
        k[0] = (byte)(k[0] & 0xFC);
        k[55] = (byte)(k[55] | 0x80);
    }

    public static void generatePublicKey(byte[] k, int kOff, byte[] r, int rOff) {
        X448.scalarMultBase(k, kOff, r, rOff);
    }

    private static void pointDouble(int[] x, int[] z) {
        int[] a = F.create();
        int[] b = F.create();
        F.add(x, z, a);
        F.sub(x, z, b);
        F.sqr(a, a);
        F.sqr(b, b);
        F.mul(a, b, x);
        F.sub(a, b, a);
        F.mul(a, 39082, z);
        F.add(z, b, z);
        F.mul(z, a, z);
    }

    public static void precompute() {
        Ed448.precompute();
    }

    public static void scalarMult(byte[] k, int kOff, byte[] u, int uOff, byte[] r, int rOff) {
        int[] n = new int[14];
        X448.decodeScalar(k, kOff, n);
        int[] x1 = F.create();
        F.decode(u, uOff, x1);
        int[] x2 = F.create();
        F.copy(x1, 0, x2, 0);
        int[] z2 = F.create();
        z2[0] = 1;
        int[] x3 = F.create();
        x3[0] = 1;
        int[] z3 = F.create();
        int[] t1 = F.create();
        int[] t2 = F.create();
        int bit = 447;
        int swap = 1;
        do {
            F.add(x3, z3, t1);
            F.sub(x3, z3, x3);
            F.add(x2, z2, z3);
            F.sub(x2, z2, x2);
            F.mul(t1, x2, t1);
            F.mul(x3, z3, x3);
            F.sqr(z3, z3);
            F.sqr(x2, x2);
            F.sub(z3, x2, t2);
            F.mul(t2, 39082, z2);
            F.add(z2, x2, z2);
            F.mul(z2, t2, z2);
            F.mul(x2, z3, x2);
            F.sub(t1, x3, z3);
            F.add(t1, x3, x3);
            F.sqr(x3, x3);
            F.sqr(z3, z3);
            F.mul(z3, x1, z3);
            int word = --bit >>> 5;
            int shift = bit & 0x1F;
            int kt = n[word] >>> shift & 1;
            F.cswap(swap ^= kt, x2, x3);
            F.cswap(swap, z2, z3);
            swap = kt;
        } while (bit >= 2);
        for (int i = 0; i < 2; ++i) {
            X448.pointDouble(x2, z2);
        }
        F.inv(z2, z2);
        F.mul(x2, z2, x2);
        F.normalize(x2);
        F.encode(x2, r, rOff);
    }

    public static void scalarMultBase(byte[] k, int kOff, byte[] r, int rOff) {
        int[] x = F.create();
        int[] y = F.create();
        Ed448.scalarMultBaseXY(Friend.INSTANCE, k, kOff, x, y);
        F.inv(x, x);
        F.mul(x, y, x);
        F.sqr(x, x);
        F.normalize(x);
        F.encode(x, r, rOff);
    }

    private static class F
    extends X448Field {
        private F() {
        }
    }

    public static class Friend {
        private static final Friend INSTANCE = new Friend();

        private Friend() {
        }
    }
}

