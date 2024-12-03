/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.internal.cluster;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.ratelimiting.cluster.ClusterEventService;
import com.atlassian.ratelimiting.cluster.RateLimitClusterEvent;
import com.atlassian.ratelimiting.node.RateLimitService;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleNodeClusterEventService
implements ClusterEventService {
    private static final Logger logger = LoggerFactory.getLogger(SingleNodeClusterEventService.class);
    private RateLimitService rateLimitService;
    private final EventPublisher eventPublisher;

    public SingleNodeClusterEventService(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    private void onStart() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    private void onStop() {
        this.eventPublisher.unregister((Object)this);
    }

    @Override
    public void publishRateLimitingClusterEvent(RateLimitClusterEvent event) {
        logger.debug("Publishing event to cluster: [{}]", (Object)event);
        this.eventPublisher.publish((Object)event);
    }

    @Override
    @EventListener
    public void handleRateLimitClusterEvent(RateLimitClusterEvent event) {
        logger.debug("Received event from cluster: [{}]", (Object)event);
    }

    @Override
    public void registerRateLimitService(RateLimitService rateLimitService) {
        logger.debug("Registering RateLimitService: [{}]", (Object)rateLimitService);
        this.rateLimitService = rateLimitService;
    }
}

