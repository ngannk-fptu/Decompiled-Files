/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.merge;

import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.impl.merge.SplitBrainDataSerializerHook;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.spi.serialization.SerializationServiceAware;
import java.io.IOException;

public class ReplicatedMapMergingEntryImpl
implements SplitBrainMergeTypes.ReplicatedMapMergeTypes,
SerializationServiceAware,
IdentifiedDataSerializable {
    private Object value;
    private Object key;
    private long creationTime = -1L;
    private long hits = -1L;
    private long lastAccessTime = -1L;
    private long lastUpdateTime = -1L;
    private long ttl;
    private transient SerializationService serializationService;

    public ReplicatedMapMergingEntryImpl() {
    }

    public ReplicatedMapMergingEntryImpl(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public Object getDeserializedValue() {
        return this.serializationService.toObject(this.value);
    }

    public ReplicatedMapMergingEntryImpl setValue(Object value) {
        this.value = value;
        return this;
    }

    @Override
    public Object getKey() {
        return this.key;
    }

    @Override
    public Object getDeserializedKey() {
        return this.serializationService.toObject(this.key);
    }

    public ReplicatedMapMergingEntryImpl setKey(Object key) {
        this.key = key;
        return this;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    public ReplicatedMapMergingEntryImpl setCreationTime(long creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    @Override
    public long getHits() {
        return this.hits;
    }

    public ReplicatedMapMergingEntryImpl setHits(long hits) {
        this.hits = hits;
        return this;
    }

    @Override
    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    public ReplicatedMapMergingEntryImpl setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
        return this;
    }

    @Override
    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public ReplicatedMapMergingEntryImpl setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
        return this;
    }

    @Override
    public long getTtl() {
        return this.ttl;
    }

    public ReplicatedMapMergingEntryImpl setTtl(long ttl) {
        this.ttl = ttl;
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
        out.writeLong(this.hits);
        out.writeLong(this.lastAccessTime);
        out.writeLong(this.lastUpdateTime);
        out.writeLong(this.ttl);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.key = IOUtil.readObject(in);
        this.value = IOUtil.readObject(in);
        this.creationTime = in.readLong();
        this.hits = in.readLong();
        this.lastAccessTime = in.readLong();
        this.lastUpdateTime = in.readLong();
        this.ttl = in.readLong();
    }

    @Override
    public int getFactoryId() {
        return SplitBrainDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 7;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ReplicatedMapMergingEntryImpl that = (ReplicatedMapMergingEntryImpl)o;
        if (this.creationTime != that.creationTime) {
            return false;
        }
        if (this.hits != that.hits) {
            return false;
        }
        if (this.lastAccessTime != that.lastAccessTime) {
            return false;
        }
        if (this.lastUpdateTime != that.lastUpdateTime) {
            return false;
        }
        if (this.ttl != that.ttl) {
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
        result = 31 * result + (int)(this.hits ^ this.hits >>> 32);
        result = 31 * result + (int)(this.lastAccessTime ^ this.lastAccessTime >>> 32);
        result = 31 * result + (int)(this.lastUpdateTime ^ this.lastUpdateTime >>> 32);
        result = 31 * result + (int)(this.ttl ^ this.ttl >>> 32);
        return result;
    }

    public String toString() {
        return "ReplicatedMapMergingEntry{key=" + this.key + ", value=" + this.value + ", creationTime=" + this.creationTime + ", hits=" + this.hits + ", lastAccessTime=" + this.lastAccessTime + ", lastUpdateTime=" + this.lastUpdateTime + ", ttl=" + this.ttl + '}';
    }
}

