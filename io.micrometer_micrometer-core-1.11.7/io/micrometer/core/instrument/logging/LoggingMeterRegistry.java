/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.common.util.internal.logging.InternalLogger
 *  io.micrometer.common.util.internal.logging.InternalLoggerFactory
 */
package io.micrometer.core.instrument.logging;

import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.logging.LoggingRegistryConfig;
import io.micrometer.core.instrument.step.StepDistributionSummary;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.step.StepTimer;
import io.micrometer.core.instrument.util.DoubleFormat;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import io.micrometer.core.instrument.util.TimeUtils;
import java.time.Duration;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Incubating(since="1.1.0")
public class LoggingMeterRegistry
extends StepMeterRegistry {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(LoggingMeterRegistry.class);
    private final LoggingRegistryConfig config;
    private final Consumer<String> loggingSink;
    private final Function<Meter, String> meterIdPrinter;

    public LoggingMeterRegistry() {
        this(LoggingRegistryConfig.DEFAULT, Clock.SYSTEM);
    }

    public LoggingMeterRegistry(LoggingRegistryConfig config, Clock clock) {
        this(config, clock, arg_0 -> ((InternalLogger)log).info(arg_0));
    }

    public LoggingMeterRegistry(Consumer<String> loggingSink) {
        this(LoggingRegistryConfig.DEFAULT, Clock.SYSTEM, loggingSink);
    }

    public LoggingMeterRegistry(LoggingRegistryConfig config, Clock clock, Consumer<String> loggingSink) {
        this(config, clock, new NamedThreadFactory("logging-metrics-publisher"), loggingSink, null);
    }

    private LoggingMeterRegistry(LoggingRegistryConfig config, Clock clock, ThreadFactory threadFactory, Consumer<String> loggingSink, @Nullable Function<Meter, String> meterIdPrinter) {
        super(config, clock);
        this.config = config;
        this.loggingSink = loggingSink;
        this.meterIdPrinter = meterIdPrinter != null ? meterIdPrinter : this.defaultMeterIdPrinter();
        this.config().namingConvention(NamingConvention.dot);
        this.start(threadFactory);
    }

    private Function<Meter, String> defaultMeterIdPrinter() {
        return meter -> this.getConventionName(meter.getId()) + this.getConventionTags(meter.getId()).stream().map(t -> t.getKey() + "=" + t.getValue()).collect(Collectors.joining(",", "{", "}"));
    }

    @Override
    protected void publish() {
        if (this.config.enabled()) {
            this.getMeters().stream().sorted((m1, m2) -> {
                int typeComp = m1.getId().getType().compareTo(m2.getId().getType());
                if (typeComp == 0) {
                    return m1.getId().getName().compareTo(m2.getId().getName());
                }
                return typeComp;
            }).forEach(m -> {
                Printer print = new Printer((Meter)m);
                m.use(gauge -> this.loggingSink.accept(print.id() + " value=" + print.value(gauge.value())), counter -> {
                    double count = counter.count();
                    if (!this.config.logInactive() && count == 0.0) {
                        return;
                    }
                    this.loggingSink.accept(print.id() + " throughput=" + print.rate(count));
                }, timer -> {
                    HistogramSnapshot snapshot = timer.takeSnapshot();
                    long count = snapshot.count();
                    if (!this.config.logInactive() && count == 0L) {
                        return;
                    }
                    this.loggingSink.accept(print.id() + " throughput=" + print.unitlessRate(count) + " mean=" + print.time(snapshot.mean(this.getBaseTimeUnit())) + " max=" + print.time(snapshot.max(this.getBaseTimeUnit())));
                }, summary -> {
                    HistogramSnapshot snapshot = summary.takeSnapshot();
                    long count = snapshot.count();
                    if (!this.config.logInactive() && count == 0L) {
                        return;
                    }
                    this.loggingSink.accept(print.id() + " throughput=" + print.unitlessRate(count) + " mean=" + print.value(snapshot.mean()) + " max=" + print.value(snapshot.max()));
                }, longTaskTimer -> {
                    int activeTasks = longTaskTimer.activeTasks();
                    if (!this.config.logInactive() && activeTasks == 0) {
                        return;
                    }
                    this.loggingSink.accept(print.id() + " active=" + print.value(activeTasks) + " duration=" + print.time(longTaskTimer.duration(this.getBaseTimeUnit())));
                }, timeGauge -> {
                    double value = timeGauge.value(this.getBaseTimeUnit());
                    if (!this.config.logInactive() && value == 0.0) {
                        return;
                    }
                    this.loggingSink.accept(print.id() + " value=" + print.time(value));
                }, counter -> {
                    double count = counter.count();
                    if (!this.config.logInactive() && count == 0.0) {
                        return;
                    }
                    this.loggingSink.accept(print.id() + " throughput=" + print.rate(count));
                }, timer -> {
                    double count = timer.count();
                    if (!this.config.logInactive() && count == 0.0) {
                        return;
                    }
                    this.loggingSink.accept(print.id() + " throughput=" + print.rate(count) + " mean=" + print.time(timer.mean(this.getBaseTimeUnit())));
                }, meter -> this.loggingSink.accept(this.writeMeter((Meter)meter, print)));
            });
        }
    }

    String writeMeter(Meter meter, Printer print) {
        return StreamSupport.stream(meter.measure().spliterator(), false).map(ms -> {
            String msLine = ms.getStatistic().getTagValueRepresentation() + "=";
            switch (ms.getStatistic()) {
                case TOTAL: 
                case MAX: 
                case VALUE: {
                    return msLine + print.value(ms.getValue());
                }
                case TOTAL_TIME: 
                case DURATION: {
                    return msLine + print.time(ms.getValue());
                }
                case COUNT: {
                    return "throughput=" + print.rate(ms.getValue());
                }
            }
            return msLine + DoubleFormat.decimalOrNan(ms.getValue());
        }).collect(Collectors.joining(", ", print.id() + " ", ""));
    }

    @Override
    protected Timer newTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector) {
        return new StepTimer(id, this.clock, distributionStatisticConfig, pauseDetector, this.getBaseTimeUnit(), this.config.step().toMillis(), false);
    }

    @Override
    protected DistributionSummary newDistributionSummary(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, double scale) {
        return new StepDistributionSummary(id, this.clock, distributionStatisticConfig, scale, this.config.step().toMillis(), false);
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }

    public static Builder builder(LoggingRegistryConfig config) {
        return new Builder(config);
    }

    static /* synthetic */ InternalLogger access$200() {
        return log;
    }

    class Printer {
        private final Meter meter;

        Printer(Meter meter) {
            this.meter = meter;
        }

        String id() {
            return (String)LoggingMeterRegistry.this.meterIdPrinter.apply(this.meter);
        }

        String time(double time) {
            return TimeUtils.format(Duration.ofNanos((long)TimeUtils.convert(time, LoggingMeterRegistry.this.getBaseTimeUnit(), TimeUnit.NANOSECONDS)));
        }

        String rate(double rate) {
            return this.humanReadableBaseUnit(rate / (double)LoggingMeterRegistry.this.config.step().getSeconds()) + "/s";
        }

        String unitlessRate(double rate) {
            return DoubleFormat.decimalOrNan(rate / (double)LoggingMeterRegistry.this.config.step().getSeconds()) + "/s";
        }

        String value(double value) {
            return this.humanReadableBaseUnit(value);
        }

        String humanReadableByteCount(double bytes) {
            int unit = 1024;
            if (bytes < (double)unit || Double.isNaN(bytes)) {
                return DoubleFormat.decimalOrNan(bytes) + " B";
            }
            int exp = (int)(Math.log(bytes) / Math.log(unit));
            String pre = "KMGTPE".charAt(exp - 1) + "i";
            return DoubleFormat.decimalOrNan(bytes / Math.pow(unit, exp)) + " " + pre + "B";
        }

        String humanReadableBaseUnit(double value) {
            String baseUnit = this.meter.getId().getBaseUnit();
            if ("bytes".equals(baseUnit)) {
                return this.humanReadableByteCount(value);
            }
            return DoubleFormat.decimalOrNan(value) + (baseUnit != null ? " " + baseUnit : "");
        }
    }

    public static class Builder {
        private final LoggingRegistryConfig config;
        private Clock clock = Clock.SYSTEM;
        private ThreadFactory threadFactory = new NamedThreadFactory("logging-metrics-publisher");
        private Consumer<String> loggingSink = arg_0 -> ((InternalLogger)LoggingMeterRegistry.access$200()).info(arg_0);
        @Nullable
        private Function<Meter, String> meterIdPrinter;

        Builder(LoggingRegistryConfig config) {
            this.config = config;
        }

        public Builder clock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public Builder threadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        public Builder loggingSink(Consumer<String> loggingSink) {
            this.loggingSink = loggingSink;
            return this;
        }

        public Builder meterIdPrinter(Function<Meter, String> meterIdPrinter) {
            this.meterIdPrinter = meterIdPrinter;
            return this;
        }

        public LoggingMeterRegistry build() {
            return new LoggingMeterRegistry(this.config, this.clock, this.threadFactory, this.loggingSink, this.meterIdPrinter);
        }
    }
}

