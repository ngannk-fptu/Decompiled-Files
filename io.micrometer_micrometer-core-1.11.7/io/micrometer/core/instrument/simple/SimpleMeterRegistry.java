/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.simple;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.AbstractDistributionSummary;
import io.micrometer.core.instrument.AbstractTimer;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Statistic;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.cumulative.CumulativeCounter;
import io.micrometer.core.instrument.cumulative.CumulativeDistributionSummary;
import io.micrometer.core.instrument.cumulative.CumulativeFunctionCounter;
import io.micrometer.core.instrument.cumulative.CumulativeFunctionTimer;
import io.micrometer.core.instrument.cumulative.CumulativeTimer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.HistogramGauges;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.internal.DefaultGauge;
import io.micrometer.core.instrument.internal.DefaultLongTaskTimer;
import io.micrometer.core.instrument.internal.DefaultMeter;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.step.StepCounter;
import io.micrometer.core.instrument.step.StepDistributionSummary;
import io.micrometer.core.instrument.step.StepFunctionCounter;
import io.micrometer.core.instrument.step.StepFunctionTimer;
import io.micrometer.core.instrument.step.StepTimer;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SimpleMeterRegistry
extends MeterRegistry {
    private final SimpleConfig config;

    public SimpleMeterRegistry() {
        this(SimpleConfig.DEFAULT, Clock.SYSTEM);
    }

    public SimpleMeterRegistry(SimpleConfig config, Clock clock) {
        super(clock);
        config.requireValid();
        this.config = config;
    }

    @Override
    protected DistributionSummary newDistributionSummary(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, double scale) {
        AbstractDistributionSummary summary;
        DistributionStatisticConfig merged = distributionStatisticConfig.merge(DistributionStatisticConfig.builder().expiry(this.config.step()).build());
        switch (this.config.mode()) {
            case CUMULATIVE: {
                summary = new CumulativeDistributionSummary(id, this.clock, merged, scale, false);
                break;
            }
            default: {
                summary = new StepDistributionSummary(id, this.clock, merged, scale, this.config.step().toMillis(), false);
            }
        }
        HistogramGauges.registerWithCommonFormat(summary, (MeterRegistry)this);
        return summary;
    }

    @Override
    protected Meter newMeter(Meter.Id id, Meter.Type type, Iterable<Measurement> measurements) {
        return new DefaultMeter(id, type, measurements);
    }

    @Override
    protected Timer newTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector) {
        AbstractTimer timer;
        DistributionStatisticConfig merged = distributionStatisticConfig.merge(DistributionStatisticConfig.builder().expiry(this.config.step()).build());
        switch (this.config.mode()) {
            case CUMULATIVE: {
                timer = new CumulativeTimer(id, this.clock, merged, pauseDetector, this.getBaseTimeUnit(), false);
                break;
            }
            default: {
                timer = new StepTimer(id, this.clock, merged, pauseDetector, this.getBaseTimeUnit(), this.config.step().toMillis(), false);
            }
        }
        HistogramGauges.registerWithCommonFormat(timer, (MeterRegistry)this);
        return timer;
    }

    @Override
    protected <T> Gauge newGauge(Meter.Id id, @Nullable T obj, ToDoubleFunction<T> valueFunction) {
        return new DefaultGauge<T>(id, obj, valueFunction);
    }

    @Override
    protected Counter newCounter(Meter.Id id) {
        switch (this.config.mode()) {
            case CUMULATIVE: {
                return new CumulativeCounter(id);
            }
        }
        return new StepCounter(id, this.clock, this.config.step().toMillis());
    }

    @Override
    protected LongTaskTimer newLongTaskTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig) {
        DefaultLongTaskTimer ltt = new DefaultLongTaskTimer(id, this.clock, this.getBaseTimeUnit(), distributionStatisticConfig, false);
        HistogramGauges.registerWithCommonFormat(ltt, (MeterRegistry)this);
        return ltt;
    }

    @Override
    protected <T> FunctionTimer newFunctionTimer(Meter.Id id, T obj, ToLongFunction<T> countFunction, ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit) {
        switch (this.config.mode()) {
            case CUMULATIVE: {
                return new CumulativeFunctionTimer<T>(id, obj, countFunction, totalTimeFunction, totalTimeFunctionUnit, this.getBaseTimeUnit());
            }
        }
        return new StepFunctionTimer<T>(id, this.clock, this.config.step().toMillis(), obj, countFunction, totalTimeFunction, totalTimeFunctionUnit, this.getBaseTimeUnit());
    }

    @Override
    protected <T> FunctionCounter newFunctionCounter(Meter.Id id, T obj, ToDoubleFunction<T> countFunction) {
        switch (this.config.mode()) {
            case CUMULATIVE: {
                return new CumulativeFunctionCounter<T>(id, obj, countFunction);
            }
        }
        return new StepFunctionCounter<T>(id, this.clock, this.config.step().toMillis(), obj, countFunction);
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.SECONDS;
    }

    @Override
    protected DistributionStatisticConfig defaultHistogramConfig() {
        return DistributionStatisticConfig.builder().expiry(this.config.step()).build().merge(DistributionStatisticConfig.DEFAULT);
    }

    @Incubating(since="1.9.0")
    public String getMetersAsString() {
        return this.getMeters().stream().sorted(Comparator.comparing(meter -> meter.getId().getName())).map(this::toString).collect(Collectors.joining("\n"));
    }

    private String toString(Meter meter) {
        Meter.Id id = meter.getId();
        String tags = id.getTags().stream().map(this::toString).collect(Collectors.joining(", "));
        String baseUnit = id.getBaseUnit();
        String meterUnitSuffix = baseUnit != null ? " " + baseUnit : "";
        String measurements = StreamSupport.stream(meter.measure().spliterator(), false).map(measurement -> this.toString((Measurement)measurement, meterUnitSuffix)).collect(Collectors.joining(", "));
        return String.format("%s(%s)[%s]; %s", new Object[]{id.getName(), id.getType(), tags, measurements});
    }

    private String toString(Tag tag) {
        return String.format("%s='%s'", tag.getKey(), tag.getValue());
    }

    private String toString(Measurement measurement, String meterUnitSuffix) {
        Statistic statistic = measurement.getStatistic();
        return String.format("%s=%s%s", statistic.toString().toLowerCase(), measurement.getValue(), this.getUnitSuffix(statistic, meterUnitSuffix));
    }

    private String getUnitSuffix(Statistic statistic, String meterUnitSuffix) {
        switch (statistic) {
            case DURATION: 
            case TOTAL_TIME: 
            case TOTAL: 
            case MAX: 
            case VALUE: {
                return meterUnitSuffix;
            }
        }
        return "";
    }
}

