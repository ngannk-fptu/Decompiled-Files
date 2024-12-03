/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writethrough;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.MapStoreWrapper;
import com.hazelcast.map.impl.mapstore.AbstractMapDataStore;
import com.hazelcast.nio.serialization.Data;

public class WriteThroughStore
extends AbstractMapDataStore<Data, Object> {
    public WriteThroughStore(MapStoreWrapper store, InternalSerializationService serializationService) {
        super(store, serializationService);
    }

    @Override
    public Object add(Data key, Object value, long time) {
        Object objectKey = this.toObject(key);
        Object objectValue = this.toObject(value);
        this.getStore().store(objectKey, objectValue);
        return this.getStore().isPostProcessingMapStore() ? objectValue : value;
    }

    @Override
    public void addTransient(Data key, long now) {
    }

    @Override
    public Object addBackup(Data key, Object value, long time) {
        return value;
    }

    @Override
    public void remove(Data key, long time) {
        this.getStore().delete(this.toObject(key));
    }

    @Override
    public void removeBackup(Data key, long time) {
    }

    @Override
    public void reset() {
    }

    @Override
    public Object load(Data key) {
        return this.getStore().load(this.toObject(key));
    }

    @Override
    public boolean loadable(Data key) {
        return true;
    }

    @Override
    public long softFlush() {
        return 0L;
    }

    @Override
    public void hardFlush() {
    }

    @Override
    public Object flush(Data key, Object value, boolean backup) {
        return value;
    }

    @Override
    public int notFinishedOperationsCount() {
        return 0;
    }
}

