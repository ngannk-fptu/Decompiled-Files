/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.merge;

import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.SplitBrainHandlerService;
import com.hazelcast.spi.impl.PartitionSpecificRunnable;
import com.hazelcast.spi.impl.operationexecutor.OperationExecutor;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import com.hazelcast.spi.partition.IPartitionService;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public abstract class AbstractSplitBrainHandlerService<Store>
implements SplitBrainHandlerService {
    private final IPartitionService partitionService;
    private final OperationExecutor operationExecutor;

    protected AbstractSplitBrainHandlerService(NodeEngine nodeEngine) {
        this.partitionService = nodeEngine.getPartitionService();
        this.operationExecutor = ((OperationServiceImpl)nodeEngine.getOperationService()).getOperationExecutor();
    }

    @Override
    public final Runnable prepareMergeRunnable() {
        ConcurrentLinkedQueue mergingStores = new ConcurrentLinkedQueue();
        this.collectStores(mergingStores);
        return this.newMergeRunnable(mergingStores);
    }

    private void collectStores(ConcurrentLinkedQueue<Store> mergingStores) {
        int partitionCount = this.partitionService.getPartitionCount();
        CountDownLatch latch = new CountDownLatch(partitionCount);
        for (int i = 0; i < partitionCount; ++i) {
            this.operationExecutor.execute(new StoreCollector(mergingStores, i, latch));
        }
        try {
            latch.await();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    void asyncDestroyStores(final Collection<Store> stores, final int partitionID) {
        this.operationExecutor.execute(new PartitionSpecificRunnable(){

            @Override
            public void run() {
                for (Object store : stores) {
                    AbstractSplitBrainHandlerService.this.destroyStore(store);
                }
            }

            @Override
            public int getPartitionId() {
                return partitionID;
            }
        });
    }

    private boolean isLocalPartition(int partitionId) {
        return this.partitionService.isPartitionOwner(partitionId);
    }

    protected void onStoreCollection(Store store) {
    }

    protected abstract Runnable newMergeRunnable(Collection<Store> var1);

    protected abstract Iterator<Store> storeIterator(int var1);

    protected abstract void destroyStore(Store var1);

    protected abstract boolean hasEntries(Store var1);

    protected abstract boolean hasMergeablePolicy(Store var1);

    private class StoreCollector
    implements PartitionSpecificRunnable {
        private final int partitionId;
        private final CountDownLatch latch;
        private final ConcurrentLinkedQueue<Store> mergingStores;

        StoreCollector(ConcurrentLinkedQueue<Store> mergingStores, int partitionId, CountDownLatch latch) {
            this.mergingStores = mergingStores;
            this.partitionId = partitionId;
            this.latch = latch;
        }

        @Override
        public int getPartitionId() {
            return this.partitionId;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            LinkedList storesToDestroy = new LinkedList();
            try {
                Iterator iterator = AbstractSplitBrainHandlerService.this.storeIterator(this.partitionId);
                while (iterator.hasNext()) {
                    Object store = iterator.next();
                    if (AbstractSplitBrainHandlerService.this.isLocalPartition(this.partitionId) && AbstractSplitBrainHandlerService.this.hasEntries(store) && AbstractSplitBrainHandlerService.this.hasMergeablePolicy(store)) {
                        this.mergingStores.add(store);
                    } else {
                        storesToDestroy.add(store);
                    }
                    AbstractSplitBrainHandlerService.this.onStoreCollection(store);
                    iterator.remove();
                }
                AbstractSplitBrainHandlerService.this.asyncDestroyStores(storesToDestroy, this.partitionId);
            }
            finally {
                this.latch.countDown();
            }
        }
    }
}

