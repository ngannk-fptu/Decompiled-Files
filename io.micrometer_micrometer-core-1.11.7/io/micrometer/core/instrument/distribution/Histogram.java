/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.distribution;

import io.micrometer.core.instrument.distribution.HistogramSnapshot;

public interface Histogram
extends AutoCloseable {
    public void recordLong(long var1);

    public void recordDouble(double var1);

    public HistogramSnapshot takeSnapshot(long var1, double var3, double var5);

    @Override
    default public void close() {
    }
}

