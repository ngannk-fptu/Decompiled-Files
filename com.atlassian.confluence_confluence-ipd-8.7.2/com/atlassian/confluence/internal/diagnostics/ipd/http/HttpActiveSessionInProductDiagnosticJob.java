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

import com.atlassian.confluence.internal.diagnostics.ipd.http.IpdSessionMonitoringService;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.metrics.wrapper.IpdValueAndStatsMetricWrapper;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import com.atlassian.util.profiling.MetricTag;
import java.util.Objects;
import java.util.Optional;
import javax.management.ObjectName;

public class HttpActiveSessionInProductDiagnosticJob
implements IpdJob {
    private static final String SOURCE_METRIC_ACTIVE_SESSIONS = "activeSessions";
    private final IpdSessionMonitoringService service;
    private final IpdValueAndStatsMetricWrapper activeSessionsMetric;

    public HttpActiveSessionInProductDiagnosticJob(IpdJobRunner ipdJobRunner, IpdSessionMonitoringService service, IpdMainRegistry ipdMainRegistry) {
        this.service = Objects.requireNonNull(service);
        Objects.requireNonNull(ipdJobRunner).register((IpdJob)this);
        this.activeSessionsMetric = Objects.requireNonNull(ipdMainRegistry).valueAndStatsMetric("http.connection.sessions.active", new MetricTag.RequiredMetricTag[0]);
    }

    public void runJob() {
        Optional<Long> activeSessions = this.getActiveSessions();
        if (activeSessions.isPresent()) {
            this.activeSessionsMetric.update(activeSessions.get());
        } else {
            this.activeSessionsMetric.updateValue(Long.valueOf(-1L));
        }
    }

    private Optional<Long> getActiveSessions() {
        return this.service.findTomcatManagerObjectName().stream().map(objectName -> this.service.getMbeanAttribute((ObjectName)objectName, SOURCE_METRIC_ACTIVE_SESSIONS)).filter(Optional::isPresent).map(Optional::get).map(Integer.class::cast).reduce(Integer::sum).map(Long::valueOf);
    }
}

