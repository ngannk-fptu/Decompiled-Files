/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.internal.LongMetric
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.vcache.internal.LongMetric;

public class DefaultLongMetric
implements LongMetric {
    private long sampleCount;
    private long samplesTotal;
    private long minSample = Long.MAX_VALUE;
    private long maxSample = Long.MIN_VALUE;

    public void record(long sample) {
        ++this.sampleCount;
        this.samplesTotal += sample;
        this.minSample = Math.min(sample, this.minSample);
        this.maxSample = Math.max(sample, this.maxSample);
    }

    public long getSampleCount() {
        return this.sampleCount;
    }

    public long getSamplesTotal() {
        return this.samplesTotal;
    }

    public long getMinSample() {
        return this.minSample;
    }

    public long getMaxSample() {
        return this.maxSample;
    }

    public String toString() {
        return "DefaultLongMetric{sampleCount=" + this.sampleCount + ", samplesTotal=" + this.samplesTotal + ", minSample=" + this.minSample + ", maxSample=" + this.maxSample + '}';
    }
}

