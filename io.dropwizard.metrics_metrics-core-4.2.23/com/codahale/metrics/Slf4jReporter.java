/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.Marker
 */
package com.codahale.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Counting;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metered;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricAttribute;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class Slf4jReporter
extends ScheduledReporter {
    private final LoggerProxy loggerProxy;
    private final Marker marker;
    private final String prefix;

    public static Builder forRegistry(MetricRegistry registry) {
        return new Builder(registry);
    }

    private Slf4jReporter(MetricRegistry registry, LoggerProxy loggerProxy, Marker marker, String prefix, TimeUnit rateUnit, TimeUnit durationUnit, MetricFilter filter, ScheduledExecutorService executor, boolean shutdownExecutorOnStop, Set<MetricAttribute> disabledMetricAttributes) {
        super(registry, "logger-reporter", filter, rateUnit, durationUnit, executor, shutdownExecutorOnStop, disabledMetricAttributes);
        this.loggerProxy = loggerProxy;
        this.marker = marker;
        this.prefix = prefix;
    }

    @Override
    public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
        if (this.loggerProxy.isEnabled(this.marker)) {
            StringBuilder b = new StringBuilder();
            for (Map.Entry<String, Gauge> entry : gauges.entrySet()) {
                this.logGauge(b, entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, Metric> entry : counters.entrySet()) {
                this.logCounter(b, entry.getKey(), (Counter)entry.getValue());
            }
            for (Map.Entry<String, Metric> entry : histograms.entrySet()) {
                this.logHistogram(b, entry.getKey(), (Histogram)entry.getValue());
            }
            for (Map.Entry<String, Metric> entry : meters.entrySet()) {
                this.logMeter(b, entry.getKey(), (Meter)entry.getValue());
            }
            for (Map.Entry<String, Metric> entry : timers.entrySet()) {
                this.logTimer(b, entry.getKey(), (Timer)entry.getValue());
            }
        }
    }

    private void logTimer(StringBuilder b, String name, Timer timer) {
        Snapshot snapshot = timer.getSnapshot();
        b.setLength(0);
        b.append("type=TIMER");
        this.append(b, "name", this.prefix(name));
        this.appendCountIfEnabled(b, timer);
        this.appendLongDurationIfEnabled(b, MetricAttribute.MIN, snapshot::getMin);
        this.appendLongDurationIfEnabled(b, MetricAttribute.MAX, snapshot::getMax);
        this.appendDoubleDurationIfEnabled(b, MetricAttribute.MEAN, snapshot::getMean);
        this.appendDoubleDurationIfEnabled(b, MetricAttribute.STDDEV, snapshot::getStdDev);
        this.appendDoubleDurationIfEnabled(b, MetricAttribute.P50, snapshot::getMedian);
        this.appendDoubleDurationIfEnabled(b, MetricAttribute.P75, snapshot::get75thPercentile);
        this.appendDoubleDurationIfEnabled(b, MetricAttribute.P95, snapshot::get95thPercentile);
        this.appendDoubleDurationIfEnabled(b, MetricAttribute.P98, snapshot::get98thPercentile);
        this.appendDoubleDurationIfEnabled(b, MetricAttribute.P99, snapshot::get99thPercentile);
        this.appendDoubleDurationIfEnabled(b, MetricAttribute.P999, snapshot::get999thPercentile);
        this.appendMetered(b, timer);
        this.append(b, "rate_unit", this.getRateUnit());
        this.append(b, "duration_unit", this.getDurationUnit());
        this.loggerProxy.log(this.marker, b.toString());
    }

    private void logMeter(StringBuilder b, String name, Meter meter) {
        b.setLength(0);
        b.append("type=METER");
        this.append(b, "name", this.prefix(name));
        this.appendCountIfEnabled(b, meter);
        this.appendMetered(b, meter);
        this.append(b, "rate_unit", this.getRateUnit());
        this.loggerProxy.log(this.marker, b.toString());
    }

    private void logHistogram(StringBuilder b, String name, Histogram histogram) {
        Snapshot snapshot = histogram.getSnapshot();
        b.setLength(0);
        b.append("type=HISTOGRAM");
        this.append(b, "name", this.prefix(name));
        this.appendCountIfEnabled(b, histogram);
        this.appendLongIfEnabled(b, MetricAttribute.MIN, snapshot::getMin);
        this.appendLongIfEnabled(b, MetricAttribute.MAX, snapshot::getMax);
        this.appendDoubleIfEnabled(b, MetricAttribute.MEAN, snapshot::getMean);
        this.appendDoubleIfEnabled(b, MetricAttribute.STDDEV, snapshot::getStdDev);
        this.appendDoubleIfEnabled(b, MetricAttribute.P50, snapshot::getMedian);
        this.appendDoubleIfEnabled(b, MetricAttribute.P75, snapshot::get75thPercentile);
        this.appendDoubleIfEnabled(b, MetricAttribute.P95, snapshot::get95thPercentile);
        this.appendDoubleIfEnabled(b, MetricAttribute.P98, snapshot::get98thPercentile);
        this.appendDoubleIfEnabled(b, MetricAttribute.P99, snapshot::get99thPercentile);
        this.appendDoubleIfEnabled(b, MetricAttribute.P999, snapshot::get999thPercentile);
        this.loggerProxy.log(this.marker, b.toString());
    }

    private void logCounter(StringBuilder b, String name, Counter counter) {
        b.setLength(0);
        b.append("type=COUNTER");
        this.append(b, "name", this.prefix(name));
        this.append(b, MetricAttribute.COUNT.getCode(), counter.getCount());
        this.loggerProxy.log(this.marker, b.toString());
    }

    private void logGauge(StringBuilder b, String name, Gauge<?> gauge) {
        b.setLength(0);
        b.append("type=GAUGE");
        this.append(b, "name", this.prefix(name));
        this.append(b, "value", gauge.getValue());
        this.loggerProxy.log(this.marker, b.toString());
    }

    private void appendLongDurationIfEnabled(StringBuilder b, MetricAttribute metricAttribute, Supplier<Long> durationSupplier) {
        if (!this.getDisabledMetricAttributes().contains((Object)metricAttribute)) {
            this.append(b, metricAttribute.getCode(), this.convertDuration(durationSupplier.get().longValue()));
        }
    }

    private void appendDoubleDurationIfEnabled(StringBuilder b, MetricAttribute metricAttribute, Supplier<Double> durationSupplier) {
        if (!this.getDisabledMetricAttributes().contains((Object)metricAttribute)) {
            this.append(b, metricAttribute.getCode(), this.convertDuration(durationSupplier.get()));
        }
    }

    private void appendLongIfEnabled(StringBuilder b, MetricAttribute metricAttribute, Supplier<Long> valueSupplier) {
        if (!this.getDisabledMetricAttributes().contains((Object)metricAttribute)) {
            this.append(b, metricAttribute.getCode(), valueSupplier.get());
        }
    }

    private void appendDoubleIfEnabled(StringBuilder b, MetricAttribute metricAttribute, Supplier<Double> valueSupplier) {
        if (!this.getDisabledMetricAttributes().contains((Object)metricAttribute)) {
            this.append(b, metricAttribute.getCode(), valueSupplier.get());
        }
    }

    private void appendCountIfEnabled(StringBuilder b, Counting counting) {
        if (!this.getDisabledMetricAttributes().contains((Object)MetricAttribute.COUNT)) {
            this.append(b, MetricAttribute.COUNT.getCode(), counting.getCount());
        }
    }

    private void appendMetered(StringBuilder b, Metered meter) {
        this.appendRateIfEnabled(b, MetricAttribute.M1_RATE, meter::getOneMinuteRate);
        this.appendRateIfEnabled(b, MetricAttribute.M5_RATE, meter::getFiveMinuteRate);
        this.appendRateIfEnabled(b, MetricAttribute.M15_RATE, meter::getFifteenMinuteRate);
        this.appendRateIfEnabled(b, MetricAttribute.MEAN_RATE, meter::getMeanRate);
    }

    private void appendRateIfEnabled(StringBuilder b, MetricAttribute metricAttribute, Supplier<Double> rateSupplier) {
        if (!this.getDisabledMetricAttributes().contains((Object)metricAttribute)) {
            this.append(b, metricAttribute.getCode(), this.convertRate(rateSupplier.get()));
        }
    }

    private void append(StringBuilder b, String key, long value) {
        b.append(", ").append(key).append('=').append(value);
    }

    private void append(StringBuilder b, String key, double value) {
        b.append(", ").append(key).append('=').append(value);
    }

    private void append(StringBuilder b, String key, String value) {
        b.append(", ").append(key).append('=').append(value);
    }

    private void append(StringBuilder b, String key, Object value) {
        b.append(", ").append(key).append('=').append(value);
    }

    @Override
    protected String getRateUnit() {
        return "events/" + super.getRateUnit();
    }

    private String prefix(String ... components) {
        return MetricRegistry.name(this.prefix, components);
    }

    static abstract class LoggerProxy {
        protected final Logger logger;

        public LoggerProxy(Logger logger) {
            this.logger = logger;
        }

        abstract void log(Marker var1, String var2);

        abstract boolean isEnabled(Marker var1);
    }

    public static class Builder {
        private final MetricRegistry registry;
        private Logger logger;
        private LoggingLevel loggingLevel;
        private Marker marker;
        private String prefix;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricFilter filter;
        private ScheduledExecutorService executor;
        private boolean shutdownExecutorOnStop;
        private Set<MetricAttribute> disabledMetricAttributes;

        private Builder(MetricRegistry registry) {
            this.registry = registry;
            this.logger = LoggerFactory.getLogger((String)"metrics");
            this.marker = null;
            this.prefix = "";
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
            this.loggingLevel = LoggingLevel.INFO;
            this.executor = null;
            this.shutdownExecutorOnStop = true;
            this.disabledMetricAttributes = Collections.emptySet();
        }

        public Builder shutdownExecutorOnStop(boolean shutdownExecutorOnStop) {
            this.shutdownExecutorOnStop = shutdownExecutorOnStop;
            return this;
        }

        public Builder scheduleOn(ScheduledExecutorService executor) {
            this.executor = executor;
            return this;
        }

        public Builder outputTo(Logger logger) {
            this.logger = logger;
            return this;
        }

        public Builder markWith(Marker marker) {
            this.marker = marker;
            return this;
        }

        public Builder prefixedWith(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        public Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        public Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }

        public Builder withLoggingLevel(LoggingLevel loggingLevel) {
            this.loggingLevel = loggingLevel;
            return this;
        }

        public Builder disabledMetricAttributes(Set<MetricAttribute> disabledMetricAttributes) {
            this.disabledMetricAttributes = disabledMetricAttributes;
            return this;
        }

        public Slf4jReporter build() {
            LoggerProxy loggerProxy;
            switch (this.loggingLevel) {
                case TRACE: {
                    loggerProxy = new TraceLoggerProxy(this.logger);
                    break;
                }
                case INFO: {
                    loggerProxy = new InfoLoggerProxy(this.logger);
                    break;
                }
                case WARN: {
                    loggerProxy = new WarnLoggerProxy(this.logger);
                    break;
                }
                case ERROR: {
                    loggerProxy = new ErrorLoggerProxy(this.logger);
                    break;
                }
                default: {
                    loggerProxy = new DebugLoggerProxy(this.logger);
                }
            }
            return new Slf4jReporter(this.registry, loggerProxy, this.marker, this.prefix, this.rateUnit, this.durationUnit, this.filter, this.executor, this.shutdownExecutorOnStop, this.disabledMetricAttributes);
        }
    }

    private static class ErrorLoggerProxy
    extends LoggerProxy {
        public ErrorLoggerProxy(Logger logger) {
            super(logger);
        }

        @Override
        public void log(Marker marker, String format) {
            this.logger.error(marker, format);
        }

        @Override
        public boolean isEnabled(Marker marker) {
            return this.logger.isErrorEnabled(marker);
        }
    }

    private static class WarnLoggerProxy
    extends LoggerProxy {
        public WarnLoggerProxy(Logger logger) {
            super(logger);
        }

        @Override
        public void log(Marker marker, String format) {
            this.logger.warn(marker, format);
        }

        @Override
        public boolean isEnabled(Marker marker) {
            return this.logger.isWarnEnabled(marker);
        }
    }

    private static class InfoLoggerProxy
    extends LoggerProxy {
        public InfoLoggerProxy(Logger logger) {
            super(logger);
        }

        @Override
        public void log(Marker marker, String format) {
            this.logger.info(marker, format);
        }

        @Override
        public boolean isEnabled(Marker marker) {
            return this.logger.isInfoEnabled(marker);
        }
    }

    private static class TraceLoggerProxy
    extends LoggerProxy {
        public TraceLoggerProxy(Logger logger) {
            super(logger);
        }

        @Override
        public void log(Marker marker, String format) {
            this.logger.trace(marker, format);
        }

        @Override
        public boolean isEnabled(Marker marker) {
            return this.logger.isTraceEnabled(marker);
        }
    }

    private static class DebugLoggerProxy
    extends LoggerProxy {
        public DebugLoggerProxy(Logger logger) {
            super(logger);
        }

        @Override
        public void log(Marker marker, String format) {
            this.logger.debug(marker, format);
        }

        @Override
        public boolean isEnabled(Marker marker) {
            return this.logger.isDebugEnabled(marker);
        }
    }

    public static enum LoggingLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR;

    }
}

