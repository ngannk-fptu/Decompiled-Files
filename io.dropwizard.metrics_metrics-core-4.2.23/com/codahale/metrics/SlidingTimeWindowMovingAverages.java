/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Clock;
import com.codahale.metrics.MovingAverages;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class SlidingTimeWindowMovingAverages
implements MovingAverages {
    private static final long TIME_WINDOW_DURATION_MINUTES = 15L;
    private static final long TICK_INTERVAL = TimeUnit.SECONDS.toNanos(1L);
    private static final Duration TIME_WINDOW_DURATION = Duration.ofMinutes(15L);
    static final int NUMBER_OF_BUCKETS = (int)(TIME_WINDOW_DURATION.toNanos() / TICK_INTERVAL);
    private final AtomicLong lastTick;
    private final Clock clock;
    private ArrayList<LongAdder> buckets;
    private int oldestBucketIndex;
    private int currentBucketIndex;
    private final Instant bucketBaseTime;
    Instant oldestBucketTime;

    public SlidingTimeWindowMovingAverages() {
        this(Clock.defaultClock());
    }

    public SlidingTimeWindowMovingAverages(Clock clock) {
        this.clock = clock;
        long startTime = clock.getTick();
        this.lastTick = new AtomicLong(startTime);
        this.buckets = new ArrayList(NUMBER_OF_BUCKETS);
        for (int i = 0; i < NUMBER_OF_BUCKETS; ++i) {
            this.buckets.add(new LongAdder());
        }
        this.oldestBucketTime = this.bucketBaseTime = Instant.ofEpochSecond(0L, startTime);
        this.oldestBucketIndex = 0;
        this.currentBucketIndex = 0;
    }

    @Override
    public void update(long n) {
        this.buckets.get(this.currentBucketIndex).add(n);
    }

    @Override
    public void tickIfNecessary() {
        long newLastTick;
        long oldTick = this.lastTick.get();
        long newTick = this.clock.getTick();
        long age = newTick - oldTick;
        if (age >= TICK_INTERVAL && this.lastTick.compareAndSet(oldTick, newLastTick = newTick - age % TICK_INTERVAL)) {
            Instant currentInstant = Instant.ofEpochSecond(0L, newLastTick);
            this.currentBucketIndex = this.normalizeIndex(this.calculateIndexOfTick(currentInstant));
            this.cleanOldBuckets(currentInstant);
        }
    }

    @Override
    public double getM15Rate() {
        return this.getMinuteRate(15);
    }

    @Override
    public double getM5Rate() {
        return this.getMinuteRate(5);
    }

    @Override
    public double getM1Rate() {
        return this.getMinuteRate(1);
    }

    private double getMinuteRate(int minutes) {
        Instant now = Instant.ofEpochSecond(0L, this.lastTick.get());
        return this.sumBuckets(now, (int)(TimeUnit.MINUTES.toNanos(minutes) / TICK_INTERVAL));
    }

    int calculateIndexOfTick(Instant tickTime) {
        return (int)(Duration.between(this.bucketBaseTime, tickTime).toNanos() / TICK_INTERVAL);
    }

    int normalizeIndex(int index) {
        int mod = index % NUMBER_OF_BUCKETS;
        return mod >= 0 ? mod : mod + NUMBER_OF_BUCKETS;
    }

    private void cleanOldBuckets(Instant currentTick) {
        int newOldestIndex;
        Instant youngestNotInWindow;
        Instant oldestStillNeededTime = currentTick.minus(TIME_WINDOW_DURATION).plusNanos(TICK_INTERVAL);
        if (oldestStillNeededTime.isAfter(youngestNotInWindow = this.oldestBucketTime.plus(TIME_WINDOW_DURATION))) {
            newOldestIndex = this.oldestBucketIndex;
            this.oldestBucketTime = currentTick;
        } else if (oldestStillNeededTime.isAfter(this.oldestBucketTime)) {
            newOldestIndex = this.normalizeIndex(this.calculateIndexOfTick(oldestStillNeededTime));
            this.oldestBucketTime = oldestStillNeededTime;
        } else {
            return;
        }
        this.cleanBucketRange(this.oldestBucketIndex, newOldestIndex);
        this.oldestBucketIndex = newOldestIndex;
    }

    private void cleanBucketRange(int fromIndex, int toIndex) {
        if (fromIndex < toIndex) {
            for (int i = fromIndex; i < toIndex; ++i) {
                this.buckets.get(i).reset();
            }
        } else {
            int i;
            for (i = fromIndex; i < NUMBER_OF_BUCKETS; ++i) {
                this.buckets.get(i).reset();
            }
            for (i = 0; i < toIndex; ++i) {
                this.buckets.get(i).reset();
            }
        }
    }

    private long sumBuckets(Instant toTime, int numberOfBuckets) {
        int toIndex = this.normalizeIndex(this.calculateIndexOfTick(toTime) + 1);
        int fromIndex = this.normalizeIndex(toIndex - numberOfBuckets);
        LongAdder adder = new LongAdder();
        if (fromIndex < toIndex) {
            this.buckets.stream().skip(fromIndex).limit(toIndex - fromIndex).mapToLong(LongAdder::longValue).forEach(adder::add);
        } else {
            this.buckets.stream().limit(toIndex).mapToLong(LongAdder::longValue).forEach(adder::add);
            this.buckets.stream().skip(fromIndex).mapToLong(LongAdder::longValue).forEach(adder::add);
        }
        long retval = adder.longValue();
        return retval;
    }
}

