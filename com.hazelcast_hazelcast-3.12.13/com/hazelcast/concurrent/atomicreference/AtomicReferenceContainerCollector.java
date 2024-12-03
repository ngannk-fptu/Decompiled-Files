/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomicreference;

import com.hazelcast.concurrent.atomicreference.AtomicReferenceContainer;
import com.hazelcast.config.AtomicReferenceConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.merge.AbstractNamedContainerCollector;
import com.hazelcast.util.MapUtil;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

class AtomicReferenceContainerCollector
extends AbstractNamedContainerCollector<AtomicReferenceContainer> {
    private final Config config;
    private final ConcurrentMap<AtomicReferenceContainer, String> containerNames;
    private final ConcurrentMap<AtomicReferenceContainer, MergePolicyConfig> containerPolicies;

    AtomicReferenceContainerCollector(NodeEngine nodeEngine, ConcurrentMap<String, AtomicReferenceContainer> containers) {
        super(nodeEngine, containers);
        this.config = nodeEngine.getConfig();
        this.containerNames = MapUtil.createConcurrentHashMap(containers.size());
        this.containerPolicies = MapUtil.createConcurrentHashMap(containers.size());
    }

    @Override
    protected void onIteration(String containerName, AtomicReferenceContainer container) {
        AtomicReferenceConfig atomicReferenceConfig = this.config.findAtomicReferenceConfig(containerName);
        this.containerNames.put(container, containerName);
        this.containerPolicies.put(container, atomicReferenceConfig.getMergePolicyConfig());
    }

    public String getContainerName(AtomicReferenceContainer container) {
        return (String)this.containerNames.get(container);
    }

    @Override
    protected MergePolicyConfig getMergePolicyConfig(AtomicReferenceContainer container) {
        return (MergePolicyConfig)this.containerPolicies.get(container);
    }

    @Override
    protected void destroy(AtomicReferenceContainer container) {
        container.set(null);
    }

    @Override
    protected void destroyBackup(AtomicReferenceContainer container) {
        container.set(null);
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

