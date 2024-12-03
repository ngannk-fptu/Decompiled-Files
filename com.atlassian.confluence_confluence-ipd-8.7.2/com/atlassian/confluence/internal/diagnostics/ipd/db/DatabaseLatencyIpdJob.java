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
package com.atlassian.confluence.internal.diagnostics.ipd.db;

import com.atlassian.confluence.internal.diagnostics.ipd.db.DatabaseConnectionStateService;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.metrics.wrapper.IpdValueAndStatsMetricWrapper;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import com.atlassian.util.profiling.MetricTag;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

public class DatabaseLatencyIpdJob
implements IpdJob {
    private static final Long NO_VALUE = -1L;
    private final DatabaseConnectionStateService databaseConnectionStateService;
    private final IpdValueAndStatsMetricWrapper latencyMetric;

    public DatabaseLatencyIpdJob(IpdJobRunner ipdJobRunner, DatabaseConnectionStateService databaseConnectionStateService, IpdMainRegistry ipdMainRegistry) {
        this.databaseConnectionStateService = Objects.requireNonNull(databaseConnectionStateService);
        Objects.requireNonNull(ipdJobRunner).register((IpdJob)this);
        Objects.requireNonNull(ipdMainRegistry);
        this.latencyMetric = ipdMainRegistry.valueAndStatsMetric("db.connection.latency", new MetricTag.RequiredMetricTag[0]);
    }

    public void runJob() {
        Optional<Duration> latency = this.databaseConnectionStateService.getLatency();
        Long value = latency.map(Duration::toMillis).orElse(NO_VALUE);
        if (latency.isPresent()) {
            this.latencyMetric.updateStats(value);
        }
        this.latencyMetric.updateValue(value);
    }
}

