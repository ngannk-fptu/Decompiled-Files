/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.cluster.Joiner;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.ClusterJoinManager;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import java.util.concurrent.atomic.AtomicBoolean;

final class SplitBrainHandler
implements Runnable {
    private final Node node;
    private final AtomicBoolean inProgress = new AtomicBoolean(false);

    public SplitBrainHandler(Node node) {
        this.node = node;
    }

    @Override
    public void run() {
        if (!this.shouldRun()) {
            return;
        }
        if (this.inProgress.compareAndSet(false, true)) {
            try {
                this.searchForOtherClusters();
            }
            finally {
                this.inProgress.set(false);
            }
        }
    }

    private boolean shouldRun() {
        ClusterServiceImpl clusterService = this.node.getClusterService();
        if (!clusterService.isJoined()) {
            return false;
        }
        if (!clusterService.isMaster()) {
            return false;
        }
        if (!this.node.isRunning()) {
            return false;
        }
        ClusterJoinManager clusterJoinManager = clusterService.getClusterJoinManager();
        if (clusterJoinManager.isJoinInProgress()) {
            return false;
        }
        ClusterState clusterState = clusterService.getClusterState();
        return clusterState.isJoinAllowed();
    }

    private void searchForOtherClusters() {
        Joiner joiner = this.node.getJoiner();
        if (joiner != null) {
            joiner.searchForOtherClusters();
        }
    }
}

