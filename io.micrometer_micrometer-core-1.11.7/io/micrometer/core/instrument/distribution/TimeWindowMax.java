/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.distribution;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.util.TimeUtils;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.DoubleSupplier;

public class TimeWindowMax {
    private static final AtomicIntegerFieldUpdater<TimeWindowMax> rotatingUpdater = AtomicIntegerFieldUpdater.newUpdater(TimeWindowMax.class, "rotating");
    private final Clock clock;
    private final long durationBetweenRotatesMillis;
    private final AtomicLong[] ringBuffer;
    private int currentBucket;
    private volatile long lastRotateTimestampMillis;
    private volatile int rotating;

    public TimeWindowMax(Clock clock, DistributionStatisticConfig config) {
        this(clock, config.getExpiry().toMillis(), config.getBufferLength());
    }

    public TimeWindowMax(Clock clock, long rotateFrequencyMillis, int bufferLength) {
        this.clock = clock;
        this.durationBetweenRotatesMillis = rotateFrequencyMillis;
        this.lastRotateTimestampMillis = clock.wallTime();
        this.currentBucket = 0;
        this.ringBuffer = new AtomicLong[bufferLength];
        for (int i = 0; i < bufferLength; ++i) {
            this.ringBuffer[i] = new AtomicLong();
        }
    }

    public void record(double sample, TimeUnit timeUnit) {
        this.record((long)TimeUtils.convert(sample, timeUnit, TimeUnit.NANOSECONDS));
    }

    private void record(long sample) {
        this.rotate();
        for (AtomicLong max : this.ringBuffer) {
            this.updateMax(max, sample);
        }
    }

    public double poll(TimeUnit timeUnit) {
        return this.poll(() -> TimeUtils.nanosToUnit(this.ringBuffer[this.currentBucket].get(), timeUnit));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private double poll(DoubleSupplier maxSupplier) {
        this.rotate();
        TimeWindowMax timeWindowMax = this;
        synchronized (timeWindowMax) {
            return maxSupplier.getAsDouble();
        }
    }

    public double poll() {
        return this.poll(() -> Double.longBitsToDouble(this.ringBuffer[this.currentBucket].get()));
    }

    public void record(double sample) {
        this.record(Double.doubleToLongBits(sample));
    }

    private void updateMax(AtomicLong max, long sample) {
        long curMax;
        while ((curMax = max.get()) < sample && !max.compareAndSet(curMax, sample)) {
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void rotate() {
        long wallTime = this.clock.wallTime();
        long timeSinceLastRotateMillis = wallTime - this.lastRotateTimestampMillis;
        if (timeSinceLastRotateMillis < this.durationBetweenRotatesMillis) {
            return;
        }
        if (!rotatingUpdater.compareAndSet(this, 0, 1)) {
            return;
        }
        try {
            TimeWindowMax timeWindowMax = this;
            synchronized (timeWindowMax) {
                block13: {
                    if (timeSinceLastRotateMillis < this.durationBetweenRotatesMillis * (long)this.ringBuffer.length) break block13;
                    for (AtomicLong bufferItem : this.ringBuffer) {
                        bufferItem.set(0L);
                    }
                    this.currentBucket = 0;
                    this.lastRotateTimestampMillis = wallTime - timeSinceLastRotateMillis % this.durationBetweenRotatesMillis;
                    return;
                }
                int iterations = 0;
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

