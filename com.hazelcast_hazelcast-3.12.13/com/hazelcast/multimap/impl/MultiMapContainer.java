/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl;

import com.hazelcast.concurrent.lock.LockService;
import com.hazelcast.concurrent.lock.LockStore;
import com.hazelcast.multimap.impl.MultiMapContainerSupport;
import com.hazelcast.multimap.impl.MultiMapMergeContainer;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.multimap.impl.MultiMapValue;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.impl.merge.MergingValueFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.Clock;
import com.hazelcast.util.MapUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class MultiMapContainer
extends MultiMapContainerSupport {
    private static final int ID_PROMOTION_OFFSET = 100000;
    private final DistributedObjectNamespace lockNamespace;
    private final LockStore lockStore;
    private final int partitionId;
    private final long creationTime;
    private final ObjectNamespace objectNamespace;
    private long idGen;
    private volatile long lastAccessTime;
    private volatile long lastUpdateTime;

    public MultiMapContainer(String name, MultiMapService service, int partitionId) {
        super(name, service.getNodeEngine());
        this.partitionId = partitionId;
        this.lockNamespace = new DistributedObjectNamespace("hz:impl:multiMapService", name);
        LockService lockService = (LockService)this.nodeEngine.getSharedService("hz:impl:lockService");
        this.lockStore = lockService == null ? null : lockService.createLockStore(partitionId, this.lockNamespace);
        this.creationTime = Clock.currentTimeMillis();
        this.objectNamespace = new DistributedObjectNamespace("hz:impl:multiMapService", name);
    }

    public boolean canAcquireLock(Data dataKey, String caller, long threadId) {
        return this.lockStore != null && this.lockStore.canAcquireLock(dataKey, caller, threadId);
    }

    public boolean isLocked(Data dataKey) {
        return this.lockStore != null && this.lockStore.isLocked(dataKey);
    }

    public boolean isTransactionallyLocked(Data key) {
        return this.lockStore != null && this.lockStore.shouldBlockReads(key);
    }

    public boolean txnLock(Data key, String caller, long threadId, long referenceId, long ttl, boolean blockReads) {
        return this.lockStore != null && this.lockStore.txnLock(key, caller, threadId, referenceId, ttl, blockReads);
    }

    public boolean unlock(Data key, String caller, long threadId, long referenceId) {
        return this.lockStore != null && this.lockStore.unlock(key, caller, threadId, referenceId);
    }

    public boolean forceUnlock(Data key) {
        return this.lockStore != null && this.lockStore.forceUnlock(key);
    }

    public boolean extendLock(Data key, String caller, long threadId, long ttl) {
        return this.lockStore != null && this.lockStore.extendLeaseTime(key, caller, threadId, ttl);
    }

    public String getLockOwnerInfo(Data dataKey) {
        return this.lockStore != null ? this.lockStore.getOwnerInfo(dataKey) : null;
    }

    public long nextId() {
        return this.idGen++;
    }

    public void setId(long newValue) {
        this.idGen = newValue + 100000L;
    }

    public boolean delete(Data dataKey) {
        return this.multiMapValues.remove(dataKey) != null;
    }

    public Collection<MultiMapRecord> remove(Data dataKey, boolean copyOf) {
        MultiMapValue multiMapValue = (MultiMapValue)this.multiMapValues.remove(dataKey);
        return multiMapValue != null ? multiMapValue.getCollection(copyOf) : null;
    }

    public Set<Data> keySet() {
        Set keySet = this.multiMapValues.keySet();
        return new HashSet<Data>(keySet);
    }

    public Collection<MultiMapRecord> values() {
        LinkedList<MultiMapRecord> valueCollection = new LinkedList<MultiMapRecord>();
        for (MultiMapValue multiMapValue : this.multiMapValues.values()) {
            valueCollection.addAll(multiMapValue.getCollection(false));
        }
        return valueCollection;
    }

    public boolean containsKey(Data key) {
        return this.multiMapValues.containsKey(key);
    }

    public boolean containsEntry(boolean binary, Data key, Data value) {
        MultiMapValue multiMapValue = (MultiMapValue)this.multiMapValues.get(key);
        if (multiMapValue == null) {
            return false;
        }
        MultiMapRecord record = new MultiMapRecord(binary ? value : this.nodeEngine.toObject(value));
        return multiMapValue.getCollection(false).contains(record);
    }

    public boolean containsValue(boolean binary, Data value) {
        for (Data key : this.multiMapValues.keySet()) {
            if (!this.containsEntry(binary, key, value)) continue;
            return true;
        }
        return false;
    }

    public Map<Data, Collection<MultiMapRecord>> copyCollections() {
        Map<Data, Collection<MultiMapRecord>> map = MapUtil.createHashMap(this.multiMapValues.size());
        for (Map.Entry entry : this.multiMapValues.entrySet()) {
            Data key = (Data)entry.getKey();
            Collection<MultiMapRecord> col = ((MultiMapValue)entry.getValue()).getCollection(true);
            map.put(key, col);
        }
        return map;
    }

    public int size() {
        int size = 0;
        for (MultiMapValue multiMapValue : this.multiMapValues.values()) {
            size += multiMapValue.getCollection(false).size();
        }
        return size;
    }

    public int clear() {
        Set<Data> locks = this.lockStore != null ? this.lockStore.getLockedKeys() : Collections.emptySet();
        Map<Data, MultiMapValue> lockedKeys = MapUtil.createHashMap(locks.size());
        for (Data key : locks) {
            MultiMapValue multiMapValue = (MultiMapValue)this.multiMapValues.get(key);
            if (multiMapValue == null) continue;
            lockedKeys.put(key, multiMapValue);
        }
        int numberOfAffectedEntries = this.multiMapValues.size() - lockedKeys.size();
        this.multiMapValues.clear();
        this.multiMapValues.putAll(lockedKeys);
        return numberOfAffectedEntries;
    }

    public void destroy() {
        LockService lockService = (LockService)this.nodeEngine.getSharedService("hz:impl:lockService");
        if (lockService != null) {
            lockService.clearLockStore(this.partitionId, this.lockNamespace);
        }
        this.multiMapValues.clear();
    }

    public void access() {
        this.lastAccessTime = Clock.currentTimeMillis();
    }

    public void update() {
        this.lastUpdateTime = Clock.currentTimeMillis();
    }

    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public long getLockedCount() {
        return this.lockStore.getLockedKeys().size();
    }

    public ObjectNamespace getObjectNamespace() {
        return this.objectNamespace;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    public MultiMapValue merge(MultiMapMergeContainer mergeContainer, SplitBrainMergePolicy<Collection<Object>, SplitBrainMergeTypes.MultiMapMergeTypes> mergePolicy) {
        SerializationService serializationService = this.nodeEngine.getSerializationService();
        serializationService.getManagedContext().initialize(mergePolicy);
        SplitBrainMergeTypes.MultiMapMergeTypes mergingEntry = MergingValueFactory.createMergingEntry(serializationService, mergeContainer);
        MultiMapValue existingValue = this.getMultiMapValueOrNull(mergeContainer.getKey());
        if (existingValue == null) {
            return this.mergeNewValue(mergePolicy, mergingEntry);
        }
        return this.mergeExistingValue(mergePolicy, mergingEntry, existingValue, serializationService);
    }

    private MultiMapValue mergeNewValue(SplitBrainMergePolicy<Collection<Object>, SplitBrainMergeTypes.MultiMapMergeTypes> mergePolicy, SplitBrainMergeTypes.MultiMapMergeTypes mergingEntry) {
        Collection<Object> newValues = mergePolicy.merge(mergingEntry, null);
        if (newValues != null && !newValues.isEmpty()) {
            MultiMapValue mergedValue = this.getOrCreateMultiMapValue((Data)mergingEntry.getKey());
            Collection<MultiMapRecord> records = mergedValue.getCollection(false);
            this.createNewMultiMapRecords(records, newValues);
            if (newValues.equals(mergingEntry.getValue())) {
                this.setMergedStatistics(mergingEntry, mergedValue);
            }
            return mergedValue;
        }
        return null;
    }

    private MultiMapValue mergeExistingValue(SplitBrainMergePolicy<Collection<Object>, SplitBrainMergeTypes.MultiMapMergeTypes> mergePolicy, SplitBrainMergeTypes.MultiMapMergeTypes mergingEntry, MultiMapValue existingValue, SerializationService ss) {
        Collection<MultiMapRecord> existingRecords = existingValue.getCollection(false);
        Data dataKey = (Data)mergingEntry.getKey();
        SplitBrainMergeTypes.MultiMapMergeTypes existingEntry = MergingValueFactory.createMergingEntry(ss, this, dataKey, existingRecords, existingValue.getHits());
        Collection<Object> newValues = mergePolicy.merge(mergingEntry, existingEntry);
        if (newValues == null || newValues.isEmpty()) {
            existingRecords.clear();
            this.multiMapValues.remove(dataKey);
        } else if (!newValues.equals(existingRecords)) {
            existingRecords.clear();
            this.createNewMultiMapRecords(existingRecords, newValues);
            if (newValues.equals(mergingEntry.getValue())) {
                this.setMergedStatistics(mergingEntry, existingValue);
            }
        }
        return existingValue;
    }

    private void createNewMultiMapRecords(Collection<MultiMapRecord> records, Collection<Object> values) {
        boolean isBinary = this.config.isBinary();
        SerializationService serializationService = this.nodeEngine.getSerializationService();
        for (Object value : values) {
            long recordId = this.nextId();
            MultiMapRecord record = new MultiMapRecord(recordId, isBinary ? serializationService.toData(value) : value);
            records.add(record);
        }
    }

    private void setMergedStatistics(SplitBrainMergeTypes.MultiMapMergeTypes mergingEntry, MultiMapValue multiMapValue) {
        multiMapValue.setHits(mergingEntry.getHits());
        this.lastAccessTime = Math.max(this.lastAccessTime, mergingEntry.getLastAccessTime());
        this.lastUpdateTime = Math.max(this.lastUpdateTime, mergingEntry.getLastUpdateTime());
    }
}

