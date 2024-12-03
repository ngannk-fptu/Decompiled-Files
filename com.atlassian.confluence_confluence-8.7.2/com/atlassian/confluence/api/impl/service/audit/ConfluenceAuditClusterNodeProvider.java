/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.core.spi.service.ClusterNodeProvider
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.api.impl.service.audit;

import com.atlassian.audit.core.spi.service.ClusterNodeProvider;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import java.util.Optional;
import javax.annotation.Nonnull;

public class ConfluenceAuditClusterNodeProvider
implements ClusterNodeProvider {
    private final ClusterManager clusterManager;

    public ConfluenceAuditClusterNodeProvider(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    @Nonnull
    public Optional<String> currentNodeId() {
        if (!this.clusterManager.isClustered()) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.clusterManager.getThisNodeInformation()).map(ClusterNodeInformation::getAnonymizedNodeIdentifier);
    }
}

