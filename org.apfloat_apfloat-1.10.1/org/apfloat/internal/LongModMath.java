/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.LongElementaryModMath;

public class LongModMath
extends LongElementaryModMath {
    public final long[] createWTable(long w, int n) {
        long[] wTable = new long[n];
        long wTemp = 1L;
        for (int i = 0; i < n; ++i) {
            wTable[i] = wTemp;
            wTemp = this.modMultiply(wTemp, w);
        }
        return wTable;
    }

    public long getForwardNthRoot(long primitiveRoot, long n) {
        return this.modPow(primitiveRoot, this.getModulus() - 1L - (this.getModulus() - 1L) / n);
    }

    public long getInverseNthRoot(long primitiveRoot, long n) {
        return this.modPow(primitiveRoot, (this.getModulus() - 1L) / n);
    }

    public final long modInverse(long a) {
        return this.modPow(a, this.getModulus() - 2L);
    }

    public final long modDivide(long a, long b) {
        return this.modMultiply(a, this.modInverse(b));
    }

    public final long negate(long a) {
        return a == 0L ? 0L : this.getModulus() - a;
    }

    public final long modPow(long a, long n) {
        assert (a != 0L || n != 0L);
        if (n == 0L) {
            return 1L;
        }
        if (n < 0L) {
            return this.modPow(a, this.getModulus() - 1L + n);
        }
        long exponent = n;
        while ((exponent & 1L) == 0L) {
            a = this.modMultiply(a, a);
            exponent >>= 1;
        }
        long r = a;
        while ((exponent >>= 1) > 0L) {
            a = this.modMultiply(a, a);
            if ((exponent & 1L) == 0L) continue;
            r = this.modMultiply(r, a);
        }
        return r;
    }
}

