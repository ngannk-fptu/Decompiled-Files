/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Clock;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricAttribute;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConsoleReporter
extends ScheduledReporter {
    private static final int CONSOLE_WIDTH = 80;
    private final PrintStream output;
    private final Locale locale;
    private final Clock clock;
    private final DateFormat dateFormat;

    public static Builder forRegistry(MetricRegistry registry) {
        return new Builder(registry);
    }

    private ConsoleReporter(MetricRegistry registry, PrintStream output, Locale locale, Clock clock, TimeZone timeZone, TimeUnit rateUnit, TimeUnit durationUnit, MetricFilter filter, ScheduledExecutorService executor, boolean shutdownExecutorOnStop, Set<MetricAttribute> disabledMetricAttributes) {
        super(registry, "console-reporter", filter, rateUnit, durationUnit, executor, shutdownExecutorOnStop, disabledMetricAttributes);
        this.output = output;
        this.locale = locale;
        this.clock = clock;
        this.dateFormat = DateFormat.getDateTimeInstance(3, 2, locale);
        this.dateFormat.setTimeZone(timeZone);
    }

    @Override
    public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
        String dateTime = this.dateFormat.format(new Date(this.clock.getTime()));
        this.printWithBanner(dateTime, '=');
        this.output.println();
        if (!gauges.isEmpty()) {
            this.printWithBanner("-- Gauges", '-');
            for (Map.Entry<String, Metric> entry : gauges.entrySet()) {
                this.output.println(entry.getKey());
                this.printGauge((Gauge)entry.getValue());
            }
            this.output.println();
        }
        if (!counters.isEmpty()) {
            this.printWithBanner("-- Counters", '-');
            for (Map.Entry<String, Metric> entry : counters.entrySet()) {
                this.output.println(entry.getKey());
                this.printCounter(entry);
            }
            this.output.println();
        }
        if (!histograms.isEmpty()) {
            this.printWithBanner("-- Histograms", '-');
            for (Map.Entry<String, Metric> entry : histograms.entrySet()) {
                this.output.println(entry.getKey());
                this.printHistogram((Histogram)entry.getValue());
            }
            this.output.println();
        }
        if (!meters.isEmpty()) {
            this.printWithBanner("-- Meters", '-');
            for (Map.Entry<String, Metric> entry : meters.entrySet()) {
                this.output.println(entry.getKey());
                this.printMeter((Meter)entry.getValue());
            }
            this.output.println();
        }
        if (!timers.isEmpty()) {
            this.printWithBanner("-- Timers", '-');
            for (Map.Entry<String, Metric> entry : timers.entrySet()) {
                this.output.println(entry.getKey());
                this.printTimer((Timer)entry.getValue());
            }
            this.output.println();
        }
        this.output.println();
        this.output.flush();
    }

    private void printMeter(Meter meter) {
        this.printIfEnabled(MetricAttribute.COUNT, String.format(this.locale, "             count = %d", meter.getCount()));
        this.printIfEnabled(MetricAttribute.MEAN_RATE, String.format(this.locale, "         mean rate = %2.2f events/%s", this.convertRate(meter.getMeanRate()), this.getRateUnit()));
        this.printIfEnabled(MetricAttribute.M1_RATE, String.format(this.locale, "     1-minute rate = %2.2f events/%s", this.convertRate(meter.getOneMinuteRate()), this.getRateUnit()));
        this.printIfEnabled(MetricAttribute.M5_RATE, String.format(this.locale, "     5-minute rate = %2.2f events/%s", this.convertRate(meter.getFiveMinuteRate()), this.getRateUnit()));
        this.printIfEnabled(MetricAttribute.M15_RATE, String.format(this.locale, "    15-minute rate = %2.2f events/%s", this.convertRate(meter.getFifteenMinuteRate()), this.getRateUnit()));
    }

    private void printCounter(Map.Entry<String, Counter> entry) {
        this.output.printf(this.locale, "             count = %d%n", entry.getValue().getCount());
    }

    private void printGauge(Gauge<?> gauge) {
        this.output.printf(this.locale, "             value = %s%n", gauge.getValue());
    }

    private void printHistogram(Histogram histogram) {
        this.printIfEnabled(MetricAttribute.COUNT, String.format(this.locale, "             count = %d", histogram.getCount()));
        Snapshot snapshot = histogram.getSnapshot();
        this.printIfEnabled(MetricAttribute.MIN, String.format(this.locale, "               min = %d", snapshot.getMin()));
        this.printIfEnabled(MetricAttribute.MAX, String.format(this.locale, "               max = %d", snapshot.getMax()));
        this.printIfEnabled(MetricAttribute.MEAN, String.format(this.locale, "              mean = %2.2f", snapshot.getMean()));
        this.printIfEnabled(MetricAttribute.STDDEV, String.format(this.locale, "            stddev = %2.2f", snapshot.getStdDev()));
        this.printIfEnabled(MetricAttribute.P50, String.format(this.locale, "            median = %2.2f", snapshot.getMedian()));
        this.printIfEnabled(MetricAttribute.P75, String.format(this.locale, "              75%% <= %2.2f", snapshot.get75thPercentile()));
        this.printIfEnabled(MetricAttribute.P95, String.format(this.locale, "              95%% <= %2.2f", snapshot.get95thPercentile()));
        this.printIfEnabled(MetricAttribute.P98, String.format(this.locale, "              98%% <= %2.2f", snapshot.get98thPercentile()));
        this.printIfEnabled(MetricAttribute.P99, String.format(this.locale, "              99%% <= %2.2f", snapshot.get99thPercentile()));
        this.printIfEnabled(MetricAttribute.P999, String.format(this.locale, "            99.9%% <= %2.2f", snapshot.get999thPercentile()));
    }

    private void printTimer(Timer timer) {
        Snapshot snapshot = timer.getSnapshot();
        this.printIfEnabled(MetricAttribute.COUNT, String.format(this.locale, "             count = %d", timer.getCount()));
        this.printIfEnabled(MetricAttribute.MEAN_RATE, String.format(this.locale, "         mean rate = %2.2f calls/%s", this.convertRate(timer.getMeanRate()), this.getRateUnit()));
        this.printIfEnabled(MetricAttribute.M1_RATE, String.format(this.locale, "     1-minute rate = %2.2f calls/%s", this.convertRate(timer.getOneMinuteRate()), this.getRateUnit()));
        this.printIfEnabled(MetricAttribute.M5_RATE, String.format(this.locale, "     5-minute rate = %2.2f calls/%s", this.convertRate(timer.getFiveMinuteRate()), this.getRateUnit()));
        this.printIfEnabled(MetricAttribute.M15_RATE, String.format(this.locale, "    15-minute rate = %2.2f calls/%s", this.convertRate(timer.getFifteenMinuteRate()), this.getRateUnit()));
        this.printIfEnabled(MetricAttribute.MIN, String.format(this.locale, "               min = %2.2f %s", this.convertDuration(snapshot.getMin()), this.getDurationUnit()));
        this.printIfEnabled(MetricAttribute.MAX, String.format(this.locale, "               max = %2.2f %s", this.convertDuration(snapshot.getMax()), this.getDurationUnit()));
        this.printIfEnabled(MetricAttribute.MEAN, String.format(this.locale, "              mean = %2.2f %s", this.convertDuration(snapshot.getMean()), this.getDurationUnit()));
        this.printIfEnabled(MetricAttribute.STDDEV, String.format(this.locale, "            stddev = %2.2f %s", this.convertDuration(snapshot.getStdDev()), this.getDurationUnit()));
        this.printIfEnabled(MetricAttribute.P50, String.format(this.locale, "            median = %2.2f %s", this.convertDuration(snapshot.getMedian()), this.getDurationUnit()));
        this.printIfEnabled(MetricAttribute.P75, String.format(this.locale, "              75%% <= %2.2f %s", this.convertDuration(snapshot.get75thPercentile()), this.getDurationUnit()));
        this.printIfEnabled(MetricAttribute.P95, String.format(this.locale, "              95%% <= %2.2f %s", this.convertDuration(snapshot.get95thPercentile()), this.getDurationUnit()));
        this.printIfEnabled(MetricAttribute.P98, String.format(this.locale, "              98%% <= %2.2f %s", this.convertDuration(snapshot.get98thPercentile()), this.getDurationUnit()));
        this.printIfEnabled(MetricAttribute.P99, String.format(this.locale, "              99%% <= %2.2f %s", this.convertDuration(snapshot.get99thPercentile()), this.getDurationUnit()));
        this.printIfEnabled(MetricAttribute.P999, String.format(this.locale, "            99.9%% <= %2.2f %s", this.convertDuration(snapshot.get999thPercentile()), this.getDurationUnit()));
    }

    private void printWithBanner(String s, char c) {
        this.output.print(s);
        this.output.print(' ');
        for (int i = 0; i < 80 - s.length() - 1; ++i) {
            this.output.print(c);
        }
        this.output.println();
    }

    private void printIfEnabled(MetricAttribute type, String status) {
        if (this.getDisabledMetricAttributes().contains((Object)type)) {
            return;
        }
        this.output.println(status);
    }

    public static class Builder {
        private final MetricRegistry registry;
        private PrintStream output;
        private Locale locale;
        private Clock clock;
        private TimeZone timeZone;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricFilter filter;
        private ScheduledExecutorService executor;
        private boolean shutdownExecutorOnStop;
        private Set<MetricAttribute> disabledMetricAttributes;

        private Builder(MetricRegistry registry) {
            this.registry = registry;
            this.output = System.out;
            this.locale = Locale.getDefault();
            this.clock = Clock.defaultClock();
            this.timeZone = TimeZone.getDefault();
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
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

        public Builder outputTo(PrintStream output) {
            this.output = output;
            return this;
        }

        public Builder formattedFor(Locale locale) {
            this.locale = locale;
            return this;
        }

        public Builder withClock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public Builder formattedFor(TimeZone timeZone) {
            this.timeZone = timeZone;
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

        public Builder disabledMetricAttributes(Set<MetricAttribute> disabledMetricAttributes) {
            this.disabledMetricAttributes = disabledMetricAttributes;
            return this;
        }

        public ConsoleReporter build() {
            return new ConsoleReporter(this.registry, this.output, this.locale, this.clock, this.timeZone, this.rateUnit, this.durationUnit, this.filter, this.executor, this.shutdownExecutorOnStop, this.disabledMetricAttributes);
        }
    }
}

