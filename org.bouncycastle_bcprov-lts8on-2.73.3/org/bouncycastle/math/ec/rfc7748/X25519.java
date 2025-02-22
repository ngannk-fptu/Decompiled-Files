/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.rfc7748;

import java.security.SecureRandom;
import org.bouncycastle.math.ec.rfc7748.X25519Field;
import org.bouncycastle.math.ec.rfc8032.Ed25519;
import org.bouncycastle.util.Arrays;

public abstract class X25519 {
    public static final int POINT_SIZE = 32;
    public static final int SCALAR_SIZE = 32;
    private static final int C_A = 486662;
    private static final int C_A24 = 121666;

    public static boolean calculateAgreement(byte[] k, int kOff, byte[] u, int uOff, byte[] r, int rOff) {
        X25519.scalarMult(k, kOff, u, uOff, r, rOff);
        return !Arrays.areAllZeroes(r, rOff, 32);
    }

    private static int decode32(byte[] bs, int off) {
        int n = bs[off] & 0xFF;
        n |= (bs[++off] & 0xFF) << 8;
        n |= (bs[++off] & 0xFF) << 16;
        return n |= bs[++off] << 24;
    }

    private static void decodeScalar(byte[] k, int kOff, int[] n) {
        for (int i = 0; i < 8; ++i) {
            n[i] = X25519.decode32(k, kOff + i * 4);
        }
        n[0] = n[0] & 0xFFFFFFF8;
        n[7] = n[7] & Integer.MAX_VALUE;
        n[7] = n[7] | 0x40000000;
    }

    public static void generatePrivateKey(SecureRandom random, byte[] k) {
        if (k.length != 32) {
            throw new IllegalArgumentException("k");
        }
        random.nextBytes(k);
        k[0] = (byte)(k[0] & 0xF8);
        k[31] = (byte)(k[31] & 0x7F);
        k[31] = (byte)(k[31] | 0x40);
    }

    public static void generatePublicKey(byte[] k, int kOff, byte[] r, int rOff) {
        X25519.scalarMultBase(k, kOff, r, rOff);
    }

    private static void pointDouble(int[] x, int[] z) {
        int[] a = F.create();
        int[] b = F.create();
        F.apm(x, z, a, b);
        F.sqr(a, a);
        F.sqr(b, b);
        F.mul(a, b, x);
        F.sub(a, b, a);
        F.mul(a, 121666, z);
        F.add(z, b, z);
        F.mul(z, a, z);
    }

    public static void precompute() {
        Ed25519.precompute();
    }

    public static void scalarMult(byte[] k, int kOff, byte[] u, int uOff, byte[] r, int rOff) {
        int[] n = new int[8];
        X25519.decodeScalar(k, kOff, n);
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
        int bit = 254;
        int swap = 1;
        do {
            F.apm(x3, z3, t1, x3);
            F.apm(x2, z2, z3, x2);
            F.mul(t1, x2, t1);
            F.mul(x3, z3, x3);
            F.sqr(z3, z3);
            F.sqr(x2, x2);
            F.sub(z3, x2, t2);
            F.mul(t2, 121666, z2);
            F.add(z2, x2, z2);
            F.mul(z2, t2, z2);
            F.mul(x2, z3, x2);
            F.apm(t1, x3, x3, z3);
            F.sqr(x3, x3);
            F.sqr(z3, z3);
            F.mul(z3, x1, z3);
            int word = --bit >>> 5;
            int shift = bit & 0x1F;
            int kt = n[word] >>> shift & 1;
            F.cswap(swap ^= kt, x2, x3);
            F.cswap(swap, z2, z3);
            swap = kt;
        } while (bit >= 3);
        for (int i = 0; i < 3; ++i) {
            X25519.pointDouble(x2, z2);
        }
        F.inv(z2, z2);
        F.mul(x2, z2, x2);
        F.normalize(x2);
        F.encode(x2, r, rOff);
    }

    public static void scalarMultBase(byte[] k, int kOff, byte[] r, int rOff) {
        int[] y = F.create();
        int[] z = F.create();
        Ed25519.scalarMultBaseYZ(Friend.INSTANCE, k, kOff, y, z);
        F.apm(z, y, y, z);
        F.inv(z, z);
        F.mul(y, z, y);
        F.normalize(y);
        F.encode(y, r, rOff);
    }

    private static class F
    extends X25519Field {
        private F() {
        }
    }

    public static class Friend {
        private static final Friend INSTANCE = new Friend();

        private Friend() {
        }
    }
}

