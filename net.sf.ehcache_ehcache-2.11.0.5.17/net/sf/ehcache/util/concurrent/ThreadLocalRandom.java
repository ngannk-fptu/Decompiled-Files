/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util.concurrent;

import java.util.Random;
import net.sf.ehcache.util.lang.VicariousThreadLocal;

public class ThreadLocalRandom
extends Random {
    private static final long multiplier = 25214903917L;
    private static final long addend = 11L;
    private static final long mask = 0xFFFFFFFFFFFFL;
    private long rnd;
    boolean initialized = true;
    private long pad0;
    private long pad1;
    private long pad2;
    private long pad3;
    private long pad4;
    private long pad5;
    private long pad6;
    private long pad7;
    private static final ThreadLocal<ThreadLocalRandom> localRandom = new VicariousThreadLocal<ThreadLocalRandom>(){

        @Override
        protected ThreadLocalRandom initialValue() {
            return new ThreadLocalRandom();
        }
    };
    private static final long serialVersionUID = -5851777807851030925L;

    ThreadLocalRandom() {
    }

    public static ThreadLocalRandom current() {
        return localRandom.get();
    }

    @Override
    public void setSeed(long seed) {
        if (this.initialized) {
            throw new UnsupportedOperationException();
        }
        this.rnd = (seed ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL;
    }

    @Override
    protected int next(int bits) {
        this.rnd = this.rnd * 25214903917L + 11L & 0xFFFFFFFFFFFFL;
        return (int)(this.rnd >>> 48 - bits);
    }

    @Override
    public int nextInt(int least, int bound) {
        if (least >= bound) {
            throw new IllegalArgumentException();
        }
        return this.nextInt(bound - least) + least;
    }

    @Override
    public long nextLong(long n) {
        if (n <= 0L) {
            throw new IllegalArgumentException("n must be positive");
        }
        long offset = 0L;
        while (n >= Integer.MAX_VALUE) {
            long nextn;
            int bits = this.next(2);
            long half = n >>> 1;
            long l = nextn = (bits & 2) == 0 ? half : n - half;
            if ((bits & 1) == 0) {
                offset += n - nextn;
            }
            n = nextn;
        }
        return offset + (long)this.nextInt((int)n);
    }

    @Override
    public long nextLong(long least, long bound) {
        if (least >= bound) {
            throw new IllegalArgumentException();
        }
        return this.nextLong(bound - least) + least;
    }

    @Override
    public double nextDouble(double n) {
        if (n <= 0.0) {
            throw new IllegalArgumentException("n must be positive");
        }
        return this.nextDouble() * n;
    }

    @Override
    public double nextDouble(double least, double bound) {
        if (least >= bound) {
            throw new IllegalArgumentException();
        }
        return this.nextDouble() * (bound - least) + least;
    }
}

