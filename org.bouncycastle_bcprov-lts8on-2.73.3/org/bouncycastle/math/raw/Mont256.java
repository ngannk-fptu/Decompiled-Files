/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.raw;

import org.bouncycastle.math.raw.Nat256;

public abstract class Mont256 {
    private static final long M = 0xFFFFFFFFL;

    public static int inverse32(int x) {
        int z = x;
        z *= 2 - x * z;
        z *= 2 - x * z;
        z *= 2 - x * z;
        z *= 2 - x * z;
        return z;
    }

    public static void multAdd(int[] x, int[] y, int[] z, int[] m, int mInv32) {
        int z_8 = 0;
        long y_0 = (long)y[0] & 0xFFFFFFFFL;
        for (int i = 0; i < 8; ++i) {
            long z_0 = (long)z[0] & 0xFFFFFFFFL;
            long x_i = (long)x[i] & 0xFFFFFFFFL;
            long prod1 = x_i * y_0;
            long carry = (prod1 & 0xFFFFFFFFL) + z_0;
            long t = (long)((int)carry * mInv32) & 0xFFFFFFFFL;
            long prod2 = t * ((long)m[0] & 0xFFFFFFFFL);
            carry += prod2 & 0xFFFFFFFFL;
            carry = (carry >>> 32) + (prod1 >>> 32) + (prod2 >>> 32);
            for (int j = 1; j < 8; ++j) {
                prod1 = x_i * ((long)y[j] & 0xFFFFFFFFL);
                prod2 = t * ((long)m[j] & 0xFFFFFFFFL);
                z[j - 1] = (int)(carry += (prod1 & 0xFFFFFFFFL) + (prod2 & 0xFFFFFFFFL) + ((long)z[j] & 0xFFFFFFFFL));
                carry = (carry >>> 32) + (prod1 >>> 32) + (prod2 >>> 32);
            }
            z[7] = (int)(carry += (long)z_8 & 0xFFFFFFFFL);
            z_8 = (int)(carry >>> 32);
        }
        if (z_8 != 0 || Nat256.gte(z, m)) {
            Nat256.sub(z, m, z);
        }
    }

    public static void multAddXF(int[] x, int[] y, int[] z, int[] m) {
        int z_8 = 0;
        long y_0 = (long)y[0] & 0xFFFFFFFFL;
        for (int i = 0; i < 8; ++i) {
            long x_i = (long)x[i] & 0xFFFFFFFFL;
            long carry = x_i * y_0 + ((long)z[0] & 0xFFFFFFFFL);
            long t = carry & 0xFFFFFFFFL;
            carry = (carry >>> 32) + t;
            for (int j = 1; j < 8; ++j) {
                long prod1 = x_i * ((long)y[j] & 0xFFFFFFFFL);
                long prod2 = t * ((long)m[j] & 0xFFFFFFFFL);
                z[j - 1] = (int)(carry += (prod1 & 0xFFFFFFFFL) + (prod2 & 0xFFFFFFFFL) + ((long)z[j] & 0xFFFFFFFFL));
                carry = (carry >>> 32) + (prod1 >>> 32) + (prod2 >>> 32);
            }
            z[7] = (int)(carry += (long)z_8 & 0xFFFFFFFFL);
            z_8 = (int)(carry >>> 32);
        }
        if (z_8 != 0 || Nat256.gte(z, m)) {
            Nat256.sub(z, m, z);
        }
    }

    public static void reduce(int[] z, int[] m, int mInv32) {
        for (int i = 0; i < 8; ++i) {
            int z_0 = z[0];
            long t = (long)(z_0 * mInv32) & 0xFFFFFFFFL;
            long carry = t * ((long)m[0] & 0xFFFFFFFFL) + ((long)z_0 & 0xFFFFFFFFL);
            carry >>>= 32;
            for (int j = 1; j < 8; ++j) {
                z[j - 1] = (int)(carry += t * ((long)m[j] & 0xFFFFFFFFL) + ((long)z[j] & 0xFFFFFFFFL));
                carry >>>= 32;
            }
            z[7] = (int)carry;
        }
        if (Nat256.gte(z, m)) {
            Nat256.sub(z, m, z);
        }
    }

    public static void reduceXF(int[] z, int[] m) {
        for (int i = 0; i < 8; ++i) {
            long t;
            int z_0 = z[0];
            long carry = t = (long)z_0 & 0xFFFFFFFFL;
            for (int j = 1; j < 8; ++j) {
                z[j - 1] = (int)(carry += t * ((long)m[j] & 0xFFFFFFFFL) + ((long)z[j] & 0xFFFFFFFFL));
                carry >>>= 32;
            }
            z[7] = (int)carry;
        }
        if (Nat256.gte(z, m)) {
            Nat256.sub(z, m, z);
        }
    }
}

