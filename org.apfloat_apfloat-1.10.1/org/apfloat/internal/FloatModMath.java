/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.FloatElementaryModMath;

public class FloatModMath
extends FloatElementaryModMath {
    public final float[] createWTable(float w, int n) {
        float[] wTable = new float[n];
        float wTemp = 1.0f;
        for (int i = 0; i < n; ++i) {
            wTable[i] = wTemp;
            wTemp = this.modMultiply(wTemp, w);
        }
        return wTable;
    }

    public float getForwardNthRoot(float primitiveRoot, long n) {
        return this.modPow(primitiveRoot, this.getModulus() - 1.0f - (this.getModulus() - 1.0f) / (float)n);
    }

    public float getInverseNthRoot(float primitiveRoot, long n) {
        return this.modPow(primitiveRoot, (this.getModulus() - 1.0f) / (float)n);
    }

    public final float modInverse(float a) {
        return this.modPow(a, this.getModulus() - 2.0f);
    }

    public final float modDivide(float a, float b) {
        return this.modMultiply(a, this.modInverse(b));
    }

    public final float negate(float a) {
        return a == 0.0f ? 0.0f : this.getModulus() - a;
    }

    public final float modPow(float a, float n) {
        assert (a != 0.0f || n != 0.0f);
        if (n == 0.0f) {
            return 1.0f;
        }
        if (n < 0.0f) {
            return this.modPow(a, this.getModulus() - 1.0f + n);
        }
        long exponent = (long)n;
        while ((exponent & 1L) == 0L) {
            a = this.modMultiply(a, a);
            exponent >>= 1;
        }
        float r = a;
        while ((exponent >>= 1) > 0L) {
            a = this.modMultiply(a, a);
            if ((exponent & 1L) == 0L) continue;
            r = this.modMultiply(r, a);
        }
        return r;
    }
}

