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
import java.util.ArrayList;
import java.util.Collection;

public class MultiMapMergingEntryImpl
implements SplitBrainMergeTypes.MultiMapMergeTypes,
SerializationServiceAware,
IdentifiedDataSerializable {
    private Data key;
    private Collection<Object> value;
    private long creationTime = -1L;
    private long expirationTime = -1L;
    private long hits = -1L;
    private long lastAccessTime = -1L;
    private long lastUpdateTime = -1L;
    private transient SerializationService serializationService;

    public MultiMapMergingEntryImpl() {
    }

    public MultiMapMergingEntryImpl(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @Override
    public Data getKey() {
        return this.key;
    }

    @Override
    public <DK> DK getDeserializedKey() {
        return (DK)this.serializationService.toObject(this.key);
    }

    public MultiMapMergingEntryImpl setKey(Data key) {
        this.key = key;
        return this;
    }

    @Override
    public Collection<Object> getValue() {
        return this.value;
    }

    @Override
    public <DV> DV getDeserializedValue() {
        ArrayList deserializedValues = new ArrayList(this.value.size());
        for (Object aValue : this.value) {
            deserializedValues.add(this.serializationService.toObject(aValue));
        }
        return (DV)deserializedValues;
    }

    public MultiMapMergingEntryImpl setValues(Collection<Object> values) {
        this.value = values;
        return this;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    public MultiMapMergingEntryImpl setCreationTime(long creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    @Override
    public long getHits() {
        return this.hits;
    }

    public MultiMapMergingEntryImpl setHits(long hits) {
        this.hits = hits;
        return this;
    }

    @Override
    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    public MultiMapMergingEntryImpl setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
        return this;
    }

    @Override
    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public MultiMapMergingEntryImpl setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
        return this;
    }

    @Override
    public void setSerializationService(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        IOUtil.writeObject(out, this.key);
        out.writeInt(this.value.size());
        for (Object aValue : this.value) {
            out.writeObject(aValue);
        }
        out.writeLong(this.creationTime);
        out.writeLong(this.expirationTime);
        out.writeLong(this.hits);
        out.writeLong(this.lastAccessTime);
        out.writeLong(this.lastUpdateTime);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.key = (Data)IOUtil.readObject(in);
        int size = in.readInt();
        this.value = new ArrayList<Object>(size);
        for (int i = 0; i < size; ++i) {
            this.value.add(in.readObject());
        }
        this.creationTime = in.readLong();
        this.expirationTime = in.readLong();
        this.hits = in.readLong();
        this.lastAccessTime = in.readLong();
        this.lastUpdateTime = in.readLong();
    }

    @Override
    public int getFactoryId() {
        return SplitBrainDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 6;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MultiMapMergingEntryImpl that = (MultiMapMergingEntryImpl)o;
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
        if (this.lastUpdateTime != that.lastUpdateTime) {
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
        result = 31 * result + (int)(this.lastUpdateTime ^ this.lastUpdateTime >>> 32);
        return result;
    }

    public String toString() {
        return "MultiMapMergingEntry{key=" + this.key + ", value=" + this.value + ", creationTime=" + this.creationTime + ", expirationTime=" + this.expirationTime + ", hits=" + this.hits + ", lastAccessTime=" + this.lastAccessTime + ", lastUpdateTime=" + this.lastUpdateTime + '}';
    }
}

