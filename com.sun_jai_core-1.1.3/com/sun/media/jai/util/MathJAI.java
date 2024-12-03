/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

public class MathJAI {
    public static final int nextPositivePowerOf2(int n) {
        int power;
        if (n < 2) {
            return 2;
        }
        for (power = 1; power < n; power <<= 1) {
        }
        return power;
    }

    public static final boolean isPositivePowerOf2(int n) {
        return n == MathJAI.nextPositivePowerOf2(n);
    }
}

