/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.vcache.internal;

public interface LongMetric {
    public long getSampleCount();

    public long getSamplesTotal();

    public long getMinSample();

    public long getMaxSample();
}

