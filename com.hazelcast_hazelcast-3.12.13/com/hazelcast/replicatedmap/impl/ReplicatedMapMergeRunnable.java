/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl;

import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.ReplicatedMapSplitBrainHandlerService;
import com.hazelcast.replicatedmap.impl.operation.LegacyMergeOperation;
import com.hazelcast.replicatedmap.impl.operation.MergeOperationFactory;
import com.hazelcast.replicatedmap.impl.record.ReplicatedMapEntryView;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecord;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.replicatedmap.merge.ReplicatedMapMergePolicy;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.impl.merge.AbstractMergeRunnable;
import com.hazelcast.spi.impl.merge.MergingValueFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.function.BiConsumer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

class ReplicatedMapMergeRunnable
extends AbstractMergeRunnable<Object, Object, ReplicatedRecordStore, SplitBrainMergeTypes.ReplicatedMapMergeTypes> {
    private final ReplicatedMapService service;

    ReplicatedMapMergeRunnable(Collection<ReplicatedRecordStore> mergingStores, ReplicatedMapSplitBrainHandlerService splitBrainHandlerService, NodeEngine nodeEngine) {
        super("hz:impl:replicatedMapService", mergingStores, splitBrainHandlerService, nodeEngine);
        this.service = (ReplicatedMapService)nodeEngine.getService("hz:impl:replicatedMapService");
    }

    @Override
    protected void mergeStore(ReplicatedRecordStore store, BiConsumer<Integer, SplitBrainMergeTypes.ReplicatedMapMergeTypes> consumer) {
        int partitionId = store.getPartitionId();
        Iterator<ReplicatedRecord> iterator = store.recordIterator();
        while (iterator.hasNext()) {
            ReplicatedRecord record = iterator.next();
            SplitBrainMergeTypes.ReplicatedMapMergeTypes mergingEntry = MergingValueFactory.createMergingEntry((SerializationService)this.getSerializationService(), record);
            consumer.accept(partitionId, mergingEntry);
        }
    }

    @Override
    protected void mergeStoreLegacy(ReplicatedRecordStore store, BiConsumer<Integer, Operation> consumer) {
        int partitionId = store.getPartitionId();
        String name = store.getName();
        ReplicatedMapMergePolicy mergePolicy = (ReplicatedMapMergePolicy)this.getMergePolicy(name);
        Iterator<ReplicatedRecord> iterator = store.recordIterator();
        while (iterator.hasNext()) {
            ReplicatedRecord record = iterator.next();
            ReplicatedMapEntryView entryView = ReplicatedMapMergeRunnable.createEntryView(record, this.getSerializationService());
            LegacyMergeOperation operation = new LegacyMergeOperation(name, record.getKeyInternal(), entryView, mergePolicy);
            consumer.accept(partitionId, operation);
        }
    }

    private static ReplicatedMapEntryView createEntryView(ReplicatedRecord record, SerializationService ss) {
        return new ReplicatedMapEntryView(ss).setKey(record.getKeyInternal()).setValue(record.getValueInternal()).setHits(record.getHits()).setTtl(record.getTtlMillis()).setLastAccessTime(record.getLastAccessTime()).setCreationTime(record.getCreationTime()).setLastUpdateTime(record.getUpdateTime());
    }

    @Override
    protected int getBatchSize(String dataStructureName) {
        ReplicatedMapConfig replicatedMapConfig = this.getReplicatedMapConfig(dataStructureName);
        MergePolicyConfig mergePolicyConfig = replicatedMapConfig.getMergePolicyConfig();
        return mergePolicyConfig.getBatchSize();
    }

    @Override
    protected InMemoryFormat getInMemoryFormat(String dataStructureName) {
        ReplicatedMapConfig replicatedMapConfig = this.getReplicatedMapConfig(dataStructureName);
        return replicatedMapConfig.getInMemoryFormat();
    }

    @Override
    protected Object getMergePolicy(String dataStructureName) {
        return this.service.getMergePolicy(dataStructureName);
    }

    @Override
    protected String getDataStructureName(ReplicatedRecordStore replicatedRecordStore) {
        return replicatedRecordStore.getName();
    }

    @Override
    protected int getPartitionId(ReplicatedRecordStore replicatedRecordStore) {
        return replicatedRecordStore.getPartitionId();
    }

    @Override
    protected OperationFactory createMergeOperationFactory(String dataStructureName, SplitBrainMergePolicy<Object, SplitBrainMergeTypes.ReplicatedMapMergeTypes> mergePolicy, int[] partitions, List<SplitBrainMergeTypes.ReplicatedMapMergeTypes>[] entries) {
        return new MergeOperationFactory(dataStructureName, partitions, entries, mergePolicy);
    }

    private ReplicatedMapConfig getReplicatedMapConfig(String name) {
        return this.service.getReplicatedMapConfig(name);
    }
}

