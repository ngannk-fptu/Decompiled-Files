/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.merge;

import com.hazelcast.cache.CacheEntryView;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.cardinality.impl.hyperloglog.HyperLogLog;
import com.hazelcast.collection.impl.collection.CollectionItem;
import com.hazelcast.collection.impl.queue.QueueItem;
import com.hazelcast.core.EntryView;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapMergeContainer;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecord;
import com.hazelcast.ringbuffer.impl.Ringbuffer;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskDescriptor;
import com.hazelcast.spi.impl.merge.AtomicLongMergingValueImpl;
import com.hazelcast.spi.impl.merge.AtomicReferenceMergingValueImpl;
import com.hazelcast.spi.impl.merge.CacheMergingEntryImpl;
import com.hazelcast.spi.impl.merge.CardinalityEstimatorMergingEntry;
import com.hazelcast.spi.impl.merge.CollectionMergingValueImpl;
import com.hazelcast.spi.impl.merge.MapMergingEntryImpl;
import com.hazelcast.spi.impl.merge.MultiMapMergingEntryImpl;
import com.hazelcast.spi.impl.merge.QueueMergingValueImpl;
import com.hazelcast.spi.impl.merge.ReplicatedMapMergingEntryImpl;
import com.hazelcast.spi.impl.merge.RingbufferMergingValueImpl;
import com.hazelcast.spi.impl.merge.ScheduledExecutorMergingEntryImpl;
import com.hazelcast.spi.merge.RingbufferMergeData;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;

public final class MergingValueFactory {
    private MergingValueFactory() {
    }

    public static SplitBrainMergeTypes.CollectionMergeTypes createMergingValue(SerializationService serializationService, Collection<CollectionItem> items) {
        ArrayList<Data> values = new ArrayList<Data>(items.size());
        for (CollectionItem item : items) {
            values.add(item.getValue());
        }
        return (SplitBrainMergeTypes.CollectionMergeTypes)new CollectionMergingValueImpl(serializationService).setValue(values);
    }

    public static SplitBrainMergeTypes.QueueMergeTypes createMergingValue(SerializationService serializationService, Queue<QueueItem> items) {
        ArrayList<Data> values = new ArrayList<Data>(items.size());
        for (QueueItem item : items) {
            values.add(item.getData());
        }
        return (SplitBrainMergeTypes.QueueMergeTypes)new QueueMergingValueImpl(serializationService).setValue(values);
    }

    public static SplitBrainMergeTypes.AtomicLongMergeTypes createMergingValue(SerializationService serializationService, Long value) {
        return (SplitBrainMergeTypes.AtomicLongMergeTypes)new AtomicLongMergingValueImpl(serializationService).setValue(value);
    }

    public static SplitBrainMergeTypes.AtomicReferenceMergeTypes createMergingValue(SerializationService serializationService, Data value) {
        return (SplitBrainMergeTypes.AtomicReferenceMergeTypes)new AtomicReferenceMergingValueImpl(serializationService).setValue(value);
    }

    public static SplitBrainMergeTypes.MapMergeTypes createMergingEntry(SerializationService serializationService, EntryView<Data, Data> entryView) {
        return new MapMergingEntryImpl(serializationService).setKey(entryView.getKey()).setValue(entryView.getValue()).setCreationTime(entryView.getCreationTime()).setExpirationTime(entryView.getExpirationTime()).setLastStoredTime(entryView.getLastStoredTime()).setLastUpdateTime(entryView.getLastUpdateTime()).setLastAccessTime(entryView.getLastAccessTime()).setHits(entryView.getHits()).setTtl(entryView.getTtl()).setMaxIdle(entryView.getMaxIdle()).setVersion(entryView.getVersion()).setCost(entryView.getCost());
    }

    public static SplitBrainMergeTypes.MapMergeTypes createMergingEntry(SerializationService serializationService, Record record) {
        return new MapMergingEntryImpl(serializationService).setKey(record.getKey()).setValue((Data)serializationService.toData(record.getValue())).setCreationTime(record.getCreationTime()).setExpirationTime(record.getExpirationTime()).setLastStoredTime(record.getLastStoredTime()).setLastAccessTime(record.getLastAccessTime()).setLastStoredTime(record.getLastStoredTime()).setLastUpdateTime(record.getLastUpdateTime()).setHits(record.getHits()).setTtl(record.getTtl()).setMaxIdle(record.getMaxIdle()).setVersion(record.getVersion()).setCost(record.getCost());
    }

    public static SplitBrainMergeTypes.MapMergeTypes createMergingEntry(SerializationService serializationService, Data dataKey, Data dataValue, Record record) {
        return new MapMergingEntryImpl(serializationService).setKey(dataKey).setValue(dataValue).setCreationTime(record.getCreationTime()).setExpirationTime(record.getExpirationTime()).setLastStoredTime(record.getLastStoredTime()).setLastAccessTime(record.getLastAccessTime()).setLastUpdateTime(record.getLastUpdateTime()).setHits(record.getHits()).setTtl(record.getTtl()).setMaxIdle(record.getMaxIdle()).setVersion(record.getVersion()).setCost(record.getCost());
    }

    public static SplitBrainMergeTypes.CacheMergeTypes createMergingEntry(SerializationService serializationService, CacheEntryView<Data, Data> entryView) {
        return new CacheMergingEntryImpl(serializationService).setKey(entryView.getKey()).setValue(entryView.getValue()).setCreationTime(entryView.getCreationTime()).setExpirationTime(entryView.getExpirationTime()).setLastAccessTime(entryView.getLastAccessTime()).setHits(entryView.getAccessHit());
    }

    public static <R extends CacheRecord> SplitBrainMergeTypes.CacheMergeTypes createMergingEntry(SerializationService serializationService, Data key, Data value, R record) {
        return new CacheMergingEntryImpl(serializationService).setKey(key).setValue(value).setCreationTime(record.getCreationTime()).setExpirationTime(record.getExpirationTime()).setLastAccessTime(record.getLastAccessTime()).setHits(record.getAccessHit());
    }

    public static SplitBrainMergeTypes.ReplicatedMapMergeTypes createMergingEntry(SerializationService serializationService, ReplicatedRecord record) {
        return new ReplicatedMapMergingEntryImpl(serializationService).setKey(record.getKeyInternal()).setValue(record.getValueInternal()).setCreationTime(record.getCreationTime()).setHits(record.getHits()).setLastAccessTime(record.getLastAccessTime()).setLastUpdateTime(record.getUpdateTime()).setTtl(record.getTtlMillis());
    }

    public static SplitBrainMergeTypes.MultiMapMergeTypes createMergingEntry(SerializationService serializationService, MultiMapMergeContainer container) {
        ArrayList<Object> values = new ArrayList<Object>(container.getRecords().size());
        for (MultiMapRecord record : container.getRecords()) {
            values.add(record.getObject());
        }
        return new MultiMapMergingEntryImpl(serializationService).setKey(container.getKey()).setValues(values).setCreationTime(container.getCreationTime()).setLastAccessTime(container.getLastAccessTime()).setLastUpdateTime(container.getLastUpdateTime()).setHits(container.getHits());
    }

    public static SplitBrainMergeTypes.MultiMapMergeTypes createMergingEntry(SerializationService serializationService, MultiMapContainer container, Data dataKey, Collection<MultiMapRecord> records, long hits) {
        ArrayList<Object> values = new ArrayList<Object>(records.size());
        for (MultiMapRecord record : records) {
            values.add(record.getObject());
        }
        return new MultiMapMergingEntryImpl(serializationService).setKey(dataKey).setValues(values).setCreationTime(container.getCreationTime()).setLastAccessTime(container.getLastAccessTime()).setLastUpdateTime(container.getLastUpdateTime()).setHits(hits);
    }

    public static SplitBrainMergeTypes.RingbufferMergeTypes createMergingValue(SerializationService serializationService, Ringbuffer<Object> items) {
        RingbufferMergeData mergingData = new RingbufferMergeData(items);
        return new RingbufferMergingValueImpl(serializationService).setValues(mergingData);
    }

    public static SplitBrainMergeTypes.CardinalityEstimatorMergeTypes createMergingEntry(SerializationService serializationService, String name, HyperLogLog hyperLogLog) {
        return (SplitBrainMergeTypes.CardinalityEstimatorMergeTypes)((CardinalityEstimatorMergingEntry)new CardinalityEstimatorMergingEntry(serializationService).setKey(name)).setValue(hyperLogLog);
    }

    public static SplitBrainMergeTypes.ScheduledExecutorMergeTypes createMergingEntry(SerializationService serializationService, ScheduledTaskDescriptor task) {
        return (SplitBrainMergeTypes.ScheduledExecutorMergeTypes)((ScheduledExecutorMergingEntryImpl)new ScheduledExecutorMergingEntryImpl(serializationService).setKey(task.getDefinition().getName())).setValue(task);
    }
}

