/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.service.cluster.ClusterInformation
 *  com.atlassian.crowd.service.cluster.ClusterNode
 *  com.atlassian.crowd.service.cluster.ClusterService
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.impl.user.crowd.cluster;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.impl.user.crowd.cluster.ConfluenceCrowdClusterInformation;
import com.atlassian.confluence.impl.user.crowd.cluster.ConfluenceCrowdClusterNode;
import com.atlassian.crowd.service.cluster.ClusterInformation;
import com.atlassian.crowd.service.cluster.ClusterNode;
import com.atlassian.crowd.service.cluster.ClusterService;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public final class ConfluenceCrowdClusterService
implements ClusterService {
    private static final String NOT_CLUSTERED = "NOT_CLUSTERED";
    private final ClusterManager clusterManager;

    public ConfluenceCrowdClusterService(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    public boolean isAvailable() {
        return this.clusterManager.isClustered();
    }

    @Nonnull
    public String getNodeId() {
        return this.getClusterNode().map(ClusterNode::getNodeId).orElse(NOT_CLUSTERED);
    }

    public Optional<ClusterNode> getClusterNode() {
        ClusterNodeInformation nodeInformation = this.clusterManager.getThisNodeInformation();
        return Optional.ofNullable(nodeInformation).map(info -> new ConfluenceCrowdClusterNode((ClusterNodeInformation)info, true));
    }

    public ClusterInformation getInformation() {
        ClusterNodeInformation thisNodeInformation = this.clusterManager.getThisNodeInformation();
        Set nodes = this.clusterManager.getAllNodesInformation().stream().map(info -> new ConfluenceCrowdClusterNode((ClusterNodeInformation)info, info.equals(thisNodeInformation))).collect(Collectors.toSet());
        return new ConfluenceCrowdClusterInformation(Collections.unmodifiableSet(nodes));
    }
}

