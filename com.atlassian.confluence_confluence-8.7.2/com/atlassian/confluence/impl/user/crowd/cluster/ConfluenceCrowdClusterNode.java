/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.service.cluster.ClusterNode
 *  com.atlassian.crowd.service.cluster.ClusterNodeDetails
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.user.crowd.cluster;

import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.crowd.service.cluster.ClusterNode;
import com.atlassian.crowd.service.cluster.ClusterNodeDetails;
import java.time.Instant;
import java.util.Optional;
import javax.annotation.Nullable;

public final class ConfluenceCrowdClusterNode
implements ClusterNode {
    private final ClusterNodeInformation nodeInformation;
    private final boolean isCurrentNode;

    public ConfluenceCrowdClusterNode(ClusterNodeInformation nodeInformation, boolean isCurrentNode) {
        this.nodeInformation = nodeInformation;
        this.isCurrentNode = isCurrentNode;
    }

    public String getNodeId() {
        return this.nodeInformation.getAnonymizedNodeIdentifier();
    }

    @Nullable
    public String getNodeName() {
        return this.nodeInformation.humanReadableNodeName().orElse(null);
    }

    public Instant getLastHeartbeat() {
        throw new UnsupportedOperationException("crowd-4.x : node heartbeat not available");
    }

    public boolean isLocal() {
        return this.isCurrentNode;
    }

    public Optional<ClusterNodeDetails> getDetails() {
        return Optional.empty();
    }
}

