/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.distribution;

import io.micrometer.core.instrument.distribution.Histogram;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;

public class NoopHistogram
implements Histogram {
    public static final NoopHistogram INSTANCE = new NoopHistogram();

    private NoopHistogram() {
    }

    @Override
    public void recordLong(long value) {
    }

    @Override
    public void recordDouble(double value) {
    }

    @Override
    public HistogramSnapshot takeSnapshot(long count, double total, double max) {
        return HistogramSnapshot.empty(count, total, max);
    }
}

