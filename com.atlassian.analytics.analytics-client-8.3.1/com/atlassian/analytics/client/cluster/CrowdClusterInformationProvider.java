/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.service.cluster.ClusterService
 */
package com.atlassian.analytics.client.cluster;

import com.atlassian.analytics.client.cluster.ClusterInformationProvider;
import com.atlassian.crowd.service.cluster.ClusterService;

public class CrowdClusterInformationProvider
implements ClusterInformationProvider {
    private final ClusterService clusterService;

    public CrowdClusterInformationProvider(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @Override
    public String getCurrentNodeId() {
        if (this.clusterService.isAvailable()) {
            return this.clusterService.getNodeId();
        }
        return null;
    }
}

