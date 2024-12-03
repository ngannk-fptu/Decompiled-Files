/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.cluster;

import com.atlassian.ratelimiting.cluster.RateLimitClusterEvent;
import com.atlassian.ratelimiting.node.RateLimitService;

public interface ClusterEventService {
    public void publishRateLimitingClusterEvent(RateLimitClusterEvent var1);

    public void registerRateLimitService(RateLimitService var1);

    public void handleRateLimitClusterEvent(RateLimitClusterEvent var1);
}

