/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.impl.MapMergeRunnable;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.PartitionContainer;
import com.hazelcast.map.impl.recordstore.DefaultRecordStore;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.map.merge.IgnoreMergingEntryMapMergePolicy;
import com.hazelcast.spi.impl.merge.AbstractSplitBrainHandlerService;
import com.hazelcast.spi.merge.DiscardMergePolicy;
import com.hazelcast.util.ThreadUtil;
import java.util.Collection;
import java.util.Iterator;

class MapSplitBrainHandlerService
extends AbstractSplitBrainHandlerService<RecordStore> {
    private final MapServiceContext mapServiceContext;

    MapSplitBrainHandlerService(MapServiceContext mapServiceContext) {
        super(mapServiceContext.getNodeEngine());
        this.mapServiceContext = mapServiceContext;
    }

    @Override
    protected Runnable newMergeRunnable(Collection<RecordStore> mergingStores) {
        return new MapMergeRunnable(mergingStores, this, this.mapServiceContext);
    }

    @Override
    protected Iterator<RecordStore> storeIterator(int partitionId) {
        PartitionContainer partitionContainer = this.mapServiceContext.getPartitionContainer(partitionId);
        Collection<RecordStore> recordStores = partitionContainer.getAllRecordStores();
        return recordStores.iterator();
    }

    @Override
    protected void onStoreCollection(RecordStore recordStore) {
        ThreadUtil.assertRunningOnPartitionThread();
        ((DefaultRecordStore)recordStore).clearOtherDataThanStorage(false, true);
    }

    @Override
    protected void destroyStore(RecordStore store) {
        ThreadUtil.assertRunningOnPartitionThread();
        ((DefaultRecordStore)store).destroyStorageAfterClear(false, true);
    }

    @Override
    protected boolean hasEntries(RecordStore store) {
        ThreadUtil.assertRunningOnPartitionThread();
        return !store.isEmpty();
    }

    @Override
    protected boolean hasMergeablePolicy(RecordStore store) {
        Object mergePolicy = this.mapServiceContext.getMergePolicy(store.getName());
        return !(mergePolicy instanceof DiscardMergePolicy) && !(mergePolicy instanceof IgnoreMergingEntryMapMergePolicy);
    }
}

