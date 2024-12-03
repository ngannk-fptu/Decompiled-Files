/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.record;

import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.Member;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.replicatedmap.impl.ReplicatedMapEventPublishingService;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.operation.ReplicateUpdateOperation;
import com.hazelcast.replicatedmap.impl.operation.VersionResponsePair;
import com.hazelcast.replicatedmap.impl.record.AbstractBaseReplicatedRecordStore;
import com.hazelcast.replicatedmap.impl.record.EntrySetIteratorFactory;
import com.hazelcast.replicatedmap.impl.record.InternalReplicatedMapStorage;
import com.hazelcast.replicatedmap.impl.record.KeySetIteratorFactory;
import com.hazelcast.replicatedmap.impl.record.LazyCollection;
import com.hazelcast.replicatedmap.impl.record.LazySet;
import com.hazelcast.replicatedmap.impl.record.RecordMigrationInfo;
import com.hazelcast.replicatedmap.impl.record.ReplicatedMapEntryView;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecord;
import com.hazelcast.replicatedmap.impl.record.ValuesIteratorFactory;
import com.hazelcast.replicatedmap.merge.ReplicatedMapMergePolicy;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.impl.merge.MergingValueFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.util.Clock;
import com.hazelcast.util.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class AbstractReplicatedRecordStore<K, V>
extends AbstractBaseReplicatedRecordStore<K, V> {
    public AbstractReplicatedRecordStore(String name, ReplicatedMapService replicatedMapService, int partitionId) {
        super(name, replicatedMapService, partitionId);
    }

    @Override
    public Object remove(Object key) {
        InternalReplicatedMapStorage storage = this.getStorage();
        Object old = this.remove(storage, key);
        storage.incrementVersion();
        return old;
    }

    @Override
    public Object removeWithVersion(Object key, long version) {
        InternalReplicatedMapStorage storage = this.getStorage();
        Object old = this.remove(storage, key);
        storage.setVersion(version);
        return old;
    }

    private Object remove(InternalReplicatedMapStorage<K, V> storage, Object key) {
        Object oldValue;
        Preconditions.isNotNull(key, "key");
        long time = Clock.currentTimeMillis();
        Object marshalledKey = this.marshall(key);
        ReplicatedRecord<K, V> current = storage.get(marshalledKey);
        if (current == null) {
            oldValue = null;
        } else {
            oldValue = current.getValueInternal();
            storage.remove(marshalledKey, current);
        }
        if (this.replicatedMapConfig.isStatisticsEnabled()) {
            this.getStats().incrementRemoves(Clock.currentTimeMillis() - time);
        }
        this.cancelTtlEntry(marshalledKey);
        return oldValue;
    }

    @Override
    public void evict(Object key) {
        Object oldValue;
        Preconditions.isNotNull(key, "key");
        long time = Clock.currentTimeMillis();
        Object marshalledKey = this.marshall(key);
        InternalReplicatedMapStorage storage = this.getStorage();
        ReplicatedRecord current = storage.get(marshalledKey);
        if (current == null) {
            oldValue = null;
        } else {
            oldValue = current.getValueInternal();
            storage.remove(marshalledKey, current);
        }
        Data dataKey = this.nodeEngine.toData(key);
        Data dataOldValue = this.nodeEngine.toData(oldValue);
        ReplicatedMapEventPublishingService eventPublishingService = this.replicatedMapService.getEventPublishingService();
        eventPublishingService.fireEntryListenerEvent(dataKey, dataOldValue, null, EntryEventType.EVICTED, this.name, this.nodeEngine.getThisAddress());
        if (this.replicatedMapConfig.isStatisticsEnabled()) {
            this.getStats().incrementRemoves(Clock.currentTimeMillis() - time);
        }
    }

    @Override
    public Object get(Object key) {
        Object value;
        long ttlMillis;
        Preconditions.isNotNull(key, "key");
        long time = Clock.currentTimeMillis();
        ReplicatedRecord replicatedRecord = this.getStorage().get(this.marshall(key));
        long l = ttlMillis = replicatedRecord == null ? 0L : replicatedRecord.getTtlMillis();
        if (ttlMillis > 0L && Clock.currentTimeMillis() - replicatedRecord.getUpdateTime() >= ttlMillis) {
            replicatedRecord = null;
        }
        Object object = value = replicatedRecord == null ? null : this.unmarshall(replicatedRecord.getValue());
        if (this.replicatedMapConfig.isStatisticsEnabled()) {
            this.getStats().incrementGets(Clock.currentTimeMillis() - time);
        }
        return value;
    }

    @Override
    public Object put(Object key, Object value) {
        Preconditions.isNotNull(key, "key");
        Preconditions.isNotNull(value, "value");
        return this.put(key, value, 0L, TimeUnit.MILLISECONDS, true);
    }

    @Override
    public Object put(Object key, Object value, long ttl, TimeUnit timeUnit, boolean incrementHits) {
        InternalReplicatedMapStorage storage = this.getStorage();
        Object old = this.put(storage, key, value, ttl, timeUnit, incrementHits);
        storage.incrementVersion();
        return old;
    }

    @Override
    public Object putWithVersion(Object key, Object value, long ttl, TimeUnit timeUnit, boolean incrementHits, long version) {
        InternalReplicatedMapStorage storage = this.getStorage();
        Object old = this.put(storage, key, value, ttl, timeUnit, incrementHits);
        storage.setVersion(version);
        return old;
    }

    private Object put(InternalReplicatedMapStorage<K, V> storage, Object key, Object value, long ttl, TimeUnit timeUnit, boolean incrementHits) {
        Preconditions.isNotNull(key, "key");
        Preconditions.isNotNull(value, "value");
        Preconditions.isNotNull(timeUnit, "timeUnit");
        if (ttl < 0L) {
            throw new IllegalArgumentException("ttl must be a positive integer");
        }
        long time = Clock.currentTimeMillis();
        Object oldValue = null;
        Object marshalledKey = this.marshall(key);
        Object marshalledValue = this.marshall(value);
        long ttlMillis = ttl == 0L ? 0L : timeUnit.toMillis(ttl);
        ReplicatedRecord<K, Object> old = storage.get(marshalledKey);
        if (old == null) {
            ReplicatedRecord<Object, Object> record = this.buildReplicatedRecord(marshalledKey, marshalledValue, ttlMillis);
            storage.put(marshalledKey, record);
        } else {
            oldValue = old.getValueInternal();
            if (incrementHits) {
                old.setValue(marshalledValue, ttlMillis);
            } else {
                old.setValueInternal(marshalledValue, ttlMillis);
            }
            storage.put(marshalledKey, old);
        }
        if (ttlMillis > 0L) {
            this.scheduleTtlEntry(ttlMillis, marshalledKey, marshalledValue);
        } else {
            this.cancelTtlEntry(marshalledKey);
        }
        if (this.replicatedMapConfig.isStatisticsEnabled()) {
            this.getStats().incrementPuts(Clock.currentTimeMillis() - time);
        }
        return oldValue;
    }

    @Override
    public boolean containsKey(Object key) {
        Preconditions.isNotNull(key, "key");
        this.getStats().incrementOtherOperations();
        return this.containsKeyAndValue(key);
    }

    private boolean containsKeyAndValue(Object key) {
        ReplicatedRecord replicatedRecord = this.getStorage().get(this.marshall(key));
        return replicatedRecord != null && replicatedRecord.getValue() != null;
    }

    @Override
    public boolean containsValue(Object value) {
        Preconditions.isNotNull(value, "value");
        this.getStats().incrementOtherOperations();
        Object v = this.unmarshall(value);
        for (Map.Entry entry : this.getStorage().entrySet()) {
            Object entryValue = entry.getValue().getValue();
            if (v != entryValue && (entryValue == null || !this.unmarshall(entryValue).equals(v))) continue;
            return true;
        }
        return false;
    }

    @Override
    public Set keySet(boolean lazy) {
        this.getStats().incrementOtherOperations();
        if (lazy) {
            return new LazySet(new KeySetIteratorFactory(this), this.getStorage());
        }
        return this.getStorage().keySet();
    }

    @Override
    public Collection values(boolean lazy) {
        this.getStats().incrementOtherOperations();
        if (lazy) {
            return new LazyCollection(new ValuesIteratorFactory(this), this.getStorage());
        }
        return this.getStorage().values();
    }

    @Override
    public Collection values(Comparator comparator) {
        InternalReplicatedMapStorage storage = this.getStorage();
        ArrayList<Object> values = new ArrayList<Object>(storage.size());
        for (ReplicatedRecord record : storage.values()) {
            values.add(this.unmarshall(record.getValue()));
        }
        this.getStats().incrementOtherOperations();
        return values;
    }

    @Override
    public Set entrySet(boolean lazy) {
        this.getStats().incrementOtherOperations();
        if (lazy) {
            return new LazySet(new EntrySetIteratorFactory(this), this.getStorage());
        }
        return this.getStorage().entrySet();
    }

    @Override
    public ReplicatedRecord getReplicatedRecord(Object key) {
        Preconditions.isNotNull(key, "key");
        return this.getStorage().get(this.marshall(key));
    }

    @Override
    public boolean isEmpty() {
        this.getStats().incrementOtherOperations();
        return this.getStorage().isEmpty();
    }

    @Override
    public int size() {
        this.getStats().incrementOtherOperations();
        return this.getStorage().size();
    }

    @Override
    public void clear() {
        this.clearInternal().incrementVersion();
    }

    @Override
    public void clearWithVersion(long version) {
        this.clearInternal().setVersion(version);
    }

    @Override
    public void reset() {
        this.destroy();
    }

    public Iterator recordIterator() {
        return new RecordIterator(this.getStorage().entrySet().iterator());
    }

    @Override
    public void putRecords(Collection<RecordMigrationInfo> records, long version) {
        InternalReplicatedMapStorage storage = this.getStorage();
        for (RecordMigrationInfo record : records) {
            this.putRecord(storage, record);
        }
        storage.syncVersion(version);
    }

    private void putRecord(InternalReplicatedMapStorage<K, V> storage, RecordMigrationInfo record) {
        Object key = this.marshall(record.getKey());
        Object value = this.marshall(record.getValue());
        ReplicatedRecord<Object, Object> newRecord = this.buildReplicatedRecord(key, value, record.getTtl());
        newRecord.setHits(record.getHits());
        newRecord.setCreationTime(record.getCreationTime());
        newRecord.setLastAccessTime(record.getLastAccessTime());
        newRecord.setUpdateTime(record.getLastUpdateTime());
        storage.put(key, newRecord);
        if (record.getTtl() > 0L) {
            this.scheduleTtlEntry(record.getTtl(), key, value);
        }
    }

    private ReplicatedRecord<K, V> buildReplicatedRecord(K key, V value, long ttlMillis) {
        return new ReplicatedRecord<K, V>(key, value, ttlMillis);
    }

    @Override
    public boolean merge(SplitBrainMergeTypes.ReplicatedMapMergeTypes mergingEntry, SplitBrainMergePolicy<Object, SplitBrainMergeTypes.ReplicatedMapMergeTypes> mergePolicy) {
        this.serializationService.getManagedContext().initialize(mergingEntry);
        this.serializationService.getManagedContext().initialize(mergePolicy);
        Object marshalledKey = this.marshall(mergingEntry.getKey());
        InternalReplicatedMapStorage<Object, Object> storage = this.getStorage();
        ReplicatedRecord<Object, Object> record = storage.get(marshalledKey);
        if (record == null) {
            Object newValue = mergePolicy.merge(mergingEntry, null);
            if (newValue == null) {
                return false;
            }
            record = this.buildReplicatedRecord(marshalledKey, newValue, 0L);
            storage.put(marshalledKey, record);
            storage.incrementVersion();
            Object dataKey = this.serializationService.toData(marshalledKey);
            Object dataValue = this.serializationService.toData(newValue);
            VersionResponsePair responsePair = new VersionResponsePair(mergingEntry.getValue(), this.getVersion());
            this.sendReplicationOperation(false, this.name, (Data)dataKey, (Data)dataValue, record.getTtlMillis(), responsePair);
        } else {
            SplitBrainMergeTypes.ReplicatedMapMergeTypes existingEntry = MergingValueFactory.createMergingEntry(this.serializationService, record);
            Object newValue = mergePolicy.merge(mergingEntry, existingEntry);
            if (newValue == null) {
                storage.remove(marshalledKey, record);
                storage.incrementVersion();
                Object dataKey = this.serializationService.toData(marshalledKey);
                VersionResponsePair responsePair = new VersionResponsePair(mergingEntry.getValue(), this.getVersion());
                this.sendReplicationOperation(true, this.name, (Data)dataKey, null, record.getTtlMillis(), responsePair);
                return false;
            }
            record.setValueInternal(newValue, record.getTtlMillis());
            storage.incrementVersion();
            Object dataKey = this.serializationService.toData(marshalledKey);
            Object dataValue = this.serializationService.toData(newValue);
            VersionResponsePair responsePair = new VersionResponsePair(mergingEntry.getValue(), this.getVersion());
            this.sendReplicationOperation(false, this.name, (Data)dataKey, (Data)dataValue, record.getTtlMillis(), responsePair);
        }
        return true;
    }

    @Override
    public boolean merge(Object key, ReplicatedMapEntryView mergingEntry, ReplicatedMapMergePolicy mergePolicy) {
        Object marshalledKey = this.marshall(key);
        InternalReplicatedMapStorage<Object, Object> storage = this.getStorage();
        ReplicatedRecord<Object, Object> existingRecord = storage.get(marshalledKey);
        if (existingRecord == null) {
            ReplicatedMapEntryView nullEntryView = new ReplicatedMapEntryView(this.serializationService).setKey(key);
            Object newValue = mergePolicy.merge(this.name, mergingEntry, nullEntryView);
            if (newValue == null) {
                return false;
            }
            existingRecord = this.buildReplicatedRecord(marshalledKey, newValue, 0L);
            storage.put(marshalledKey, existingRecord);
            storage.incrementVersion();
            Object dataKey = this.serializationService.toData(marshalledKey);
            Object dataValue = this.serializationService.toData(newValue);
            VersionResponsePair responsePair = new VersionResponsePair(mergingEntry.getValue(), this.getVersion());
            this.sendReplicationOperation(false, this.name, (Data)dataKey, (Data)dataValue, existingRecord.getTtlMillis(), responsePair);
        } else {
            ReplicatedMapEntryView existingEntry = new ReplicatedMapEntryView(this.serializationService).setKey(key).setValue(existingRecord.getValueInternal()).setCreationTime(existingRecord.getCreationTime()).setLastUpdateTime(existingRecord.getUpdateTime()).setLastAccessTime(existingRecord.getLastAccessTime()).setHits(existingRecord.getHits()).setTtl(existingRecord.getTtlMillis());
            Object newValue = mergePolicy.merge(this.name, mergingEntry, existingEntry);
            if (newValue == null) {
                storage.remove(marshalledKey, existingRecord);
                storage.incrementVersion();
                Object dataKey = this.serializationService.toData(marshalledKey);
                VersionResponsePair responsePair = new VersionResponsePair(mergingEntry.getValue(), this.getVersion());
                this.sendReplicationOperation(true, this.name, (Data)dataKey, null, existingRecord.getTtlMillis(), responsePair);
                return false;
            }
            existingRecord.setValueInternal(newValue, existingRecord.getTtlMillis());
            storage.incrementVersion();
            Object dataKey = this.serializationService.toData(marshalledKey);
            Object dataValue = this.serializationService.toData(newValue);
            VersionResponsePair responsePair = new VersionResponsePair(mergingEntry.getValue(), this.getVersion());
            this.sendReplicationOperation(false, this.name, (Data)dataKey, (Data)dataValue, existingRecord.getTtlMillis(), responsePair);
        }
        return true;
    }

    private void sendReplicationOperation(boolean isRemove, String name, Data key, Data value, long ttl, VersionResponsePair response) {
        Collection<Member> members = this.nodeEngine.getClusterService().getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
        for (Member member : members) {
            this.invoke(isRemove, member.getAddress(), name, key, value, ttl, response);
        }
    }

    private void invoke(boolean isRemove, Address address, String name, Data key, Data value, long ttl, VersionResponsePair response) {
        OperationService operationService = this.nodeEngine.getOperationService();
        ReplicateUpdateOperation updateOperation = new ReplicateUpdateOperation(name, key, value, ttl, response, isRemove, this.nodeEngine.getThisAddress());
        updateOperation.setPartitionId(this.partitionId);
        updateOperation.setValidateTarget(false);
        operationService.invokeOnTarget("hz:impl:replicatedMapService", updateOperation, address);
    }

    private final class RecordIterator
    implements Iterator<ReplicatedRecord<K, V>> {
        private final Iterator<Map.Entry<K, ReplicatedRecord<K, V>>> iterator;
        private Map.Entry<K, ReplicatedRecord<K, V>> entry;

        private RecordIterator(Iterator<Map.Entry<K, ReplicatedRecord<K, V>>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            while (this.iterator.hasNext()) {
                this.entry = this.iterator.next();
                if (!this.testEntry(this.entry)) continue;
                return true;
            }
            return false;
        }

        @Override
        public ReplicatedRecord<K, V> next() {
            ReplicatedRecord record;
            Map.Entry entry = this.entry;
            ReplicatedRecord replicatedRecord = record = entry != null ? entry.getValue() : null;
            while (entry == null) {
                Object value;
                entry = this.findNextEntry();
                Object key = entry.getKey();
                record = entry.getValue();
                Object v1 = value = record != null ? record.getValue() : null;
                if (key == null || value == null) continue;
                break;
            }
            this.entry = null;
            return record;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Lazy structures are not modifiable");
        }

        private boolean testEntry(Map.Entry<K, ReplicatedRecord<K, V>> entry) {
            return entry.getKey() != null && entry.getValue() != null && !entry.getValue().isTombstone();
        }

        private Map.Entry<K, ReplicatedRecord<K, V>> findNextEntry() {
            Map.Entry entry = null;
            while (this.iterator.hasNext() && !this.testEntry(entry = this.iterator.next())) {
                entry = null;
            }
            if (entry == null) {
                throw new NoSuchElementException();
            }
            return entry;
        }
    }
}

