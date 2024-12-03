/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.spi.discovery.NodeFilter;
import com.hazelcast.spi.discovery.integration.DiscoveryServiceProvider;
import java.util.List;

public class DiscoveryConfigReadOnly
extends DiscoveryConfig {
    DiscoveryConfigReadOnly(DiscoveryConfig discoveryConfig) {
        super(discoveryConfig.getDiscoveryServiceProvider(), discoveryConfig.getNodeFilter(), discoveryConfig.getNodeFilterClass(), discoveryConfig.getDiscoveryStrategyConfigs());
    }

    @Override
    public void setDiscoveryServiceProvider(DiscoveryServiceProvider discoveryServiceProvider) {
        throw new UnsupportedOperationException("Configuration is readonly");
    }

    @Override
    public void setNodeFilter(NodeFilter nodeFilter) {
        throw new UnsupportedOperationException("Configuration is readonly");
    }

    @Override
    public void setNodeFilterClass(String nodeFilterClass) {
        throw new UnsupportedOperationException("Configuration is readonly");
    }

    @Override
    public void addDiscoveryStrategyConfig(DiscoveryStrategyConfig discoveryStrategyConfig) {
        throw new UnsupportedOperationException("Configuration is readonly");
    }

    @Override
    public void setDiscoveryStrategyConfigs(List<DiscoveryStrategyConfig> discoveryStrategyConfigs) {
        throw new UnsupportedOperationException("Configuration is readonly");
    }
}

