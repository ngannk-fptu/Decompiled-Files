/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.diagnostics.detail.ThreadDumpProducer
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.http.HttpServletRequest
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.confluence.internal.diagnostics.ConfluenceMonitor;
import com.atlassian.confluence.internal.diagnostics.DiagnosticsInfo;
import com.atlassian.confluence.internal.diagnostics.DiagnosticsWorker;
import com.atlassian.confluence.internal.diagnostics.EventListeningDarkFeatureSetting;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.detail.ThreadDumpProducer;
import com.google.common.collect.ImmutableMap;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HttpRequestMonitor
extends ConfluenceMonitor {
    private static final long SLOW_HTTP_REQUEST_SECS = Integer.getInteger("diagnostics.slow.http.request.secs", 60).intValue();
    private static final int REQUEST_EXCEEDS_TIME_LIMIT_ID = 1001;
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestMonitor.class);
    private static final String MONITOR_ID = "HTTP";
    private final Map<HttpServletRequest, DiagnosticsInfo> requests = new ConcurrentHashMap<HttpServletRequest, DiagnosticsInfo>();
    private final ThreadDumpProducer threadDumpProducer;
    private final EventListeningDarkFeatureSetting riskyDiagnosticMonitorsEnabled;

    public HttpRequestMonitor(@NonNull ThreadDumpProducer threadDumpProducer, @NonNull EventListeningDarkFeatureSetting riskyDiagnosticMonitorsEnabled) {
        this.threadDumpProducer = Objects.requireNonNull(threadDumpProducer);
        this.riskyDiagnosticMonitorsEnabled = riskyDiagnosticMonitorsEnabled;
    }

    @Override
    public void init(MonitoringService monitoringService) {
        super.init(monitoringService);
        this.monitor = monitoringService.createMonitor(MONITOR_ID, "diagnostics.http.name", this.riskyDiagnosticMonitorsEnabled::isEnabled);
        this.defineIssue("diagnostics.http.issue", 1001, Severity.WARNING);
        this.startMonitorThread();
        logger.debug("{} monitor has been initialized", (Object)MONITOR_ID);
    }

    @Override
    protected String getMonitorId() {
        return MONITOR_ID;
    }

    public void start(HttpServletRequest httpRequest) {
        this.requests.put(httpRequest, new DiagnosticsInfo(Thread.currentThread(), AuthenticatedUserThreadLocal.getUsername(), Duration.ofSeconds(SLOW_HTTP_REQUEST_SECS)));
    }

    public void stop(HttpServletRequest httpRequest) {
        this.requests.remove(httpRequest);
    }

    private void startMonitorThread() {
        this.startMonitorThread(new DiagnosticsWorker<HttpServletRequest>(this.requests, this::alert, Duration.ofSeconds(SLOW_HTTP_REQUEST_SECS)), "diagnostics-http-thread");
    }

    private void alert(HttpServletRequest request, DiagnosticsInfo info) {
        this.alert(1001, builder -> builder.timestamp(Instant.now()).details(() -> ImmutableMap.builder().put((Object)"request", (Object)(request != null && request.getRequestURI() != null ? request.getRequestURI() : "")).put((Object)"username", (Object)info.getUsername().orElse("")).put((Object)"actualTimeInSecs", (Object)info.getActualTime()).put((Object)"timeLimitInSecs", (Object)info.getTimeLimit()).put((Object)"threadId", (Object)info.getWorkerThread().getId()).put((Object)"threadName", (Object)info.getWorkerThread().getName()).put((Object)"threadStatus", (Object)info.getWorkerThread().getState()).put((Object)"threadDump", (Object)this.threadDumpProducer.produce(Collections.singleton(info.getWorkerThread()))).build()));
    }
}

