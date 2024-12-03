/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.multimap.impl.MultiMapDataSerializerHook;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class EntrySetResponse
implements IdentifiedDataSerializable {
    private Map<Data, Collection<Data>> map;

    public EntrySetResponse() {
    }

    public EntrySetResponse(Map<Data, Collection<MultiMapRecord>> map, NodeEngine nodeEngine) {
        this.map = MapUtil.createHashMap(map.size());
        for (Map.Entry<Data, Collection<MultiMapRecord>> entry : map.entrySet()) {
            Collection<MultiMapRecord> records = entry.getValue();
            ArrayList<Data> coll = new ArrayList<Data>(records.size());
            for (MultiMapRecord record : records) {
                coll.add(nodeEngine.toData(record.getObject()));
            }
            this.map.put(entry.getKey(), coll);
        }
    }

    public Set<Map.Entry<Data, Data>> getDataEntrySet() {
        Set<Map.Entry<Data, Data>> entrySet = SetUtil.createHashSet(this.map.size() * 2);
        for (Map.Entry<Data, Collection<Data>> entry : this.map.entrySet()) {
            Data key = entry.getKey();
            Collection<Data> coll = entry.getValue();
            for (Data data : coll) {
                entrySet.add(new AbstractMap.SimpleEntry<Data, Data>(key, data));
            }
        }
        return entrySet;
    }

    public <K, V> Set<Map.Entry<K, V>> getObjectEntrySet(NodeEngine nodeEngine) {
        Set<Map.Entry<K, V>> entrySet = SetUtil.createHashSet(this.map.size() * 2);
        for (Map.Entry<Data, Collection<Data>> entry : this.map.entrySet()) {
            Object key = nodeEngine.toObject(entry.getKey());
            Collection<Data> coll = entry.getValue();
            for (Data data : coll) {
                Object val = nodeEngine.toObject(data);
                entrySet.add(new AbstractMap.SimpleEntry(key, val));
            }
        }
        return entrySet;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.map.size());
        for (Map.Entry<Data, Collection<Data>> entry : this.map.entrySet()) {
            out.writeData(entry.getKey());
            Collection<Data> coll = entry.getValue();
            out.writeInt(coll.size());
            for (Data data : coll) {
                out.writeData(data);
            }
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        this.map = MapUtil.createHashMap(size);
        for (int i = 0; i < size; ++i) {
            Data key = in.readData();
            int collSize = in.readInt();
            ArrayList<Data> coll = new ArrayList<Data>(collSize);
            for (int j = 0; j < collSize; ++j) {
                coll.add(in.readData());
            }
            this.map.put(key, coll);
        }
    }

    @Override
    public int getFactoryId() {
        return MultiMapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 47;
    }
}

