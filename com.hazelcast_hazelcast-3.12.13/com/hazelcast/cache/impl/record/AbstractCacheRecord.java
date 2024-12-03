/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.cache.impl.record;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.version.Version;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;

public abstract class AbstractCacheRecord<V, E>
implements CacheRecord<V, E>,
IdentifiedDataSerializable {
    public static final Version EXPIRY_POLICY_VERSION = Versions.V3_11;
    protected long creationTime = -1L;
    protected volatile long expirationTime = -1L;
    protected volatile long accessTime = -1L;
    protected volatile int accessHit;

    protected AbstractCacheRecord() {
    }

    public AbstractCacheRecord(long creationTime, long expirationTime) {
        this.creationTime = creationTime;
        this.expirationTime = expirationTime;
    }

    @Override
    public long getExpirationTime() {
        return this.expirationTime;
    }

    @Override
    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public long getLastAccessTime() {
        return this.accessTime;
    }

    @Override
    public void setAccessTime(long accessTime) {
        this.accessTime = accessTime;
    }

    @Override
    public int getAccessHit() {
        return this.accessHit;
    }

    @Override
    public void setAccessHit(int accessHit) {
        this.accessHit = accessHit;
    }

    @Override
    @SuppressFBWarnings(value={"VO_VOLATILE_INCREMENT"}, justification="CacheRecord can be accessed by only its own partition thread.")
    public void incrementAccessHit() {
        ++this.accessHit;
    }

    @Override
    public void resetAccessHit() {
        this.accessHit = 0;
    }

    @Override
    public boolean isExpiredAt(long now) {
        return this.expirationTime > -1L && this.expirationTime <= now;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.creationTime);
        out.writeLong(this.expirationTime);
        out.writeLong(this.accessTime);
        out.writeInt(this.accessHit);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.creationTime = in.readLong();
        this.expirationTime = in.readLong();
        this.accessTime = in.readLong();
        this.accessHit = in.readInt();
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }
}

