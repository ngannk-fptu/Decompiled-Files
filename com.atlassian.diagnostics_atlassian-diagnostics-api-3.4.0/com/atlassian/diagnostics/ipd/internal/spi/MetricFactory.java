/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.ipd.internal.spi;

import com.atlassian.diagnostics.ipd.internal.spi.IpdMetric;
import com.atlassian.diagnostics.ipd.internal.spi.MetricOptions;

@FunctionalInterface
public interface MetricFactory<T extends IpdMetric> {
    public T createMetric(MetricOptions var1);
}

