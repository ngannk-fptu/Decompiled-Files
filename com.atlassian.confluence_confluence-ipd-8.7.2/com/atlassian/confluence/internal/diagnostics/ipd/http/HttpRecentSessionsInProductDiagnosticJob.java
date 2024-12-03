/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 *  com.atlassian.diagnostics.internal.ipd.metrics.IpdValueMetric
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJob
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 */
package com.atlassian.confluence.internal.diagnostics.ipd.http;

import com.atlassian.confluence.internal.diagnostics.ipd.http.IpdSessionMonitoringService;
import com.atlassian.confluence.internal.diagnostics.ipd.http.session.ConfluenceHttpSession;
import com.atlassian.confluence.internal.diagnostics.ipd.http.session.HttpSessionTracker;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdValueMetric;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import com.atlassian.util.profiling.MetricTag;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.management.ObjectName;

public class HttpRecentSessionsInProductDiagnosticJob
implements IpdJob {
    public static final String LIST_SESSION_IDS = "listSessionIds";
    public static final int RECENTLY_ACTIVE_SESSIONS_PERIOD_IN_MINUTES = 60;
    private final HttpSessionTracker sessionTracker;
    private final IpdSessionMonitoringService service;
    private final IpdValueMetric recentSessionsMetric;

    public HttpRecentSessionsInProductDiagnosticJob(IpdJobRunner ipdJobRunner, HttpSessionTracker sessionTracker, IpdSessionMonitoringService service, IpdMainRegistry ipdMainRegistry) {
        this.sessionTracker = Objects.requireNonNull(sessionTracker);
        this.service = Objects.requireNonNull(service);
        Objects.requireNonNull(ipdJobRunner).register((IpdJob)this);
        this.recentSessionsMetric = Objects.requireNonNull(ipdMainRegistry).valueMetric("http.connection.sessions.recent", new MetricTag.RequiredMetricTag[0]);
    }

    public void runJob() {
        Instant startTime = this.getStartTime();
        Map<String, ConfluenceHttpSession> snapshot = this.sessionTracker.getSnapshot();
        long recentSessionsCount = this.getActiveSessionIds().filter(sessionId -> this.isRecentSession(snapshot, (String)sessionId, startTime)).count();
        this.recentSessionsMetric.update(Long.valueOf(recentSessionsCount));
    }

    private Instant getStartTime() {
        return Instant.now().minus(60L, ChronoUnit.MINUTES);
    }

    private Stream<String> getActiveSessionIds() {
        return this.service.findTomcatManagerObjectName().stream().map(objectName -> this.service.invokeMbeanOperation((ObjectName)objectName, LIST_SESSION_IDS, null)).filter(Optional::isPresent).map(Optional::get).map(String.class::cast).flatMap(s -> Stream.of(s.split(" "))).filter(sessionId -> sessionId.length() != 0);
    }

    private boolean isRecentSession(Map<String, ConfluenceHttpSession> sessionSnapshot, String sessionId, Instant startTime) {
        return Optional.ofNullable(sessionSnapshot.get(sessionId)).map(session -> session.getLastAccessTime().isAfter(startTime)).orElse(false);
    }
}

