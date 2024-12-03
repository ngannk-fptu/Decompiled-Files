/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.manager.ClusterEnvironmentProvider
 */
package com.atlassian.confluence.plugin;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.plugin.manager.ClusterEnvironmentProvider;

public class ConfluenceClusterEnvironmentProvider
implements ClusterEnvironmentProvider {
    private final ClusterManager clusterManager;

    public ConfluenceClusterEnvironmentProvider(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    public boolean isInCluster() {
        return this.clusterManager.isClustered();
    }
}

