/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.merge;

import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.config.MergePolicyValidator;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.DataType;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.impl.merge.AbstractSplitBrainHandlerService;
import com.hazelcast.spi.merge.MergingEntry;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.MutableLong;
import com.hazelcast.util.function.BiConsumer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public abstract class AbstractMergeRunnable<K, V, Store, MergingItem extends MergingEntry<K, V>>
implements Runnable {
    private static final long TIMEOUT_FACTOR = 500L;
    private static final long MINIMAL_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(5L);
    private final Semaphore semaphore = new Semaphore(0);
    private final ILogger logger;
    private final String serviceName;
    private final ClusterService clusterService;
    private final OperationService operationService;
    private final IPartitionService partitionService;
    private final AbstractSplitBrainHandlerService<Store> splitBrainHandlerService;
    private final InternalSerializationService serializationService;
    private Map<String, Collection<Store>> mergingStoresByName;

    protected AbstractMergeRunnable(String serviceName, Collection<Store> mergingStores, AbstractSplitBrainHandlerService<Store> splitBrainHandlerService, NodeEngine nodeEngine) {
        this.mergingStoresByName = this.groupStoresByName(mergingStores);
        this.serviceName = serviceName;
        this.logger = nodeEngine.getLogger(this.getClass());
        this.partitionService = nodeEngine.getPartitionService();
        this.clusterService = nodeEngine.getClusterService();
        this.operationService = nodeEngine.getOperationService();
        this.serializationService = (InternalSerializationService)nodeEngine.getSerializationService();
        this.splitBrainHandlerService = splitBrainHandlerService;
    }

    private Map<String, Collection<Store>> groupStoresByName(Collection<Store> stores) {
        HashMap<String, Collection<Store>> storesByName = new HashMap<String, Collection<Store>>();
        for (Store store : stores) {
            String dataStructureName = this.getDataStructureName(store);
            LinkedList<Store> storeList = (LinkedList<Store>)storesByName.get(dataStructureName);
            if (storeList == null) {
                storeList = new LinkedList<Store>();
                storesByName.put(dataStructureName, storeList);
            }
            storeList.add(store);
        }
        return storesByName;
    }

    @Override
    public final void run() {
        this.onRunStart();
        int mergedCount = 0;
        mergedCount += this.mergeWithSplitBrainMergePolicy();
        this.waitMergeEnd(mergedCount += this.mergeWithLegacyMergePolicy());
    }

    protected void onRunStart() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int mergeWithSplitBrainMergePolicy() {
        int mergedCount = 0;
        Iterator<Map.Entry<String, Collection<Store>>> iterator = this.mergingStoresByName.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Collection<Store>> entry = iterator.next();
            String dataStructureName = entry.getKey();
            Collection<Store> stores = entry.getValue();
            if (!(this.getMergePolicy(dataStructureName) instanceof SplitBrainMergePolicy)) continue;
            MergingItemBiConsumer consumer = this.newConsumer(dataStructureName);
            for (Store store : stores) {
                try {
                    this.mergeStore(store, consumer);
                    consumer.consumeRemaining();
                }
                finally {
                    this.asyncDestroyStores(Collections.singleton(store));
                }
            }
            mergedCount += consumer.mergedCount;
            this.onMerge(dataStructureName);
            iterator.remove();
        }
        return mergedCount;
    }

    private MergingItemBiConsumer newConsumer(String dataStructureName) {
        SplitBrainMergePolicy<V, MergingItem> policy = this.getSplitBrainMergePolicy(dataStructureName);
        int batchSize = this.getBatchSize(dataStructureName);
        return new MergingItemBiConsumer(dataStructureName, policy, batchSize);
    }

    private SplitBrainMergePolicy<V, MergingItem> getSplitBrainMergePolicy(String dataStructureName) {
        return (SplitBrainMergePolicy)this.getMergePolicy(dataStructureName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int mergeWithLegacyMergePolicy() {
        LegacyOperationBiConsumer consumer = new LegacyOperationBiConsumer();
        Iterator<Map.Entry<String, Collection<Store>>> iterator = this.mergingStoresByName.entrySet().iterator();
        while (iterator.hasNext()) {
            try {
                Map.Entry<String, Collection<Store>> entry = iterator.next();
                String dataStructureName = entry.getKey();
                Collection<Store> stores = entry.getValue();
                if (this.canMergeLegacy(dataStructureName)) {
                    for (Store store : stores) {
                        try {
                            this.mergeStoreLegacy(store, consumer);
                        }
                        finally {
                            this.asyncDestroyStores(Collections.singleton(store));
                        }
                    }
                    this.onMerge(dataStructureName);
                    continue;
                }
                this.asyncDestroyStores(stores);
            }
            finally {
                iterator.remove();
            }
        }
        return consumer.mergedCount;
    }

    private boolean canMergeLegacy(String dataStructureName) {
        Object mergePolicy = this.getMergePolicy(dataStructureName);
        InMemoryFormat inMemoryFormat = this.getInMemoryFormat(dataStructureName);
        return MergePolicyValidator.checkMergePolicySupportsInMemoryFormat(dataStructureName, mergePolicy, inMemoryFormat, false, this.logger);
    }

    private void waitMergeEnd(int mergedCount) {
        try {
            long timeoutMillis = Math.max((long)mergedCount * 500L, MINIMAL_TIMEOUT_MILLIS);
            if (!this.semaphore.tryAcquire(mergedCount, timeoutMillis, TimeUnit.MILLISECONDS)) {
                this.logger.warning("Split-brain healing didn't finish within the timeout...");
            }
        }
        catch (InterruptedException e) {
            this.logger.finest("Interrupted while waiting for split-brain healing...");
            Thread.currentThread().interrupt();
        }
    }

    protected InternalSerializationService getSerializationService() {
        return this.serializationService;
    }

    protected Data toData(Object object) {
        return this.serializationService.toData(object);
    }

    protected Data toHeapData(Object object) {
        return this.serializationService.toData(object, DataType.HEAP);
    }

    private void asyncDestroyStores(Collection<Store> stores) {
        for (Store store : stores) {
            this.splitBrainHandlerService.asyncDestroyStores(Collections.singleton(store), this.getPartitionId(store));
        }
    }

    protected void onMerge(String dataStructureName) {
    }

    protected abstract void mergeStore(Store var1, BiConsumer<Integer, MergingItem> var2);

    protected abstract void mergeStoreLegacy(Store var1, BiConsumer<Integer, Operation> var2);

    protected abstract int getBatchSize(String var1);

    protected abstract Object getMergePolicy(String var1);

    protected abstract String getDataStructureName(Store var1);

    protected abstract int getPartitionId(Store var1);

    protected abstract InMemoryFormat getInMemoryFormat(String var1);

    protected abstract OperationFactory createMergeOperationFactory(String var1, SplitBrainMergePolicy<V, MergingItem> var2, int[] var3, List<MergingItem>[] var4);

    private class LegacyOperationBiConsumer
    implements BiConsumer<Integer, Operation> {
        private final ExecutionCallback<Object> mergeCallback = new ExecutionCallback<Object>(){

            @Override
            public void onResponse(Object response) {
                AbstractMergeRunnable.this.semaphore.release(1);
            }

            @Override
            public void onFailure(Throwable t) {
                AbstractMergeRunnable.this.logger.warning("Error while running merge operation: " + t.getMessage());
                AbstractMergeRunnable.this.semaphore.release(1);
            }
        };
        private int mergedCount;

        private LegacyOperationBiConsumer() {
        }

        @Override
        public void accept(Integer partitionId, Operation operation) {
            try {
                AbstractMergeRunnable.this.operationService.invokeOnPartition(AbstractMergeRunnable.this.serviceName, operation, partitionId).andThen(this.mergeCallback);
            }
            catch (Throwable t) {
                throw ExceptionUtil.rethrow(t);
            }
            ++this.mergedCount;
        }
    }

    private class MergingItemBiConsumer
    implements BiConsumer<Integer, MergingItem> {
        private final int batchSize;
        private final int partitionCount;
        private final String dataStructureName;
        private final Address[] addresses;
        private final MutableLong[] counterPerMember;
        private final SplitBrainMergePolicy<V, MergingItem> mergePolicy;
        private final List<MergingItem>[] mergingItemsPerPartition;
        private final Map<Address, List<Integer>> memberPartitionsMap;
        private int mergedCount;

        MergingItemBiConsumer(String dataStructureName, SplitBrainMergePolicy<V, MergingItem> mergePolicy, int batchSize) {
            this.dataStructureName = dataStructureName;
            this.batchSize = batchSize;
            this.mergePolicy = mergePolicy;
            this.memberPartitionsMap = AbstractMergeRunnable.this.partitionService.getMemberPartitionsMap();
            this.partitionCount = AbstractMergeRunnable.this.partitionService.getPartitionCount();
            this.addresses = new Address[this.partitionCount];
            this.counterPerMember = new MutableLong[this.partitionCount];
            this.mergingItemsPerPartition = new List[this.partitionCount];
            this.init();
        }

        private void init() {
            for (Map.Entry<Address, List<Integer>> addressListEntry : this.memberPartitionsMap.entrySet()) {
                MutableLong counter = new MutableLong();
                Address address = addressListEntry.getKey();
                for (int partitionId : addressListEntry.getValue()) {
                    this.counterPerMember[partitionId] = counter;
                    this.addresses[partitionId] = address;
                }
            }
        }

        @Override
        public void accept(Integer partitionId, MergingItem mergingItem) {
            List entries = this.mergingItemsPerPartition[partitionId];
            if (entries == null) {
                entries = new LinkedList();
                this.mergingItemsPerPartition[partitionId.intValue()] = entries;
            }
            entries.add(mergingItem);
            ++this.mergedCount;
            long currentSize = ++this.counterPerMember[partitionId.intValue()].value;
            if (currentSize % (long)this.batchSize == 0L) {
                List<Integer> partitions = this.memberPartitionsMap.get(this.addresses[partitionId]);
                this.sendBatch(this.dataStructureName, partitions, this.mergingItemsPerPartition, this.mergePolicy);
            }
        }

        private void consumeRemaining() {
            for (Map.Entry<Address, List<Integer>> entry : this.memberPartitionsMap.entrySet()) {
                this.sendBatch(this.dataStructureName, entry.getValue(), this.mergingItemsPerPartition, this.mergePolicy);
            }
        }

        private void sendBatch(String dataStructureName, List<Integer> memberPartitions, List<MergingItem>[] entriesPerPartition, SplitBrainMergePolicy<V, MergingItem> mergePolicy) {
            int size = memberPartitions.size();
            int[] partitions = new int[size];
            int index = 0;
            for (Integer partitionId : memberPartitions) {
                if (entriesPerPartition[partitionId] == null) continue;
                partitions[index++] = partitionId;
            }
            if (index == 0) {
                return;
            }
            if (index < size) {
                partitions = Arrays.copyOf(partitions, index);
                size = index;
            }
            List[] entries = new List[size];
            index = 0;
            int totalSize = 0;
            for (int partitionId : partitions) {
                int batchSize = entriesPerPartition[partitionId].size();
                entries[index++] = entriesPerPartition[partitionId];
                totalSize += batchSize;
                entriesPerPartition[partitionId] = null;
            }
            if (totalSize == 0) {
                return;
            }
            this.sendMergingData(dataStructureName, mergePolicy, partitions, entries, totalSize);
        }

        private void sendMergingData(String dataStructureName, SplitBrainMergePolicy<V, MergingItem> mergePolicy, int[] partitions, List<MergingItem>[] entries, int totalSize) {
            try {
                OperationFactory factory = AbstractMergeRunnable.this.createMergeOperationFactory(dataStructureName, mergePolicy, partitions, entries);
                AbstractMergeRunnable.this.operationService.invokeOnPartitions(AbstractMergeRunnable.this.serviceName, factory, partitions);
            }
            catch (Throwable t) {
                AbstractMergeRunnable.this.logger.warning("Error while running merge operation: " + t.getMessage());
                throw ExceptionUtil.rethrow(t);
            }
            finally {
                AbstractMergeRunnable.this.semaphore.release(totalSize);
            }
        }
    }
}

