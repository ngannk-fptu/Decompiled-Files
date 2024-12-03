/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.MonitorConfiguration
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.diagnostics.detail.ThreadDumpProducer
 *  com.atlassian.diagnostics.internal.InitializingMonitor
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.platform.monitor.db;

import com.atlassian.diagnostics.MonitorConfiguration;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.detail.ThreadDumpProducer;
import com.atlassian.diagnostics.internal.InitializingMonitor;
import com.atlassian.diagnostics.internal.platform.monitor.db.DatabaseMonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.monitor.db.DatabaseOperationDiagnostic;
import com.atlassian.diagnostics.internal.platform.monitor.db.DatabasePoolDiagnostic;
import com.atlassian.diagnostics.internal.platform.plugin.PluginFinder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseMonitor
extends InitializingMonitor {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseMonitor.class);
    private static final String KEY_PREFIX = "diagnostics.db.issue";
    private static final int DB_POOL_CONNECTION_LEAK_ID = 2001;
    private static final int DB_SLOW_OPERATION_ISSUE_ID = 3001;
    private static final int DB_POOL_HIGH_UTILIZATION_ISSUE_ID = 3002;
    private final DatabaseMonitorConfiguration databaseMonitorConfiguration;
    private final ThreadDumpProducer threadDumpProducer;
    private final PluginFinder pluginFinder;

    public DatabaseMonitor(@Nonnull DatabaseMonitorConfiguration databaseMonitorConfiguration, @Nonnull ThreadDumpProducer threadDumpProducer, @Nonnull PluginFinder pluginFinder) {
        this.databaseMonitorConfiguration = databaseMonitorConfiguration;
        this.threadDumpProducer = threadDumpProducer;
        this.pluginFinder = pluginFinder;
    }

    public void init(MonitoringService monitoringService) {
        logger.info("Initializing DatabaseMonitor");
        this.monitor = monitoringService.createMonitor("DB", "diagnostics.db.name", (MonitorConfiguration)this.databaseMonitorConfiguration);
        this.defineIssue(KEY_PREFIX, 2001, Severity.WARNING);
        this.defineIssue(KEY_PREFIX, 3001, Severity.INFO);
        this.defineIssue(KEY_PREFIX, 3002, Severity.INFO);
    }

    public void raiseAlertForConnectionLeak(@Nonnull Instant timestamp, @Nonnull Instant connectionAcquiredTimestamp, @Nonnull DatabasePoolDiagnostic diagnostic) {
        this.alert(2001, builder -> builder.timestamp(timestamp).details(() -> this.connectionLeakAlertDetails(diagnostic, connectionAcquiredTimestamp)));
    }

    private Map<Object, Object> connectionLeakAlertDetails(DatabasePoolDiagnostic diagnostic, Instant connectionAcquiredTimestamp) {
        return ImmutableMap.builder().put((Object)"activeConnections", (Object)diagnostic.getActiveConnections()).put((Object)"idleConnections", (Object)diagnostic.getIdleConnections()).put((Object)"maxConnections", (Object)diagnostic.getMaxConnections()).put((Object)"connectionAcquiredTimestampInMillis", (Object)connectionAcquiredTimestamp.toEpochMilli()).build();
    }

    public void raiseAlertForHighPoolUtilization(@Nonnull Instant timestamp, @Nonnull DatabasePoolDiagnostic diagnostic) {
        this.alert(3002, builder -> builder.timestamp(timestamp).details(() -> this.highUtilizationAlertDetails(diagnostic)));
    }

    private Map<Object, Object> highUtilizationAlertDetails(DatabasePoolDiagnostic diagnostic) {
        return ImmutableMap.builder().put((Object)"activeConnections", (Object)diagnostic.getActiveConnections()).put((Object)"idleConnections", (Object)diagnostic.getIdleConnections()).put((Object)"maxConnections", (Object)diagnostic.getMaxConnections()).build();
    }

    public void raiseAlertForSlowOperation(@Nonnull Instant timestamp, @Nonnull DatabaseOperationDiagnostic diagnostic) {
        this.alert(3001, builder -> builder.timestamp(timestamp).details(() -> this.slowOperationAlertDetails(diagnostic)));
    }

    private Map<String, Object> slowOperationAlertDetails(DatabaseOperationDiagnostic diagnostic) {
        ImmutableMap.Builder map = ImmutableMap.builder().put((Object)"executionTimeInMillis", (Object)diagnostic.getExecutionTime().toMillis()).put((Object)"theadDump", (Object)this.threadDumpProducer.produce((Set)ImmutableSet.of((Object)Thread.currentThread()))).put((Object)"plugins", (Object)String.join((CharSequence)" -> ", this.pluginFinder.getPluginNamesInCurrentCallStack()));
        if (this.databaseMonitorConfiguration.includeSqlQueryInAlerts()) {
            map.put((Object)"sql", (Object)diagnostic.getSql());
        }
        return map.build();
    }
}

