/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.ClusterNodeInformation
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.troubleshooting.api.ClusterNode;
import com.atlassian.troubleshooting.api.ClusterService;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfluenceClusterService
implements ClusterService {
    private final ClusterManager clusterManager;

    @Autowired
    public ConfluenceClusterService(ClusterManager clusterManager) {
        this.clusterManager = Objects.requireNonNull(clusterManager);
    }

    @Override
    public Optional<ClusterNode> getCurrentNode() {
        return Optional.ofNullable(this.clusterManager.getThisNodeInformation()).map(ConfluenceClusterService::asClusterNode);
    }

    @Override
    public Optional<String> getCurrentNodeId() {
        return Optional.ofNullable(this.clusterManager.getThisNodeInformation()).map(ConfluenceClusterService::asClusterNode).map(ClusterNode::getId);
    }

    @Override
    @Nonnull
    public Collection<ClusterNode> getNodes() {
        return this.clusterManager.getAllNodesInformation().stream().map(ConfluenceClusterService::asClusterNode).collect(Collectors.toList());
    }

    private static ClusterNode asClusterNode(ClusterNodeInformation nodeInformation) {
        String inetAddress = Optional.ofNullable(nodeInformation.getLocalSocketAddress()).map(InetSocketAddress::toString).orElse(null);
        return new ClusterNode(nodeInformation.getAnonymizedNodeIdentifier(), inetAddress, nodeInformation.humanReadableNodeName().orElse(null));
    }
}

