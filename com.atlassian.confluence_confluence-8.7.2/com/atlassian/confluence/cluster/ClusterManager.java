/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.FifoBuffer
 *  org.checkerframework.checker.nullness.qual.EnsuresNonNullIf
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.ClusterConfig;
import com.atlassian.confluence.cluster.ClusterException;
import com.atlassian.confluence.cluster.ClusterInformation;
import com.atlassian.confluence.cluster.ClusterInvariants;
import com.atlassian.confluence.cluster.ClusterNodeExecution;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.cluster.ClusteredLock;
import com.atlassian.confluence.cluster.NoSuchClusterNodeException;
import com.atlassian.confluence.cluster.NodeStatus;
import com.atlassian.confluence.concurrent.LockFactory;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.core.task.FifoBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ClusterManager
extends LockFactory {
    public static final String DEFAULT_EXECUTOR_SVC_NAME = "cluster-manager-executor";

    public boolean isClusterSupported();

    @EnsuresNonNullIf(expression={"getThisNodesInformation()"}, result=true)
    public boolean isClustered();

    @Deprecated(since="8.2", forRemoval=true)
    public ClusterInformation getClusterInformation();

    @Deprecated
    public ClusteredLock getClusteredLock(String var1);

    @Deprecated(since="8.2", forRemoval=true)
    public <T> FifoBuffer<T> getFifoBuffer(String var1);

    public void publishEvent(ConfluenceEvent var1);

    public void publishEventImmediately(ConfluenceEvent var1);

    public @Nullable ClusterNodeInformation getThisNodeInformation();

    public Collection<ClusterNodeInformation> getAllNodesInformation();

    public void configure(ClusterConfig var1);

    public boolean isConfigured();

    public void reconfigure(ClusterConfig var1);

    public void stopCluster();

    public void startCluster();

    @Deprecated
    public Map<Integer, NodeStatus> getNodeStatuses();

    public Map<ClusterNodeInformation, NodeStatus> getNodeStatusMap();

    public Map<ClusterNodeInformation, CompletionStage<NodeStatus>> getNodeStatusMapAsync();

    public ClusterInvariants getClusterInvariants() throws ClusterException;

    public <T> CompletionStage<T> submitToKeyOwner(Callable<T> var1, String var2, Object var3);

    public <T> ClusterNodeExecution<T> submitToNode(@Nullable String var1, Callable<T> var2, String var3) throws NoSuchClusterNodeException;

    public <T> List<ClusterNodeExecution<T>> submitToAllNodes(Callable<T> var1, String var2);

    public long getClusterUptime();
}

