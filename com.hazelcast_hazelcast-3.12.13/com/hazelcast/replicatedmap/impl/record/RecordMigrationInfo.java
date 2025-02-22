/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.record;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.replicatedmap.impl.operation.ReplicatedMapDataSerializerHook;
import java.io.IOException;

public class RecordMigrationInfo
implements IdentifiedDataSerializable {
    private Data key;
    private Data value;
    private long ttl;
    private long hits;
    private long lastAccessTime;
    private long lastUpdateTime;
    private long creationTime;

    public RecordMigrationInfo() {
    }

    public RecordMigrationInfo(Data key, Data value, long ttl) {
        this.key = key;
        this.value = value;
        this.ttl = ttl;
    }

    public Data getKey() {
        return this.key;
    }

    public void setKey(Data key) {
        this.key = key;
    }

    public Data getValue() {
        return this.value;
    }

    public void setValue(Data value) {
        this.value = value;
    }

    public long getTtl() {
        return this.ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public long getHits() {
        return this.hits;
    }

    public void setHits(long hits) {
        this.hits = hits;
    }

    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeData(this.key);
        out.writeData(this.value);
        out.writeLong(this.ttl);
        out.writeLong(this.hits);
        out.writeLong(this.lastAccessTime);
        out.writeLong(this.lastUpdateTime);
        out.writeLong(this.creationTime);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.key = in.readData();
        this.value = in.readData();
        this.ttl = in.readLong();
        this.hits = in.readLong();
        this.lastAccessTime = in.readLong();
        this.lastUpdateTime = in.readLong();
        this.creationTime = in.readLong();
    }

    public String toString() {
        return "RecordMigrationInfo{key=" + this.key + ", value=" + this.value + ", ttl=" + this.ttl + ", hits=" + this.hits + ", lastAccessTime=" + this.lastAccessTime + ", lastUpdateTime=" + this.lastUpdateTime + ", creationTime=" + this.creationTime + '}';
    }

    @Override
    public int getFactoryId() {
        return ReplicatedMapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 25;
    }
}

