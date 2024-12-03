/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Snapshot;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

public class UniformSnapshot
extends Snapshot {
    private final long[] values;

    public UniformSnapshot(Collection<Long> values) {
        Object[] copy = values.toArray();
        this.values = new long[copy.length];
        for (int i = 0; i < copy.length; ++i) {
            this.values[i] = (Long)copy[i];
        }
        Arrays.sort(this.values);
    }

    public UniformSnapshot(long[] values) {
        this.values = Arrays.copyOf(values, values.length);
        Arrays.sort(this.values);
    }

    @Override
    public double getValue(double quantile) {
        if (quantile < 0.0 || quantile > 1.0 || Double.isNaN(quantile)) {
            throw new IllegalArgumentException(quantile + " is not in [0..1]");
        }
        if (this.values.length == 0) {
            return 0.0;
        }
        double pos = quantile * (double)(this.values.length + 1);
        int index = (int)pos;
        if (index < 1) {
            return this.values[0];
        }
        if (index >= this.values.length) {
            return this.values[this.values.length - 1];
        }
        double lower = this.values[index - 1];
        double upper = this.values[index];
        return lower + (pos - Math.floor(pos)) * (upper - lower);
    }

    @Override
    public int size() {
        return this.values.length;
    }

    @Override
    public long[] getValues() {
        return Arrays.copyOf(this.values, this.values.length);
    }

    @Override
    public long getMax() {
        if (this.values.length == 0) {
            return 0L;
        }
        return this.values[this.values.length - 1];
    }

    @Override
    public long getMin() {
        if (this.values.length == 0) {
            return 0L;
        }
        return this.values[0];
    }

    @Override
    public double getMean() {
        if (this.values.length == 0) {
            return 0.0;
        }
        double sum = 0.0;
        for (long value : this.values) {
            sum += (double)value;
        }
        return sum / (double)this.values.length;
    }

    @Override
    public double getStdDev() {
        if (this.values.length <= 1) {
            return 0.0;
        }
        double mean = this.getMean();
        double sum = 0.0;
        for (long value : this.values) {
            double diff = (double)value - mean;
            sum += diff * diff;
        }
        double variance = sum / (double)(this.values.length - 1);
        return Math.sqrt(variance);
    }

    @Override
    public void dump(OutputStream output) {
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));){
            for (long value : this.values) {
                out.printf("%d%n", value);
            }
        }
    }
}

