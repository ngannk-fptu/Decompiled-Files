/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

public class IntElementaryModMath {
    private int modulus;
    private double inverseModulus;

    public final int modMultiply(int a, int b) {
        int r1 = a * b - (int)(this.inverseModulus * (double)a * (double)b) * this.modulus;
        int r2 = r1 - this.modulus;
        return r2 < 0 ? r1 : r2;
    }

    public final int modAdd(int a, int b) {
        int r1 = a + b;
        int r2 = r1 - this.modulus;
        return r2 < 0 ? r1 : r2;
    }

    public final int modSubtract(int a, int b) {
        int r1 = a - b;
        int r2 = r1 + this.modulus;
        return r1 < 0 ? r2 : r1;
    }

    public final int getModulus() {
        return this.modulus;
    }

    public final void setModulus(int modulus) {
        this.inverseModulus = 1.0 / ((double)modulus + 0.5);
        this.modulus = modulus;
    }
}

