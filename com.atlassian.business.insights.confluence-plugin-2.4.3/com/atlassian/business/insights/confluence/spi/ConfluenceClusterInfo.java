/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.cluster.ClusterInfo
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.ClusterNodeInformation
 *  javax.annotation.Nullable
 */
package com.atlassian.business.insights.confluence.spi;

import com.atlassian.business.insights.api.cluster.ClusterInfo;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import java.util.Optional;
import javax.annotation.Nullable;

public class ConfluenceClusterInfo
implements ClusterInfo {
    private final ClusterManager clusterManager;

    public ConfluenceClusterInfo(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    @Nullable
    public String getNodeId() {
        return Optional.ofNullable(this.clusterManager.getThisNodeInformation()).map(ClusterNodeInformation::getAnonymizedNodeIdentifier).orElse(null);
    }
}

