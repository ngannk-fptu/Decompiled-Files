/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.record;

import com.hazelcast.config.CacheDeserializedValues;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.map.impl.record.AbstractRecord;
import com.hazelcast.map.impl.record.CachedDataRecord;
import com.hazelcast.map.impl.record.CachedDataRecordWithStats;
import com.hazelcast.map.impl.record.DataRecord;
import com.hazelcast.map.impl.record.DataRecordWithStats;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.record.RecordFactory;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;

public class DataRecordFactory
implements RecordFactory<Data> {
    private final SerializationService serializationService;
    private final PartitioningStrategy partitionStrategy;
    private final CacheDeserializedValues cacheDeserializedValues;
    private final boolean statisticsEnabled;

    public DataRecordFactory(MapConfig config, SerializationService serializationService, PartitioningStrategy partitionStrategy) {
        this.serializationService = serializationService;
        this.partitionStrategy = partitionStrategy;
        this.statisticsEnabled = config.isStatisticsEnabled();
        this.cacheDeserializedValues = config.getCacheDeserializedValues();
    }

    @Override
    public Record<Data> newRecord(Data key, Object value) {
        AbstractRecord record;
        assert (value != null) : "value can not be null";
        Object data = this.serializationService.toData(value, this.partitionStrategy);
        switch (this.cacheDeserializedValues) {
            case NEVER: {
                record = this.statisticsEnabled ? new DataRecordWithStats((Data)data) : new DataRecord((Data)data);
                break;
            }
            default: {
                record = this.statisticsEnabled ? new CachedDataRecordWithStats((Data)data) : new CachedDataRecord((Data)data);
            }
        }
        record.setKey(key);
        return record;
    }

    @Override
    public void setValue(Record<Data> record, Object value) {
        assert (value != null) : "value can not be null";
        Data v = value instanceof Data ? (Data)value : this.serializationService.toData(value, this.partitionStrategy);
        record.setValue(v);
    }
}

