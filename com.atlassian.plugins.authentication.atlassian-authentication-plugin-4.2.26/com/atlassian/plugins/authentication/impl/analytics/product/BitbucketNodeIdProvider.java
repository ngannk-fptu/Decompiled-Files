/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.cluster.ClusterService
 *  com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.BitbucketImport
 *  javax.inject.Inject
 */
package com.atlassian.plugins.authentication.impl.analytics.product;

import com.atlassian.bitbucket.cluster.ClusterService;
import com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.BitbucketImport;
import com.atlassian.plugins.authentication.impl.analytics.NodeIdProvider;
import javax.inject.Inject;

@BitbucketComponent
public class BitbucketNodeIdProvider
implements NodeIdProvider {
    private final ClusterService clusterService;

    @Inject
    public BitbucketNodeIdProvider(@BitbucketImport ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @Override
    public String getNodeId() {
        return this.clusterService.isClustered() ? this.clusterService.getNodeId() : "NOT_CLUSTERED";
    }
}

