/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.ClusterNodeInformation
 *  com.atlassian.dc.filestore.api.FileStore$Path
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 *  com.atlassian.diagnostics.internal.ipd.IpdMetricBuilder
 *  com.atlassian.diagnostics.internal.ipd.metrics.wrapper.IpdValueAndStatsMetricWrapper
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJob
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 */
package com.atlassian.confluence.internal.diagnostics.ipd.filesystem;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.internal.diagnostics.ipd.IpdExecutors;
import com.atlassian.confluence.internal.diagnostics.ipd.filesystem.IpdFileWriteLatencyMeter;
import com.atlassian.confluence.internal.diagnostics.ipd.filesystem.IpdSharedFileWriteLatencyMeter;
import com.atlassian.dc.filestore.api.FileStore;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.IpdMetricBuilder;
import com.atlassian.diagnostics.internal.ipd.metrics.wrapper.IpdValueAndStatsMetricWrapper;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import com.atlassian.util.profiling.MetricTag;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SharedHomeLatencyIpdJob
implements IpdJob {
    private static final int TIMEOUT = 15;
    private static final int NUM_OF_MEASUREMENTS = 3;
    private final IpdValueAndStatsMetricWrapper sharedHomeLatency;
    final ClusterManager clusterManager;
    private final IpdFileWriteLatencyMeter ipdFileWriteLatencyMeter;
    private final ExecutorService executorService;

    public SharedHomeLatencyIpdJob(IpdJobRunner ipdJobRunner, FileStore.Path sharedHome, IpdMainRegistry ipdMainRegistry, ClusterManager clusterManager, IpdExecutors ipdExecutors) {
        this(ipdJobRunner, ipdMainRegistry, clusterManager, ipdExecutors.createSingleTaskExecutorService("ipd-shared-home"), new IpdSharedFileWriteLatencyMeter(SharedHomeLatencyIpdJob.getSharedTmpFile(sharedHome, clusterManager), 3));
    }

    @VisibleForTesting
    SharedHomeLatencyIpdJob(IpdJobRunner ipdJobRunner, IpdMainRegistry ipdMainRegistry, ClusterManager clusterManager, ExecutorService executorService, IpdFileWriteLatencyMeter ipdFileWriteLatencyMeter) {
        this.clusterManager = clusterManager;
        ipdJobRunner.register((IpdJob)this);
        this.sharedHomeLatency = ipdMainRegistry.createRegistry(IpdMetricBuilder::asWorkInProgress).valueAndStatsMetric("home.shared.write.latency", new MetricTag.RequiredMetricTag[0]);
        this.executorService = executorService;
        this.ipdFileWriteLatencyMeter = ipdFileWriteLatencyMeter;
    }

    public void runJob() {
        if (!this.clusterManager.isClustered()) {
            return;
        }
        Future<List> measurementFuture = null;
        try {
            measurementFuture = this.executorService.submit(this.ipdFileWriteLatencyMeter::makeWriteLatencyMeasurements);
            List<Long> latenciesInMillis = measurementFuture.get(15L, TimeUnit.SECONDS).stream().map(Duration::toMillis).collect(Collectors.toList());
            latenciesInMillis.forEach(arg_0 -> ((IpdValueAndStatsMetricWrapper)this.sharedHomeLatency).updateStats(arg_0));
            this.sharedHomeLatency.updateValue(Long.valueOf(IpdFileWriteLatencyMeter.getMedian(latenciesInMillis)));
        }
        catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        catch (Exception e) {
            this.sharedHomeLatency.updateValue(Long.valueOf(-1L));
        }
        finally {
            if (measurementFuture != null) {
                measurementFuture.cancel(true);
            }
        }
    }

    public boolean isWorkInProgressJob() {
        return true;
    }

    private static FileStore.Path getSharedTmpFile(FileStore.Path sharedHome, ClusterManager clusterManager) {
        String nodeId = Optional.ofNullable(clusterManager.getThisNodeInformation()).map(ClusterNodeInformation::getAnonymizedNodeIdentifier).orElse("node-not-in-cluster");
        return sharedHome.path(new String[]{"tmp", "latency-check-" + nodeId + ".tmp"});
    }
}

