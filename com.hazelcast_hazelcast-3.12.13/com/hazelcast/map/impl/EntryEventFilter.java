/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.EventFilter;
import java.io.IOException;

public class EntryEventFilter
implements EventFilter,
IdentifiedDataSerializable {
    protected boolean includeValue;
    protected Data key;

    public EntryEventFilter(boolean includeValue, Data key) {
        this.includeValue = includeValue;
        this.key = key;
    }

    public EntryEventFilter() {
    }

    public boolean isIncludeValue() {
        return this.includeValue;
    }

    public Data getKey() {
        return this.key;
    }

    @Override
    public boolean eval(Object arg) {
        return this.key == null || this.key.equals(arg);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeBoolean(this.includeValue);
        out.writeData(this.key);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.includeValue = in.readBoolean();
        this.key = in.readData();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EntryEventFilter that = (EntryEventFilter)o;
        if (this.includeValue != that.includeValue) {
            return false;
        }
        return !(this.key != null ? !this.key.equals(that.key) : that.key != null);
    }

    public int hashCode() {
        int result = this.includeValue ? 1 : 0;
        result = 31 * result + (this.key != null ? this.key.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "EntryEventFilter{includeValue=" + this.includeValue + ", key=" + this.key + '}';
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 91;
    }
}

