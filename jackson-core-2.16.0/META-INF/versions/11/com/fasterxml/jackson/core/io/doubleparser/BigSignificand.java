/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core.io.doubleparser;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.math.BigInteger;
import java.nio.ByteOrder;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class BigSignificand {
    private static final long LONG_MASK = 0xFFFFFFFFL;
    private static final VarHandle readIntBE = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    private final int numInts;
    private final byte[] x;
    private int firstNonZeroInt;

    public BigSignificand(long numBits) {
        if (numBits <= 0L || numBits >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("numBits=" + numBits);
        }
        int numLongs = (int)(numBits + 63L >>> 6) + 1;
        this.numInts = numLongs << 1;
        int numBytes = this.numInts << 2;
        this.x = new byte[numBytes];
        this.firstNonZeroInt = this.numInts;
    }

    public void add(int value) {
        if (value == 0) {
            return;
        }
        long carry = (long)value & 0xFFFFFFFFL;
        int i = this.numInts - 1;
        while (carry != 0L) {
            long sum = ((long)this.x(i) & 0xFFFFFFFFL) + carry;
            this.x(i, (int)sum);
            carry = sum >>> 32;
            --i;
        }
        this.firstNonZeroInt = Math.min(this.firstNonZeroInt, i + 1);
    }

    public void fma(int factor, int addend) {
        int i;
        long factorL = (long)factor & 0xFFFFFFFFL;
        long carry = addend;
        for (i = this.numInts - 1; i >= this.firstNonZeroInt; --i) {
            long product = factorL * ((long)this.x(i) & 0xFFFFFFFFL) + carry;
            this.x(i, (int)product);
            carry = product >>> 32;
        }
        if (carry != 0L) {
            this.x(i, (int)carry);
            this.firstNonZeroInt = i;
        }
    }

    public BigInteger toBigInteger() {
        return new BigInteger(this.x);
    }

    private void x(int i, int value) {
        readIntBE.set(this.x, i << 2, value);
    }

    private int x(int i) {
        return readIntBE.get(this.x, i << 2);
    }
}

