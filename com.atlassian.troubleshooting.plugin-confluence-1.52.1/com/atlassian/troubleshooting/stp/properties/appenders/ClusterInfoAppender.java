/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.properties.appenders;

import com.atlassian.troubleshooting.api.ClusterNode;
import com.atlassian.troubleshooting.api.ClusterService;
import com.atlassian.troubleshooting.stp.spi.RootLevelSupportDataAppender;
import com.atlassian.troubleshooting.stp.spi.SupportDataBuilder;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

public class ClusterInfoAppender
extends RootLevelSupportDataAppender {
    private static final String CLUSTER_INFO = "stp.properties.cluster";
    private static final String CLUSTER_NODE_COUNT = "stp.properties.cluster.node.count";
    private static final String CLUSTER_NODES = "stp.properties.cluster.nodes";
    private static final String CLUSTER_NODE = "stp.properties.cluster.nodes.node";
    private static final String CLUSTER_NODE_ID = "stp.properties.cluster.nodes.node.id";
    private static final String CLUSTER_IP_ADDRESS = "stp.properties.cluster.nodes.node.ipaddress";
    private static final String CLUSTER_NODE_CURRENT = "stp.properties.cluster.nodes.node.current";
    private final ClusterService clusterService;

    @Autowired
    public ClusterInfoAppender(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @Override
    protected void addSupportData(SupportDataBuilder builder) {
        if (!this.clusterService.isClustered()) {
            return;
        }
        SupportDataBuilder clusterBuilder = builder.addCategory(CLUSTER_INFO);
        this.addNodeCountElement(clusterBuilder);
        this.addNodesElement(clusterBuilder);
    }

    private void addNodeCountElement(SupportDataBuilder builder) {
        int nodeCount = this.clusterService.getNodeCount().orElseThrow(IllegalStateException::new);
        builder.addValue(CLUSTER_NODE_COUNT, String.valueOf(nodeCount));
    }

    private void addNodesElement(SupportDataBuilder builder) {
        SupportDataBuilder nodesBuilder = builder.addCategory(CLUSTER_NODES);
        Optional<ClusterNode> maybeCurrentNode = this.clusterService.getCurrentNode();
        this.clusterService.getNodes().forEach(node -> {
            SupportDataBuilder nodeBuilder = nodesBuilder.addCategory(CLUSTER_NODE);
            nodeBuilder.addValue(CLUSTER_NODE_ID, node.getId());
            node.getInetAddress().ifPresent(addr -> nodeBuilder.addValue(CLUSTER_IP_ADDRESS, (String)addr));
            maybeCurrentNode.ifPresent(currentNode -> {
                if (node.getId().equals(currentNode.getId())) {
                    nodeBuilder.addValue(CLUSTER_NODE_CURRENT, Boolean.TRUE.toString());
                }
            });
        });
    }
}

