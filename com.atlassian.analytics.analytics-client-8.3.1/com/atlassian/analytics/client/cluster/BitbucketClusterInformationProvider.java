/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.cluster.ClusterService
 */
package com.atlassian.analytics.client.cluster;

import com.atlassian.analytics.client.cluster.ClusterInformationProvider;
import com.atlassian.bitbucket.cluster.ClusterService;

public class BitbucketClusterInformationProvider
implements ClusterInformationProvider {
    private ClusterService clusterService;

    public BitbucketClusterInformationProvider(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @Override
    public String getCurrentNodeId() {
        return this.clusterService.getNodeId();
    }
}

