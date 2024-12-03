/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.IntElementaryModMath;

public class IntModMath
extends IntElementaryModMath {
    public final int[] createWTable(int w, int n) {
        int[] wTable = new int[n];
        int wTemp = 1;
        for (int i = 0; i < n; ++i) {
            wTable[i] = wTemp;
            wTemp = this.modMultiply(wTemp, w);
        }
        return wTable;
    }

    public int getForwardNthRoot(int primitiveRoot, long n) {
        return this.modPow(primitiveRoot, this.getModulus() - 1 - (this.getModulus() - 1) / (int)n);
    }

    public int getInverseNthRoot(int primitiveRoot, long n) {
        return this.modPow(primitiveRoot, (this.getModulus() - 1) / (int)n);
    }

    public final int modInverse(int a) {
        return this.modPow(a, this.getModulus() - 2);
    }

    public final int modDivide(int a, int b) {
        return this.modMultiply(a, this.modInverse(b));
    }

    public final int negate(int a) {
        return a == 0 ? 0 : this.getModulus() - a;
    }

    public final int modPow(int a, int n) {
        assert (a != 0 || n != 0);
        if (n == 0) {
            return 1;
        }
        if (n < 0) {
            return this.modPow(a, this.getModulus() - 1 + n);
        }
        long exponent = n;
        while ((exponent & 1L) == 0L) {
            a = this.modMultiply(a, a);
            exponent >>= 1;
        }
        int r = a;
        while ((exponent >>= 1) > 0L) {
            a = this.modMultiply(a, a);
            if ((exponent & 1L) == 0L) continue;
            r = this.modMultiply(r, a);
        }
        return r;
    }
}

