/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Clock;
import com.codahale.metrics.Reservoir;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.WeightedSnapshot;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ExponentiallyDecayingReservoir
implements Reservoir {
    private static final int DEFAULT_SIZE = 1028;
    private static final double DEFAULT_ALPHA = 0.015;
    private static final long RESCALE_THRESHOLD = TimeUnit.HOURS.toNanos(1L);
    private final ConcurrentSkipListMap<Double, WeightedSnapshot.WeightedSample> values = new ConcurrentSkipListMap();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final double alpha;
    private final int size;
    private final AtomicLong count;
    private volatile long startTime;
    private final AtomicLong lastScaleTick;
    private final Clock clock;

    public ExponentiallyDecayingReservoir() {
        this(1028, 0.015);
    }

    public ExponentiallyDecayingReservoir(int size, double alpha) {
        this(size, alpha, Clock.defaultClock());
    }

    public ExponentiallyDecayingReservoir(int size, double alpha, Clock clock) {
        this.alpha = alpha;
        this.size = size;
        this.clock = clock;
        this.count = new AtomicLong(0L);
        this.startTime = this.currentTimeInSeconds();
        this.lastScaleTick = new AtomicLong(clock.getTick());
    }

    @Override
    public int size() {
        return (int)Math.min((long)this.size, this.count.get());
    }

    @Override
    public void update(long value) {
        this.update(value, this.currentTimeInSeconds());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update(long value, long timestamp) {
        this.rescaleIfNeeded();
        this.lockForRegularUsage();
        try {
            double itemWeight = this.weight(timestamp - this.startTime);
            WeightedSnapshot.WeightedSample sample = new WeightedSnapshot.WeightedSample(value, itemWeight);
            double priority = itemWeight / ThreadLocalRandom.current().nextDouble();
            long newCount = this.count.incrementAndGet();
            if (newCount <= (long)this.size || this.values.isEmpty()) {
                this.values.put(priority, sample);
            } else {
                Double first = this.values.firstKey();
                if (first < priority && this.values.putIfAbsent(priority, sample) == null) {
                    while (this.values.remove(first) == null) {
                        first = this.values.firstKey();
                    }
                }
            }
        }
        finally {
            this.unlockForRegularUsage();
        }
    }

    private void rescaleIfNeeded() {
        long lastScaleTickSnapshot;
        long now = this.clock.getTick();
        if (now - (lastScaleTickSnapshot = this.lastScaleTick.get()) >= RESCALE_THRESHOLD) {
            this.rescale(now, lastScaleTickSnapshot);
        }
    }

    @Override
    public Snapshot getSnapshot() {
        this.rescaleIfNeeded();
        this.lockForRegularUsage();
        try {
            WeightedSnapshot weightedSnapshot = new WeightedSnapshot(this.values.values());
            return weightedSnapshot;
        }
        finally {
            this.unlockForRegularUsage();
        }
    }

    private long currentTimeInSeconds() {
        return TimeUnit.MILLISECONDS.toSeconds(this.clock.getTime());
    }

    private double weight(long t) {
        return Math.exp(this.alpha * (double)t);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void rescale(long now, long lastTick) {
        this.lockForRescale();
        try {
            if (this.lastScaleTick.compareAndSet(lastTick, now)) {
                long oldStartTime = this.startTime;
                this.startTime = this.currentTimeInSeconds();
                double scalingFactor = Math.exp(-this.alpha * (double)(this.startTime - oldStartTime));
                if (Double.compare(scalingFactor, 0.0) == 0) {
                    this.values.clear();
                } else {
                    ArrayList keys = new ArrayList(this.values.keySet());
                    for (Double key : keys) {
                        WeightedSnapshot.WeightedSample sample = this.values.remove(key);
                        WeightedSnapshot.WeightedSample newSample = new WeightedSnapshot.WeightedSample(sample.value, sample.weight * scalingFactor);
                        if (Double.compare(newSample.weight, 0.0) == 0) continue;
                        this.values.put(key * scalingFactor, newSample);
                    }
                }
                this.count.set(this.values.size());
            }
        }
        finally {
            this.unlockForRescale();
        }
    }

    private void unlockForRescale() {
        this.lock.writeLock().unlock();
    }

    private void lockForRescale() {
        this.lock.writeLock().lock();
    }

    private void lockForRegularUsage() {
        this.lock.readLock().lock();
    }

    private void unlockForRegularUsage() {
        this.lock.readLock().unlock();
    }
}

