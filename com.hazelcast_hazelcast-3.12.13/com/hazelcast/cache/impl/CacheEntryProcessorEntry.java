/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.configuration.Factory
 *  javax.cache.expiry.ExpiryPolicy
 *  javax.cache.processor.MutableEntry
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.impl.AbstractCacheRecordStore;
import com.hazelcast.cache.impl.CacheEventContextUtil;
import com.hazelcast.cache.impl.CacheStatisticsImpl;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.impl.operationexecutor.impl.PartitionOperationThread;
import com.hazelcast.util.Preconditions;
import javax.cache.configuration.Factory;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.processor.MutableEntry;

public class CacheEntryProcessorEntry<K, V, R extends CacheRecord>
implements MutableEntry<K, V> {
    protected K key;
    protected V value;
    protected State state = State.NONE;
    protected final Data keyData;
    protected R record;
    protected R recordLoaded;
    protected V valueLoaded;
    protected final AbstractCacheRecordStore cacheRecordStore;
    protected final long now;
    protected final long start;
    protected final ExpiryPolicy expiryPolicy;
    protected final int completionId;

    public CacheEntryProcessorEntry(Data keyData, R record, AbstractCacheRecordStore cacheRecordStore, long now, int completionId) {
        this.keyData = keyData;
        this.record = record;
        this.cacheRecordStore = cacheRecordStore;
        this.now = now;
        this.completionId = completionId;
        this.start = cacheRecordStore.cacheConfig.isStatisticsEnabled() ? System.nanoTime() : 0L;
        Factory<ExpiryPolicy> expiryPolicyFactory = cacheRecordStore.cacheConfig.getExpiryPolicyFactory();
        this.expiryPolicy = (ExpiryPolicy)expiryPolicyFactory.create();
    }

    public boolean exists() {
        return this.record != null && this.state == State.NONE || this.value != null;
    }

    public void remove() {
        this.value = null;
        this.state = this.state == State.CREATE || this.state == State.LOAD ? State.NONE : State.REMOVE;
    }

    public void setValue(V value) {
        Preconditions.checkNotNull(value, "Null value not allowed");
        this.state = this.record == null ? State.CREATE : State.UPDATE;
        this.value = value;
    }

    public K getKey() {
        if (this.key == null) {
            this.key = this.cacheRecordStore.cacheService.toObject(this.keyData);
        }
        return this.key;
    }

    public V getValue() {
        if (this.state == State.REMOVE) {
            return null;
        }
        if (this.value != null) {
            return this.value;
        }
        if (this.record != null) {
            this.state = State.ACCESS;
            this.value = this.getRecordValue(this.record);
            return this.value;
        }
        if (this.recordLoaded == null) {
            this.recordLoaded = this.cacheRecordStore.readThroughRecord(this.keyData, this.now);
        }
        if (this.recordLoaded != null) {
            this.state = State.LOAD;
            this.valueLoaded = this.getRecordValue(this.recordLoaded);
            return this.valueLoaded;
        }
        return null;
    }

    protected V getRecordValue(R record) {
        Object objValue;
        switch (this.cacheRecordStore.cacheConfig.getInMemoryFormat()) {
            case BINARY: {
                objValue = this.cacheRecordStore.cacheService.toObject(record.getValue());
                break;
            }
            case OBJECT: {
                objValue = record.getValue();
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid storage format: " + (Object)((Object)this.cacheRecordStore.cacheConfig.getInMemoryFormat()));
            }
        }
        return objValue;
    }

    public R getRecord() {
        assert (Thread.currentThread() instanceof PartitionOperationThread);
        return this.record;
    }

    public void applyChanges() {
        boolean isStatisticsEnabled = this.cacheRecordStore.cacheConfig.isStatisticsEnabled();
        CacheStatisticsImpl statistics = this.cacheRecordStore.statistics;
        switch (this.state) {
            case ACCESS: {
                this.cacheRecordStore.accessRecord(this.keyData, this.record, this.expiryPolicy, this.now);
                break;
            }
            case CREATE: {
                if (isStatisticsEnabled) {
                    statistics.increaseCachePuts(1L);
                    statistics.addGetTimeNanos(System.nanoTime() - this.start);
                }
                boolean saved = this.cacheRecordStore.createRecordWithExpiry(this.keyData, this.value, this.expiryPolicy, this.now, false, this.completionId) != null;
                this.onCreate(this.keyData, this.value, this.expiryPolicy, this.now, false, this.completionId, saved);
                break;
            }
            case LOAD: {
                boolean saved = this.cacheRecordStore.createRecordWithExpiry(this.keyData, this.valueLoaded, this.expiryPolicy, this.now, true, this.completionId) != null;
                this.onLoad(this.keyData, this.valueLoaded, this.expiryPolicy, this.now, true, this.completionId, saved);
                break;
            }
            case UPDATE: {
                boolean saved = this.cacheRecordStore.updateRecordWithExpiry(this.keyData, this.value, this.record, this.expiryPolicy, this.now, false, this.completionId);
                this.onUpdate(this.keyData, this.value, this.record, this.expiryPolicy, this.now, false, this.completionId, saved);
                if (!isStatisticsEnabled) break;
                statistics.increaseCachePuts(1L);
                statistics.addGetTimeNanos(System.nanoTime() - this.start);
                break;
            }
            case REMOVE: {
                boolean removed = this.cacheRecordStore.remove(this.keyData, null, null, this.completionId);
                this.onRemove(this.keyData, null, this.completionId, removed);
                break;
            }
            case NONE: {
                this.cacheRecordStore.publishEvent(CacheEventContextUtil.createCacheCompleteEvent(this.cacheRecordStore.toEventData(this.keyData), this.completionId));
                break;
            }
        }
    }

    protected void onCreate(Data key, Object value, ExpiryPolicy expiryPolicy, long now, boolean disableWriteThrough, int completionId, boolean saved) {
    }

    protected void onLoad(Data key, Object value, ExpiryPolicy expiryPolicy, long now, boolean disableWriteThrough, int completionId, boolean saved) {
    }

    protected void onUpdate(Data key, Object value, R record, ExpiryPolicy expiryPolicy, long now, boolean disableWriteThrough, int completionId, boolean saved) {
    }

    protected void onRemove(Data key, String source, int completionId, boolean removed) {
    }

    public <T> T unwrap(Class<T> clazz) {
        if (clazz.isAssignableFrom(this.getClass())) {
            return clazz.cast(this);
        }
        throw new IllegalArgumentException("Unwrapping to " + clazz + " is not supported by this implementation");
    }

    protected static enum State {
        NONE,
        ACCESS,
        UPDATE,
        LOAD,
        CREATE,
        REMOVE;

    }
}

