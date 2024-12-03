/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class MapEntries
implements IdentifiedDataSerializable {
    private List<Data> keys;
    private List<Data> values;

    public MapEntries() {
    }

    public MapEntries(int initialSize) {
        this.keys = new ArrayList<Data>(initialSize);
        this.values = new ArrayList<Data>(initialSize);
    }

    public MapEntries(List<Map.Entry<Data, Data>> entries) {
        int initialSize = entries.size();
        this.keys = new ArrayList<Data>(initialSize);
        this.values = new ArrayList<Data>(initialSize);
        for (Map.Entry<Data, Data> entry : entries) {
            this.keys.add(entry.getKey());
            this.values.add(entry.getValue());
        }
    }

    public void add(Data key, Data value) {
        this.ensureEntriesCreated();
        this.keys.add(key);
        this.values.add(value);
    }

    public List<Map.Entry<Data, Data>> entries() {
        ArrayList<Map.Entry<Data, Data>> entries = new ArrayList<Map.Entry<Data, Data>>(this.keys.size());
        this.putAllToList(entries);
        return entries;
    }

    public Data getKey(int index) {
        return this.keys.get(index);
    }

    public Data getValue(int index) {
        return this.values.get(index);
    }

    public int size() {
        return this.keys == null ? 0 : this.keys.size();
    }

    public boolean isEmpty() {
        return this.keys == null || this.keys.size() == 0;
    }

    public void clear() {
        if (this.keys != null) {
            this.keys.clear();
            this.values.clear();
        }
    }

    public void putAllToList(Collection<Map.Entry<Data, Data>> targetList) {
        if (this.keys == null) {
            return;
        }
        Iterator<Data> keyIterator = this.keys.iterator();
        Iterator<Data> valueIterator = this.values.iterator();
        while (keyIterator.hasNext()) {
            targetList.add(new AbstractMap.SimpleImmutableEntry<Data, Data>(keyIterator.next(), valueIterator.next()));
        }
    }

    public <K, V> void putAllToMap(SerializationService serializationService, Map<K, V> map) {
        if (this.keys == null) {
            return;
        }
        Iterator<Data> keyIterator = this.keys.iterator();
        Iterator<Data> valueIterator = this.values.iterator();
        while (keyIterator.hasNext()) {
            Object key = serializationService.toObject(keyIterator.next());
            Object value = serializationService.toObject(valueIterator.next());
            map.put(key, value);
        }
    }

    private void ensureEntriesCreated() {
        if (this.keys == null) {
            this.keys = new ArrayList<Data>();
            this.values = new ArrayList<Data>();
        }
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 7;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        int size = this.size();
        out.writeInt(size);
        for (int i = 0; i < size; ++i) {
            out.writeData(this.keys.get(i));
            out.writeData(this.values.get(i));
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        this.keys = new ArrayList<Data>(size);
        this.values = new ArrayList<Data>(size);
        for (int i = 0; i < size; ++i) {
            this.keys.add(in.readData());
            this.values.add(in.readData());
        }
    }
}

