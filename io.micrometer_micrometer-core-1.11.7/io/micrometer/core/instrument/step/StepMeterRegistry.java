/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.common.util.internal.logging.InternalLogger
 *  io.micrometer.common.util.internal.logging.InternalLoggerFactory
 */
package io.micrometer.core.instrument.step;

import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
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
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.HistogramGauges;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.internal.DefaultGauge;
import io.micrometer.core.instrument.internal.DefaultLongTaskTimer;
import io.micrometer.core.instrument.internal.DefaultMeter;
import io.micrometer.core.instrument.push.PushMeterRegistry;
import io.micrometer.core.instrument.step.StepCounter;
import io.micrometer.core.instrument.step.StepDistributionSummary;
import io.micrometer.core.instrument.step.StepFunctionCounter;
import io.micrometer.core.instrument.step.StepFunctionTimer;
import io.micrometer.core.instrument.step.StepMeter;
import io.micrometer.core.instrument.step.StepRegistryConfig;
import io.micrometer.core.instrument.step.StepTimer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

public abstract class StepMeterRegistry
extends PushMeterRegistry {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(StepMeterRegistry.class);
    private final StepRegistryConfig config;
    @Nullable
    private ScheduledExecutorService meterPollingService;
    private long lastMeterRolloverStartTime = -1L;

    public StepMeterRegistry(StepRegistryConfig config, Clock clock) {
        super(config, clock);
        this.config = config;
    }

    @Override
    protected <T> Gauge newGauge(Meter.Id id, @Nullable T obj, ToDoubleFunction<T> valueFunction) {
        return new DefaultGauge<T>(id, obj, valueFunction);
    }

    @Override
    protected Counter newCounter(Meter.Id id) {
        return new StepCounter(id, this.clock, this.config.step().toMillis());
    }

    @Override
    protected LongTaskTimer newLongTaskTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig) {
        DefaultLongTaskTimer ltt = new DefaultLongTaskTimer(id, this.clock, this.getBaseTimeUnit(), distributionStatisticConfig, false);
        HistogramGauges.registerWithCommonFormat(ltt, (MeterRegistry)this);
        return ltt;
    }

    @Override
    protected Timer newTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector) {
        StepTimer timer = new StepTimer(id, this.clock, distributionStatisticConfig, pauseDetector, this.getBaseTimeUnit(), this.config.step().toMillis(), false);
        HistogramGauges.registerWithCommonFormat(timer, (MeterRegistry)this);
        return timer;
    }

    @Override
    protected DistributionSummary newDistributionSummary(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, double scale) {
        StepDistributionSummary summary = new StepDistributionSummary(id, this.clock, distributionStatisticConfig, scale, this.config.step().toMillis(), false);
        HistogramGauges.registerWithCommonFormat(summary, (MeterRegistry)this);
        return summary;
    }

    @Override
    protected <T> FunctionTimer newFunctionTimer(Meter.Id id, T obj, ToLongFunction<T> countFunction, ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit) {
        return new StepFunctionTimer<T>(id, this.clock, this.config.step().toMillis(), obj, countFunction, totalTimeFunction, totalTimeFunctionUnit, this.getBaseTimeUnit());
    }

    @Override
    protected <T> FunctionCounter newFunctionCounter(Meter.Id id, T obj, ToDoubleFunction<T> countFunction) {
        return new StepFunctionCounter<T>(id, this.clock, this.config.step().toMillis(), obj, countFunction);
    }

    @Override
    protected Meter newMeter(Meter.Id id, Meter.Type type, Iterable<Measurement> measurements) {
        return new DefaultMeter(id, type, measurements);
    }

    @Override
    protected DistributionStatisticConfig defaultHistogramConfig() {
        return DistributionStatisticConfig.builder().expiry(this.config.step()).build().merge(DistributionStatisticConfig.DEFAULT);
    }

    @Override
    public void start(ThreadFactory threadFactory) {
        super.start(threadFactory);
        if (this.config.enabled()) {
            this.meterPollingService = Executors.newSingleThreadScheduledExecutor(threadFactory);
            this.meterPollingService.scheduleAtFixedRate(this::pollMetersToRollover, this.getInitialDelay(), this.config.step().toMillis(), TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (this.meterPollingService != null) {
            this.meterPollingService.shutdown();
        }
    }

    @Override
    public void close() {
        this.stop();
        if (this.config.enabled() && !this.isClosed()) {
            if (this.shouldPublishDataForLastStep() && !this.isPublishing()) {
                try {
                    this.publish();
                }
                catch (Throwable e) {
                    logger.warn("Unexpected exception thrown while publishing metrics for " + this.getClass().getSimpleName(), e);
                }
            } else if (this.isPublishing()) {
                this.waitForInProgressScheduledPublish();
            }
            this.closingRolloverStepMeters();
        }
        super.close();
    }

    private boolean shouldPublishDataForLastStep() {
        long lastPolledStep;
        if (this.lastMeterRolloverStartTime < 0L) {
            return false;
        }
        long lastPublishedStep = this.getLastScheduledPublishStartTime() / this.config.step().toMillis();
        return lastPublishedStep < (lastPolledStep = this.lastMeterRolloverStartTime / this.config.step().toMillis());
    }

    private void closingRolloverStepMeters() {
        this.getMeters().stream().filter(StepMeter.class::isInstance).map(StepMeter.class::cast).forEach(StepMeter::_closingRollover);
    }

    void pollMetersToRollover() {
        this.lastMeterRolloverStartTime = this.clock.wallTime();
        this.getMeters().forEach(m -> m.match(gauge -> null, Counter::count, Timer::count, DistributionSummary::count, meter -> null, meter -> null, FunctionCounter::count, FunctionTimer::count, meter -> null));
    }

    private long getInitialDelay() {
        long stepMillis = this.config.step().toMillis();
        return stepMillis - this.clock.wallTime() % stepMillis + 1L;
    }
}

