/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.ipd.metrics.wrapper;

import com.atlassian.diagnostics.internal.ipd.metrics.IpdStatsMetric;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdValueMetric;

public class IpdValueAndStatsMetricWrapper {
    private final IpdStatsMetric ipdStatsMetric;
    private final IpdValueMetric ipdValueMetric;

    public IpdValueAndStatsMetricWrapper(IpdStatsMetric ipdStatsMetric, IpdValueMetric ipdValueMetric) {
        this.ipdValueMetric = ipdValueMetric;
        this.ipdStatsMetric = ipdStatsMetric;
    }

    public void update(Long value) {
        this.ipdStatsMetric.update(value);
        this.ipdValueMetric.update(value);
    }

    public void updateValue(Long value) {
        this.ipdValueMetric.update(value);
    }

    public void updateStats(Long value) {
        this.ipdStatsMetric.update(value);
    }
}

