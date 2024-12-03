/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.discovery.multicast;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryStrategyFactory;
import com.hazelcast.spi.discovery.multicast.MulticastDiscoveryStrategy;
import com.hazelcast.spi.discovery.multicast.MulticastProperties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class MulticastDiscoveryStrategyFactory
implements DiscoveryStrategyFactory {
    private static final Collection<PropertyDefinition> PROPERTY_DEFINITIONS;

    @Override
    public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType() {
        return MulticastDiscoveryStrategy.class;
    }

    @Override
    public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> prop) {
        return new MulticastDiscoveryStrategy(discoveryNode, logger, prop);
    }

    @Override
    public Collection<PropertyDefinition> getConfigurationProperties() {
        return PROPERTY_DEFINITIONS;
    }

    static {
        ArrayList<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(MulticastProperties.GROUP);
        propertyDefinitions.add(MulticastProperties.PORT);
        PROPERTY_DEFINITIONS = Collections.unmodifiableCollection(propertyDefinitions);
    }
}

