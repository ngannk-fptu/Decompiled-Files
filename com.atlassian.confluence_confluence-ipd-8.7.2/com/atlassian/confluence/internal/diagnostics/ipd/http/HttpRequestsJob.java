/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 *  com.atlassian.diagnostics.internal.ipd.metrics.wrapper.IpdValueAndStatsMetricWrapper
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJob
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 */
package com.atlassian.confluence.internal.diagnostics.ipd.http;

import com.atlassian.confluence.internal.diagnostics.ipd.http.IpdHttpMonitoringService;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.metrics.wrapper.IpdValueAndStatsMetricWrapper;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import com.atlassian.util.profiling.MetricTag;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HttpRequestsJob
implements IpdJob {
    private static final long METRIC_PERIOD = TimeUnit.MINUTES.toMillis(1L);
    private final IpdHttpMonitoringService ipdHttpMonitoringService;
    private final IpdValueAndStatsMetricWrapper requestsPerMinuteMetric;

    public HttpRequestsJob(IpdJobRunner ipdJobRunner, IpdHttpMonitoringService ipdHttpMonitoringService, IpdMainRegistry ipdMainRegistry) {
        this.ipdHttpMonitoringService = Objects.requireNonNull(ipdHttpMonitoringService);
        Objects.requireNonNull(ipdJobRunner).register((IpdJob)this);
        this.requestsPerMinuteMetric = Objects.requireNonNull(ipdMainRegistry).valueAndStatsMetric("http.requests", new MetricTag.RequiredMetricTag[0]);
    }

    public void runJob() {
        long requestsPerMinute = this.ipdHttpMonitoringService.numberOfRecentRequests(METRIC_PERIOD);
        this.requestsPerMinuteMetric.update(Long.valueOf(requestsPerMinute));
    }
}

