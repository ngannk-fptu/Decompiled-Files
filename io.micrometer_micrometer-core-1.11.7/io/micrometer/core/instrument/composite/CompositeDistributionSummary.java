/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.composite;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.AbstractCompositeMeter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.noop.NoopDistributionSummary;

class CompositeDistributionSummary
extends AbstractCompositeMeter<DistributionSummary>
implements DistributionSummary {
    private final DistributionStatisticConfig distributionStatisticConfig;
    private final double scale;

    CompositeDistributionSummary(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, double scale) {
        super(id);
        this.distributionStatisticConfig = distributionStatisticConfig;
        this.scale = scale;
    }

    @Override
    public void record(double amount) {
        for (DistributionSummary ds : this.getChildren()) {
            ds.record(amount);
        }
    }

    @Override
    public long count() {
        return ((DistributionSummary)this.firstChild()).count();
    }

    @Override
    public double totalAmount() {
        return ((DistributionSummary)this.firstChild()).totalAmount();
    }

    @Override
    public double max() {
        return ((DistributionSummary)this.firstChild()).max();
    }

    @Override
    public HistogramSnapshot takeSnapshot() {
        return ((DistributionSummary)this.firstChild()).takeSnapshot();
    }

    @Override
    DistributionSummary newNoopMeter() {
        return new NoopDistributionSummary(this.getId());
    }

    @Override
    DistributionSummary registerNewMeter(MeterRegistry registry) {
        return DistributionSummary.builder(this.getId().getName()).tags(this.getId().getTagsAsIterable()).description(this.getId().getDescription()).baseUnit(this.getId().getBaseUnit()).publishPercentiles(this.distributionStatisticConfig.getPercentiles()).publishPercentileHistogram(this.distributionStatisticConfig.isPercentileHistogram()).maximumExpectedValue(this.distributionStatisticConfig.getMaximumExpectedValueAsDouble()).minimumExpectedValue(this.distributionStatisticConfig.getMinimumExpectedValueAsDouble()).distributionStatisticBufferLength(this.distributionStatisticConfig.getBufferLength()).distributionStatisticExpiry(this.distributionStatisticConfig.getExpiry()).percentilePrecision(this.distributionStatisticConfig.getPercentilePrecision()).serviceLevelObjectives(this.distributionStatisticConfig.getServiceLevelObjectiveBoundaries()).scale(this.scale).register(registry);
    }
}

