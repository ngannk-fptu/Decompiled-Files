/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.discovery.impl;

import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.integration.DiscoveryService;
import com.hazelcast.util.Preconditions;
import java.util.Map;

public class PredefinedDiscoveryService
implements DiscoveryService {
    private final DiscoveryStrategy strategy;

    public PredefinedDiscoveryService(DiscoveryStrategy strategy) {
        this.strategy = Preconditions.checkNotNull(strategy, "Discovery strategy should be not-null");
    }

    @Override
    public void start() {
        this.strategy.start();
    }

    @Override
    public Iterable<DiscoveryNode> discoverNodes() {
        return this.strategy.discoverNodes();
    }

    @Override
    public Map<String, Object> discoverLocalMetadata() {
        return this.strategy.discoverLocalMetadata();
    }

    @Override
    public void destroy() {
        this.strategy.destroy();
    }
}

