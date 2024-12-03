/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.hazelcast.map.impl.recordstore;

import com.hazelcast.concurrent.lock.LockService;
import com.hazelcast.concurrent.lock.LockStore;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.internal.util.comparators.ValueComparator;
import com.hazelcast.map.impl.EntryCostEstimator;
import com.hazelcast.map.impl.ExpirationTimeSetter;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.mapstore.MapDataStore;
import com.hazelcast.map.impl.mapstore.MapStoreContext;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.record.RecordFactory;
import com.hazelcast.map.impl.record.Records;
import com.hazelcast.map.impl.recordstore.BasicRecordStoreLoader;
import com.hazelcast.map.impl.recordstore.CompositeRecordStoreMutationObserver;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.map.impl.recordstore.RecordStoreLoader;
import com.hazelcast.map.impl.recordstore.RecordStoreMutationObserver;
import com.hazelcast.map.impl.recordstore.Storage;
import com.hazelcast.map.impl.recordstore.StorageImpl;
import com.hazelcast.monitor.LocalRecordStoreStats;
import com.hazelcast.monitor.impl.LocalRecordStoreStatsImpl;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.Clock;
import com.hazelcast.wan.impl.CallerProvenance;
import java.util.Collection;
import javax.annotation.Nonnull;

abstract class AbstractRecordStore
implements RecordStore<Record> {
    protected final int partitionId;
    protected final String name;
    protected final LockStore lockStore;
    protected final MapContainer mapContainer;
    protected final RecordFactory recordFactory;
    protected final InMemoryFormat inMemoryFormat;
    protected final MapStoreContext mapStoreContext;
    protected final ValueComparator valueComparator;
    protected final MapServiceContext mapServiceContext;
    protected final SerializationService serializationService;
    protected final MapDataStore<Data, Object> mapDataStore;
    protected final LocalRecordStoreStatsImpl stats = new LocalRecordStoreStatsImpl();
    protected final RecordStoreMutationObserver<Record> mutationObserver;
    protected Storage<Data, Record> storage;

    protected AbstractRecordStore(MapContainer mapContainer, int partitionId) {
        this.name = mapContainer.getName();
        this.mapContainer = mapContainer;
        this.partitionId = partitionId;
        this.mapServiceContext = mapContainer.getMapServiceContext();
        NodeEngine nodeEngine = this.mapServiceContext.getNodeEngine();
        this.serializationService = nodeEngine.getSerializationService();
        this.inMemoryFormat = mapContainer.getMapConfig().getInMemoryFormat();
        this.recordFactory = mapContainer.getRecordFactoryConstructor().createNew(null);
        this.valueComparator = this.mapServiceContext.getValueComparatorOf(this.inMemoryFormat);
        this.mapStoreContext = mapContainer.getMapStoreContext();
        this.mapDataStore = this.mapStoreContext.getMapStoreManager().getMapDataStore(this.name, partitionId);
        this.lockStore = this.createLockStore();
        Collection mutationObservers = this.mapServiceContext.createRecordStoreMutationObservers(this.getName(), partitionId);
        this.mutationObserver = new CompositeRecordStoreMutationObserver<Record>(mutationObservers);
    }

    protected boolean persistenceEnabledFor(@Nonnull CallerProvenance provenance) {
        switch (provenance) {
            case WAN: {
                return this.mapContainer.isPersistWanReplicatedData();
            }
            case NOT_WAN: {
                return true;
            }
        }
        throw new IllegalArgumentException("Unexpected provenance: `" + (Object)((Object)provenance) + "`");
    }

    @Override
    public LocalRecordStoreStats getLocalRecordStoreStats() {
        return this.stats;
    }

    @Override
    public void init() {
        this.storage = this.createStorage(this.recordFactory, this.inMemoryFormat);
    }

    @Override
    public Record createRecord(Data key, Object value, long ttlMillis, long maxIdle, long now) {
        Record record = this.recordFactory.newRecord(key, value);
        record.setCreationTime(now);
        record.setLastUpdateTime(now);
        ExpirationTimeSetter.setExpirationTimes(ttlMillis, maxIdle, record, this.mapContainer.getMapConfig(), true);
        this.updateStatsOnPut(false, now);
        return record;
    }

    @Override
    public Storage createStorage(RecordFactory recordFactory, InMemoryFormat memoryFormat) {
        return new StorageImpl(recordFactory, memoryFormat, this.serializationService);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public MapContainer getMapContainer() {
        return this.mapContainer;
    }

    @Override
    public long getOwnedEntryCost() {
        return this.storage.getEntryCostEstimator().getEstimate();
    }

    protected long getNow() {
        return Clock.currentTimeMillis();
    }

    protected void updateRecord(Data key, Record record, Object value, long now, boolean countAsAccess) {
        this.updateStatsOnPut(countAsAccess, now);
        if (countAsAccess) {
            record.onAccess(now);
        }
        record.onUpdate(now);
        this.mutationObserver.onUpdateRecord(key, record, value);
        this.storage.updateRecordValue(key, record, value);
    }

    @Override
    public int getPartitionId() {
        return this.partitionId;
    }

    protected void saveIndex(Record record, Object oldValue) {
        Data dataKey = record.getKey();
        Indexes indexes = this.mapContainer.getIndexes(this.partitionId);
        if (indexes.haveAtLeastOneIndex()) {
            Object value = Records.getValueOrCachedValue(record, this.serializationService);
            QueryableEntry queryableEntry = this.mapContainer.newQueryEntry(dataKey, value);
            queryableEntry.setMetadata(record.getMetadata());
            indexes.putEntry(queryableEntry, oldValue, Index.OperationSource.USER);
        }
    }

    protected void removeIndex(Record record) {
        Indexes indexes = this.mapContainer.getIndexes(this.partitionId);
        if (indexes.haveAtLeastOneIndex()) {
            Data key = record.getKey();
            Object value = Records.getValueOrCachedValue(record, this.serializationService);
            indexes.removeEntry(key, value, Index.OperationSource.USER);
        }
    }

    protected void removeIndex(Collection<Record> records) {
        Indexes indexes = this.mapContainer.getIndexes(this.partitionId);
        if (!indexes.haveAtLeastOneIndex()) {
            return;
        }
        for (Record record : records) {
            this.removeIndex(record);
        }
    }

    protected LockStore createLockStore() {
        NodeEngine nodeEngine = this.mapServiceContext.getNodeEngine();
        LockService lockService = (LockService)nodeEngine.getSharedService("hz:impl:lockService");
        if (lockService == null) {
            return null;
        }
        return lockService.createLockStore(this.partitionId, MapService.getObjectNamespace(this.name));
    }

    @Override
    public int getLockedEntryCount() {
        return this.lockStore.getLockedEntryCount();
    }

    protected RecordStoreLoader createRecordStoreLoader(MapStoreContext mapStoreContext) {
        return mapStoreContext.getMapStoreWrapper() == null ? RecordStoreLoader.EMPTY_LOADER : new BasicRecordStoreLoader(this);
    }

    protected Data toData(Object value) {
        return this.mapServiceContext.toData(value);
    }

    public void setSizeEstimator(EntryCostEstimator entryCostEstimator) {
        this.storage.setEntryCostEstimator(entryCostEstimator);
    }

    @Override
    public void disposeDeferredBlocks() {
        this.storage.disposeDeferredBlocks();
    }

    @Override
    public Storage<Data, ? extends Record> getStorage() {
        return this.storage;
    }

    protected void updateStatsOnPut(boolean countAsAccess, long now) {
        this.stats.setLastUpdateTime(now);
        if (countAsAccess) {
            this.updateStatsOnGet(now);
        }
    }

    protected void updateStatsOnPut(long hits) {
        this.stats.increaseHits(hits);
    }

    protected void updateStatsOnGet(long now) {
        this.stats.setLastAccessTime(now);
        this.stats.increaseHits();
    }
}

