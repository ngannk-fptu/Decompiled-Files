/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.config.properties.PropertyDefinition
 *  com.hazelcast.logging.ILogger
 *  com.hazelcast.spi.discovery.DiscoveryNode
 *  com.hazelcast.spi.discovery.DiscoveryStrategy
 *  com.hazelcast.spi.discovery.DiscoveryStrategyFactory
 */
package com.hazelcast.aws;

import com.hazelcast.aws.AwsDiscoveryStrategy;
import com.hazelcast.aws.AwsProperties;
import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryStrategyFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class AwsDiscoveryStrategyFactory
implements DiscoveryStrategyFactory {
    public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType() {
        return AwsDiscoveryStrategy.class;
    }

    public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties) {
        return new AwsDiscoveryStrategy(properties);
    }

    public Collection<PropertyDefinition> getConfigurationProperties() {
        AwsProperties[] props = AwsProperties.values();
        ArrayList<PropertyDefinition> definitions = new ArrayList<PropertyDefinition>(props.length);
        for (AwsProperties prop : props) {
            definitions.add(prop.getDefinition());
        }
        return definitions;
    }
}

