/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.ClusterNodeInformation
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.email.soy;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.fugue.Maybe;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class ClusterNodeName
implements SoyServerFunction<String> {
    private final ClusterManager clusterManager;

    public ClusterNodeName(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    public String apply(Object ... objects) {
        ClusterNodeInformation node = this.clusterManager.getThisNodeInformation();
        if (node != null) {
            Maybe nodeName = node.getHumanReadableNodeName();
            return (String)nodeName.getOrElse((Object)"");
        }
        return "";
    }

    public String getName() {
        return "notificationsClusterNodeName";
    }

    public Set<Integer> validArgSizes() {
        return ImmutableSet.of((Object)0);
    }
}

