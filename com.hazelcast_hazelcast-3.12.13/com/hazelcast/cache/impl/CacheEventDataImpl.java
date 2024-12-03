/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.CacheEventType;
import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.CacheEventData;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.Data;
import java.io.IOException;

@BinaryInterface
public class CacheEventDataImpl
implements CacheEventData {
    private String name;
    private CacheEventType eventType;
    private Data dataKey;
    private Data dataNewValue;
    private Data dataOldValue;
    private boolean isOldValueAvailable;

    public CacheEventDataImpl() {
    }

    public CacheEventDataImpl(String name, CacheEventType eventType, Data dataKey, Data dataNewValue, Data dataOldValue, boolean isOldValueAvailable) {
        this.name = name;
        this.eventType = eventType;
        this.dataKey = dataKey;
        this.dataNewValue = dataNewValue;
        this.dataOldValue = dataOldValue;
        this.isOldValueAvailable = isOldValueAvailable;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public CacheEventType getCacheEventType() {
        return this.eventType;
    }

    @Override
    public Data getDataKey() {
        return this.dataKey;
    }

    @Override
    public Data getDataValue() {
        return this.dataNewValue;
    }

    @Override
    public Data getDataOldValue() {
        return this.dataOldValue;
    }

    @Override
    public boolean isOldValueAvailable() {
        return this.isOldValueAvailable;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.eventType.getType());
        out.writeData(this.dataKey);
        out.writeData(this.dataNewValue);
        out.writeData(this.dataOldValue);
        out.writeBoolean(this.isOldValueAvailable);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.eventType = CacheEventType.getByType(in.readInt());
        this.dataKey = in.readData();
        this.dataNewValue = in.readData();
        this.dataOldValue = in.readData();
        this.isOldValueAvailable = in.readBoolean();
    }

    @Override
    public int getId() {
        return 31;
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    public String toString() {
        return "CacheEventDataImpl{name='" + this.name + '\'' + ", eventType=" + (Object)((Object)this.eventType) + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CacheEventDataImpl that = (CacheEventDataImpl)o;
        if (this.isOldValueAvailable != that.isOldValueAvailable) {
            return false;
        }
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        if (this.eventType != that.eventType) {
            return false;
        }
        if (this.dataKey != null ? !this.dataKey.equals(that.dataKey) : that.dataKey != null) {
            return false;
        }
        if (this.dataNewValue != null ? !this.dataNewValue.equals(that.dataNewValue) : that.dataNewValue != null) {
            return false;
        }
        return this.dataOldValue != null ? this.dataOldValue.equals(that.dataOldValue) : that.dataOldValue == null;
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.eventType != null ? this.eventType.hashCode() : 0);
        result = 31 * result + (this.dataKey != null ? this.dataKey.hashCode() : 0);
        result = 31 * result + (this.dataNewValue != null ? this.dataNewValue.hashCode() : 0);
        result = 31 * result + (this.dataOldValue != null ? this.dataOldValue.hashCode() : 0);
        result = 31 * result + (this.isOldValueAvailable ? 1 : 0);
        return result;
    }
}

