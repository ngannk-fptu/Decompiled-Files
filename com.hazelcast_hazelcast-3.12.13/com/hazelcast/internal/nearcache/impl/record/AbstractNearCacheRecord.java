/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.record;

import com.hazelcast.internal.nearcache.NearCacheRecord;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public abstract class AbstractNearCacheRecord<V>
implements NearCacheRecord<V> {
    public static final int NUMBER_OF_LONG_FIELD_TYPES = 5;
    public static final int NUMBER_OF_INTEGER_FIELD_TYPES = 1;
    private static final AtomicIntegerFieldUpdater<AbstractNearCacheRecord> ACCESS_HIT = AtomicIntegerFieldUpdater.newUpdater(AbstractNearCacheRecord.class, "accessHit");
    private static final AtomicLongFieldUpdater<AbstractNearCacheRecord> RECORD_STATE = AtomicLongFieldUpdater.newUpdater(AbstractNearCacheRecord.class, "recordState");
    protected long creationTime = -1L;
    protected volatile int partitionId;
    protected volatile long sequence;
    protected volatile UUID uuid;
    protected volatile V value;
    protected volatile long expirationTime = -1L;
    protected volatile long accessTime = -1L;
    protected volatile long recordState = -4L;
    protected volatile int accessHit;

    public AbstractNearCacheRecord(V value, long creationTime, long expirationTime) {
        this.value = value;
        this.creationTime = creationTime;
        this.expirationTime = expirationTime;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public void setValue(V value) {
        this.value = value;
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
        ACCESS_HIT.set(this, accessHit);
    }

    @Override
    public void incrementAccessHit() {
        ACCESS_HIT.addAndGet(this, 1);
    }

    @Override
    public void resetAccessHit() {
        ACCESS_HIT.set(this, 0);
    }

    @Override
    public boolean isExpiredAt(long now) {
        return this.expirationTime > -1L && this.expirationTime <= now;
    }

    @Override
    public boolean isIdleAt(long maxIdleMilliSeconds, long now) {
        if (maxIdleMilliSeconds > 0L) {
            if (this.accessTime > -1L) {
                return this.accessTime + maxIdleMilliSeconds < now;
            }
            return this.creationTime + maxIdleMilliSeconds < now;
        }
        return false;
    }

    @Override
    public long getRecordState() {
        return this.recordState;
    }

    @Override
    public boolean casRecordState(long expect, long update) {
        return RECORD_STATE.compareAndSet(this, expect, update);
    }

    @Override
    public int getPartitionId() {
        return this.partitionId;
    }

    @Override
    public void setPartitionId(int partitionId) {
        this.partitionId = partitionId;
    }

    @Override
    public long getInvalidationSequence() {
        return this.sequence;
    }

    @Override
    public void setInvalidationSequence(long sequence) {
        this.sequence = sequence;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean hasSameUuid(UUID thatUuid) {
        return this.uuid != null && thatUuid != null && this.uuid.equals(thatUuid);
    }

    public String toString() {
        return "creationTime=" + this.creationTime + ", sequence=" + this.sequence + ", uuid=" + this.uuid + ", expirationTime=" + this.expirationTime + ", accessTime=" + this.accessTime + ", accessHit=" + this.accessHit + ", recordState=" + this.recordState + ", value=" + this.value;
    }
}

