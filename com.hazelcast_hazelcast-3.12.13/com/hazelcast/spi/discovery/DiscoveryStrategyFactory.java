/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.discovery;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import java.util.Collection;
import java.util.Map;

public interface DiscoveryStrategyFactory {
    public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType();

    public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode var1, ILogger var2, Map<String, Comparable> var3);

    public Collection<PropertyDefinition> getConfigurationProperties();
}

