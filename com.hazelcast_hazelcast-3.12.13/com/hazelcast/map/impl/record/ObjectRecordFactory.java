/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.record;

import com.hazelcast.config.MapConfig;
import com.hazelcast.map.impl.record.AbstractRecord;
import com.hazelcast.map.impl.record.ObjectRecord;
import com.hazelcast.map.impl.record.ObjectRecordWithStats;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.record.RecordFactory;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;

public class ObjectRecordFactory
implements RecordFactory<Object> {
    private final SerializationService serializationService;
    private final boolean statisticsEnabled;

    public ObjectRecordFactory(MapConfig config, SerializationService serializationService) {
        this.serializationService = serializationService;
        this.statisticsEnabled = config.isStatisticsEnabled();
    }

    @Override
    public Record<Object> newRecord(Data key, Object value) {
        assert (value != null) : "value can not be null";
        Object objectValue = this.serializationService.toObject(value);
        AbstractRecord record = this.statisticsEnabled ? new ObjectRecordWithStats(objectValue) : new ObjectRecord(objectValue);
        return record;
    }

    @Override
    public void setValue(Record<Object> record, Object value) {
        assert (value != null) : "value can not be null";
        Object v = value;
        if (value instanceof Data) {
            v = this.serializationService.toObject(value);
        }
        record.setValue(v);
    }
}

