/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.recordstore;

import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.recordstore.LazyEntryViewFromRecord;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.SampleableConcurrentHashMap;

@SerializableByConvention
public class StorageSCHM<R extends Record>
extends SampleableConcurrentHashMap<Data, R> {
    private static final int DEFAULT_INITIAL_CAPACITY = 256;
    private final SerializationService serializationService;

    public StorageSCHM(SerializationService serializationService) {
        super(256);
        this.serializationService = serializationService;
    }

    @Override
    protected <E extends SampleableConcurrentHashMap.SamplingEntry> E createSamplingEntry(Data key, R record) {
        return (E)new LazyEntryViewFromRecord<R>(record, this.serializationService);
    }
}

