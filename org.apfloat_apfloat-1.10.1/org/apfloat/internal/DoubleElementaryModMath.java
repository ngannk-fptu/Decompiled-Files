/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

public class DoubleElementaryModMath {
    private long longModulus;
    private double modulus;
    private double inverseModulus;

    public final double modMultiply(double a, double b) {
        double r = (long)a * (long)b - this.longModulus * (long)(a * b * this.inverseModulus);
        return r >= this.modulus ? r - this.modulus : r;
    }

    public final double modAdd(double a, double b) {
        double r = a + b;
        return r >= this.modulus ? r - this.modulus : r;
    }

    public final double modSubtract(double a, double b) {
        double r = a - b;
        return r < 0.0 ? r + this.modulus : r;
    }

    public final double getModulus() {
        return this.modulus;
    }

    public final void setModulus(double modulus) {
        this.inverseModulus = 1.0 / (modulus + 0.5);
        this.longModulus = (long)modulus;
        this.modulus = modulus;
    }
}

