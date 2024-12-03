/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 *  com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry
 *  com.atlassian.diagnostics.internal.ipd.metrics.wrapper.IpdValueAndStatsMetricWrapper
 *  com.atlassian.diagnostics.internal.platform.monitor.db.DatabasePoolDiagnostic
 *  com.atlassian.diagnostics.internal.platform.monitor.db.DatabasePoolDiagnosticProvider
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJob
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 */
package com.atlassian.confluence.internal.diagnostics.ipd.db;

import com.atlassian.confluence.internal.diagnostics.ipd.db.DatabaseConnectionStateService;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry;
import com.atlassian.diagnostics.internal.ipd.metrics.wrapper.IpdValueAndStatsMetricWrapper;
import com.atlassian.diagnostics.internal.platform.monitor.db.DatabasePoolDiagnostic;
import com.atlassian.diagnostics.internal.platform.monitor.db.DatabasePoolDiagnosticProvider;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import com.atlassian.util.profiling.MetricTag;
import java.util.Objects;

public class DatabaseConnectionPoolIpdJob
implements IpdJob {
    private static final long NO_VALUE = -1L;
    private final DatabaseConnectionStateService databaseConnectionStateService;
    private final DatabasePoolDiagnosticProvider databasePoolDiagnosticProvider;
    private final IpdValueAndStatsMetricWrapper activeConnectionsMetric;
    private final IpdValueAndStatsMetricWrapper idleConnectionsMetric;

    public DatabaseConnectionPoolIpdJob(IpdJobRunner ipdJobRunner, DatabaseConnectionStateService databaseConnectionStateService, DatabasePoolDiagnosticProvider databasePoolDiagnosticProvider, IpdMainRegistry ipdMainRegistry) {
        this.databaseConnectionStateService = Objects.requireNonNull(databaseConnectionStateService);
        this.databasePoolDiagnosticProvider = Objects.requireNonNull(databasePoolDiagnosticProvider);
        Objects.requireNonNull(ipdJobRunner).register((IpdJob)this);
        Objects.requireNonNull(ipdMainRegistry);
        IpdMetricRegistry registry = ipdMainRegistry.createRegistry("db.connection.pool", new MetricTag.RequiredMetricTag[0]);
        this.activeConnectionsMetric = registry.valueAndStatsMetric("numActive", new MetricTag.RequiredMetricTag[0]);
        this.idleConnectionsMetric = registry.valueAndStatsMetric("numIdle", new MetricTag.RequiredMetricTag[0]);
    }

    public void runJob() {
        DatabaseConnectionStateService.DatabaseConnectionState dbState = this.databaseConnectionStateService.getState();
        DatabasePoolDiagnostic dbDiagnostic = this.databasePoolDiagnosticProvider.getDiagnostic();
        if (dbState == DatabaseConnectionStateService.DatabaseConnectionState.DISCONNECTED || dbDiagnostic.isEmpty()) {
            this.activeConnectionsMetric.updateValue(Long.valueOf(-1L));
            this.idleConnectionsMetric.updateValue(Long.valueOf(-1L));
            return;
        }
        this.activeConnectionsMetric.update(Long.valueOf(dbDiagnostic.getActiveConnections()));
        this.idleConnectionsMetric.update(Long.valueOf(dbDiagnostic.getIdleConnections()));
    }
}

