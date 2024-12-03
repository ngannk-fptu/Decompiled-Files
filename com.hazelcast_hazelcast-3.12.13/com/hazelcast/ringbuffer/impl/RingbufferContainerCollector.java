/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer.impl;

import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.ringbuffer.impl.RingbufferContainer;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.impl.merge.AbstractContainerCollector;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

class RingbufferContainerCollector
extends AbstractContainerCollector<RingbufferContainer> {
    private final Map<Integer, Map<ObjectNamespace, RingbufferContainer>> containers;

    RingbufferContainerCollector(NodeEngine nodeEngine, Map<Integer, Map<ObjectNamespace, RingbufferContainer>> containers) {
        super(nodeEngine);
        this.containers = containers;
    }

    @Override
    protected Iterator<RingbufferContainer> containerIterator(int partitionId) {
        Map<ObjectNamespace, RingbufferContainer> containerMap = this.containers.get(partitionId);
        if (containerMap == null) {
            return new AbstractContainerCollector.EmptyIterator(this);
        }
        return containerMap.values().iterator();
    }

    @Override
    protected MergePolicyConfig getMergePolicyConfig(RingbufferContainer container) {
        return container.getConfig().getMergePolicyConfig();
    }

    @Override
    protected void destroy(RingbufferContainer container) {
        container.clear();
    }

    @Override
    protected void destroyBackup(RingbufferContainer container) {
        container.clear();
    }

    @Override
    protected boolean isMergeable(RingbufferContainer container) {
        String containerServiceName = container.getNamespace().getServiceName();
        return "hz:impl:ringbufferService".equals(containerServiceName);
    }

    @Override
    protected int getMergingValueCount() {
        int size = 0;
        for (Collection containers : this.getCollectedContainers().values()) {
            for (RingbufferContainer container : containers) {
                size = (int)((long)size + container.size());
            }
        }
        return size;
    }
}

