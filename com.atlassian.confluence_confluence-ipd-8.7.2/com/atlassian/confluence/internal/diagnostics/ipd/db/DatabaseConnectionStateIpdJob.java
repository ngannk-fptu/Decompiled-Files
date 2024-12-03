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
package com.atlassian.confluence.internal.diagnostics.ipd.db;

import com.atlassian.confluence.internal.diagnostics.ipd.db.DatabaseConnectionStateService;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdValueMetric;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import com.atlassian.util.profiling.MetricTag;
import java.util.Objects;

public class DatabaseConnectionStateIpdJob
implements IpdJob {
    private final DatabaseConnectionStateService databaseConnectionStateService;
    private final IpdValueMetric connectionState;

    public DatabaseConnectionStateIpdJob(IpdJobRunner ipdJobRunner, DatabaseConnectionStateService databaseConnectionStateService, IpdMainRegistry ipdMainRegistry) {
        this.databaseConnectionStateService = Objects.requireNonNull(databaseConnectionStateService);
        Objects.requireNonNull(ipdJobRunner).register((IpdJob)this);
        Objects.requireNonNull(ipdMainRegistry);
        this.connectionState = ipdMainRegistry.valueMetric("db.connection.state", new MetricTag.RequiredMetricTag[0]);
    }

    public void runJob() {
        this.connectionState.update(Long.valueOf(this.databaseConnectionStateService.getState().getValue()));
    }
}

