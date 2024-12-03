/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.service.cluster.ClusterInformation
 *  com.atlassian.crowd.service.cluster.ClusterNode
 */
package com.atlassian.confluence.impl.user.crowd.cluster;

import com.atlassian.crowd.service.cluster.ClusterInformation;
import com.atlassian.crowd.service.cluster.ClusterNode;
import java.util.Set;

public final class ConfluenceCrowdClusterInformation
implements ClusterInformation {
    private final Set<ClusterNode> nodes;

    public ConfluenceCrowdClusterInformation(Set<ClusterNode> nodes) {
        this.nodes = nodes;
    }

    public Set<ClusterNode> getNodes() {
        return this.nodes;
    }
}

