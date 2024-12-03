/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class LongElementaryModMath {
    private long modulus;
    private double inverseModulus;

    public final long modMultiply(long a, long b) {
        long r = a * b - this.modulus * (long)((double)a * (double)b * this.inverseModulus);
        r = (r -= this.modulus * (long)((int)((double)r * this.inverseModulus))) >= this.modulus ? r - this.modulus : r;
        r = r < 0L ? r + this.modulus : r;
        return r;
    }

    public final long modAdd(long a, long b) {
        long r = a + b;
        return r >= this.modulus ? r - this.modulus : r;
    }

    public final long modSubtract(long a, long b) {
        long r = a - b;
        return r < 0L ? r + this.modulus : r;
    }

    public final long getModulus() {
        return this.modulus;
    }

    public final void setModulus(long modulus) {
        this.inverseModulus = 1.0 / (double)modulus;
        this.modulus = modulus;
    }
}

