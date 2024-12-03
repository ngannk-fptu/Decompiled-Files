/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 *  com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry
 *  com.atlassian.diagnostics.internal.ipd.metrics.IpdValueMetric
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJob
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 */
package com.atlassian.confluence.internal.diagnostics.ipd.http;

import com.atlassian.confluence.internal.diagnostics.ipd.http.DefaultHttpConnectionPoolService;
import com.atlassian.confluence.internal.diagnostics.ipd.http.HttpConnectionPoolMetric;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdValueMetric;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import com.atlassian.util.profiling.MetricTag;
import java.util.Objects;

public class HttpConnectionPoolIpdJob
implements IpdJob {
    private final DefaultHttpConnectionPoolService defaultHttpConnectionPoolService;
    private final IpdValueMetric numIdleMetric;
    private final IpdValueMetric numActiveMetric;
    private final IpdValueMetric numMaxMetric;

    public HttpConnectionPoolIpdJob(IpdJobRunner ipdJobRunner, DefaultHttpConnectionPoolService defaultHttpConnectionPoolService, IpdMainRegistry ipdMainRegistry) {
        this.defaultHttpConnectionPoolService = Objects.requireNonNull(defaultHttpConnectionPoolService);
        Objects.requireNonNull(ipdJobRunner).register((IpdJob)this);
        IpdMetricRegistry registry = Objects.requireNonNull(ipdMainRegistry).createRegistry("http.connection.pool", new MetricTag.RequiredMetricTag[0]);
        this.numActiveMetric = registry.valueMetric("numActive", new MetricTag.RequiredMetricTag[0]);
        this.numIdleMetric = registry.valueMetric("numIdle", new MetricTag.RequiredMetricTag[0]);
        this.numMaxMetric = registry.valueMetric("numMax", new MetricTag.RequiredMetricTag[0]);
    }

    public void runJob() {
        HttpConnectionPoolMetric metric = this.defaultHttpConnectionPoolService.getHttpPoolSizeValue();
        this.numMaxMetric.update(Long.valueOf(metric.getNumMax()));
        this.numIdleMetric.update(Long.valueOf(metric.getNumIdle()));
        this.numActiveMetric.update(Long.valueOf(metric.getNumActive()));
    }
}

