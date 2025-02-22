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
package com.hazelcast.kubernetes;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.kubernetes.HazelcastKubernetesDiscoveryStrategy;
import com.hazelcast.kubernetes.KubernetesProperties;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryStrategyFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class HazelcastKubernetesDiscoveryStrategyFactory
implements DiscoveryStrategyFactory {
    private static final Collection<PropertyDefinition> PROPERTY_DEFINITIONS = Collections.unmodifiableCollection(Arrays.asList(KubernetesProperties.SERVICE_DNS, KubernetesProperties.SERVICE_DNS_TIMEOUT, KubernetesProperties.SERVICE_NAME, KubernetesProperties.SERVICE_LABEL_NAME, KubernetesProperties.SERVICE_LABEL_VALUE, KubernetesProperties.NAMESPACE, KubernetesProperties.POD_LABEL_NAME, KubernetesProperties.POD_LABEL_VALUE, KubernetesProperties.RESOLVE_NOT_READY_ADDRESSES, KubernetesProperties.USE_NODE_NAME_AS_EXTERNAL_ADDRESS, KubernetesProperties.KUBERNETES_API_RETIRES, KubernetesProperties.KUBERNETES_MASTER_URL, KubernetesProperties.KUBERNETES_API_TOKEN, KubernetesProperties.KUBERNETES_CA_CERTIFICATE, KubernetesProperties.SERVICE_PORT));

    public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType() {
        return HazelcastKubernetesDiscoveryStrategy.class;
    }

    public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties) {
        return new HazelcastKubernetesDiscoveryStrategy(logger, properties);
    }

    public Collection<PropertyDefinition> getConfigurationProperties() {
        return PROPERTY_DEFINITIONS;
    }
}

