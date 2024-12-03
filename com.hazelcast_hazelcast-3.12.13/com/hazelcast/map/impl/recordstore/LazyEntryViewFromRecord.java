/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.recordstore;

import com.hazelcast.core.EntryView;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.SampleableConcurrentHashMap;

public class LazyEntryViewFromRecord<R extends Record>
extends SampleableConcurrentHashMap.SamplingEntry
implements EntryView {
    private Object key;
    private Object value;
    private R record;
    private SerializationService serializationService;

    public LazyEntryViewFromRecord(R record, SerializationService serializationService) {
        super(record.getKey(), record);
        this.record = record;
        this.serializationService = serializationService;
    }

    public Object getKey() {
        if (this.key == null) {
            this.key = this.serializationService.toObject(this.record.getKey());
        }
        return this.key;
    }

    public Object getValue() {
        if (this.value == null) {
            this.value = this.serializationService.toObject(this.record.getValue());
        }
        return this.value;
    }

    @Override
    public long getCost() {
        return this.record.getCost();
    }

    @Override
    public long getCreationTime() {
        return this.record.getCreationTime();
    }

    @Override
    public long getExpirationTime() {
        return this.record.getExpirationTime();
    }

    @Override
    public long getHits() {
        return this.record.getHits();
    }

    @Override
    public long getLastAccessTime() {
        return this.record.getLastAccessTime();
    }

    @Override
    public long getLastStoredTime() {
        return this.record.getLastStoredTime();
    }

    @Override
    public long getLastUpdateTime() {
        return this.record.getLastUpdateTime();
    }

    @Override
    public long getVersion() {
        return this.record.getVersion();
    }

    @Override
    public long getTtl() {
        return this.record.getTtl();
    }

    @Override
    public Long getMaxIdle() {
        return this.record.getMaxIdle();
    }

    public Record getRecord() {
        return this.record;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EntryView)) {
            return false;
        }
        EntryView that = (EntryView)o;
        return this.getKey().equals(that.getKey()) && this.getValue().equals(that.getValue()) && this.getVersion() == that.getVersion() && this.getCost() == that.getCost() && this.getCreationTime() == that.getCreationTime() && this.getExpirationTime() == that.getExpirationTime() && this.getHits() == that.getHits() && this.getLastAccessTime() == that.getLastAccessTime() && this.getLastStoredTime() == that.getLastStoredTime() && this.getLastUpdateTime() == that.getLastUpdateTime() && this.getTtl() == that.getTtl();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.getKey().hashCode();
        result = 31 * result + this.getValue().hashCode();
        long cost = this.getCost();
        long creationTime = this.getCreationTime();
        long expirationTime = this.getExpirationTime();
        long hits = this.getHits();
        long lastAccessTime = this.getLastAccessTime();
        long lastStoredTime = this.getLastStoredTime();
        long lastUpdateTime = this.getLastUpdateTime();
        long version = this.getVersion();
        long ttl = this.getTtl();
        result = 31 * result + (int)(cost ^ cost >>> 32);
        result = 31 * result + (int)(creationTime ^ creationTime >>> 32);
        result = 31 * result + (int)(expirationTime ^ expirationTime >>> 32);
        result = 31 * result + (int)(hits ^ hits >>> 32);
        result = 31 * result + (int)(lastAccessTime ^ lastAccessTime >>> 32);
        result = 31 * result + (int)(lastStoredTime ^ lastStoredTime >>> 32);
        result = 31 * result + (int)(lastUpdateTime ^ lastUpdateTime >>> 32);
        result = 31 * result + (int)(version ^ version >>> 32);
        result = 31 * result + (int)(ttl ^ ttl >>> 32);
        return result;
    }

    @Override
    public String toString() {
        return "EntryView{key=" + this.getKey() + ", value=" + this.getValue() + ", cost=" + this.getCost() + ", version=" + this.getVersion() + ", creationTime=" + this.getCreationTime() + ", expirationTime=" + this.getExpirationTime() + ", hits=" + this.getHits() + ", lastAccessTime=" + this.getLastAccessTime() + ", lastStoredTime=" + this.getLastStoredTime() + ", lastUpdateTime=" + this.getLastUpdateTime() + ", ttl=" + this.getTtl() + '}';
    }
}

