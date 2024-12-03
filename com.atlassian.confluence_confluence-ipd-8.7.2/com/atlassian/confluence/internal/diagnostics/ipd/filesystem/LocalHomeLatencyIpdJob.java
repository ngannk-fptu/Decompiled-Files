/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 *  com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry
 *  com.atlassian.diagnostics.internal.ipd.metrics.IpdStatsMetric
 *  com.atlassian.diagnostics.internal.ipd.metrics.IpdValueMetric
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJob
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 */
package com.atlassian.confluence.internal.diagnostics.ipd.filesystem;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.internal.diagnostics.ipd.IpdExecutors;
import com.atlassian.confluence.internal.diagnostics.ipd.filesystem.IpdFileWriteLatencyMeter;
import com.atlassian.confluence.internal.diagnostics.ipd.filesystem.IpdLocalFileWriteLatencyMeter;
import com.atlassian.confluence.internal.diagnostics.ipd.filesystem.PathNotConfiguredException;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdStatsMetric;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdValueMetric;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.util.profiling.MetricTag;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class LocalHomeLatencyIpdJob
implements IpdJob {
    private static final int TIMEOUT = 5;
    private static final int NUM_OF_MEASUREMENTS = 7;
    private final IpdStatsMetric localHomeLatencyStats;
    private final IpdValueMetric localHomeLatencyValue;
    private final ExecutorService executorService;
    private final IpdFileWriteLatencyMeter ipdFileWriteLatencyMeter;

    public LocalHomeLatencyIpdJob(IpdJobRunner ipdJobRunner, ApplicationProperties applicationProperties, IpdMainRegistry ipdMainRegistry, IpdExecutors ipdExecutors) {
        this(ipdJobRunner, ipdMainRegistry, ipdExecutors.createSingleTaskExecutorService("ipd-local-home"), new IpdLocalFileWriteLatencyMeter(LocalHomeLatencyIpdJob.getLocalTmpFile(applicationProperties), 7));
    }

    @VisibleForTesting
    LocalHomeLatencyIpdJob(IpdJobRunner ipdJobRunner, IpdMainRegistry ipdMainRegistry, ExecutorService executorService, IpdFileWriteLatencyMeter ipdFileWriteLatencyMeter) {
        ipdJobRunner.register((IpdJob)this);
        IpdMetricRegistry registry = ipdMainRegistry.createRegistry(b -> b.withPrefix("home.local.write.latency.synthetic").asWorkInProgress());
        this.localHomeLatencyStats = registry.statsMetric("", new MetricTag.RequiredMetricTag[0]);
        this.localHomeLatencyValue = registry.valueMetric("", new MetricTag.RequiredMetricTag[0]);
        this.ipdFileWriteLatencyMeter = ipdFileWriteLatencyMeter;
        this.executorService = executorService;
    }

    public void runJob() {
        Future<List> measurementsFuture = null;
        try {
            measurementsFuture = this.executorService.submit(this.ipdFileWriteLatencyMeter::makeWriteLatencyMeasurements);
            List<Long> latenciesInMicros = measurementsFuture.get(5L, TimeUnit.SECONDS).stream().map(duration -> (long)duration.getNano() / 1000L).collect(Collectors.toList());
            latenciesInMicros.forEach(latency -> this.localHomeLatencyStats.update(latency, TimeUnit.MICROSECONDS));
            this.localHomeLatencyValue.update(Long.valueOf(IpdFileWriteLatencyMeter.getMedian(latenciesInMicros)));
        }
        catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        catch (Exception e) {
            this.localHomeLatencyValue.update(Long.valueOf(-1L));
        }
        finally {
            if (measurementsFuture != null) {
                measurementsFuture.cancel(true);
            }
        }
    }

    private static File getLocalTmpFile(ApplicationProperties applicationProperties) {
        return applicationProperties.getLocalHomeDirectory().map(localHomePath -> localHomePath.resolve(Path.of("temp", "latency-check.tmp")).toFile()).orElseThrow(() -> new PathNotConfiguredException("Local home path is not configured"));
    }

    public boolean isWorkInProgressJob() {
        return true;
    }
}

