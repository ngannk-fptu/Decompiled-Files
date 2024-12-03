/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.merge;

import com.hazelcast.partition.strategy.StringPartitioningStrategy;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.merge.AbstractContainerCollector;
import com.hazelcast.spi.partition.IPartitionService;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractNamedContainerCollector<C>
extends AbstractContainerCollector<C> {
    protected final ConcurrentMap<String, C> containers;
    private final IPartitionService partitionService;

    protected AbstractNamedContainerCollector(NodeEngine nodeEngine, ConcurrentMap<String, C> containers) {
        super(nodeEngine);
        this.containers = containers;
        this.partitionService = nodeEngine.getPartitionService();
    }

    @Override
    protected final Iterator<C> containerIterator(int partitionId) {
        return new ContainerIterator(partitionId);
    }

    protected void onIteration(String containerName, C container) {
    }

    int getContainerPartitionId(String containerName) {
        String partitionKey = StringPartitioningStrategy.getPartitionKey(containerName);
        return this.partitionService.getPartitionId(partitionKey);
    }

    class ContainerIterator
    implements Iterator<C> {
        private final Iterator<Map.Entry<String, C>> containerEntryIterator;
        private final int partitionId;
        private boolean hasNextWasCalled;
        private Map.Entry<String, C> currentContainerEntry;

        ContainerIterator(int partitionId) {
            this.containerEntryIterator = AbstractNamedContainerCollector.this.containers.entrySet().iterator();
            this.partitionId = partitionId;
        }

        @Override
        public boolean hasNext() {
            this.hasNextWasCalled = true;
            while (this.containerEntryIterator.hasNext()) {
                Map.Entry next = this.containerEntryIterator.next();
                if (AbstractNamedContainerCollector.this.getContainerPartitionId(next.getKey()) != this.partitionId) continue;
                AbstractNamedContainerCollector.this.onIteration(next.getKey(), next.getValue());
                this.currentContainerEntry = next;
                return true;
            }
            this.currentContainerEntry = null;
            return false;
        }

        @Override
        public C next() {
            if (!this.hasNextWasCalled) {
                throw new IllegalStateException("This iterator needs hasNext() to be called before next()");
            }
            this.hasNextWasCalled = false;
            if (this.currentContainerEntry != null) {
                return this.currentContainerEntry.getValue();
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            if (this.currentContainerEntry != null) {
                this.containerEntryIterator.remove();
            }
        }
    }
}

