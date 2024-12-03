/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.ScheduledExecutorConfig;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorContainer;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorPartition;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.merge.AbstractContainerCollector;
import java.util.Collection;
import java.util.Iterator;

class ScheduledExecutorContainerCollector
extends AbstractContainerCollector<ScheduledExecutorContainer> {
    private final ScheduledExecutorPartition[] partitions;

    ScheduledExecutorContainerCollector(NodeEngine nodeEngine, ScheduledExecutorPartition[] partitions) {
        super(nodeEngine);
        this.partitions = partitions;
    }

    @Override
    protected Iterator<ScheduledExecutorContainer> containerIterator(int partitionId) {
        ScheduledExecutorPartition partition = this.partitions[partitionId];
        if (partition == null) {
            return new AbstractContainerCollector.EmptyIterator(this);
        }
        return partition.iterator();
    }

    @Override
    public MergePolicyConfig getMergePolicyConfig(ScheduledExecutorContainer container) {
        ScheduledExecutorConfig config = container.getNodeEngine().getConfig().findScheduledExecutorConfig(container.getName());
        return config.getMergePolicyConfig();
    }

    @Override
    protected void destroy(ScheduledExecutorContainer container) {
        container.destroy();
    }

    @Override
    protected void destroyBackup(ScheduledExecutorContainer container) {
        container.destroy();
    }

    @Override
    protected int getMergingValueCount() {
        int size = 0;
        for (Collection containers : this.getCollectedContainers().values()) {
            for (ScheduledExecutorContainer container : containers) {
                size += container.tasks.size();
            }
        }
        return size;
    }
}

