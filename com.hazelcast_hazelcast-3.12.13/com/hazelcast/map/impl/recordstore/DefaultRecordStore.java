/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.hazelcast.map.impl.recordstore;

import com.hazelcast.concurrent.lock.LockService;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.NativeMemoryConfig;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.EntryView;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.logging.ILogger;
import com.hazelcast.map.impl.EntryViews;
import com.hazelcast.map.impl.ExpirationTimeSetter;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.map.impl.MapKeyLoader;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.event.EntryEventData;
import com.hazelcast.map.impl.iterator.MapEntriesWithCursor;
import com.hazelcast.map.impl.iterator.MapKeysWithCursor;
import com.hazelcast.map.impl.mapstore.MapDataStore;
import com.hazelcast.map.impl.mapstore.MapDataStores;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindQueue;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindStore;
import com.hazelcast.map.impl.mapstore.writebehind.entry.DelayedEntry;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.publisher.MapPublisherRegistry;
import com.hazelcast.map.impl.querycache.publisher.PublisherContext;
import com.hazelcast.map.impl.querycache.publisher.PublisherRegistry;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.record.Records;
import com.hazelcast.map.impl.recordstore.AbstractEvictableRecordStore;
import com.hazelcast.map.impl.recordstore.RecordStoreLoader;
import com.hazelcast.map.merge.MapMergePolicy;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.InternalIndex;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.exception.RetryableHazelcastException;
import com.hazelcast.spi.impl.merge.MergingValueFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.Clock;
import com.hazelcast.util.CollectionUtil;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.MapUtil;
import com.hazelcast.wan.impl.CallerProvenance;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import javax.annotation.Nonnull;

public class DefaultRecordStore
extends AbstractEvictableRecordStore {
    protected final ILogger logger;
    protected final RecordStoreLoader recordStoreLoader;
    protected final MapKeyLoader keyLoader;
    protected final Collection<Future> loadingFutures = new ConcurrentLinkedQueue<Future>();
    private boolean loadedOnCreate;
    private boolean loadedOnPreMigration;
    private final IPartitionService partitionService;

    public DefaultRecordStore(MapContainer mapContainer, int partitionId, MapKeyLoader keyLoader, ILogger logger) {
        super(mapContainer, partitionId);
        this.logger = logger;
        this.keyLoader = keyLoader;
        this.recordStoreLoader = this.createRecordStoreLoader(this.mapStoreContext);
        this.partitionService = this.mapServiceContext.getNodeEngine().getPartitionService();
    }

    @Override
    public MapDataStore<Data, Object> getMapDataStore() {
        return this.mapDataStore;
    }

    @Override
    public long softFlush() {
        this.updateStoreStats();
        return this.mapDataStore.softFlush();
    }

    private void flush(Collection<Record> recordsToBeFlushed, boolean backup) {
        for (Record record : recordsToBeFlushed) {
            this.mapDataStore.flush(record.getKey(), record.getValue(), backup);
        }
    }

    @Override
    public Record getRecord(Data key) {
        return (Record)this.storage.get(key);
    }

    @Override
    public void putRecord(Data key, Record record) {
        this.markRecordStoreExpirable(record.getTtl(), record.getMaxIdle());
        this.storage.put(key, record);
        this.mutationObserver.onReplicationPutRecord(key, record);
        this.updateStatsOnPut(record.getHits());
    }

    @Override
    public Record putBackup(Data key, Object value, CallerProvenance provenance) {
        return this.putBackup(key, value, -1L, -1L, false, provenance);
    }

    @Override
    public Record putBackup(Data key, Object value, long ttl, long maxIdle, boolean putTransient, CallerProvenance provenance) {
        long now = this.getNow();
        this.markRecordStoreExpirable(ttl, maxIdle);
        Record record = this.getRecordOrNull(key, now, true);
        if (record == null) {
            record = this.createRecord(key, value, ttl, maxIdle, now);
            this.storage.put(key, record);
            this.mutationObserver.onPutRecord(key, record);
        } else {
            this.updateRecord(key, record, value, now, true);
        }
        if (this.persistenceEnabledFor(provenance)) {
            if (putTransient) {
                this.mapDataStore.addTransient(key, now);
            } else {
                this.mapDataStore.addBackup(key, value, now);
            }
        }
        return record;
    }

    @Override
    public Iterator<Record> iterator() {
        return new AbstractEvictableRecordStore.ReadOnlyRecordIterator(this.storage.values());
    }

    @Override
    public Iterator<Record> iterator(long now, boolean backup) {
        return new AbstractEvictableRecordStore.ReadOnlyRecordIterator(this.storage.values(), now, backup);
    }

    @Override
    public MapKeysWithCursor fetchKeys(int tableIndex, int size) {
        return this.storage.fetchKeys(tableIndex, size);
    }

    @Override
    public MapEntriesWithCursor fetchEntries(int tableIndex, int size) {
        return this.storage.fetchEntries(tableIndex, size, this.serializationService);
    }

    @Override
    public Iterator<Record> loadAwareIterator(long now, boolean backup) {
        this.checkIfLoaded();
        return this.iterator(now, backup);
    }

    @Override
    public int size() {
        return this.storage.size();
    }

    @Override
    public boolean isEmpty() {
        this.checkIfLoaded();
        return this.storage.isEmpty();
    }

    @Override
    public boolean containsValue(Object value) {
        this.checkIfLoaded();
        long now = this.getNow();
        Collection records = this.storage.values();
        if (!records.isEmpty()) {
            value = this.inMemoryFormat == InMemoryFormat.OBJECT ? this.serializationService.toObject(value) : this.serializationService.toData(value);
        }
        for (Record record : records) {
            if (this.getOrNullIfExpired(record, now, false) == null || !this.valueComparator.isEqual(value, record.getValue(), this.serializationService)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean txnLock(Data key, String caller, long threadId, long referenceId, long ttl, boolean blockReads) {
        this.checkIfLoaded();
        return this.lockStore != null && this.lockStore.txnLock(key, caller, threadId, referenceId, ttl, blockReads);
    }

    @Override
    public boolean extendLock(Data key, String caller, long threadId, long ttl) {
        this.checkIfLoaded();
        return this.lockStore != null && this.lockStore.extendLeaseTime(key, caller, threadId, ttl);
    }

    @Override
    public boolean localLock(Data key, String caller, long threadId, long referenceId, long ttl) {
        this.checkIfLoaded();
        return this.lockStore != null && this.lockStore.localLock(key, caller, threadId, referenceId, ttl);
    }

    @Override
    public boolean unlock(Data key, String caller, long threadId, long referenceId) {
        this.checkIfLoaded();
        return this.lockStore != null && this.lockStore.unlock(key, caller, threadId, referenceId);
    }

    @Override
    public boolean lock(Data key, String caller, long threadId, long referenceId, long ttl) {
        this.checkIfLoaded();
        return this.lockStore != null && this.lockStore.lock(key, caller, threadId, referenceId, ttl);
    }

    @Override
    public boolean forceUnlock(Data dataKey) {
        return this.lockStore != null && this.lockStore.forceUnlock(dataKey);
    }

    @Override
    public boolean isLocked(Data dataKey) {
        return this.lockStore != null && this.lockStore.isLocked(dataKey);
    }

    @Override
    public boolean isTransactionallyLocked(Data key) {
        return this.lockStore != null && this.lockStore.shouldBlockReads(key);
    }

    @Override
    public boolean canAcquireLock(Data key, String caller, long threadId) {
        return this.lockStore == null || this.lockStore.canAcquireLock(key, caller, threadId);
    }

    @Override
    public boolean isLockedBy(Data key, String caller, long threadId) {
        return this.lockStore != null && this.lockStore.isLockedBy(key, caller, threadId);
    }

    @Override
    public String getLockOwnerInfo(Data key) {
        return this.lockStore != null ? this.lockStore.getOwnerInfo(key) : null;
    }

    @Override
    public Record loadRecordOrNull(Data key, boolean backup, Address callerAddress) {
        Record record = null;
        Object value = this.mapDataStore.load(key);
        if (value != null) {
            record = this.createRecord(key, value, -1L, -1L, this.getNow());
            this.storage.put(key, record);
            this.mutationObserver.onLoadRecord(key, record);
            if (!backup) {
                this.saveIndex(record, null);
                this.mapEventPublisher.publishEvent(callerAddress, this.name, EntryEventType.LOADED, key, null, value, null);
            }
            this.evictEntries(key);
        }
        if (!backup && record != null && this.hasQueryCache()) {
            this.addEventToQueryCache(record);
        }
        return record;
    }

    protected List<Data> getKeysFromRecords(Collection<Record> clearableRecords) {
        ArrayList<Data> keys = new ArrayList<Data>(clearableRecords.size());
        for (Record clearableRecord : clearableRecords) {
            keys.add(clearableRecord.getKey());
        }
        return keys;
    }

    protected int removeRecords(Collection<Record> recordsToRemove) {
        return this.removeOrEvictRecords(recordsToRemove, false);
    }

    protected int evictRecords(Collection<Record> recordsToEvict) {
        return this.removeOrEvictRecords(recordsToEvict, true);
    }

    private int removeOrEvictRecords(Collection<Record> recordsToRemove, boolean eviction) {
        if (CollectionUtil.isEmpty(recordsToRemove)) {
            return 0;
        }
        int removalSize = recordsToRemove.size();
        Iterator<Record> iterator = recordsToRemove.iterator();
        while (iterator.hasNext()) {
            Record record = iterator.next();
            if (eviction) {
                this.mutationObserver.onEvictRecord(record.getKey(), record);
            } else {
                this.mutationObserver.onRemoveRecord(record.getKey(), record);
            }
            this.storage.removeRecord(record);
            iterator.remove();
        }
        return removalSize;
    }

    protected Collection<Record> getNotLockedRecords() {
        Set<Data> lockedKeySet;
        Set<Data> set = lockedKeySet = this.lockStore == null ? null : this.lockStore.getLockedKeys();
        if (CollectionUtil.isEmpty(lockedKeySet)) {
            return this.storage.values();
        }
        int notLockedKeyCount = this.storage.size() - lockedKeySet.size();
        if (notLockedKeyCount <= 0) {
            return Collections.emptyList();
        }
        ArrayList<Record> notLockedRecords = new ArrayList<Record>(notLockedKeyCount);
        Collection records = this.storage.values();
        for (Record record : records) {
            if (lockedKeySet.contains(record.getKey())) continue;
            notLockedRecords.add(record);
        }
        return notLockedRecords;
    }

    @Override
    public Object evict(Data key, boolean backup) {
        Record record = (Record)this.storage.get(key);
        Object value = null;
        if (record != null) {
            value = record.getValue();
            this.mapDataStore.flush(key, value, backup);
            this.removeIndex(record);
            this.mutationObserver.onEvictRecord(key, record);
            this.storage.removeRecord(record);
            if (!backup) {
                this.mapServiceContext.interceptRemove(this.name, value);
            }
        }
        return value;
    }

    @Override
    public int evictAll(boolean backup) {
        this.checkIfLoaded();
        Collection<Record> evictableRecords = this.getNotLockedRecords();
        this.flush(evictableRecords, backup);
        this.removeIndex(evictableRecords);
        return this.evictRecords(evictableRecords);
    }

    @Override
    public void removeBackup(Data key, CallerProvenance provenance) {
        long now = this.getNow();
        Record record = this.getRecordOrNull(key, now, true);
        if (record == null) {
            return;
        }
        this.mutationObserver.onRemoveRecord(key, record);
        this.storage.removeRecord(record);
        if (this.persistenceEnabledFor(provenance)) {
            this.mapDataStore.removeBackup(key, now);
        }
    }

    @Override
    public boolean delete(Data key, CallerProvenance provenance) {
        this.checkIfLoaded();
        long now = this.getNow();
        Record record = this.getRecordOrNull(key, now, false);
        if (record == null) {
            if (this.persistenceEnabledFor(provenance)) {
                this.mapDataStore.remove(key, now);
            }
        } else {
            return this.removeRecord(key, record, now, provenance) != null;
        }
        return false;
    }

    @Override
    public Object remove(Data key, CallerProvenance provenance) {
        Object oldValue;
        this.checkIfLoaded();
        long now = this.getNow();
        Record record = this.getRecordOrNull(key, now, false);
        if (record == null) {
            oldValue = this.mapDataStore.load(key);
            if (oldValue != null && this.persistenceEnabledFor(provenance)) {
                this.mapDataStore.remove(key, now);
            }
        } else {
            oldValue = this.removeRecord(key, record, now, provenance);
        }
        return oldValue;
    }

    @Override
    public boolean remove(Data key, Object testValue) {
        Object oldValue;
        this.checkIfLoaded();
        long now = this.getNow();
        Record record = this.getRecordOrNull(key, now, false);
        boolean removed = false;
        if (record == null) {
            oldValue = this.mapDataStore.load(key);
            if (oldValue == null) {
                return false;
            }
        } else {
            oldValue = record.getValue();
        }
        if (this.valueComparator.isEqual(testValue, oldValue, this.serializationService)) {
            this.mapServiceContext.interceptRemove(this.name, oldValue);
            this.mapDataStore.remove(key, now);
            if (record != null) {
                this.removeIndex(record);
                this.onStore(record);
                this.mutationObserver.onRemoveRecord(key, record);
                this.storage.removeRecord(record);
            }
            removed = true;
        }
        return removed;
    }

    @Override
    public Object get(Data key, boolean backup, Address callerAddress, boolean touch) {
        this.checkIfLoaded();
        long now = this.getNow();
        Record record = this.getRecordOrNull(key, now, backup);
        if (record == null) {
            record = this.loadRecordOrNull(key, backup, callerAddress);
        } else if (touch) {
            this.accessRecord(record, now);
        }
        Object value = record == null ? null : (Object)record.getValue();
        value = this.mapServiceContext.interceptGet(this.name, value);
        return value;
    }

    @Override
    public Data readBackupData(Data key) {
        Record record = this.getRecord(key);
        if (record == null) {
            return null;
        }
        if (this.partitionService.isPartitionOwner(this.partitionId)) {
            record.setLastAccessTime(Clock.currentTimeMillis());
        }
        Object value = record.getValue();
        this.mapServiceContext.interceptAfterGet(this.name, value);
        return this.mapServiceContext.toData(value);
    }

    @Override
    public MapEntries getAll(Set<Data> keys, Address callerAddress) {
        this.checkIfLoaded();
        long now = this.getNow();
        MapEntries mapEntries = new MapEntries(keys.size());
        Iterator<Data> iterator = keys.iterator();
        while (iterator.hasNext()) {
            Data key = iterator.next();
            Record record = this.getRecordOrNull(key, now, false);
            if (record == null) continue;
            this.addMapEntrySet(key, record.getValue(), mapEntries);
            this.accessRecord(record, now);
            iterator.remove();
        }
        Map<Object, Object> loadedEntries = this.loadEntries(keys, callerAddress);
        this.addMapEntrySet(loadedEntries, mapEntries);
        return mapEntries;
    }

    protected Map<Data, Object> loadEntries(Set<Data> keys, Address callerAddress) {
        Map loadedEntries = this.mapDataStore.loadAll(keys);
        if (loadedEntries == null || loadedEntries.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Data, Object> resultMap = MapUtil.createHashMap(loadedEntries.size());
        Set entrySet = loadedEntries.entrySet();
        Iterator iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry object;
            Map.Entry entry = object = iterator.next();
            Data key = this.toData(entry.getKey());
            Object value = entry.getValue();
            resultMap.put(key, value);
            this.putFromLoad(key, value, callerAddress);
        }
        if (this.hasQueryCache()) {
            for (Data key : resultMap.keySet()) {
                Record record = (Record)this.storage.get(key);
                this.addEventToQueryCache(record);
            }
        }
        return resultMap;
    }

    protected void addMapEntrySet(Object key, Object value, MapEntries mapEntries) {
        if (key == null || value == null) {
            return;
        }
        value = this.mapServiceContext.interceptGet(this.name, value);
        Data dataKey = this.mapServiceContext.toData(key);
        Data dataValue = this.mapServiceContext.toData(value);
        mapEntries.add(dataKey, dataValue);
    }

    protected void addMapEntrySet(Map<Object, Object> entries, MapEntries mapEntries) {
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            this.addMapEntrySet(entry.getKey(), entry.getValue(), mapEntries);
        }
    }

    @Override
    public boolean existInMemory(Data key) {
        return this.storage.containsKey(key);
    }

    @Override
    public boolean containsKey(Data key, Address callerAddress) {
        boolean contains;
        this.checkIfLoaded();
        long now = this.getNow();
        Record record = this.getRecordOrNull(key, now, false);
        if (record == null) {
            record = this.loadRecordOrNull(key, false, callerAddress);
        }
        boolean bl = contains = record != null;
        if (contains) {
            this.accessRecord(record, now);
        }
        return contains;
    }

    @Override
    public boolean hasQueryCache() {
        QueryCacheContext queryCacheContext = this.mapServiceContext.getQueryCacheContext();
        PublisherContext publisherContext = queryCacheContext.getPublisherContext();
        MapPublisherRegistry mapPublisherRegistry = publisherContext.getMapPublisherRegistry();
        PublisherRegistry publisherRegistry = mapPublisherRegistry.getOrNull(this.name);
        return publisherRegistry != null;
    }

    private void addEventToQueryCache(Record record) {
        EntryEventData eventData = new EntryEventData(this.thisAddress.toString(), this.name, this.thisAddress, record.getKey(), this.mapServiceContext.toData(record.getValue()), null, null, EntryEventType.ADDED.getType());
        this.mapEventPublisher.addEventToQueryCache(eventData);
    }

    @Override
    public boolean setTtl(Data key, long ttl) {
        if (this.mapServiceContext.getNodeEngine().getClusterService().getClusterVersion().isLessThan(Versions.V3_11)) {
            throw new UnsupportedOperationException("Modifying TTL is available when cluster version is 3.11 or higher");
        }
        long now = this.getNow();
        Record record = this.getRecordOrNull(key, now, false);
        if (record == null) {
            return false;
        }
        this.markRecordStoreExpirable(ttl, -1L);
        ExpirationTimeSetter.setExpirationTimes(ttl, -1L, record, this.mapContainer.getMapConfig(), true);
        return true;
    }

    @Override
    public Object set(Data dataKey, Object value, long ttl, long maxIdle) {
        return this.putInternal(dataKey, value, ttl, maxIdle, false, true);
    }

    @Override
    public Object put(Data key, Object value, long ttl, long maxIdle) {
        return this.putInternal(key, value, ttl, maxIdle, true, true);
    }

    protected Object putInternal(Data key, Object value, long ttl, long maxIdle, boolean loadFromStore, boolean countAsAccess) {
        this.checkIfLoaded();
        long now = this.getNow();
        this.markRecordStoreExpirable(ttl, maxIdle);
        Record record = this.getRecordOrNull(key, now, false);
        Object oldValue = record == null ? (loadFromStore ? this.mapDataStore.load(key) : null) : record.getValue();
        value = this.mapServiceContext.interceptPut(this.name, oldValue, value);
        value = this.mapDataStore.add(key, value, now);
        this.onStore(record);
        if (record == null) {
            record = this.createRecord(key, value, ttl, maxIdle, now);
            this.storage.put(key, record);
            this.mutationObserver.onPutRecord(key, record);
        } else {
            this.updateRecord(key, record, value, now, countAsAccess);
            ExpirationTimeSetter.setExpirationTimes(ttl, maxIdle, record, this.mapContainer.getMapConfig(), false);
        }
        this.saveIndex(record, oldValue);
        return oldValue;
    }

    @Override
    public boolean merge(SplitBrainMergeTypes.MapMergeTypes mergingEntry, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.MapMergeTypes> mergePolicy) {
        return this.merge(mergingEntry, mergePolicy, CallerProvenance.NOT_WAN);
    }

    @Override
    public boolean merge(SplitBrainMergeTypes.MapMergeTypes mergingEntry, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.MapMergeTypes> mergePolicy, CallerProvenance provenance) {
        Data newValue;
        this.checkIfLoaded();
        long now = this.getNow();
        this.serializationService.getManagedContext().initialize(mergingEntry);
        this.serializationService.getManagedContext().initialize(mergePolicy);
        Data key = (Data)mergingEntry.getKey();
        Record record = this.getRecordOrNull(key, now, false);
        Object oldValue = null;
        if (record == null) {
            newValue = mergePolicy.merge(mergingEntry, null);
            if (newValue == null) {
                return false;
            }
            newValue = this.persistenceEnabledFor(provenance) ? this.mapDataStore.add(key, newValue, now) : newValue;
            record = this.createRecord(key, newValue, -1L, -1L, now);
            this.mergeRecordExpiration(record, mergingEntry);
            this.storage.put(key, record);
            this.mutationObserver.onPutRecord(key, record);
        } else {
            oldValue = record.getValue();
            SplitBrainMergeTypes.MapMergeTypes existingEntry = MergingValueFactory.createMergingEntry(this.serializationService, record);
            newValue = mergePolicy.merge(mergingEntry, existingEntry);
            if (newValue == null) {
                this.removeIndex(record);
                if (this.persistenceEnabledFor(provenance)) {
                    this.mapDataStore.remove(key, now);
                }
                this.onStore(record);
                this.mutationObserver.onRemoveRecord(key, record);
                this.storage.removeRecord(record);
                return true;
            }
            if (this.valueComparator.isEqual(newValue, oldValue, this.serializationService)) {
                this.mergeRecordExpiration(record, mergingEntry);
                return true;
            }
            newValue = this.persistenceEnabledFor(provenance) ? this.mapDataStore.add(key, newValue, now) : newValue;
            this.onStore(record);
            this.mutationObserver.onUpdateRecord(key, record, newValue);
            this.storage.updateRecordValue(key, record, newValue);
        }
        this.saveIndex(record, oldValue);
        return newValue != null;
    }

    @Override
    public boolean merge(Data key, EntryView mergingEntry, MapMergePolicy mergePolicy) {
        return this.merge(key, mergingEntry, mergePolicy, CallerProvenance.NOT_WAN);
    }

    @Override
    public boolean merge(Data key, EntryView mergingEntry, MapMergePolicy mergePolicy, CallerProvenance provenance) {
        Object newValue;
        this.checkIfLoaded();
        long now = this.getNow();
        Record record = this.getRecordOrNull(key, now, false);
        mergingEntry = EntryViews.toLazyEntryView(mergingEntry, this.serializationService, mergePolicy);
        Object oldValue = null;
        if (record == null) {
            Object notExistingKey = this.mapServiceContext.toObject(key);
            EntryView nullEntryView = EntryViews.createNullEntryView(notExistingKey);
            newValue = mergePolicy.merge(this.name, mergingEntry, nullEntryView);
            if (newValue == null) {
                return false;
            }
            newValue = this.persistenceEnabledFor(provenance) ? this.mapDataStore.add(key, newValue, now) : newValue;
            record = this.createRecord(key, newValue, -1L, -1L, now);
            this.mergeRecordExpiration(record, mergingEntry);
            this.storage.put(key, record);
            this.mutationObserver.onPutRecord(key, record);
        } else {
            oldValue = record.getValue();
            EntryView existingEntry = EntryViews.createLazyEntryView(record.getKey(), record.getValue(), record, this.serializationService, mergePolicy);
            newValue = mergePolicy.merge(this.name, mergingEntry, existingEntry);
            if (newValue == null) {
                this.removeIndex(record);
                if (this.persistenceEnabledFor(provenance)) {
                    this.mapDataStore.remove(key, now);
                }
                this.onStore(record);
                this.mutationObserver.onRemoveRecord(key, record);
                this.storage.removeRecord(record);
                return true;
            }
            if (this.valueComparator.isEqual(newValue, oldValue, this.serializationService)) {
                this.mergeRecordExpiration(record, mergingEntry);
                return true;
            }
            newValue = this.persistenceEnabledFor(provenance) ? this.mapDataStore.add(key, newValue, now) : newValue;
            this.onStore(record);
            this.mutationObserver.onUpdateRecord(key, record, newValue);
            this.storage.updateRecordValue(key, record, newValue);
        }
        this.saveIndex(record, oldValue);
        return newValue != null;
    }

    @Override
    public Object replace(Data key, Object update) {
        this.checkIfLoaded();
        long now = this.getNow();
        Record record = this.getRecordOrNull(key, now, false);
        if (record == null || record.getValue() == null) {
            return null;
        }
        Object oldValue = record.getValue();
        update = this.mapServiceContext.interceptPut(this.name, oldValue, update);
        update = this.mapDataStore.add(key, update, now);
        this.onStore(record);
        this.updateRecord(key, record, update, now, true);
        ExpirationTimeSetter.setExpirationTimes(record.getTtl(), record.getMaxIdle(), record, this.mapContainer.getMapConfig(), false);
        this.saveIndex(record, oldValue);
        return oldValue;
    }

    @Override
    public boolean replace(Data key, Object expect, Object update) {
        this.checkIfLoaded();
        long now = this.getNow();
        Record record = this.getRecordOrNull(key, now, false);
        if (record == null) {
            return false;
        }
        Object current = record.getValue();
        if (!this.valueComparator.isEqual(expect, current, this.serializationService)) {
            return false;
        }
        update = this.mapServiceContext.interceptPut(this.name, current, update);
        update = this.mapDataStore.add(key, update, now);
        this.onStore(record);
        this.updateRecord(key, record, update, now, true);
        ExpirationTimeSetter.setExpirationTimes(record.getTtl(), record.getMaxIdle(), record, this.mapContainer.getMapConfig(), false);
        this.saveIndex(record, current);
        return true;
    }

    @Override
    public Object putTransient(Data key, Object value, long ttl, long maxIdle) {
        this.checkIfLoaded();
        long now = this.getNow();
        this.markRecordStoreExpirable(ttl, maxIdle);
        Record record = this.getRecordOrNull(key, now, false);
        Object oldValue = null;
        if (record == null) {
            value = this.mapServiceContext.interceptPut(this.name, null, value);
            record = this.createRecord(key, value, ttl, maxIdle, now);
            this.storage.put(key, record);
            this.mutationObserver.onPutRecord(key, record);
        } else {
            oldValue = record.getValue();
            value = this.mapServiceContext.interceptPut(this.name, oldValue, value);
            this.updateRecord(key, record, value, now, true);
            ExpirationTimeSetter.setExpirationTimes(ttl, maxIdle, record, this.mapContainer.getMapConfig(), false);
        }
        this.saveIndex(record, oldValue);
        this.mapDataStore.addTransient(key, now);
        return oldValue;
    }

    @Override
    public Object putFromLoad(Data key, Object value, Address callerAddress) {
        return this.putFromLoadInternal(key, value, -1L, -1L, false, callerAddress);
    }

    @Override
    public Object putFromLoadBackup(Data key, Object value) {
        return this.putFromLoadInternal(key, value, -1L, -1L, true, null);
    }

    private Object putFromLoadInternal(Data key, Object value, long ttl, long maxIdle, boolean backup, Address callerAddress) {
        if (!this.isKeyAndValueLoadable(key, value)) {
            return null;
        }
        long now = this.getNow();
        if (this.shouldEvict()) {
            return null;
        }
        this.markRecordStoreExpirable(ttl, maxIdle);
        Record record = this.getRecordOrNull(key, now, false);
        Object oldValue = null;
        EntryEventType entryEventType = null;
        if (record == null) {
            value = this.mapServiceContext.interceptPut(this.name, null, value);
            record = this.createRecord(key, value, ttl, maxIdle, now);
            this.storage.put(key, record);
            if (this.canPublishLoadEvent()) {
                this.mutationObserver.onLoadRecord(key, record);
            } else {
                this.mutationObserver.onPutRecord(key, record);
            }
        } else {
            oldValue = record.getValue();
            value = this.mapServiceContext.interceptPut(this.name, oldValue, value);
            this.updateRecord(key, record, value, now, true);
            ExpirationTimeSetter.setExpirationTimes(ttl, maxIdle, record, this.mapContainer.getMapConfig(), false);
            entryEventType = EntryEventType.UPDATED;
        }
        if (!backup) {
            this.saveIndex(record, oldValue);
            if (entryEventType == EntryEventType.UPDATED) {
                this.mapEventPublisher.publishEvent(callerAddress, this.name, EntryEventType.UPDATED, key, oldValue, value);
            } else if (this.canPublishLoadEvent()) {
                this.mapEventPublisher.publishEvent(callerAddress, this.name, EntryEventType.LOADED, key, null, value);
            } else {
                this.mapEventPublisher.publishEvent(callerAddress, this.name, EntryEventType.ADDED, key, null, value);
            }
        }
        return oldValue;
    }

    private boolean canPublishLoadEvent() {
        NodeEngine nodeEngine = this.mapServiceContext.getNodeEngine();
        ClusterService clusterService = nodeEngine.getClusterService();
        boolean version311OrLater = clusterService.getClusterVersion().isGreaterOrEqual(Versions.V3_11);
        boolean addEventPublishingEnabled = this.mapContainer.isAddEventPublishingEnabled();
        return version311OrLater && !addEventPublishingEnabled;
    }

    protected boolean isKeyAndValueLoadable(Data key, Object value) {
        if (key == null) {
            this.logger.warning("Found an attempt to load a null key from map-store, ignoring it.");
            return false;
        }
        if (value == null) {
            this.logger.warning("Found an attempt to load a null value from map-store, ignoring it.");
            return false;
        }
        if (this.partitionService.getPartitionId(key) != this.partitionId) {
            throw new IllegalStateException("MapLoader loaded an item belongs to a different partition");
        }
        return true;
    }

    @Override
    public boolean setWithUncountedAccess(Data dataKey, Object value, long ttl, long maxIdle) {
        Object oldValue = this.putInternal(dataKey, value, ttl, maxIdle, false, false);
        return oldValue == null;
    }

    @Override
    public Object putIfAbsent(Data key, Object value, long ttl, long maxIdle, Address callerAddress) {
        Object oldValue;
        this.checkIfLoaded();
        long now = this.getNow();
        this.markRecordStoreExpirable(ttl, maxIdle);
        Record record = this.getRecordOrNull(key, now, false);
        if (record == null) {
            oldValue = this.mapDataStore.load(key);
            if (oldValue != null) {
                record = this.createRecord(key, oldValue, -1L, -1L, now);
                this.storage.put(key, record);
                this.mutationObserver.onPutRecord(key, record);
                this.mapEventPublisher.publishEvent(callerAddress, this.name, EntryEventType.LOADED, key, null, oldValue);
            }
        } else {
            this.accessRecord(record, now);
            oldValue = record.getValue();
        }
        if (oldValue == null) {
            value = this.mapServiceContext.interceptPut(this.name, null, value);
            value = this.mapDataStore.add(key, value, now);
            this.onStore(record);
            record = this.createRecord(key, value, ttl, maxIdle, now);
            this.storage.put(key, record);
            this.mutationObserver.onPutRecord(key, record);
            ExpirationTimeSetter.setExpirationTimes(ttl, maxIdle, record, this.mapContainer.getMapConfig(), false);
        }
        this.saveIndex(record, oldValue);
        return oldValue;
    }

    protected Object removeRecord(Data key, @Nonnull Record record, long now, CallerProvenance provenance) {
        Object oldValue = record.getValue();
        if ((oldValue = this.mapServiceContext.interceptRemove(this.name, oldValue)) != null) {
            this.removeIndex(record);
            if (this.persistenceEnabledFor(provenance)) {
                this.mapDataStore.remove(key, now);
            }
            this.onStore(record);
        }
        this.mutationObserver.onRemoveRecord(key, record);
        this.storage.removeRecord(record);
        return oldValue;
    }

    @Override
    public Record getRecordOrNull(Data key) {
        long now = this.getNow();
        return this.getRecordOrNull(key, now, false);
    }

    protected Record getRecordOrNull(Data key, long now, boolean backup) {
        Record record = (Record)this.storage.get(key);
        if (record == null) {
            return null;
        }
        return this.getOrNullIfExpired(record, now, backup);
    }

    protected void onStore(Record record) {
        if (record == null || this.mapDataStore == MapDataStores.EMPTY_MAP_DATA_STORE) {
            return;
        }
        record.onStore();
    }

    private void updateStoreStats() {
        if (!(this.mapDataStore instanceof WriteBehindStore) || !this.mapContainer.getMapConfig().isStatisticsEnabled()) {
            return;
        }
        long now = Clock.currentTimeMillis();
        WriteBehindQueue<DelayedEntry> writeBehindQueue = ((WriteBehindStore)this.mapDataStore).getWriteBehindQueue();
        List<DelayedEntry> delayedEntries = writeBehindQueue.asList();
        for (DelayedEntry delayedEntry : delayedEntries) {
            Record record = this.getRecordOrNull(this.toData(delayedEntry.getKey()), now, false);
            this.onStore(record);
        }
    }

    @Override
    public boolean isKeyLoadFinished() {
        return this.keyLoader.isKeyLoadFinished();
    }

    @Override
    public void checkIfLoaded() {
        if (this.loadingFutures.isEmpty()) {
            return;
        }
        if (FutureUtil.allDone(this.loadingFutures)) {
            List<Future> doneFutures = null;
            try {
                doneFutures = FutureUtil.getAllDone(this.loadingFutures);
                FutureUtil.checkAllDone(doneFutures);
            }
            catch (Exception e) {
                this.logger.severe("Exception while loading map " + this.name, e);
                throw ExceptionUtil.rethrow(e);
            }
            finally {
                this.loadingFutures.removeAll(doneFutures);
            }
        } else {
            this.keyLoader.triggerLoadingWithDelay();
            throw new RetryableHazelcastException("Map " + this.getName() + " is still loading data from external store");
        }
    }

    @Override
    public boolean isLoaded() {
        boolean result = FutureUtil.allDone(this.loadingFutures);
        if (result) {
            this.loadingFutures.removeAll(FutureUtil.getAllDone(this.loadingFutures));
        }
        return result;
    }

    public Collection<Future> getLoadingFutures() {
        return this.loadingFutures;
    }

    @Override
    public void startLoading() {
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("StartLoading invoked " + this.getStateMessage());
        }
        if (this.mapStoreContext.isMapLoader() && !this.loadedOnCreate) {
            if (!this.loadedOnPreMigration) {
                if (this.logger.isFinestEnabled()) {
                    this.logger.finest("Triggering load " + this.getStateMessage());
                }
                this.loadedOnCreate = true;
                this.loadingFutures.add(this.keyLoader.startInitialLoad(this.mapStoreContext, this.partitionId));
            } else {
                if (this.logger.isFinestEnabled()) {
                    this.logger.finest("Promoting to loaded on migration " + this.getStateMessage());
                }
                this.keyLoader.promoteToLoadedOnMigration();
            }
        }
    }

    @Override
    public void setPreMigrationLoadedStatus(boolean loaded) {
        this.loadedOnPreMigration = loaded;
    }

    @Override
    public void loadAll(boolean replaceExistingValues) {
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("loadAll invoked " + this.getStateMessage());
        }
        this.logger.info("Starting to load all keys for map " + this.name + " on partitionId=" + this.partitionId);
        Future<?> loadingKeysFuture = this.keyLoader.startLoading(this.mapStoreContext, replaceExistingValues);
        this.loadingFutures.add(loadingKeysFuture);
    }

    @Override
    public void loadAllFromStore(List<Data> keys, boolean replaceExistingValues) {
        if (!keys.isEmpty()) {
            Future<?> f = this.recordStoreLoader.loadValues(keys, replaceExistingValues);
            this.loadingFutures.add(f);
        }
    }

    @Override
    public void updateLoadStatus(boolean lastBatch, Throwable exception) {
        this.keyLoader.trackLoading(lastBatch, exception);
        if (lastBatch) {
            this.logger.finest("Completed loading map " + this.name + " on partitionId=" + this.partitionId);
        }
    }

    @Override
    public void maybeDoInitialLoad() {
        if (this.keyLoader.shouldDoInitialLoad()) {
            this.loadAll(false);
        }
    }

    private String getStateMessage() {
        return "on partitionId=" + this.partitionId + " on " + this.mapServiceContext.getNodeEngine().getThisAddress() + " loadedOnCreate=" + this.loadedOnCreate + " loadedOnPreMigration=" + this.loadedOnPreMigration + " isLoaded=" + this.isLoaded();
    }

    @Override
    public int clear() {
        this.checkIfLoaded();
        Collection<Record> clearableRecords = this.getNotLockedRecords();
        List<Data> keys = this.getKeysFromRecords(clearableRecords);
        this.mapDataStore.removeAll(keys);
        this.clearMapStore();
        this.removeIndex(clearableRecords);
        return this.removeRecords(clearableRecords);
    }

    @Override
    public void reset() {
        this.clearMapStore();
        this.storage.clear(false);
        this.stats.reset();
        this.mutationObserver.onReset();
    }

    @Override
    public void destroy() {
        this.clearPartition(false, true);
    }

    @Override
    public void clearPartition(boolean onShutdown, boolean onStorageDestroy) {
        this.clearLockStore();
        this.clearOtherDataThanStorage(onShutdown, onStorageDestroy);
        if (onShutdown) {
            if (this.hasPooledMemoryAllocator()) {
                this.destroyStorageImmediate(true, true);
            } else {
                this.destroyStorageAfterClear(true, true);
            }
        } else if (onStorageDestroy) {
            this.destroyStorageAfterClear(false, false);
        } else {
            this.clearStorage(false);
        }
    }

    private boolean hasPooledMemoryAllocator() {
        NodeEngine nodeEngine = this.mapServiceContext.getNodeEngine();
        NativeMemoryConfig nativeMemoryConfig = nodeEngine.getConfig().getNativeMemoryConfig();
        return nativeMemoryConfig != null && nativeMemoryConfig.getAllocatorType() == NativeMemoryConfig.MemoryAllocatorType.POOLED;
    }

    public void clearOtherDataThanStorage(boolean onShutdown, boolean onStorageDestroy) {
        this.clearMapStore();
        this.clearIndexedData(onShutdown, onStorageDestroy);
    }

    private void destroyStorageImmediate(boolean isDuringShutdown, boolean internal) {
        this.storage.destroy(isDuringShutdown);
        this.mutationObserver.onDestroy(internal);
    }

    public void destroyStorageAfterClear(boolean isDuringShutdown, boolean internal) {
        this.clearStorage(isDuringShutdown);
        this.destroyStorageImmediate(isDuringShutdown, internal);
    }

    private void clearStorage(boolean isDuringShutdown) {
        this.storage.clear(isDuringShutdown);
        this.mutationObserver.onClear();
    }

    private void clearLockStore() {
        NodeEngine nodeEngine = this.mapServiceContext.getNodeEngine();
        LockService lockService = (LockService)nodeEngine.getSharedService("hz:impl:lockService");
        if (lockService != null) {
            ObjectNamespace namespace = MapService.getObjectNamespace(this.name);
            lockService.clearLockStore(this.partitionId, namespace);
        }
    }

    private void clearMapStore() {
        this.mapDataStore.reset();
    }

    private void clearIndexedData(boolean onShutdown, boolean onStorageDestroy) {
        this.clearGlobalIndexes(onShutdown);
        this.clearPartitionedIndexes(onStorageDestroy);
    }

    private void clearGlobalIndexes(boolean onShutdown) {
        Indexes indexes = this.mapContainer.getIndexes(this.partitionId);
        if (indexes.isGlobal()) {
            if (onShutdown) {
                indexes.destroyIndexes();
            } else if (indexes.haveAtLeastOneIndex()) {
                this.fullScanLocalDataToClear(indexes);
            }
        }
    }

    private void clearPartitionedIndexes(boolean onStorageDestroy) {
        Indexes indexes = this.mapContainer.getIndexes(this.partitionId);
        if (indexes.isGlobal()) {
            return;
        }
        if (onStorageDestroy) {
            indexes.destroyIndexes();
        } else {
            indexes.clearAll();
        }
    }

    private void fullScanLocalDataToClear(Indexes indexes) {
        InternalIndex[] indexesSnapshot = indexes.getIndexes();
        for (Record record : this.storage.values()) {
            Data key = record.getKey();
            Object value = Records.getValueOrCachedValue(record, this.serializationService);
            indexes.removeEntry(key, value, Index.OperationSource.SYSTEM);
        }
        Indexes.markPartitionAsUnindexed(this.partitionId, indexesSnapshot);
    }
}

