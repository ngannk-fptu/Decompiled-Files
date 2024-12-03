/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.ResettableLazyReference
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.atlassian.confluence.setup.velocity;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.setup.velocity.VelocityContextItemProvider;
import com.atlassian.util.concurrent.ResettableLazyReference;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class ClusterContextItemProvider
implements VelocityContextItemProvider {
    private final ResettableLazyReference<ClusterNodeInformation> clusterNodeRef;

    public ClusterContextItemProvider(final ClusterManager clusterManager) {
        Preconditions.checkNotNull((Object)clusterManager);
        this.clusterNodeRef = new ResettableLazyReference<ClusterNodeInformation>(){

            protected ClusterNodeInformation create() throws Exception {
                return clusterManager.getThisNodeInformation();
            }
        };
    }

    @VisibleForTesting
    void reset() {
        this.clusterNodeRef.reset();
    }

    @Override
    public Map<String, Object> getContextMap() {
        ClusterNodeInformation clusterNode = (ClusterNodeInformation)this.clusterNodeRef.get();
        if (clusterNode == null) {
            return Collections.emptyMap();
        }
        ImmutableMap.Builder builder = ImmutableMap.builder().put((Object)"clusterNodeId", (Object)clusterNode.getAnonymizedNodeIdentifier());
        Optional<String> optionalName = clusterNode.humanReadableNodeName();
        optionalName.ifPresent(name -> builder.put((Object)"clusterNodeName", name));
        return builder.build();
    }
}

