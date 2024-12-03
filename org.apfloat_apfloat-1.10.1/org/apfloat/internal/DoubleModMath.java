/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.DoubleElementaryModMath;

public class DoubleModMath
extends DoubleElementaryModMath {
    public final double[] createWTable(double w, int n) {
        double[] wTable = new double[n];
        double wTemp = 1.0;
        for (int i = 0; i < n; ++i) {
            wTable[i] = wTemp;
            wTemp = this.modMultiply(wTemp, w);
        }
        return wTable;
    }

    public double getForwardNthRoot(double primitiveRoot, long n) {
        return this.modPow(primitiveRoot, this.getModulus() - 1.0 - (this.getModulus() - 1.0) / (double)n);
    }

    public double getInverseNthRoot(double primitiveRoot, long n) {
        return this.modPow(primitiveRoot, (this.getModulus() - 1.0) / (double)n);
    }

    public final double modInverse(double a) {
        return this.modPow(a, this.getModulus() - 2.0);
    }

    public final double modDivide(double a, double b) {
        return this.modMultiply(a, this.modInverse(b));
    }

    public final double negate(double a) {
        return a == 0.0 ? 0.0 : this.getModulus() - a;
    }

    public final double modPow(double a, double n) {
        assert (a != 0.0 || n != 0.0);
        if (n == 0.0) {
            return 1.0;
        }
        if (n < 0.0) {
            return this.modPow(a, this.getModulus() - 1.0 + n);
        }
        long exponent = (long)n;
        while ((exponent & 1L) == 0L) {
            a = this.modMultiply(a, a);
            exponent >>= 1;
        }
        double r = a;
        while ((exponent >>= 1) > 0L) {
            a = this.modMultiply(a, a);
            if ((exponent & 1L) == 0L) continue;
            r = this.modMultiply(r, a);
        }
        return r;
    }
}

