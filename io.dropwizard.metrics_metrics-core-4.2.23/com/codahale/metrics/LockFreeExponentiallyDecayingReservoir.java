/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Clock;
import com.codahale.metrics.Reservoir;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.WeightedSnapshot;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.BiConsumer;

public final class LockFreeExponentiallyDecayingReservoir
implements Reservoir {
    private static final double SECONDS_PER_NANO = 1.0E-9;
    private static final AtomicReferenceFieldUpdater<LockFreeExponentiallyDecayingReservoir, State> stateUpdater = AtomicReferenceFieldUpdater.newUpdater(LockFreeExponentiallyDecayingReservoir.class, State.class, "state");
    private final int size;
    private final long rescaleThresholdNanos;
    private final Clock clock;
    private volatile State state;

    private LockFreeExponentiallyDecayingReservoir(int size, double alpha, Duration rescaleThreshold, Clock clock) {
        double alphaNanos = alpha * 1.0E-9;
        this.size = size;
        this.clock = clock;
        this.rescaleThresholdNanos = rescaleThreshold.toNanos();
        this.state = new State(alphaNanos, size, clock.getTick(), 0, new ConcurrentSkipListMap<Double, WeightedSnapshot.WeightedSample>());
    }

    @Override
    public int size() {
        return Math.min(this.size, this.state.count);
    }

    @Override
    public void update(long value) {
        long now = this.clock.getTick();
        this.rescaleIfNeeded(now).update(value, now);
    }

    private State rescaleIfNeeded(long currentTick) {
        State stateSnapshot = this.state;
        if (currentTick - stateSnapshot.startTick >= this.rescaleThresholdNanos) {
            return this.doRescale(currentTick, stateSnapshot);
        }
        return stateSnapshot;
    }

    private State doRescale(long currentTick, State stateSnapshot) {
        State newState = stateSnapshot.rescale(currentTick);
        if (stateUpdater.compareAndSet(this, stateSnapshot, newState)) {
            return newState;
        }
        return this.state;
    }

    @Override
    public Snapshot getSnapshot() {
        State stateSnapshot = this.rescaleIfNeeded(this.clock.getTick());
        return new WeightedSnapshot(stateSnapshot.values.values());
    }

    public static Builder builder() {
        return new Builder();
    }

    private static final class State {
        private static final AtomicIntegerFieldUpdater<State> countUpdater = AtomicIntegerFieldUpdater.newUpdater(State.class, "count");
        private final double alphaNanos;
        private final int size;
        private final long startTick;
        private final ConcurrentSkipListMap<Double, WeightedSnapshot.WeightedSample> values;
        private volatile int count;

        State(double alphaNanos, int size, long startTick, int count, ConcurrentSkipListMap<Double, WeightedSnapshot.WeightedSample> values) {
            this.alphaNanos = alphaNanos;
            this.size = size;
            this.startTick = startTick;
            this.values = values;
            this.count = count;
        }

        private void update(long value, long timestampNanos) {
            boolean mapIsFull;
            double itemWeight = this.weight(timestampNanos - this.startTick);
            double priority = itemWeight / ThreadLocalRandom.current().nextDouble();
            boolean bl = mapIsFull = this.count >= this.size;
            if (!mapIsFull || this.values.firstKey() < priority) {
                this.addSample(priority, value, itemWeight, mapIsFull);
            }
        }

        private void addSample(double priority, long value, double itemWeight, boolean bypassIncrement) {
            if (this.values.putIfAbsent(priority, new WeightedSnapshot.WeightedSample(value, itemWeight)) == null && (bypassIncrement || countUpdater.incrementAndGet(this) > this.size)) {
                this.values.pollFirstEntry();
            }
        }

        State rescale(long newTick) {
            long durationNanos = newTick - this.startTick;
            double scalingFactor = Math.exp(-this.alphaNanos * (double)durationNanos);
            int newCount = 0;
            ConcurrentSkipListMap<Double, WeightedSnapshot.WeightedSample> newValues = new ConcurrentSkipListMap<Double, WeightedSnapshot.WeightedSample>();
            if (Double.compare(scalingFactor, 0.0) != 0) {
                RescalingConsumer consumer = new RescalingConsumer(scalingFactor, newValues);
                this.values.forEach(consumer);
                newCount = consumer.count;
            }
            while (newCount > this.size) {
                Objects.requireNonNull(newValues.pollFirstEntry(), "Expected an entry");
                --newCount;
            }
            return new State(this.alphaNanos, this.size, newTick, newCount, newValues);
        }

        private double weight(long durationNanos) {
            return Math.exp(this.alphaNanos * (double)durationNanos);
        }
    }

    public static final class Builder {
        private static final int DEFAULT_SIZE = 1028;
        private static final double DEFAULT_ALPHA = 0.015;
        private static final Duration DEFAULT_RESCALE_THRESHOLD = Duration.ofHours(1L);
        private int size = 1028;
        private double alpha = 0.015;
        private Duration rescaleThreshold = DEFAULT_RESCALE_THRESHOLD;
        private Clock clock = Clock.defaultClock();

        private Builder() {
        }

        public Builder size(int value) {
            if (value <= 0) {
                throw new IllegalArgumentException("LockFreeExponentiallyDecayingReservoir size must be positive: " + value);
            }
            this.size = value;
            return this;
        }

        public Builder alpha(double value) {
            this.alpha = value;
            return this;
        }

        public Builder rescaleThreshold(Duration value) {
            this.rescaleThreshold = Objects.requireNonNull(value, "rescaleThreshold is required");
            return this;
        }

        public Builder clock(Clock value) {
            this.clock = Objects.requireNonNull(value, "clock is required");
            return this;
        }

        public Reservoir build() {
            return new LockFreeExponentiallyDecayingReservoir(this.size, this.alpha, this.rescaleThreshold, this.clock);
        }
    }

    private static final class RescalingConsumer
    implements BiConsumer<Double, WeightedSnapshot.WeightedSample> {
        private final double scalingFactor;
        private final ConcurrentSkipListMap<Double, WeightedSnapshot.WeightedSample> values;
        private int count;

        RescalingConsumer(double scalingFactor, ConcurrentSkipListMap<Double, WeightedSnapshot.WeightedSample> values) {
            this.scalingFactor = scalingFactor;
            this.values = values;
        }

        @Override
        public void accept(Double priority, WeightedSnapshot.WeightedSample sample) {
            double newWeight = sample.weight * this.scalingFactor;
            if (Double.compare(newWeight, 0.0) == 0) {
                return;
            }
            WeightedSnapshot.WeightedSample newSample = new WeightedSnapshot.WeightedSample(sample.value, newWeight);
            if (this.values.put(priority * this.scalingFactor, newSample) == null) {
                ++this.count;
            }
        }
    }
}

