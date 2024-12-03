/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.distribution;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.distribution.CountAtBucket;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import io.micrometer.core.instrument.util.TimeUtils;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public final class HistogramSnapshot {
    private static final ValueAtPercentile[] EMPTY_VALUES = new ValueAtPercentile[0];
    private static final CountAtBucket[] EMPTY_COUNTS = new CountAtBucket[0];
    private final ValueAtPercentile[] percentileValues;
    private final CountAtBucket[] histogramCounts;
    private final long count;
    private final double total;
    private final double max;
    @Nullable
    private final BiConsumer<PrintStream, Double> summaryOutput;

    public HistogramSnapshot(long count, double total, double max, @Nullable ValueAtPercentile[] percentileValues, @Nullable CountAtBucket[] histogramCounts, @Nullable BiConsumer<PrintStream, Double> summaryOutput) {
        this.count = count;
        this.total = total;
        this.max = max;
        this.percentileValues = percentileValues != null ? percentileValues : EMPTY_VALUES;
        this.histogramCounts = histogramCounts != null ? histogramCounts : EMPTY_COUNTS;
        this.summaryOutput = summaryOutput;
    }

    public long count() {
        return this.count;
    }

    public double total() {
        return this.total;
    }

    public double total(TimeUnit unit) {
        return TimeUtils.nanosToUnit(this.total, unit);
    }

    public double max() {
        return this.max;
    }

    public double max(TimeUnit unit) {
        return TimeUtils.nanosToUnit(this.max, unit);
    }

    public double mean() {
        return this.count == 0L ? 0.0 : this.total / (double)this.count;
    }

    public double mean(TimeUnit unit) {
        return TimeUtils.nanosToUnit(this.mean(), unit);
    }

    public ValueAtPercentile[] percentileValues() {
        return this.percentileValues;
    }

    public CountAtBucket[] histogramCounts() {
        return this.histogramCounts;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("HistogramSnapshot{count=");
        buf.append(this.count);
        buf.append(", total=");
        buf.append(this.total);
        buf.append(", mean=");
        buf.append(this.mean());
        buf.append(", max=");
        buf.append(this.max);
        if (this.percentileValues.length > 0) {
            buf.append(", percentileValues=");
            buf.append(Arrays.toString(this.percentileValues));
        }
        if (this.histogramCounts.length > 0) {
            buf.append(", histogramCounts=");
            buf.append(Arrays.toString(this.histogramCounts));
        }
        buf.append('}');
        return buf.toString();
    }

    public static HistogramSnapshot empty(long count, double total, double max) {
        return new HistogramSnapshot(count, total, max, null, null, null);
    }

    public void outputSummary(PrintStream out, double scale) {
        if (this.summaryOutput != null) {
            this.summaryOutput.accept(out, scale);
        }
    }
}

