/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.wan;

import com.hazelcast.core.EntryView;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

@BinaryInterface
public class WanMapEntryView<K, V>
implements EntryView<K, V>,
IdentifiedDataSerializable {
    private final K key;
    private final V value;
    private final long cost;
    private final long creationTime;
    private final long expirationTime;
    private final long hits;
    private final long lastAccessTime;
    private final long lastStoredTime;
    private final long lastUpdateTime;
    private final long version;
    private final long ttl;

    public WanMapEntryView(EntryView<K, V> entryView) {
        this.key = entryView.getKey();
        this.value = entryView.getValue();
        this.cost = entryView.getCost();
        this.version = entryView.getVersion();
        this.hits = entryView.getHits();
        this.lastAccessTime = entryView.getLastAccessTime();
        this.lastUpdateTime = entryView.getLastUpdateTime();
        this.ttl = entryView.getTtl();
        this.creationTime = entryView.getCreationTime();
        this.expirationTime = entryView.getExpirationTime();
        this.lastStoredTime = entryView.getLastStoredTime();
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public long getCost() {
        return this.cost;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public long getExpirationTime() {
        return this.expirationTime;
    }

    @Override
    public long getHits() {
        return this.hits;
    }

    @Override
    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    @Override
    public long getLastStoredTime() {
        return this.lastStoredTime;
    }

    @Override
    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    @Override
    public long getVersion() {
        return this.version;
    }

    @Override
    public long getTtl() {
        return this.ttl;
    }

    @Override
    public Long getMaxIdle() {
        return null;
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
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName() + " should not be deserialized!");
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
        WanMapEntryView that = (WanMapEntryView)o;
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
        return result;
    }

    public String toString() {
        return "WanMapEntryView{key=" + this.key + ", value=" + this.value + ", cost=" + this.cost + ", creationTime=" + this.creationTime + ", expirationTime=" + this.expirationTime + ", hits=" + this.hits + ", lastAccessTime=" + this.lastAccessTime + ", lastStoredTime=" + this.lastStoredTime + ", lastUpdateTime=" + this.lastUpdateTime + ", version=" + this.version + ", ttl=" + this.ttl + '}';
    }
}

