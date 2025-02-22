/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl;

import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.replicatedmap.impl.PartitionContainer;
import com.hazelcast.replicatedmap.impl.ReplicatedMapMergeRunnable;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.impl.merge.AbstractSplitBrainHandlerService;
import com.hazelcast.spi.merge.DiscardMergePolicy;
import com.hazelcast.util.ThreadUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

class ReplicatedMapSplitBrainHandlerService
extends AbstractSplitBrainHandlerService<ReplicatedRecordStore> {
    private final ReplicatedMapService service;

    ReplicatedMapSplitBrainHandlerService(ReplicatedMapService service) {
        super(service.getNodeEngine());
        this.service = service;
    }

    public ReplicatedMapConfig getReplicatedMapConfig(String name) {
        return this.service.getReplicatedMapConfig(name);
    }

    @Override
    protected Runnable newMergeRunnable(Collection<ReplicatedRecordStore> mergingStores) {
        return new ReplicatedMapMergeRunnable(mergingStores, this, this.service.getNodeEngine());
    }

    @Override
    protected Iterator<ReplicatedRecordStore> storeIterator(int partitionId) {
        PartitionContainer partitionContainer = this.service.getPartitionContainer(partitionId);
        if (partitionContainer == null) {
            return Collections.emptyList().iterator();
        }
        ConcurrentMap<String, ReplicatedRecordStore> stores = partitionContainer.getStores();
        return stores.values().iterator();
    }

    @Override
    protected void destroyStore(ReplicatedRecordStore replicatedRecordStore) {
        ThreadUtil.assertRunningOnPartitionThread();
        replicatedRecordStore.destroy();
    }

    @Override
    protected boolean hasEntries(ReplicatedRecordStore store) {
        ThreadUtil.assertRunningOnPartitionThread();
        return !store.isEmpty();
    }

    @Override
    protected boolean hasMergeablePolicy(ReplicatedRecordStore store) {
        Object mergePolicy = this.service.getMergePolicy(store.getName());
        return !(mergePolicy instanceof DiscardMergePolicy);
    }
}

