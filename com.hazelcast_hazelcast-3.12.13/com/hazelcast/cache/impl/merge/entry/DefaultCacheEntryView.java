/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.merge.entry;

import com.hazelcast.cache.CacheEntryView;
import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import java.io.IOException;

public class DefaultCacheEntryView
implements CacheEntryView<Data, Data>,
IdentifiedDataSerializable,
Versioned {
    private Data key;
    private Data value;
    private long creationTime;
    private long expirationTime;
    private long lastAccessTime;
    private long accessHit;
    private Data expiryPolicy;

    public DefaultCacheEntryView() {
    }

    public DefaultCacheEntryView(Data key, Data value, long creationTime, long expirationTime, long lastAccessTime, long accessHit, Data expiryPolicy) {
        this.key = key;
        this.value = value;
        this.creationTime = creationTime;
        this.expirationTime = expirationTime;
        this.lastAccessTime = lastAccessTime;
        this.accessHit = accessHit;
        this.expiryPolicy = expiryPolicy;
    }

    @Override
    public Data getKey() {
        return this.key;
    }

    @Override
    public Data getValue() {
        return this.value;
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
    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    @Override
    public long getAccessHit() {
        return this.accessHit;
    }

    @Override
    public Data getExpiryPolicy() {
        return this.expiryPolicy;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.creationTime);
        out.writeLong(this.expirationTime);
        out.writeLong(this.lastAccessTime);
        out.writeLong(this.accessHit);
        out.writeData(this.key);
        out.writeData(this.value);
        if (out.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            out.writeData(this.expiryPolicy);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.creationTime = in.readLong();
        this.expirationTime = in.readLong();
        this.lastAccessTime = in.readLong();
        this.accessHit = in.readLong();
        this.key = in.readData();
        this.value = in.readData();
        if (in.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            this.expiryPolicy = in.readData();
        }
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 44;
    }
}

