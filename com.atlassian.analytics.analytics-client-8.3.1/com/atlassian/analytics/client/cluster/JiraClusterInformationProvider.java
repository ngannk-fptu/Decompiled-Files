/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.cluster.ClusterManager
 */
package com.atlassian.analytics.client.cluster;

import com.atlassian.analytics.client.cluster.ClusterInformationProvider;
import com.atlassian.jira.cluster.ClusterManager;

public class JiraClusterInformationProvider
implements ClusterInformationProvider {
    private final ClusterManager clusterManager;

    public JiraClusterInformationProvider(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    @Override
    public String getCurrentNodeId() {
        if (this.clusterManager.isClustered()) {
            return this.clusterManager.getNodeId();
        }
        return null;
    }
}

