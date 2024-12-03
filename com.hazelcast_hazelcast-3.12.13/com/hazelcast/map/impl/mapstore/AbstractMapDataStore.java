/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.MapStoreWrapper;
import com.hazelcast.map.impl.mapstore.MapDataStore;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.DataType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractMapDataStore<K, V>
implements MapDataStore<K, V> {
    private final MapStoreWrapper store;
    private final InternalSerializationService serializationService;

    protected AbstractMapDataStore(MapStoreWrapper store, InternalSerializationService serializationService) {
        if (store == null || serializationService == null) {
            throw new NullPointerException();
        }
        this.store = store;
        this.serializationService = serializationService;
    }

    @Override
    public Map loadAll(Collection keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Object> objectKeys = this.convertToObjectKeys(keys);
        Map entries = this.getStore().loadAll(objectKeys);
        if (entries == null || entries.isEmpty()) {
            return Collections.emptyMap();
        }
        return entries;
    }

    @Override
    public void removeAll(Collection keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        List<Object> objectKeys = this.convertToObjectKeys(keys);
        this.getStore().deleteAll(objectKeys);
    }

    protected Object toObject(Object obj) {
        return this.serializationService.toObject(obj);
    }

    protected Data toHeapData(Object obj) {
        return this.serializationService.toData(obj, DataType.HEAP);
    }

    public MapStoreWrapper getStore() {
        return this.store;
    }

    private List<Object> convertToObjectKeys(Collection keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Object> objectKeys = new ArrayList<Object>(keys.size());
        for (Object key : keys) {
            objectKeys.add(this.toObject(key));
        }
        return objectKeys;
    }

    @Override
    public boolean isPostProcessingMapStore() {
        return this.store.isPostProcessingMapStore();
    }
}

