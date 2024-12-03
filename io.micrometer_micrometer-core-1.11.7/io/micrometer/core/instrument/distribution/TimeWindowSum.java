/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.distribution;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;

public class TimeWindowSum {
    private static final AtomicIntegerFieldUpdater<TimeWindowSum> rotatingUpdater = AtomicIntegerFieldUpdater.newUpdater(TimeWindowSum.class, "rotating");
    private final long durationBetweenRotatesMillis;
    private final AtomicLong[] ringBuffer;
    private int currentBucket;
    private volatile long lastRotateTimestampMillis;
    private volatile int rotating;

    public TimeWindowSum(int bufferLength, Duration expiry) {
        this.durationBetweenRotatesMillis = expiry.toMillis();
        this.lastRotateTimestampMillis = System.currentTimeMillis();
        this.currentBucket = 0;
        this.ringBuffer = new AtomicLong[bufferLength];
        for (int i = 0; i < bufferLength; ++i) {
            this.ringBuffer[i] = new AtomicLong();
        }
    }

    public void record(long sampleMillis) {
        this.rotate();
        for (AtomicLong sum : this.ringBuffer) {
            sum.addAndGet(sampleMillis);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public double poll() {
        this.rotate();
        TimeWindowSum timeWindowSum = this;
        synchronized (timeWindowSum) {
            return this.ringBuffer[this.currentBucket].get();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void rotate() {
        long timeSinceLastRotateMillis = System.currentTimeMillis() - this.lastRotateTimestampMillis;
        if (timeSinceLastRotateMillis < this.durationBetweenRotatesMillis) {
            return;
        }
        if (!rotatingUpdater.compareAndSet(this, 0, 1)) {
            return;
        }
        try {
            int iterations = 0;
            TimeWindowSum timeWindowSum = this;
            synchronized (timeWindowSum) {
                do {
                    this.ringBuffer[this.currentBucket].set(0L);
                    if (++this.currentBucket >= this.ringBuffer.length) {
                        this.currentBucket = 0;
                    }
                    this.lastRotateTimestampMillis += this.durationBetweenRotatesMillis;
                } while ((timeSinceLastRotateMillis -= this.durationBetweenRotatesMillis) >= this.durationBetweenRotatesMillis && ++iterations < this.ringBuffer.length);
            }
        }
        finally {
            this.rotating = 0;
        }
    }
}

