/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.discovery.impl;

import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.properties.ValidationException;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryStrategyFactory;
import com.hazelcast.spi.discovery.NodeFilter;
import com.hazelcast.spi.discovery.impl.DiscoveryServicePropertiesUtil;
import com.hazelcast.spi.discovery.integration.DiscoveryService;
import com.hazelcast.spi.discovery.integration.DiscoveryServiceSettings;
import com.hazelcast.util.CollectionUtil;
import com.hazelcast.util.ServiceLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DefaultDiscoveryService
implements DiscoveryService {
    private static final String SERVICE_LOADER_TAG = DiscoveryStrategyFactory.class.getCanonicalName();
    private final ILogger logger;
    private final DiscoveryNode discoveryNode;
    private final NodeFilter nodeFilter;
    private final Iterable<DiscoveryStrategy> discoveryStrategies;

    public DefaultDiscoveryService(DiscoveryServiceSettings settings) {
        this.logger = settings.getLogger();
        this.discoveryNode = settings.getDiscoveryNode();
        this.nodeFilter = this.getNodeFilter(settings);
        this.discoveryStrategies = this.loadDiscoveryStrategies(settings);
    }

    @Override
    public void start() {
        for (DiscoveryStrategy discoveryStrategy : this.discoveryStrategies) {
            discoveryStrategy.start();
        }
    }

    @Override
    public Iterable<DiscoveryNode> discoverNodes() {
        HashSet<DiscoveryNode> discoveryNodes = new HashSet<DiscoveryNode>();
        for (DiscoveryStrategy discoveryStrategy : this.discoveryStrategies) {
            Iterable<DiscoveryNode> candidates = discoveryStrategy.discoverNodes();
            if (candidates == null) continue;
            for (DiscoveryNode candidate : candidates) {
                if (!this.validateCandidate(candidate)) continue;
                discoveryNodes.add(candidate);
            }
        }
        return discoveryNodes;
    }

    @Override
    public Map<String, Object> discoverLocalMetadata() {
        HashMap<String, Object> metadata = new HashMap<String, Object>();
        for (DiscoveryStrategy discoveryStrategy : this.discoveryStrategies) {
            metadata.putAll(discoveryStrategy.discoverLocalMetadata());
        }
        return metadata;
    }

    @Override
    public void destroy() {
        for (DiscoveryStrategy discoveryStrategy : this.discoveryStrategies) {
            discoveryStrategy.destroy();
        }
    }

    public Iterable<DiscoveryStrategy> getDiscoveryStrategies() {
        return this.discoveryStrategies;
    }

    private NodeFilter getNodeFilter(DiscoveryServiceSettings settings) {
        DiscoveryConfig discoveryConfig = settings.getDiscoveryConfig();
        ClassLoader configClassLoader = settings.getConfigClassLoader();
        if (discoveryConfig.getNodeFilter() != null) {
            return discoveryConfig.getNodeFilter();
        }
        if (discoveryConfig.getNodeFilterClass() != null) {
            try {
                ClassLoader cl = configClassLoader;
                if (cl == null) {
                    cl = DefaultDiscoveryService.class.getClassLoader();
                }
                String className = discoveryConfig.getNodeFilterClass();
                return (NodeFilter)cl.loadClass(className).newInstance();
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to configure discovery node filter", e);
            }
        }
        return null;
    }

    private boolean validateCandidate(DiscoveryNode candidate) {
        return this.nodeFilter == null || this.nodeFilter.test(candidate);
    }

    private Iterable<DiscoveryStrategy> loadDiscoveryStrategies(DiscoveryServiceSettings settings) {
        ClassLoader configClassLoader = settings.getConfigClassLoader();
        try {
            ArrayList<DiscoveryStrategyConfig> discoveryStrategyConfigs = new ArrayList<DiscoveryStrategyConfig>(settings.getAllDiscoveryConfigs());
            List<DiscoveryStrategyFactory> factories = this.collectFactories(discoveryStrategyConfigs, configClassLoader);
            ArrayList<DiscoveryStrategy> discoveryStrategies = new ArrayList<DiscoveryStrategy>();
            for (DiscoveryStrategyConfig config : discoveryStrategyConfigs) {
                DiscoveryStrategy discoveryStrategy = this.buildDiscoveryStrategy(config, factories);
                discoveryStrategies.add(discoveryStrategy);
            }
            return discoveryStrategies;
        }
        catch (Exception e) {
            if (e instanceof ValidationException) {
                throw new InvalidConfigurationException("Invalid configuration", e);
            }
            throw new RuntimeException("Failed to configure discovery strategies", e);
        }
    }

    private List<DiscoveryStrategyFactory> collectFactories(Collection<DiscoveryStrategyConfig> strategyConfigs, ClassLoader classloader) throws Exception {
        Iterator<DiscoveryStrategyFactory> iterator = ServiceLoader.iterator(DiscoveryStrategyFactory.class, SERVICE_LOADER_TAG, classloader);
        ArrayList<DiscoveryStrategyFactory> factories = new ArrayList<DiscoveryStrategyFactory>();
        while (iterator.hasNext()) {
            factories.add(iterator.next());
        }
        for (DiscoveryStrategyConfig config : strategyConfigs) {
            DiscoveryStrategyFactory factory = config.getDiscoveryStrategyFactory();
            if (factory == null) continue;
            factories.add(factory);
        }
        return factories;
    }

    private DiscoveryStrategy buildDiscoveryStrategy(DiscoveryStrategyConfig config, List<DiscoveryStrategyFactory> candidateFactories) {
        for (DiscoveryStrategyFactory factory : candidateFactories) {
            String factoryClassName;
            Class<? extends DiscoveryStrategy> discoveryStrategyType = factory.getDiscoveryStrategyType();
            String className = discoveryStrategyType.getName();
            if (!className.equals(factoryClassName = this.getFactoryClassName(config))) continue;
            Map<String, Comparable> properties = DiscoveryServicePropertiesUtil.prepareProperties(config.getProperties(), CollectionUtil.nullToEmpty(factory.getConfigurationProperties()));
            return factory.newDiscoveryStrategy(this.discoveryNode, this.logger, properties);
        }
        throw new ValidationException("There is no discovery strategy factory to create '" + config + "' Is it a typo in a strategy classname? Perhaps you forgot to include implementation on a classpath?");
    }

    private String getFactoryClassName(DiscoveryStrategyConfig config) {
        if (config.getDiscoveryStrategyFactory() != null) {
            DiscoveryStrategyFactory factory = config.getDiscoveryStrategyFactory();
            return factory.getDiscoveryStrategyType().getName();
        }
        return config.getClassName();
    }
}

