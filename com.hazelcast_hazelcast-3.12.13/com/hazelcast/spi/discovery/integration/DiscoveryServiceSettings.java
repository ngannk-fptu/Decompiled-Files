/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.discovery.integration;

import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.integration.DiscoveryMode;
import java.util.ArrayList;
import java.util.List;

public final class DiscoveryServiceSettings {
    private DiscoveryNode discoveryNode;
    private ILogger logger;
    private ClassLoader configClassLoader;
    private DiscoveryConfig discoveryConfig;
    private List<DiscoveryStrategyConfig> aliasedDiscoveryConfigs = new ArrayList<DiscoveryStrategyConfig>();
    private DiscoveryMode discoveryMode;

    public DiscoveryNode getDiscoveryNode() {
        return this.discoveryNode;
    }

    public DiscoveryServiceSettings setDiscoveryNode(DiscoveryNode discoveryNode) {
        this.discoveryNode = discoveryNode;
        return this;
    }

    public ILogger getLogger() {
        return this.logger;
    }

    public DiscoveryServiceSettings setLogger(ILogger logger) {
        this.logger = logger;
        return this;
    }

    public ClassLoader getConfigClassLoader() {
        return this.configClassLoader;
    }

    public DiscoveryServiceSettings setConfigClassLoader(ClassLoader configClassLoader) {
        this.configClassLoader = configClassLoader;
        return this;
    }

    public DiscoveryConfig getDiscoveryConfig() {
        return this.discoveryConfig;
    }

    public DiscoveryServiceSettings setDiscoveryConfig(DiscoveryConfig discoveryConfig) {
        this.discoveryConfig = discoveryConfig;
        return this;
    }

    public DiscoveryMode getDiscoveryMode() {
        return this.discoveryMode;
    }

    public DiscoveryServiceSettings setDiscoveryMode(DiscoveryMode discoveryMode) {
        this.discoveryMode = discoveryMode;
        return this;
    }

    public List<DiscoveryStrategyConfig> getAllDiscoveryConfigs() {
        ArrayList<DiscoveryStrategyConfig> result = new ArrayList<DiscoveryStrategyConfig>();
        result.addAll(this.discoveryConfig.getDiscoveryStrategyConfigs());
        result.addAll(this.aliasedDiscoveryConfigs);
        return result;
    }

    public DiscoveryServiceSettings setAliasedDiscoveryConfigs(List<DiscoveryStrategyConfig> aliasedDiscoveryConfigs) {
        this.aliasedDiscoveryConfigs = aliasedDiscoveryConfigs;
        return this;
    }
}

