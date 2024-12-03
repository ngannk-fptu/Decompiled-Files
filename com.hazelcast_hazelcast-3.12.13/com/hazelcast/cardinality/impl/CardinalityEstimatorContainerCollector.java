/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cardinality.impl;

import com.hazelcast.cardinality.impl.CardinalityEstimatorContainer;
import com.hazelcast.config.CardinalityEstimatorConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.merge.AbstractNamedContainerCollector;
import com.hazelcast.util.MapUtil;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

class CardinalityEstimatorContainerCollector
extends AbstractNamedContainerCollector<CardinalityEstimatorContainer> {
    private final Config config;
    private final ConcurrentMap<CardinalityEstimatorContainer, String> containerNames;
    private final ConcurrentMap<CardinalityEstimatorContainer, MergePolicyConfig> containerPolicies;

    CardinalityEstimatorContainerCollector(NodeEngine nodeEngine, ConcurrentMap<String, CardinalityEstimatorContainer> containers) {
        super(nodeEngine, containers);
        this.config = nodeEngine.getConfig();
        this.containerNames = MapUtil.createConcurrentHashMap(containers.size());
        this.containerPolicies = MapUtil.createConcurrentHashMap(containers.size());
    }

    @Override
    protected void onIteration(String containerName, CardinalityEstimatorContainer container) {
        CardinalityEstimatorConfig cardinalityEstimatorConfig = this.config.findCardinalityEstimatorConfig(containerName);
        this.containerNames.put(container, containerName);
        this.containerPolicies.put(container, cardinalityEstimatorConfig.getMergePolicyConfig());
    }

    public String getContainerName(CardinalityEstimatorContainer container) {
        return (String)this.containerNames.get(container);
    }

    @Override
    protected MergePolicyConfig getMergePolicyConfig(CardinalityEstimatorContainer container) {
        return (MergePolicyConfig)this.containerPolicies.get(container);
    }

    @Override
    protected void destroy(CardinalityEstimatorContainer container) {
    }

    @Override
    protected void destroyBackup(CardinalityEstimatorContainer container) {
    }

    @Override
    public void onDestroy() {
        this.containerNames.clear();
        this.containerPolicies.clear();
    }

    @Override
    protected int getMergingValueCount() {
        int size = 0;
        for (Collection containers : this.getCollectedContainers().values()) {
            size += containers.size();
        }
        return size;
    }
}

