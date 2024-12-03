/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.Map;

public class QueryResultRow
implements IdentifiedDataSerializable,
Map.Entry<Data, Data> {
    private Data key;
    private Data value;

    public QueryResultRow() {
    }

    public QueryResultRow(Data key, Data valueData) {
        this.key = key;
        this.value = valueData;
    }

    @Override
    public Data getKey() {
        return this.key;
    }

    @Override
    public Data getValue() {
        return this.value;
    }

    @Override
    public Data setValue(Data value) {
        return value;
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 9;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeData(this.key);
        out.writeData(this.value);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.key = in.readData();
        this.value = in.readData();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        QueryResultRow that = (QueryResultRow)o;
        if (this.key != null ? !this.key.equals(that.key) : that.key != null) {
            return false;
        }
        return !(this.value != null ? !this.value.equals(that.value) : that.value != null);
    }

    @Override
    public int hashCode() {
        return this.hashCode(this.key) * 31 + this.hashCode(this.value);
    }

    private int hashCode(Data data) {
        return data == null ? 0 : data.hashCode();
    }
}

