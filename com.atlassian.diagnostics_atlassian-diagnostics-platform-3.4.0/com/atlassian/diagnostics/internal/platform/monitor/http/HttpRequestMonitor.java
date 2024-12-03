/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.MonitorConfiguration
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.diagnostics.internal.InitializingMonitor
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.monitor.http;

import com.atlassian.diagnostics.MonitorConfiguration;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.internal.InitializingMonitor;
import com.atlassian.diagnostics.internal.platform.monitor.http.HttpMonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.monitor.http.HttpRequestDiagnostic;
import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import java.util.Map;
import javax.annotation.Nonnull;

public class HttpRequestMonitor
extends InitializingMonitor {
    private static final String KEY_PREFIX = "diagnostics.http.issue";
    private static final int SLOW_HTTP_REQUEST_ISSUE_ID = 3001;
    private final HttpMonitorConfiguration httpMonitorConfiguration;

    public HttpRequestMonitor(HttpMonitorConfiguration httpMonitorConfiguration) {
        this.httpMonitorConfiguration = httpMonitorConfiguration;
    }

    public void init(@Nonnull MonitoringService monitoringService) {
        this.monitor = monitoringService.createMonitor("HTTP", "diagnostics.http.name", (MonitorConfiguration)this.httpMonitorConfiguration);
        this.defineIssue(KEY_PREFIX, 3001, Severity.INFO);
    }

    public void raiseAlertForSlowHttpRequest(@Nonnull Instant timestamp, HttpRequestDiagnostic diagnostic) {
        this.alert(3001, builder -> builder.timestamp(timestamp).details(() -> this.slowHttpRequestAlertDetails(diagnostic)).build());
    }

    private Map<Object, Object> slowHttpRequestAlertDetails(HttpRequestDiagnostic diagnostic) {
        return ImmutableMap.builder().put((Object)"requestPath", (Object)diagnostic.getRequestPath()).put((Object)"username", (Object)diagnostic.getUsername()).put((Object)"elapsedTimeInMillis", (Object)diagnostic.getRequestDuration().toMillis()).build();
    }
}

