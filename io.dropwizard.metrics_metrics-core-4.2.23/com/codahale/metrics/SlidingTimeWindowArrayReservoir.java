/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.ChunkedAssociativeLongArray;
import com.codahale.metrics.Clock;
import com.codahale.metrics.Reservoir;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.UniformSnapshot;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class SlidingTimeWindowArrayReservoir
implements Reservoir {
    private static final long COLLISION_BUFFER = 256L;
    private static final long TRIM_THRESHOLD = 256L;
    private static final long CLEAR_BUFFER = TimeUnit.HOURS.toNanos(1L) * 256L;
    private final Clock clock;
    private final ChunkedAssociativeLongArray measurements;
    private final long window;
    private final AtomicLong lastTick;
    private final AtomicLong count;
    private final long startTick;

    public SlidingTimeWindowArrayReservoir(long window, TimeUnit windowUnit) {
        this(window, windowUnit, Clock.defaultClock());
    }

    public SlidingTimeWindowArrayReservoir(long window, TimeUnit windowUnit, Clock clock) {
        this.startTick = clock.getTick();
        this.clock = clock;
        this.measurements = new ChunkedAssociativeLongArray();
        this.window = windowUnit.toNanos(window) * 256L;
        this.lastTick = new AtomicLong((clock.getTick() - this.startTick) * 256L);
        this.count = new AtomicLong();
    }

    @Override
    public int size() {
        this.trim();
        return this.measurements.size();
    }

    @Override
    public void update(long value) {
        long newTick;
        do {
            boolean longOverflow;
            if (this.count.incrementAndGet() % 256L == 0L) {
                this.trim();
            }
            long lastTick = this.lastTick.get();
            newTick = this.getTick();
            boolean bl = longOverflow = newTick < lastTick;
            if (!longOverflow) continue;
            this.measurements.clear();
        } while (!this.measurements.put(newTick, value));
    }

    @Override
    public Snapshot getSnapshot() {
        this.trim();
        return new UniformSnapshot(this.measurements.values());
    }

    private long getTick() {
        long tick;
        long newTick;
        long oldTick;
        while (!this.lastTick.compareAndSet(oldTick = this.lastTick.get(), newTick = (tick = (this.clock.getTick() - this.startTick) * 256L) - oldTick > 0L ? tick : oldTick + 1L)) {
        }
        return newTick;
    }

    void trim() {
        long windowEnd;
        long now = this.getTick();
        long windowStart = now - this.window;
        if (windowStart < (windowEnd = now + CLEAR_BUFFER)) {
            this.measurements.trim(windowStart, windowEnd);
        } else {
            this.measurements.clear();
        }
    }
}

