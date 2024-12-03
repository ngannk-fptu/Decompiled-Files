/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.merge;

import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.impl.merge.SplitBrainDataSerializerHook;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.spi.serialization.SerializationServiceAware;
import java.io.IOException;

public class CacheMergingEntryImpl
implements SplitBrainMergeTypes.CacheMergeTypes,
SerializationServiceAware,
IdentifiedDataSerializable {
    private Data value;
    private Data key;
    private long creationTime = -1L;
    private long expirationTime = -1L;
    private long hits = -1L;
    private long lastAccessTime = -1L;
    private transient SerializationService serializationService;

    public CacheMergingEntryImpl() {
    }

    public CacheMergingEntryImpl(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @Override
    public Data getValue() {
        return this.value;
    }

    @Override
    public Object getDeserializedValue() {
        return this.serializationService.toObject(this.value);
    }

    public CacheMergingEntryImpl setValue(Data value) {
        this.value = value;
        return this;
    }

    @Override
    public Data getKey() {
        return this.key;
    }

    @Override
    public Object getDeserializedKey() {
        return this.serializationService.toObject(this.key);
    }

    public CacheMergingEntryImpl setKey(Data key) {
        this.key = key;
        return this;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    public CacheMergingEntryImpl setCreationTime(long creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    @Override
    public long getExpirationTime() {
        return this.expirationTime;
    }

    public CacheMergingEntryImpl setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
        return this;
    }

    @Override
    public long getHits() {
        return this.hits;
    }

    public CacheMergingEntryImpl setHits(long hits) {
        this.hits = hits;
        return this;
    }

    @Override
    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    public CacheMergingEntryImpl setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
        return this;
    }

    @Override
    public void setSerializationService(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        IOUtil.writeObject(out, this.key);
        IOUtil.writeObject(out, this.value);
        out.writeLong(this.creationTime);
        out.writeLong(this.expirationTime);
        out.writeLong(this.hits);
        out.writeLong(this.lastAccessTime);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.key = (Data)IOUtil.readObject(in);
        this.value = (Data)IOUtil.readObject(in);
        this.creationTime = in.readLong();
        this.expirationTime = in.readLong();
        this.hits = in.readLong();
        this.lastAccessTime = in.readLong();
    }

    @Override
    public int getFactoryId() {
        return SplitBrainDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 5;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CacheMergingEntryImpl that = (CacheMergingEntryImpl)o;
        if (this.creationTime != that.creationTime) {
            return false;
        }
        if (this.expirationTime != that.expirationTime) {
            return false;
        }
        if (this.hits != that.hits) {
            return false;
        }
        if (this.lastAccessTime != that.lastAccessTime) {
            return false;
        }
        if (this.key != null ? !this.key.equals(that.key) : that.key != null) {
            return false;
        }
        return this.value != null ? this.value.equals(that.value) : that.value == null;
    }

    public int hashCode() {
        int result = this.key != null ? this.key.hashCode() : 0;
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        result = 31 * result + (int)(this.creationTime ^ this.creationTime >>> 32);
        result = 31 * result + (int)(this.expirationTime ^ this.expirationTime >>> 32);
        result = 31 * result + (int)(this.hits ^ this.hits >>> 32);
        result = 31 * result + (int)(this.lastAccessTime ^ this.lastAccessTime >>> 32);
        return result;
    }

    public String toString() {
        return "CacheMergingEntry{key=" + this.key + ", value=" + this.value + ", creationTime=" + this.creationTime + ", expirationTime=" + this.expirationTime + ", hits=" + this.hits + ", lastAccessTime=" + this.lastAccessTime + '}';
    }
}

