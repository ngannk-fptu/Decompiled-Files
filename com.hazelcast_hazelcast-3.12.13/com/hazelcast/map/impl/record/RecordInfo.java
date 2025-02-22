/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.record;

import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import java.io.IOException;

public class RecordInfo
implements IdentifiedDataSerializable,
Versioned {
    protected long version;
    protected long ttl;
    protected long maxIdle;
    protected long creationTime;
    protected long lastAccessTime;
    protected long lastUpdateTime;
    protected long hits;
    protected long lastStoredTime;
    protected long expirationTime;

    public RecordInfo() {
    }

    public RecordInfo(RecordInfo recordInfo) {
        this.version = recordInfo.version;
        this.hits = recordInfo.hits;
        this.ttl = recordInfo.ttl;
        this.maxIdle = recordInfo.maxIdle;
        this.creationTime = recordInfo.creationTime;
        this.lastAccessTime = recordInfo.lastAccessTime;
        this.lastUpdateTime = recordInfo.lastUpdateTime;
        this.lastStoredTime = recordInfo.lastStoredTime;
        this.expirationTime = recordInfo.expirationTime;
    }

    public long getVersion() {
        return this.version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getHits() {
        return this.hits;
    }

    public void setHits(long hits) {
        this.hits = hits;
    }

    public long getTtl() {
        return this.ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public long getMaxIdle() {
        return this.maxIdle;
    }

    public void setMaxIdle(long maxIdle) {
        this.maxIdle = maxIdle;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
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

    public long getExpirationTime() {
        return this.expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public long getLastStoredTime() {
        return this.lastStoredTime;
    }

    public void setLastStoredTime(long lastStoredTime) {
        this.lastStoredTime = lastStoredTime;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.version);
        out.writeLong(this.hits);
        out.writeLong(this.ttl);
        out.writeLong(this.creationTime);
        out.writeLong(this.lastAccessTime);
        out.writeLong(this.lastUpdateTime);
        boolean statsEnabled = this.lastStoredTime != -1L || this.expirationTime != -1L;
        out.writeBoolean(statsEnabled);
        if (statsEnabled) {
            out.writeLong(this.lastStoredTime);
            out.writeLong(this.expirationTime);
        }
        if (out.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            out.writeLong(this.maxIdle);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.version = in.readLong();
        this.hits = in.readLong();
        this.ttl = in.readLong();
        this.creationTime = in.readLong();
        this.lastAccessTime = in.readLong();
        this.lastUpdateTime = in.readLong();
        boolean statsEnabled = in.readBoolean();
        this.lastStoredTime = statsEnabled ? in.readLong() : -1L;
        long l = this.expirationTime = statsEnabled ? in.readLong() : -1L;
        if (in.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            this.maxIdle = in.readLong();
        }
    }

    public String toString() {
        return "RecordInfo{creationTime=" + this.creationTime + ", version=" + this.version + ", ttl=" + this.ttl + ", maxIdle=" + this.maxIdle + ", lastAccessTime=" + this.lastAccessTime + ", lastUpdateTime=" + this.lastUpdateTime + ", hits=" + this.hits + ", lastStoredTime=" + this.lastStoredTime + ", expirationTime=" + this.expirationTime + '}';
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 103;
    }
}

