/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.merge;

import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.PartitionSpecificRunnable;
import com.hazelcast.spi.impl.operationexecutor.OperationExecutor;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import com.hazelcast.spi.merge.DiscardMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergePolicyProvider;
import com.hazelcast.spi.partition.IPartitionService;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

public abstract class AbstractContainerCollector<C> {
    private final ConcurrentMap<Integer, Collection<C>> containersByPartitionId = new ConcurrentHashMap<Integer, Collection<C>>();
    private final OperationExecutor operationExecutor;
    private final IPartitionService partitionService;
    private final SplitBrainMergePolicyProvider mergePolicyProvider;
    private CountDownLatch latch;

    protected AbstractContainerCollector(NodeEngine nodeEngine) {
        this.operationExecutor = ((OperationServiceImpl)nodeEngine.getOperationService()).getOperationExecutor();
        this.partitionService = nodeEngine.getPartitionService();
        this.mergePolicyProvider = nodeEngine.getSplitBrainMergePolicyProvider();
    }

    public final void run() {
        int partitionCount = this.partitionService.getPartitionCount();
        this.latch = new CountDownLatch(partitionCount);
        for (int partitionId = 0; partitionId < partitionCount; ++partitionId) {
            this.operationExecutor.execute(new CollectContainerRunnable(partitionId));
        }
        try {
            this.latch.await();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public final ConcurrentMap<Integer, Collection<C>> getCollectedContainers() {
        return this.containersByPartitionId;
    }

    public final void destroy() {
        for (Collection containers : this.containersByPartitionId.values()) {
            for (Object container : containers) {
                this.destroy(container);
            }
        }
        this.containersByPartitionId.clear();
        this.onDestroy();
    }

    protected void onDestroy() {
    }

    protected abstract Iterator<C> containerIterator(int var1);

    protected abstract MergePolicyConfig getMergePolicyConfig(C var1);

    protected abstract void destroy(C var1);

    protected abstract void destroyBackup(C var1);

    protected abstract int getMergingValueCount();

    protected boolean isMergeable(C container) {
        return true;
    }

    private final class CollectContainerRunnable
    implements PartitionSpecificRunnable {
        private final Collection<C> containers = new LinkedList();
        private final int partitionId;

        CollectContainerRunnable(int partitionId) {
            this.partitionId = partitionId;
        }

        @Override
        public int getPartitionId() {
            return this.partitionId;
        }

        @Override
        public void run() {
            try {
                Iterator iterator = AbstractContainerCollector.this.containerIterator(this.partitionId);
                while (iterator.hasNext()) {
                    Object container = iterator.next();
                    this.collect(container);
                    iterator.remove();
                }
            }
            finally {
                if (!this.containers.isEmpty()) {
                    AbstractContainerCollector.this.containersByPartitionId.put(this.partitionId, this.containers);
                }
                AbstractContainerCollector.this.latch.countDown();
            }
        }

        private void collect(C container) {
            if (AbstractContainerCollector.this.partitionService.isPartitionOwner(this.partitionId)) {
                MergePolicyConfig mergePolicyconfig = AbstractContainerCollector.this.getMergePolicyConfig(container);
                SplitBrainMergePolicy mergePolicy = AbstractContainerCollector.this.mergePolicyProvider.getMergePolicy(mergePolicyconfig.getPolicy());
                if (AbstractContainerCollector.this.isMergeable(container) && !(mergePolicy instanceof DiscardMergePolicy)) {
                    this.containers.add(container);
                } else {
                    AbstractContainerCollector.this.destroy(container);
                }
            } else {
                AbstractContainerCollector.this.destroyBackup(container);
            }
        }
    }

    protected final class EmptyIterator
    implements Iterator<C> {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public C next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

