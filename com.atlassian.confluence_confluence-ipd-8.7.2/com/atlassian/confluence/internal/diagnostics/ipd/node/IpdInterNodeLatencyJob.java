/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.ClusterNodeExecution
 *  com.atlassian.confluence.cluster.ClusterNodeInformation
 *  com.atlassian.confluence.cluster.NoSuchClusterNodeException
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJob
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner
 */
package com.atlassian.confluence.internal.diagnostics.ipd.node;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeExecution;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.cluster.NoSuchClusterNodeException;
import com.atlassian.confluence.internal.diagnostics.ipd.node.IpdInterNodesStats;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class IpdInterNodeLatencyJob
implements IpdJob {
    private static final long LATENCY_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(10L);
    private final ClusterManager clusterManager;
    private final IpdInterNodesStats ipdInterNodesStats;
    private final Supplier<Long> nanoTimeSupplier;

    public IpdInterNodeLatencyJob(IpdJobRunner ipdJobRunner, IpdInterNodesStats ipdInterNodesStats, ClusterManager clusterManager) {
        this(ipdJobRunner, ipdInterNodesStats, clusterManager, System::nanoTime);
    }

    @VisibleForTesting
    IpdInterNodeLatencyJob(IpdJobRunner ipdJobRunner, IpdInterNodesStats ipdInterNodesStats, ClusterManager clusterManager, Supplier<Long> nanoTimeSupplier) {
        Objects.requireNonNull(ipdJobRunner).register((IpdJob)this);
        this.ipdInterNodesStats = Objects.requireNonNull(ipdInterNodesStats);
        this.clusterManager = Objects.requireNonNull(clusterManager);
        this.nanoTimeSupplier = nanoTimeSupplier;
    }

    public boolean isWorkInProgressJob() {
        return true;
    }

    public void runJob() {
        if (this.clusterManager.isClustered()) {
            this.measureNodesLatencies();
        }
    }

    private void measureNodesLatencies() {
        Set<String> nodesIds = this.getAllOtherNodesIds();
        this.ipdInterNodesStats.remainMetricsForNodes(nodesIds);
        this.pingNodes(nodesIds).forEach(this::subscribeToNodeResponseFuture);
    }

    private List<ClusterNodeExecution<Long>> pingNodes(Set<String> nodesIds) {
        return nodesIds.stream().map(nodeId -> {
            try {
                return this.clusterManager.submitToNode(nodeId, (Callable)new PingTask(this.nanoTimeSupplier.get()), "cluster-manager-executor");
            }
            catch (NoSuchClusterNodeException e) {
                this.ipdInterNodesStats.setNodeDisconnected((String)nodeId);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private void subscribeToNodeResponseFuture(ClusterNodeExecution<Long> clusterNodeExecution) {
        if (clusterNodeExecution.getClusterNode() == null) {
            return;
        }
        String nodeId = IpdInterNodeLatencyJob.getNodeId(clusterNodeExecution.getClusterNode());
        BiFunction<Long, Throwable, Boolean> pingResponseHandler = this.getPingResponseHandler(nodeId);
        clusterNodeExecution.getCompletionStage().toCompletableFuture().orTimeout(LATENCY_TIMEOUT_MS, TimeUnit.MILLISECONDS).handle(pingResponseHandler);
    }

    private BiFunction<Long, Throwable, Boolean> getPingResponseHandler(String nodeId) {
        return (startTime, throwable) -> {
            if (startTime != null) {
                this.ipdInterNodesStats.updateNodeLatency(nodeId, this.nanoTimeSupplier.get() - startTime);
            } else {
                this.ipdInterNodesStats.setNodeDisconnected(nodeId);
            }
            return true;
        };
    }

    private Set<String> getAllOtherNodesIds() {
        return this.clusterManager.getAllNodesInformation().stream().filter(p -> !p.isLocal()).map(IpdInterNodeLatencyJob::getNodeId).collect(Collectors.toSet());
    }

    private static String getNodeId(ClusterNodeInformation clusterNodeInformation) {
        return clusterNodeInformation.getAnonymizedNodeIdentifier();
    }

    public static class PingTask
    implements Callable<Long>,
    Serializable {
        private final long startTimeNs;

        public PingTask(long startTimeNs) {
            this.startTimeNs = startTimeNs;
        }

        @Override
        public Long call() throws Exception {
            return this.startTimeNs;
        }
    }
}

