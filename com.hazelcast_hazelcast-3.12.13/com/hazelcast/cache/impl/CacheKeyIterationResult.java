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
import java.util.ArrayList;
import java.util.List;

public class CacheKeyIterationResult
implements IdentifiedDataSerializable {
    private int tableIndex;
    private List<Data> keys;

    public CacheKeyIterationResult() {
    }

    public CacheKeyIterationResult(List<Data> keys, int tableIndex) {
        this.keys = keys;
        this.tableIndex = tableIndex;
    }

    public int getTableIndex() {
        return this.tableIndex;
    }

    public List<Data> getKeys() {
        return this.keys;
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 23;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.tableIndex);
        int size = this.keys.size();
        out.writeInt(size);
        for (Data o : this.keys) {
            out.writeData(o);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.tableIndex = in.readInt();
        int size = in.readInt();
        this.keys = new ArrayList<Data>(size);
        for (int i = 0; i < size; ++i) {
            Data data = in.readData();
            this.keys.add(data);
        }
    }

    public String toString() {
        return "CacheKeyIteratorResult{tableIndex=" + this.tableIndex + '}';
    }

    public int getCount() {
        return this.keys != null ? this.keys.size() : 0;
    }

    public Data getKey(int index) {
        return this.keys != null ? this.keys.get(index) : null;
    }
}

