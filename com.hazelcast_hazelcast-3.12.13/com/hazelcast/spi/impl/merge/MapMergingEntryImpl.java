/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.merge;

import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.impl.merge.SplitBrainDataSerializerHook;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.spi.serialization.SerializationServiceAware;
import java.io.IOException;

public class MapMergingEntryImpl
implements SplitBrainMergeTypes.MapMergeTypes,
SerializationServiceAware,
IdentifiedDataSerializable,
Versioned {
    private Data value;
    private Data key;
    private long cost = -1L;
    private long creationTime = -1L;
    private long expirationTime = -1L;
    private long hits = -1L;
    private long lastAccessTime = -1L;
    private long lastStoredTime = -1L;
    private long lastUpdateTime = -1L;
    private long version = -1L;
    private long ttl = -1L;
    private Long maxIdle;
    private transient SerializationService serializationService;

    public MapMergingEntryImpl() {
    }

    public MapMergingEntryImpl(SerializationService serializationService) {
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

    public MapMergingEntryImpl setValue(Data value) {
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

    public MapMergingEntryImpl setKey(Data key) {
        this.key = key;
        return this;
    }

    @Override
    public long getCost() {
        return this.cost;
    }

    public MapMergingEntryImpl setCost(long cost) {
        this.cost = cost;
        return this;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    public MapMergingEntryImpl setCreationTime(long creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    @Override
    public long getExpirationTime() {
        return this.expirationTime;
    }

    public MapMergingEntryImpl setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
        return this;
    }

    @Override
    public long getHits() {
        return this.hits;
    }

    public MapMergingEntryImpl setHits(long hits) {
        this.hits = hits;
        return this;
    }

    @Override
    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    public MapMergingEntryImpl setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
        return this;
    }

    @Override
    public long getLastStoredTime() {
        return this.lastStoredTime;
    }

    public MapMergingEntryImpl setLastStoredTime(long lastStoredTime) {
        this.lastStoredTime = lastStoredTime;
        return this;
    }

    @Override
    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public MapMergingEntryImpl setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
        return this;
    }

    @Override
    public long getVersion() {
        return this.version;
    }

    public MapMergingEntryImpl setVersion(long version) {
        this.version = version;
        return this;
    }

    @Override
    public long getTtl() {
        return this.ttl;
    }

    public MapMergingEntryImpl setTtl(long ttl) {
        this.ttl = ttl;
        return this;
    }

    @Override
    public Long getMaxIdle() {
        return this.maxIdle;
    }

    public MapMergingEntryImpl setMaxIdle(Long maxIdle) {
        this.maxIdle = maxIdle;
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
        out.writeLong(this.cost);
        out.writeLong(this.creationTime);
        out.writeLong(this.expirationTime);
        out.writeLong(this.hits);
        out.writeLong(this.lastAccessTime);
        out.writeLong(this.lastStoredTime);
        out.writeLong(this.lastUpdateTime);
        out.writeLong(this.version);
        out.writeLong(this.ttl);
        if (out.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            boolean hasMaxIdle = this.maxIdle != null;
            out.writeBoolean(hasMaxIdle);
            if (hasMaxIdle) {
                out.writeLong(this.maxIdle);
            }
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        boolean hasMaxIdle;
        this.key = (Data)IOUtil.readObject(in);
        this.value = (Data)IOUtil.readObject(in);
        this.cost = in.readLong();
        this.creationTime = in.readLong();
        this.expirationTime = in.readLong();
        this.hits = in.readLong();
        this.lastAccessTime = in.readLong();
        this.lastStoredTime = in.readLong();
        this.lastUpdateTime = in.readLong();
        this.version = in.readLong();
        this.ttl = in.readLong();
        if (in.getVersion().isGreaterOrEqual(Versions.V3_11) && (hasMaxIdle = in.readBoolean())) {
            this.maxIdle = in.readLong();
        }
    }

    @Override
    public int getFactoryId() {
        return SplitBrainDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 4;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MapMergingEntryImpl that = (MapMergingEntryImpl)o;
        if (this.cost != that.cost) {
            return false;
        }
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
        if (this.lastStoredTime != that.lastStoredTime) {
            return false;
        }
        if (this.lastUpdateTime != that.lastUpdateTime) {
            return false;
        }
        if (this.version != that.version) {
            return false;
        }
        if (this.ttl != that.ttl) {
            return false;
        }
        if (this.value != null ? !this.value.equals(that.value) : that.value != null) {
            return false;
        }
        if (this.key != null ? !this.key.equals(that.key) : that.key != null) {
            return false;
        }
        return this.maxIdle != null ? this.maxIdle.equals(that.maxIdle) : that.maxIdle == null;
    }

    public int hashCode() {
        int result = this.value != null ? this.value.hashCode() : 0;
        result = 31 * result + (this.key != null ? this.key.hashCode() : 0);
        result = 31 * result + (int)(this.cost ^ this.cost >>> 32);
        result = 31 * result + (int)(this.creationTime ^ this.creationTime >>> 32);
        result = 31 * result + (int)(this.expirationTime ^ this.expirationTime >>> 32);
        result = 31 * result + (int)(this.hits ^ this.hits >>> 32);
        result = 31 * result + (int)(this.lastAccessTime ^ this.lastAccessTime >>> 32);
        result = 31 * result + (int)(this.lastStoredTime ^ this.lastStoredTime >>> 32);
        result = 31 * result + (int)(this.lastUpdateTime ^ this.lastUpdateTime >>> 32);
        result = 31 * result + (int)(this.version ^ this.version >>> 32);
        result = 31 * result + (int)(this.ttl ^ this.ttl >>> 32);
        result = 31 * result + (this.maxIdle != null ? this.maxIdle.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "MapMergingEntry{key=" + this.key + ", value=" + this.value + ", cost=" + this.cost + ", creationTime=" + this.creationTime + ", expirationTime=" + this.expirationTime + ", hits=" + this.hits + ", lastAccessTime=" + this.lastAccessTime + ", lastStoredTime=" + this.lastStoredTime + ", lastUpdateTime=" + this.lastUpdateTime + ", version=" + this.version + ", ttl=" + this.ttl + ", maxIdle=" + this.maxIdle + '}';
    }
}

