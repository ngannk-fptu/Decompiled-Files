/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang.math;

import java.util.Random;

public final class JVMRandom
extends Random {
    private static final long serialVersionUID = 1L;
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
        if (n <= 0) {
            throw new IllegalArgumentException("Upper bound for nextInt must be positive");
        }
        return (int)(Math.random() * (double)n);
    }

    public long nextLong() {
        return JVMRandom.nextLong(Long.MAX_VALUE);
    }

    public static long nextLong(long n) {
        if (n <= 0L) {
            throw new IllegalArgumentException("Upper bound for nextInt must be positive");
        }
        return (long)(Math.random() * (double)n);
    }

    public boolean nextBoolean() {
        return Math.random() > 0.5;
    }

    public float nextFloat() {
        return (float)Math.random();
    }

    public double nextDouble() {
        return Math.random();
    }
}

