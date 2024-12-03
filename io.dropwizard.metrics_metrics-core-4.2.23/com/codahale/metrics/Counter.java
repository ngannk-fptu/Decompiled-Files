/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Counting;
import com.codahale.metrics.Metric;
import java.util.concurrent.atomic.LongAdder;

public class Counter
implements Metric,
Counting {
    private final LongAdder count = new LongAdder();

    public void inc() {
        this.inc(1L);
    }

    public void inc(long n) {
        this.count.add(n);
    }

    public void dec() {
        this.dec(1L);
    }

    public void dec(long n) {
        this.count.add(-n);
    }

    @Override
    public long getCount() {
        return this.count.sum();
    }
}

