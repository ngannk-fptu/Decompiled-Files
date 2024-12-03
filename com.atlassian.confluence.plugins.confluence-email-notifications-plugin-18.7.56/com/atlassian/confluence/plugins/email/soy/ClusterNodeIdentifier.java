/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.ClusterNodeInformation
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.email.soy;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class ClusterNodeIdentifier
implements SoyServerFunction<String> {
    private final ClusterManager clusterManager;

    public ClusterNodeIdentifier(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    public String apply(Object ... objects) {
        ClusterNodeInformation node = this.clusterManager.getThisNodeInformation();
        if (node != null) {
            return node.getAnonymizedNodeIdentifier();
        }
        return "";
    }

    public String getName() {
        return "notificationsClusterNodeId";
    }

    public Set<Integer> validArgSizes() {
        return ImmutableSet.of((Object)0);
    }
}

