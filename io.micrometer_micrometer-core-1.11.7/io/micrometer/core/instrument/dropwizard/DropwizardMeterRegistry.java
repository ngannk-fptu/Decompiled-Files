/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codahale.metrics.Clock
 *  com.codahale.metrics.ExponentiallyDecayingReservoir
 *  com.codahale.metrics.Gauge
 *  com.codahale.metrics.Meter
 *  com.codahale.metrics.Metric
 *  com.codahale.metrics.MetricRegistry
 *  com.codahale.metrics.Reservoir
 *  com.codahale.metrics.Timer
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.common.util.internal.logging.WarnThenDebugLogger
 */
package io.micrometer.core.instrument.dropwizard;

import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Reservoir;
import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.internal.logging.WarnThenDebugLogger;
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
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.HistogramGauges;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.dropwizard.DropwizardClock;
import io.micrometer.core.instrument.dropwizard.DropwizardConfig;
import io.micrometer.core.instrument.dropwizard.DropwizardCounter;
import io.micrometer.core.instrument.dropwizard.DropwizardDistributionSummary;
import io.micrometer.core.instrument.dropwizard.DropwizardFunctionCounter;
import io.micrometer.core.instrument.dropwizard.DropwizardFunctionTimer;
import io.micrometer.core.instrument.dropwizard.DropwizardGauge;
import io.micrometer.core.instrument.dropwizard.DropwizardTimer;
import io.micrometer.core.instrument.internal.DefaultLongTaskTimer;
import io.micrometer.core.instrument.internal.DefaultMeter;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

public abstract class DropwizardMeterRegistry
extends MeterRegistry {
    private static final WarnThenDebugLogger logger = new WarnThenDebugLogger(DropwizardMeterRegistry.class);
    private final MetricRegistry registry;
    private final HierarchicalNameMapper nameMapper;
    private final DropwizardClock dropwizardClock;
    private final DropwizardConfig dropwizardConfig;
    private final AtomicBoolean warnLogged = new AtomicBoolean();

    public DropwizardMeterRegistry(DropwizardConfig config, MetricRegistry registry, HierarchicalNameMapper nameMapper, Clock clock) {
        super(clock);
        config.requireValid();
        this.dropwizardConfig = config;
        this.dropwizardClock = new DropwizardClock(clock);
        this.registry = registry;
        this.nameMapper = nameMapper;
        this.config().namingConvention(NamingConvention.camelCase).onMeterRemoved(this::onMeterRemoved);
    }

    private void onMeterRemoved(Meter meter) {
        this.registry.remove(this.hierarchicalName(meter.getId()));
    }

    public MetricRegistry getDropwizardRegistry() {
        return this.registry;
    }

    @Override
    protected Counter newCounter(Meter.Id id) {
        com.codahale.metrics.Meter meter = new com.codahale.metrics.Meter((com.codahale.metrics.Clock)this.dropwizardClock);
        this.registry.register(this.hierarchicalName(id), (Metric)meter);
        return new DropwizardCounter(id, meter);
    }

    @Override
    protected <T> Gauge newGauge(Meter.Id id, @Nullable T obj, ToDoubleFunction<T> valueFunction) {
        WeakReference ref = new WeakReference(obj);
        com.codahale.metrics.Gauge gauge = () -> {
            Object obj2 = ref.get();
            if (obj2 != null) {
                try {
                    return valueFunction.applyAsDouble(obj2);
                }
                catch (Throwable ex) {
                    logger.log(() -> "Failed to apply the value function for the gauge '" + id.getName() + "'.", ex);
                }
            }
            return this.nullGaugeValue();
        };
        this.registry.register(this.hierarchicalName(id), (Metric)gauge);
        return new DropwizardGauge(id, (com.codahale.metrics.Gauge<Double>)gauge);
    }

    @Override
    protected Timer newTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector) {
        DropwizardTimer timer = new DropwizardTimer(id, this.registry.timer(this.hierarchicalName(id), () -> new com.codahale.metrics.Timer((Reservoir)new ExponentiallyDecayingReservoir(), (com.codahale.metrics.Clock)this.dropwizardClock)), this.clock, distributionStatisticConfig, pauseDetector);
        HistogramGauges.registerWithCommonFormat(timer, (MeterRegistry)this);
        return timer;
    }

    @Override
    protected DistributionSummary newDistributionSummary(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, double scale) {
        DropwizardDistributionSummary summary = new DropwizardDistributionSummary(id, this.clock, this.registry.histogram(this.hierarchicalName(id)), distributionStatisticConfig, scale);
        HistogramGauges.registerWithCommonFormat(summary, (MeterRegistry)this);
        return summary;
    }

    @Override
    protected LongTaskTimer newLongTaskTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig) {
        DefaultLongTaskTimer ltt = new DefaultLongTaskTimer(id, this.clock, this.getBaseTimeUnit(), distributionStatisticConfig, false);
        this.registry.register(this.hierarchicalName(id.withTag(Statistic.ACTIVE_TASKS)), (Metric)((com.codahale.metrics.Gauge)ltt::activeTasks));
        this.registry.register(this.hierarchicalName(id.withTag(Statistic.DURATION)), (Metric)((com.codahale.metrics.Gauge)() -> ltt.duration(TimeUnit.NANOSECONDS)));
        this.registry.register(this.hierarchicalName(id.withTag(Statistic.MAX)), (Metric)((com.codahale.metrics.Gauge)() -> ltt.max(TimeUnit.NANOSECONDS)));
        HistogramGauges.registerWithCommonFormat(ltt, (MeterRegistry)this);
        return ltt;
    }

    @Override
    protected <T> FunctionTimer newFunctionTimer(Meter.Id id, T obj, ToLongFunction<T> countFunction, ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit) {
        DropwizardFunctionTimer<T> ft = new DropwizardFunctionTimer<T>(id, this.clock, obj, countFunction, totalTimeFunction, totalTimeFunctionUnit, this.getBaseTimeUnit());
        this.registry.register(this.hierarchicalName(id), (Metric)ft.getDropwizardMeter());
        return ft;
    }

    @Override
    protected <T> FunctionCounter newFunctionCounter(Meter.Id id, T obj, ToDoubleFunction<T> countFunction) {
        DropwizardFunctionCounter<T> fc = new DropwizardFunctionCounter<T>(id, this.clock, obj, countFunction);
        this.registry.register(this.hierarchicalName(id), (Metric)fc.getDropwizardMeter());
        return fc;
    }

    @Override
    protected Meter newMeter(Meter.Id id, Meter.Type type, Iterable<Measurement> measurements) {
        measurements.forEach(ms -> this.registry.register(this.hierarchicalName(id.withTag(ms.getStatistic())), (Metric)((com.codahale.metrics.Gauge)ms::getValue)));
        return new DefaultMeter(id, type, measurements);
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }

    private String hierarchicalName(Meter.Id id) {
        return this.nameMapper.toHierarchicalName(id, this.config().namingConvention());
    }

    @Override
    protected DistributionStatisticConfig defaultHistogramConfig() {
        return DistributionStatisticConfig.builder().expiry(this.dropwizardConfig.step()).build().merge(DistributionStatisticConfig.DEFAULT);
    }

    protected abstract Double nullGaugeValue();
}

