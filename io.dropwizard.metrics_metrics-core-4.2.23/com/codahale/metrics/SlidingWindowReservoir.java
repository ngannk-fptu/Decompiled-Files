/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Reservoir;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.UniformSnapshot;

public class SlidingWindowReservoir
implements Reservoir {
    private final long[] measurements;
    private long count;

    public SlidingWindowReservoir(int size) {
        this.measurements = new long[size];
        this.count = 0L;
    }

    @Override
    public synchronized int size() {
        return (int)Math.min(this.count, (long)this.measurements.length);
    }

    @Override
    public synchronized void update(long value) {
        this.measurements[(int)(this.count++ % (long)this.measurements.length)] = value;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Snapshot getSnapshot() {
        long[] values = new long[this.size()];
        for (int i = 0; i < values.length; ++i) {
            SlidingWindowReservoir slidingWindowReservoir = this;
            synchronized (slidingWindowReservoir) {
                values[i] = this.measurements[i];
                continue;
            }
        }
        return new UniformSnapshot(values);
    }
}

