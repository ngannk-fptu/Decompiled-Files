/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.distribution;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;

public interface HistogramSupport
extends Meter {
    public HistogramSnapshot takeSnapshot();

    @Deprecated
    default public HistogramSnapshot takeSnapshot(boolean supportsAggregablePercentiles) {
        return this.takeSnapshot();
    }
}

