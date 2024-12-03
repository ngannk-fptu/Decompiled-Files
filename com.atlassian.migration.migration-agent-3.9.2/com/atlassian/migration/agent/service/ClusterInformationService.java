/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.ClusterNodeInformation
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.migration.agent.service.ClusterLimits;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ClusterInformationService {
    private static final String NON_CLUSTERED_NODE_ID = "non-clustered-server-node";
    private final ClusterManager clusterManager;
    private final ClusterLimits clusterLimits;

    public ClusterInformationService(ClusterManager clusterManager, ClusterLimits clusterLimits) {
        this.clusterManager = clusterManager;
        this.clusterLimits = clusterLimits;
    }

    public boolean isClustered() {
        return this.clusterManager.isClustered();
    }

    public String getCurrentNodeId() {
        if (!this.clusterManager.isClustered()) {
            return NON_CLUSTERED_NODE_ID;
        }
        ClusterNodeInformation nodeInfo = this.clusterManager.getThisNodeInformation();
        if (nodeInfo == null) {
            throw new IllegalStateException("Confluence is operating in clustered mode but the node information could not be found");
        }
        return nodeInfo.getAnonymizedNodeIdentifier();
    }

    public ClusterLimits getClusterLimits() {
        return this.clusterLimits;
    }

    public List<String> getAllNodeIds() {
        if (this.clusterManager.isClustered()) {
            return this.clusterManager.getAllNodesInformation().stream().map(ClusterNodeInformation::getAnonymizedNodeIdentifier).collect(Collectors.toList());
        }
        return Collections.singletonList(NON_CLUSTERED_NODE_ID);
    }
}

