/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.ClusterNodeInformation
 */
package com.atlassian.analytics.client.cluster;

import com.atlassian.analytics.client.cluster.ClusterInformationProvider;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;

public class ConfluenceClusterInformationProvider
implements ClusterInformationProvider {
    private final ClusterManager clusterManager;

    public ConfluenceClusterInformationProvider(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    @Override
    public String getCurrentNodeId() {
        ClusterNodeInformation clusterNodeInformation;
        if (this.clusterManager.isClustered() && (clusterNodeInformation = this.clusterManager.getThisNodeInformation()) != null) {
            return Boolean.getBoolean("readable.analytics.logfile.name") ? clusterNodeInformation.humanReadableNodeName().orElseGet(() -> ((ClusterNodeInformation)clusterNodeInformation).getAnonymizedNodeIdentifier()) : clusterNodeInformation.getAnonymizedNodeIdentifier();
        }
        return null;
    }
}

