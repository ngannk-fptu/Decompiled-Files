/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.composite;

import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.AbstractCompositeMeter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.noop.NoopLongTaskTimer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

class CompositeLongTaskTimer
extends AbstractCompositeMeter<LongTaskTimer>
implements LongTaskTimer {
    private final DistributionStatisticConfig distributionStatisticConfig;

    CompositeLongTaskTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig) {
        super(id);
        this.distributionStatisticConfig = distributionStatisticConfig;
    }

    @Override
    public LongTaskTimer.Sample start() {
        ArrayList<LongTaskTimer.Sample> samples = new ArrayList<LongTaskTimer.Sample>();
        for (LongTaskTimer ltt : this.getChildren()) {
            samples.add(ltt.start());
        }
        return new CompositeSample(samples);
    }

    @Override
    public double duration(TimeUnit unit) {
        return ((LongTaskTimer)this.firstChild()).duration(unit);
    }

    @Override
    public int activeTasks() {
        return ((LongTaskTimer)this.firstChild()).activeTasks();
    }

    @Override
    public double max(TimeUnit unit) {
        return ((LongTaskTimer)this.firstChild()).max(unit);
    }

    @Override
    public TimeUnit baseTimeUnit() {
        return TimeUnit.SECONDS;
    }

    @Override
    public HistogramSnapshot takeSnapshot() {
        return ((LongTaskTimer)this.firstChild()).takeSnapshot();
    }

    @Override
    LongTaskTimer newNoopMeter() {
        return new NoopLongTaskTimer(this.getId());
    }

    @Override
    LongTaskTimer registerNewMeter(MeterRegistry registry) {
        LongTaskTimer.Builder builder = LongTaskTimer.builder(this.getId().getName()).tags(this.getId().getTagsAsIterable()).description(this.getId().getDescription()).maximumExpectedValue(Duration.ofNanos(this.distributionStatisticConfig.getMaximumExpectedValueAsDouble().longValue())).minimumExpectedValue(Duration.ofNanos(this.distributionStatisticConfig.getMinimumExpectedValueAsDouble().longValue())).publishPercentiles(this.distributionStatisticConfig.getPercentiles()).publishPercentileHistogram(this.distributionStatisticConfig.isPercentileHistogram()).distributionStatisticBufferLength(this.distributionStatisticConfig.getBufferLength()).distributionStatisticExpiry(this.distributionStatisticConfig.getExpiry()).percentilePrecision(this.distributionStatisticConfig.getPercentilePrecision());
        double[] sloNanos = this.distributionStatisticConfig.getServiceLevelObjectiveBoundaries();
        if (sloNanos != null) {
            Duration[] slo = new Duration[sloNanos.length];
            for (int i = 0; i < sloNanos.length; ++i) {
                slo[i] = Duration.ofNanos((long)sloNanos[i]);
            }
            builder = builder.serviceLevelObjectives(slo);
        }
        return builder.register(registry);
    }

    static class CompositeSample
    extends LongTaskTimer.Sample {
        private final List<LongTaskTimer.Sample> samples;

        private CompositeSample(List<LongTaskTimer.Sample> samples) {
            this.samples = samples;
        }

        @Override
        public long stop() {
            return this.samples.stream().reduce(0L, (stopped, sample) -> sample.stop(), (s1, s2) -> s1);
        }

        @Override
        public double duration(TimeUnit unit) {
            return this.samples.stream().findAny().map(s -> s.duration(unit)).orElse(0.0);
        }
    }
}

