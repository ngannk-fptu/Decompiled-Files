/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang.math;

import java.util.Random;

public final class JVMRandom
extends Random {
    private static final long serialVersionUID = 1L;
    private static final Random SHARED_RANDOM = new Random();
    private boolean constructed = true;

    public synchronized void setSeed(long seed) {
        if (this.constructed) {
            throw new UnsupportedOperationException();
        }
    }

    public synchronized double nextGaussian() {
        throw new UnsupportedOperationException();
    }

    public void nextBytes(byte[] byteArray) {
        throw new UnsupportedOperationException();
    }

    public int nextInt() {
        return this.nextInt(Integer.MAX_VALUE);
    }

    public int nextInt(int n) {
        return SHARED_RANDOM.nextInt(n);
    }

    public long nextLong() {
        return JVMRandom.nextLong(Long.MAX_VALUE);
    }

    public static long nextLong(long n) {
        long val;
        long bits;
        if (n <= 0L) {
            throw new IllegalArgumentException("Upper bound for nextInt must be positive");
        }
        if ((n & -n) == n) {
            return JVMRandom.next63bits() >> 63 - JVMRandom.bitsRequired(n - 1L);
        }
        while ((bits = JVMRandom.next63bits()) - (val = bits % n) + (n - 1L) < 0L) {
        }
        return val;
    }

    public boolean nextBoolean() {
        return SHARED_RANDOM.nextBoolean();
    }

    public float nextFloat() {
        return SHARED_RANDOM.nextFloat();
    }

    public double nextDouble() {
        return SHARED_RANDOM.nextDouble();
    }

    private static long next63bits() {
        return SHARED_RANDOM.nextLong() & Long.MAX_VALUE;
    }

    private static int bitsRequired(long num) {
        long y = num;
        int n = 0;
        while (num >= 0L) {
            if (y == 0L) {
                return n;
            }
            ++n;
            num <<= 1;
            y >>= 1;
        }
        return 64 - n;
    }
}

