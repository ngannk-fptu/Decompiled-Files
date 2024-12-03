/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Reservoir;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.UniformSnapshot;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

public class UniformReservoir
implements Reservoir {
    private static final int DEFAULT_SIZE = 1028;
    private final AtomicLong count = new AtomicLong();
    private final AtomicLongArray values;

    public UniformReservoir() {
        this(1028);
    }

    public UniformReservoir(int size) {
        this.values = new AtomicLongArray(size);
        for (int i = 0; i < this.values.length(); ++i) {
            this.values.set(i, 0L);
        }
        this.count.set(0L);
    }

    @Override
    public int size() {
        long c = this.count.get();
        if (c > (long)this.values.length()) {
            return this.values.length();
        }
        return (int)c;
    }

    @Override
    public void update(long value) {
        long c = this.count.incrementAndGet();
        if (c <= (long)this.values.length()) {
            this.values.set((int)c - 1, value);
        } else {
            long r = ThreadLocalRandom.current().nextLong(c);
            if (r < (long)this.values.length()) {
                this.values.set((int)r, value);
            }
        }
    }

    @Override
    public Snapshot getSnapshot() {
        int s = this.size();
        long[] copy = new long[s];
        for (int i = 0; i < s; ++i) {
            copy[i] = this.values.get(i);
        }
        return new UniformSnapshot(copy);
    }
}

