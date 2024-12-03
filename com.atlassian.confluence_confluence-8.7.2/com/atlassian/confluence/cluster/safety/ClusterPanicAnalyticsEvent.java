/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.Critical
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.cluster.safety;

import com.atlassian.analytics.api.annotations.Critical;
import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="confluence.cluster.panic")
@Critical
public class ClusterPanicAnalyticsEvent {
    private final boolean isClustered;
    private final int nodesCount;
    private final int maxNodes;
    private final int maxUsers;

    public ClusterPanicAnalyticsEvent(boolean isClustered, int nodesCount, int maxNodes, int maxUsers) {
        this.isClustered = isClustered;
        this.nodesCount = nodesCount;
        this.maxNodes = maxNodes;
        this.maxUsers = maxUsers;
    }

    public boolean isClustered() {
        return this.isClustered;
    }

    public int getNodesCount() {
        return this.nodesCount;
    }

    public int getMaxNodes() {
        return this.maxNodes;
    }

    public int getMaxUsers() {
        return this.maxUsers;
    }
}

