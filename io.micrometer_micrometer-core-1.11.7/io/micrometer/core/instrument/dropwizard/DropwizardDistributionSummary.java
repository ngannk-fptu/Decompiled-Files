/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codahale.metrics.Histogram
 */
package io.micrometer.core.instrument.dropwizard;

import com.codahale.metrics.Histogram;
import io.micrometer.core.instrument.AbstractDistributionSummary;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.TimeWindowMax;
import java.util.concurrent.atomic.DoubleAdder;

public class DropwizardDistributionSummary
extends AbstractDistributionSummary {
    private final Histogram impl;
    private final DoubleAdder totalAmount = new DoubleAdder();
    private final TimeWindowMax max;

    DropwizardDistributionSummary(Meter.Id id, Clock clock, Histogram impl, DistributionStatisticConfig distributionStatisticConfig, double scale) {
        super(id, clock, distributionStatisticConfig, scale, false);
        this.impl = impl;
        this.max = new TimeWindowMax(clock, distributionStatisticConfig);
    }

    @Override
    protected void recordNonNegative(double amount) {
        if (amount >= 0.0) {
            this.impl.update((long)amount);
            this.totalAmount.add(amount);
            this.max.record(amount);
        }
    }

    @Override
    public long count() {
        return this.impl.getCount();
    }

    @Override
    public double totalAmount() {
        return this.totalAmount.doubleValue();
    }

    @Override
    public double max() {
        return this.max.poll();
    }
}

