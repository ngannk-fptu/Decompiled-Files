/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Counting;
import com.codahale.metrics.Metric;
import com.codahale.metrics.Reservoir;
import com.codahale.metrics.Sampling;
import com.codahale.metrics.Snapshot;
import java.util.concurrent.atomic.LongAdder;

public class Histogram
implements Metric,
Sampling,
Counting {
    private final Reservoir reservoir;
    private final LongAdder count;

    public Histogram(Reservoir reservoir) {
        this.reservoir = reservoir;
        this.count = new LongAdder();
    }

    public void update(int value) {
        this.update((long)value);
    }

    public void update(long value) {
        this.count.increment();
        this.reservoir.update(value);
    }

    @Override
    public long getCount() {
        return this.count.sum();
    }

    @Override
    public Snapshot getSnapshot() {
        return this.reservoir.getSnapshot();
    }
}

