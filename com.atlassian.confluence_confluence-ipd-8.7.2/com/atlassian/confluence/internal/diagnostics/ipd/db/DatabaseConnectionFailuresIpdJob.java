/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 *  com.atlassian.diagnostics.internal.ipd.metrics.IpdCounterMetric
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJob
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 */
package com.atlassian.confluence.internal.diagnostics.ipd.db;

import com.atlassian.confluence.internal.diagnostics.ipd.db.DatabaseConnectionStateService;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdCounterMetric;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import com.atlassian.util.profiling.MetricTag;
import java.util.Objects;

public class DatabaseConnectionFailuresIpdJob
implements IpdJob {
    private static final long DB_DISCONNECTED_VALUE = 1L;
    private static final long DB_CONNECTED_ZERO_VALUE = 0L;
    private final DatabaseConnectionStateService databaseConnectionStateService;
    private final IpdCounterMetric dbFailuresCounter;

    public DatabaseConnectionFailuresIpdJob(IpdJobRunner ipdJobRunner, DatabaseConnectionStateService databaseConnectionStateService, IpdMainRegistry ipdMainRegistry) {
        this.databaseConnectionStateService = Objects.requireNonNull(databaseConnectionStateService);
        Objects.requireNonNull(ipdJobRunner).register((IpdJob)this);
        this.dbFailuresCounter = Objects.requireNonNull(ipdMainRegistry).counterMetric("db.connection.failures", new MetricTag.RequiredMetricTag[0]);
    }

    public void runJob() {
        DatabaseConnectionStateService.DatabaseConnectionState state = this.databaseConnectionStateService.getState();
        this.dbFailuresCounter.increment(state == DatabaseConnectionStateService.DatabaseConnectionState.DISCONNECTED ? 1L : 0L);
    }
}

