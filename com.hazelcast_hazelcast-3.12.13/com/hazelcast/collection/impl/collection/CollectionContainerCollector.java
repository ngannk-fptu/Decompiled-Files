/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.merge.AbstractNamedContainerCollector;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

class CollectionContainerCollector
extends AbstractNamedContainerCollector<CollectionContainer> {
    CollectionContainerCollector(NodeEngine nodeEngine, ConcurrentMap<String, CollectionContainer> containers) {
        super(nodeEngine, containers);
    }

    @Override
    protected MergePolicyConfig getMergePolicyConfig(CollectionContainer container) {
        return container.getConfig().getMergePolicyConfig();
    }

    @Override
    protected void destroy(CollectionContainer container) {
        container.getCollection().clear();
    }

    @Override
    protected void destroyBackup(CollectionContainer container) {
        container.getMap().clear();
    }

    @Override
    protected int getMergingValueCount() {
        int size = 0;
        for (Collection containers : this.getCollectedContainers().values()) {
            for (CollectionContainer container : containers) {
                size += container.size();
            }
        }
        return size;
    }
}

