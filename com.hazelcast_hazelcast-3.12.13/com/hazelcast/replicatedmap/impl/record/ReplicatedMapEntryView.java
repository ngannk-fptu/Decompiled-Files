/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.record;

import com.hazelcast.core.EntryView;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.replicatedmap.impl.operation.ReplicatedMapDataSerializerHook;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;

public class ReplicatedMapEntryView<K, V>
implements EntryView,
IdentifiedDataSerializable,
Versioned {
    private static final int NOT_AVAILABLE = -1;
    private Object key;
    private Object value;
    private long creationTime;
    private long hits;
    private long lastAccessTime;
    private long lastUpdateTime;
    private long ttl;
    private long maxIdle;
    private SerializationService serializationService;

    public ReplicatedMapEntryView() {
    }

    public ReplicatedMapEntryView(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @Override
    public K getKey() {
        if (this.serializationService != null) {
            this.key = this.serializationService.toObject(this.key);
        }
        return (K)this.key;
    }

    public ReplicatedMapEntryView<K, V> setKey(K key) {
        this.key = key;
        return this;
    }

    @Override
    public V getValue() {
        if (this.serializationService != null) {
            this.value = this.serializationService.toObject(this.value);
        }
        return (V)this.value;
    }

    public ReplicatedMapEntryView<K, V> setValue(V value) {
        this.value = value;
        return this;
    }

    @Override
    public long getCost() {
        return -1L;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    public ReplicatedMapEntryView<K, V> setCreationTime(long creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    @Override
    public long getExpirationTime() {
        return -1L;
    }

    @Override
    public long getHits() {
        return this.hits;
    }

    public ReplicatedMapEntryView<K, V> setHits(long hits) {
        this.hits = hits;
        return this;
    }

    @Override
    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    public ReplicatedMapEntryView<K, V> setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
        return this;
    }

    @Override
    public long getLastStoredTime() {
        return -1L;
    }

    @Override
    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public ReplicatedMapEntryView<K, V> setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
        return this;
    }

    @Override
    public long getVersion() {
        return -1L;
    }

    @Override
    public long getTtl() {
        return this.ttl;
    }

    @Override
    public Long getMaxIdle() {
        return this.maxIdle;
    }

    public ReplicatedMapEntryView<K, V> setTtl(long ttl) {
        this.ttl = ttl;
        return this;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        IOUtil.writeObject(out, this.getKey());
        IOUtil.writeObject(out, this.getValue());
        out.writeLong(this.creationTime);
        out.writeLong(this.hits);
        out.writeLong(this.lastAccessTime);
        out.writeLong(this.lastUpdateTime);
        out.writeLong(this.ttl);
        if (out.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            out.writeLong(this.maxIdle);
        }
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
        if (in.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            this.maxIdle = in.readLong();
        }
    }

    @Override
    public int getFactoryId() {
        return ReplicatedMapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 2;
    }

    public String toString() {
        return "ReplicatedMapEntryView{key=" + this.getKey() + ", value=" + this.getValue() + ", creationTime=" + this.creationTime + ", hits=" + this.hits + ", lastAccessTime=" + this.lastAccessTime + ", lastUpdateTime=" + this.lastUpdateTime + ", ttl=" + this.ttl + '}';
    }
}

