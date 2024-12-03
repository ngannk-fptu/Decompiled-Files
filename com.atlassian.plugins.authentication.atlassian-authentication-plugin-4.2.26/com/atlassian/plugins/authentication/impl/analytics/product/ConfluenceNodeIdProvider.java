/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.ClusterNodeInformation
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport
 *  javax.inject.Inject
 */
package com.atlassian.plugins.authentication.impl.analytics.product;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import com.atlassian.plugins.authentication.impl.analytics.NodeIdProvider;
import java.util.Optional;
import javax.inject.Inject;

@ConfluenceComponent
public class ConfluenceNodeIdProvider
implements NodeIdProvider {
    private final ClusterManager clusterManager;

    @Inject
    public ConfluenceNodeIdProvider(@ConfluenceImport ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    @Override
    public String getNodeId() {
        return Optional.ofNullable(this.clusterManager.getThisNodeInformation()).map(ClusterNodeInformation::getAnonymizedNodeIdentifier).orElse("NOT_CLUSTERED");
    }
}

