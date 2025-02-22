/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.core.EntryView;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import java.io.IOException;

public class SimpleEntryView<K, V>
implements EntryView<K, V>,
IdentifiedDataSerializable,
Versioned {
    private K key;
    private V value;
    private long cost;
    private long creationTime;
    private long expirationTime;
    private long hits;
    private long lastAccessTime;
    private long lastStoredTime;
    private long lastUpdateTime;
    private long version;
    private long ttl;
    private long maxIdle;

    public SimpleEntryView(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public SimpleEntryView() {
    }

    @Override
    public K getKey() {
        return this.key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public SimpleEntryView<K, V> withKey(K key) {
        this.key = key;
        return this;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public SimpleEntryView<K, V> withValue(V value) {
        this.value = value;
        return this;
    }

    @Override
    public long getCost() {
        return this.cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public SimpleEntryView<K, V> withCost(long cost) {
        this.cost = cost;
        return this;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public SimpleEntryView<K, V> withCreationTime(long creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    @Override
    public long getExpirationTime() {
        return this.expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public SimpleEntryView<K, V> withExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
        return this;
    }

    @Override
    public long getHits() {
        return this.hits;
    }

    public void setHits(long hits) {
        this.hits = hits;
    }

    public SimpleEntryView<K, V> withHits(long hits) {
        this.hits = hits;
        return this;
    }

    @Override
    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public SimpleEntryView<K, V> withLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
        return this;
    }

    @Override
    public long getLastStoredTime() {
        return this.lastStoredTime;
    }

    public void setLastStoredTime(long lastStoredTime) {
        this.lastStoredTime = lastStoredTime;
    }

    public SimpleEntryView<K, V> withLastStoredTime(long lastStoredTime) {
        this.lastStoredTime = lastStoredTime;
        return this;
    }

    @Override
    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public SimpleEntryView<K, V> withLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
        return this;
    }

    @Override
    public long getVersion() {
        return this.version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public SimpleEntryView<K, V> withVersion(long version) {
        this.version = version;
        return this;
    }

    @Override
    public long getTtl() {
        return this.ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public SimpleEntryView<K, V> withTtl(long ttl) {
        this.ttl = ttl;
        return this;
    }

    @Override
    public Long getMaxIdle() {
        return this.maxIdle;
    }

    public void setMaxIdle(long maxIdle) {
        this.maxIdle = maxIdle;
    }

    public SimpleEntryView<K, V> withMaxIdle(long maxIdle) {
        this.maxIdle = maxIdle;
        return this;
    }

    public long getEvictionCriteriaNumber() {
        return 0L;
    }

    public void setEvictionCriteriaNumber(long evictionCriteriaNumber) {
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
        out.writeLong(0L);
        out.writeLong(this.ttl);
        if (out.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            out.writeLong(this.maxIdle);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.key = IOUtil.readObject(in);
        this.value = IOUtil.readObject(in);
        this.cost = in.readLong();
        this.creationTime = in.readLong();
        this.expirationTime = in.readLong();
        this.hits = in.readLong();
        this.lastAccessTime = in.readLong();
        this.lastStoredTime = in.readLong();
        this.lastUpdateTime = in.readLong();
        this.version = in.readLong();
        in.readLong();
        this.ttl = in.readLong();
        if (in.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            this.maxIdle = in.readLong();
        }
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 8;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SimpleEntryView that = (SimpleEntryView)o;
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
        if (this.maxIdle != that.maxIdle) {
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
        result = 31 * result + (int)(this.cost ^ this.cost >>> 32);
        result = 31 * result + (int)(this.creationTime ^ this.creationTime >>> 32);
        result = 31 * result + (int)(this.expirationTime ^ this.expirationTime >>> 32);
        result = 31 * result + (int)(this.hits ^ this.hits >>> 32);
        result = 31 * result + (int)(this.lastAccessTime ^ this.lastAccessTime >>> 32);
        result = 31 * result + (int)(this.lastStoredTime ^ this.lastStoredTime >>> 32);
        result = 31 * result + (int)(this.lastUpdateTime ^ this.lastUpdateTime >>> 32);
        result = 31 * result + (int)(this.version ^ this.version >>> 32);
        result = 31 * result + (int)(this.ttl ^ this.ttl >>> 32);
        result = 31 * result + (int)(this.maxIdle ^ this.maxIdle >>> 32);
        return result;
    }

    public String toString() {
        return "EntryView{key=" + this.key + ", value=" + this.value + ", cost=" + this.cost + ", creationTime=" + this.creationTime + ", expirationTime=" + this.expirationTime + ", hits=" + this.hits + ", lastAccessTime=" + this.lastAccessTime + ", lastStoredTime=" + this.lastStoredTime + ", lastUpdateTime=" + this.lastUpdateTime + ", version=" + this.version + ", ttl=" + this.ttl + ", maxIdle=" + this.maxIdle + '}';
    }
}

