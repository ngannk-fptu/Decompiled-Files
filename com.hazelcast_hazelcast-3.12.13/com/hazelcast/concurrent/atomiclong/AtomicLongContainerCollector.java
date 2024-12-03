/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomiclong;

import com.hazelcast.concurrent.atomiclong.AtomicLongContainer;
import com.hazelcast.config.AtomicLongConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.merge.AbstractNamedContainerCollector;
import com.hazelcast.util.MapUtil;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

class AtomicLongContainerCollector
extends AbstractNamedContainerCollector<AtomicLongContainer> {
    private final Config config;
    private final ConcurrentMap<AtomicLongContainer, String> containerNames;
    private final ConcurrentMap<AtomicLongContainer, MergePolicyConfig> containerPolicies;

    AtomicLongContainerCollector(NodeEngine nodeEngine, ConcurrentMap<String, AtomicLongContainer> containers) {
        super(nodeEngine, containers);
        this.config = nodeEngine.getConfig();
        this.containerNames = MapUtil.createConcurrentHashMap(containers.size());
        this.containerPolicies = MapUtil.createConcurrentHashMap(containers.size());
    }

    @Override
    protected void onIteration(String containerName, AtomicLongContainer container) {
        AtomicLongConfig atomicLongConfig = this.config.findAtomicLongConfig(containerName);
        this.containerNames.put(container, containerName);
        this.containerPolicies.put(container, atomicLongConfig.getMergePolicyConfig());
    }

    public String getContainerName(AtomicLongContainer container) {
        return (String)this.containerNames.get(container);
    }

    @Override
    protected MergePolicyConfig getMergePolicyConfig(AtomicLongContainer container) {
        return (MergePolicyConfig)this.containerPolicies.get(container);
    }

    @Override
    protected void destroy(AtomicLongContainer container) {
    }

    @Override
    protected void destroyBackup(AtomicLongContainer container) {
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

