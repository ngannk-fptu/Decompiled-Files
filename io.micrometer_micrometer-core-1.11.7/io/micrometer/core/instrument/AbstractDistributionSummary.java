/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.AbstractMeter;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.Histogram;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.NoopHistogram;
import io.micrometer.core.instrument.distribution.TimeWindowFixedBoundaryHistogram;
import io.micrometer.core.instrument.distribution.TimeWindowPercentileHistogram;

public abstract class AbstractDistributionSummary
extends AbstractMeter
implements DistributionSummary {
    protected final Histogram histogram;
    private final double scale;

    protected AbstractDistributionSummary(Meter.Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig, double scale, boolean supportsAggregablePercentiles) {
        this(id, scale, AbstractDistributionSummary.defaultHistogram(clock, distributionStatisticConfig, supportsAggregablePercentiles));
    }

    protected AbstractDistributionSummary(Meter.Id id, double scale, @Nullable Histogram histogram) {
        super(id);
        this.scale = scale;
        this.histogram = histogram == null ? NoopHistogram.INSTANCE : histogram;
    }

    protected static Histogram defaultHistogram(Clock clock, DistributionStatisticConfig distributionStatisticConfig, boolean supportsAggregablePercentiles) {
        if (distributionStatisticConfig.isPublishingPercentiles()) {
            return new TimeWindowPercentileHistogram(clock, distributionStatisticConfig, supportsAggregablePercentiles);
        }
        if (distributionStatisticConfig.isPublishingHistogram()) {
            return new TimeWindowFixedBoundaryHistogram(clock, distributionStatisticConfig, supportsAggregablePercentiles);
        }
        return NoopHistogram.INSTANCE;
    }

    @Override
    public final void record(double amount) {
        if (amount >= 0.0) {
            double scaledAmount = this.scale * amount;
            this.histogram.recordDouble(scaledAmount);
            this.recordNonNegative(scaledAmount);
        }
    }

    protected abstract void recordNonNegative(double var1);

    @Override
    public HistogramSnapshot takeSnapshot() {
        return this.histogram.takeSnapshot(this.count(), this.totalAmount(), this.max());
    }
}

