/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import java.math.BigInteger;
import org.xhtmlrenderer.util.ArrayUtil;

public class PermutationGenerator {
    private int[] a;
    private BigInteger numLeft;
    private BigInteger total;

    public PermutationGenerator(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("Min 1");
        }
        this.a = new int[n];
        this.total = PermutationGenerator.getFactorial(n);
        this.reset();
    }

    public void reset() {
        for (int i = 0; i < this.a.length; ++i) {
            this.a[i] = i;
        }
        this.numLeft = new BigInteger(this.total.toString());
    }

    public BigInteger getNumLeft() {
        return this.numLeft;
    }

    public BigInteger getTotal() {
        return this.total;
    }

    public boolean hasMore() {
        return this.numLeft.compareTo(BigInteger.ZERO) == 1;
    }

    private static BigInteger getFactorial(int n) {
        BigInteger fact = BigInteger.ONE;
        for (int i = n; i > 1; --i) {
            fact = fact.multiply(new BigInteger(Integer.toString(i)));
        }
        return fact;
    }

    public int[] getNext() {
        if (this.numLeft.equals(this.total)) {
            this.numLeft = this.numLeft.subtract(BigInteger.ONE);
            return ArrayUtil.cloneOrEmpty(this.a);
        }
        int j = this.a.length - 2;
        while (this.a[j] > this.a[j + 1]) {
            --j;
        }
        int k = this.a.length - 1;
        while (this.a[j] > this.a[k]) {
            --k;
        }
        int temp = this.a[k];
        this.a[k] = this.a[j];
        this.a[j] = temp;
        int r = this.a.length - 1;
        for (int s = j + 1; r > s; --r, ++s) {
            temp = this.a[s];
            this.a[s] = this.a[r];
            this.a[r] = temp;
        }
        this.numLeft = this.numLeft.subtract(BigInteger.ONE);
        return ArrayUtil.cloneOrEmpty(this.a);
    }
}

