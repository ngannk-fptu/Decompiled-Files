/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.internal;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.distribution.CountAtBucket;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.internal.DefaultLongTaskTimer;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CumulativeHistogramLongTaskTimer
extends DefaultLongTaskTimer {
    @Nullable
    private CountAtBucket[] lastSnapshot;

    public CumulativeHistogramLongTaskTimer(Meter.Id id, Clock clock, TimeUnit baseTimeUnit, DistributionStatisticConfig distributionStatisticConfig) {
        super(id, clock, baseTimeUnit, distributionStatisticConfig, true);
    }

    @Override
    public HistogramSnapshot takeSnapshot() {
        HistogramSnapshot snapshot = super.takeSnapshot();
        AtomicInteger i = new AtomicInteger();
        snapshot = new HistogramSnapshot(snapshot.count(), snapshot.total(), snapshot.max(), snapshot.percentileValues(), (CountAtBucket[])Arrays.stream(snapshot.histogramCounts()).map(countAtBucket -> this.lastSnapshot == null ? countAtBucket : new CountAtBucket(countAtBucket.bucket(), countAtBucket.count() + this.lastSnapshot[i.getAndIncrement()].count())).toArray(CountAtBucket[]::new), snapshot::outputSummary);
        this.lastSnapshot = snapshot.histogramCounts();
        return snapshot;
    }
}

