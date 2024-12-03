/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.noop;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.noop.NoopMeter;

public class NoopDistributionSummary
extends NoopMeter
implements DistributionSummary {
    public NoopDistributionSummary(Meter.Id id) {
        super(id);
    }

    @Override
    public void record(double amount) {
    }

    @Override
    public long count() {
        return 0L;
    }

    @Override
    public double totalAmount() {
        return 0.0;
    }

    @Override
    public double max() {
        return 0.0;
    }

    @Override
    public HistogramSnapshot takeSnapshot() {
        return HistogramSnapshot.empty(0L, 0.0, 0.0);
    }
}

