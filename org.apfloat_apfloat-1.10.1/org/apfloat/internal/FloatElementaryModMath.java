/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

public class FloatElementaryModMath {
    private float modulus;
    private double inverseModulus;

    public final float modMultiply(float a, float b) {
        double r = (double)a * (double)b;
        return (float)(r - (double)this.modulus * (double)((int)(this.inverseModulus * r)));
    }

    public final float modAdd(float a, float b) {
        double r = (double)a + (double)b;
        return (float)(r >= (double)this.modulus ? r - (double)this.modulus : r);
    }

    public final float modSubtract(float a, float b) {
        float r = a - b;
        return r < 0.0f ? r + this.modulus : r;
    }

    public final float getModulus() {
        return this.modulus;
    }

    public final void setModulus(float modulus) {
        this.inverseModulus = 1.0 / (double)modulus;
        this.modulus = modulus;
    }
}

