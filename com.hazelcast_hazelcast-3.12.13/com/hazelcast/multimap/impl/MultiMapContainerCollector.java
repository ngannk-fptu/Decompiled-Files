/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl;

import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapPartitionContainer;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.merge.AbstractContainerCollector;
import java.util.Collection;
import java.util.Iterator;

class MultiMapContainerCollector
extends AbstractContainerCollector<MultiMapContainer> {
    private final MultiMapPartitionContainer[] partitionContainers;

    MultiMapContainerCollector(NodeEngine nodeEngine, MultiMapPartitionContainer[] partitionContainers) {
        super(nodeEngine);
        this.partitionContainers = partitionContainers;
    }

    @Override
    protected Iterator<MultiMapContainer> containerIterator(int partitionId) {
        MultiMapPartitionContainer partitionContainer = this.partitionContainers[partitionId];
        if (partitionContainer == null) {
            return new AbstractContainerCollector.EmptyIterator(this);
        }
        return partitionContainer.containerMap.values().iterator();
    }

    @Override
    protected MergePolicyConfig getMergePolicyConfig(MultiMapContainer container) {
        return container.getConfig().getMergePolicyConfig();
    }

    @Override
    protected void destroy(MultiMapContainer container) {
        container.destroy();
    }

    @Override
    protected void destroyBackup(MultiMapContainer container) {
        container.destroy();
    }

    @Override
    protected int getMergingValueCount() {
        int size = 0;
        for (Collection containers : this.getCollectedContainers().values()) {
            for (MultiMapContainer container : containers) {
                size += container.size();
            }
        }
        return size;
    }
}

