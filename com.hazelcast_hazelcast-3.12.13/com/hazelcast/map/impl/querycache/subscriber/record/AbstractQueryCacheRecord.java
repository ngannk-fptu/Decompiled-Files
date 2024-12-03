/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber.record;

import com.hazelcast.map.impl.querycache.subscriber.record.QueryCacheRecord;
import com.hazelcast.util.Clock;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

abstract class AbstractQueryCacheRecord
implements QueryCacheRecord {
    private static final AtomicIntegerFieldUpdater<AbstractQueryCacheRecord> ACCESS_HIT = AtomicIntegerFieldUpdater.newUpdater(AbstractQueryCacheRecord.class, "accessHit");
    private final long creationTime = Clock.currentTimeMillis();
    private volatile int accessHit;
    private volatile long accessTime = -1L;

    AbstractQueryCacheRecord() {
    }

    @Override
    public int getAccessHit() {
        return this.accessHit;
    }

    @Override
    public long getLastAccessTime() {
        return this.accessTime;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public void incrementAccessHit() {
        ACCESS_HIT.incrementAndGet(this);
    }

    @Override
    public void setAccessTime(long accessTime) {
        this.accessTime = accessTime;
    }
}

