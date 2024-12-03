/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CacheEntryIterationResult
implements IdentifiedDataSerializable {
    private int tableIndex;
    private List<Map.Entry<Data, Data>> entries;

    public CacheEntryIterationResult() {
    }

    public CacheEntryIterationResult(List<Map.Entry<Data, Data>> entries, int tableIndex) {
        this.entries = entries;
        this.tableIndex = tableIndex;
    }

    public int getTableIndex() {
        return this.tableIndex;
    }

    public List<Map.Entry<Data, Data>> getEntries() {
        return this.entries;
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 42;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.tableIndex);
        int size = this.entries.size();
        out.writeInt(size);
        for (Map.Entry<Data, Data> entry : this.entries) {
            out.writeData(entry.getKey());
            out.writeData(entry.getValue());
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.tableIndex = in.readInt();
        int size = in.readInt();
        this.entries = new ArrayList<Map.Entry<Data, Data>>(size);
        for (int i = 0; i < size; ++i) {
            Data key = in.readData();
            Data value = in.readData();
            this.entries.add(new AbstractMap.SimpleEntry<Data, Data>(key, value));
        }
    }

    public String toString() {
        return "CacheEntryIteratorResult{tableIndex=" + this.tableIndex + '}';
    }

    public int getCount() {
        return this.entries != null ? this.entries.size() : 0;
    }
}

