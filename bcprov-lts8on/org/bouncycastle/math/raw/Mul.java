/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.raw;

public class Mul {
    public static void multiplyAcc(long[] x, int xOff, long[] y, int yOff, long[] z) {
        Mul.cmulAcc(x, xOff, y, yOff, z);
    }

    private static native void cmulAcc(long[] var0, int var1, long[] var2, int var3, long[] var4);
}

