/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.merge.AbstractNamedContainerCollector;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

class QueueContainerCollector
extends AbstractNamedContainerCollector<QueueContainer> {
    QueueContainerCollector(NodeEngine nodeEngine, ConcurrentMap<String, QueueContainer> containers) {
        super(nodeEngine, containers);
    }

    @Override
    protected MergePolicyConfig getMergePolicyConfig(QueueContainer container) {
        return container.getConfig().getMergePolicyConfig();
    }

    @Override
    protected void destroy(QueueContainer container) {
        container.getItemQueue().clear();
    }

    @Override
    protected void destroyBackup(QueueContainer container) {
        container.getBackupMap().clear();
    }

    @Override
    protected int getMergingValueCount() {
        int size = 0;
        for (Collection containers : this.getCollectedContainers().values()) {
            for (QueueContainer container : containers) {
                size += container.size();
            }
        }
        return size;
    }
}

