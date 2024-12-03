/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.recordstore;

import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.EntryView;
import com.hazelcast.map.impl.EntryCostEstimator;
import com.hazelcast.map.impl.OwnedEntryCostEstimatorFactory;
import com.hazelcast.map.impl.iterator.MapEntriesWithCursor;
import com.hazelcast.map.impl.iterator.MapKeysWithCursor;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.record.RecordFactory;
import com.hazelcast.map.impl.recordstore.LazyEntryViewFromRecord;
import com.hazelcast.map.impl.recordstore.Storage;
import com.hazelcast.map.impl.recordstore.StorageSCHM;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StorageImpl<R extends Record>
implements Storage<Data, R> {
    private final RecordFactory<R> recordFactory;
    private final StorageSCHM<R> records;
    private EntryCostEstimator<Data, Record> entryCostEstimator;

    StorageImpl(RecordFactory<R> recordFactory, InMemoryFormat inMemoryFormat, SerializationService serializationService) {
        this.recordFactory = recordFactory;
        this.entryCostEstimator = OwnedEntryCostEstimatorFactory.createMapSizeEstimator(inMemoryFormat);
        this.records = new StorageSCHM(serializationService);
    }

    @Override
    public void clear(boolean isDuringShutdown) {
        this.records.clear();
        this.entryCostEstimator.reset();
    }

    @Override
    public Collection<R> values() {
        return this.records.values();
    }

    @Override
    public Iterator<R> mutationTolerantIterator() {
        return this.records.values().iterator();
    }

    @Override
    public void put(Data key, R record) {
        record.setKey(key);
        Record previousRecord = (Record)this.records.put(key, record);
        if (previousRecord == null) {
            this.updateCostEstimate(this.entryCostEstimator.calculateEntryCost(key, (Record)record));
        } else {
            this.updateCostEstimate(-this.entryCostEstimator.calculateValueCost(previousRecord));
            this.updateCostEstimate(this.entryCostEstimator.calculateValueCost((Record)record));
        }
    }

    @Override
    public void updateRecordValue(Data key, R record, Object value) {
        this.updateCostEstimate(-this.entryCostEstimator.calculateValueCost((Record)record));
        this.recordFactory.setValue((Record<R>)record, value);
        this.updateCostEstimate(this.entryCostEstimator.calculateValueCost((Record)record));
    }

    @Override
    public R get(Data key) {
        return (R)((Record)this.records.get(key));
    }

    @Override
    public R getIfSameKey(Data key) {
        throw new UnsupportedOperationException("StorageImpl#getIfSameKey");
    }

    @Override
    public int size() {
        return this.records.size();
    }

    @Override
    public boolean isEmpty() {
        return this.records.isEmpty();
    }

    @Override
    public void destroy(boolean isDuringShutdown) {
        this.clear(isDuringShutdown);
    }

    @Override
    public EntryCostEstimator getEntryCostEstimator() {
        return this.entryCostEstimator;
    }

    @Override
    public boolean containsKey(Data key) {
        return this.records.containsKey(key);
    }

    @Override
    public void removeRecord(R record) {
        if (record == null) {
            return;
        }
        Data key = record.getKey();
        this.records.remove(key);
        this.updateCostEstimate(-this.entryCostEstimator.calculateEntryCost(key, (Record)record));
    }

    protected void updateCostEstimate(long entrySize) {
        this.entryCostEstimator.adjustEstimateBy(entrySize);
    }

    @Override
    public void setEntryCostEstimator(EntryCostEstimator entryCostEstimator) {
        this.entryCostEstimator = entryCostEstimator;
    }

    @Override
    public void disposeDeferredBlocks() {
    }

    @Override
    public Iterable getRandomSamples(int sampleCount) {
        return this.records.getRandomSamples(sampleCount);
    }

    @Override
    public MapKeysWithCursor fetchKeys(int tableIndex, int size) {
        ArrayList<Data> keys = new ArrayList<Data>(size);
        int newTableIndex = this.records.fetchKeys(tableIndex, size, keys);
        return new MapKeysWithCursor((List<Data>)keys, newTableIndex);
    }

    @Override
    public MapEntriesWithCursor fetchEntries(int tableIndex, int size, SerializationService serializationService) {
        ArrayList entries = new ArrayList(size);
        int newTableIndex = this.records.fetchEntries(tableIndex, size, entries);
        ArrayList<Map.Entry<Data, Data>> entriesData = new ArrayList<Map.Entry<Data, Data>>(entries.size());
        for (Map.Entry entry : entries) {
            Record record = (Record)entry.getValue();
            Object dataValue = serializationService.toData(record.getValue());
            entriesData.add(new AbstractMap.SimpleEntry(entry.getKey(), dataValue));
        }
        return new MapEntriesWithCursor((List<Map.Entry<Data, Data>>)entriesData, newTableIndex);
    }

    @Override
    public Record extractRecordFromLazy(EntryView entryView) {
        return ((LazyEntryViewFromRecord)entryView).getRecord();
    }
}

