/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.expiry.ExpiryPolicy
 */
package com.hazelcast.cache.impl.record;

import com.hazelcast.cache.CacheEntryView;
import com.hazelcast.cache.impl.CacheContext;
import com.hazelcast.cache.impl.CacheEntryIterationResult;
import com.hazelcast.cache.impl.CacheKeyIterationResult;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.cache.impl.record.SampleableCacheRecordMap;
import com.hazelcast.internal.eviction.Evictable;
import com.hazelcast.internal.eviction.EvictionCandidate;
import com.hazelcast.internal.eviction.EvictionListener;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.SampleableConcurrentHashMap;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import javax.cache.expiry.ExpiryPolicy;

@SerializableByConvention
public class CacheRecordHashMap
extends SampleableConcurrentHashMap<Data, CacheRecord>
implements SampleableCacheRecordMap<Data, CacheRecord> {
    private static final long serialVersionUID = 1L;
    private final transient SerializationService serializationService;
    private final transient CacheContext cacheContext;
    private boolean entryCountingEnable;

    public CacheRecordHashMap(SerializationService serializationService, int initialCapacity, CacheContext cacheContext) {
        super(initialCapacity);
        this.serializationService = serializationService;
        this.cacheContext = cacheContext;
    }

    @Override
    public void setEntryCounting(boolean enable) {
        if (enable) {
            if (!this.entryCountingEnable) {
                this.cacheContext.increaseEntryCount(this.size());
            }
        } else if (this.entryCountingEnable) {
            this.cacheContext.decreaseEntryCount(this.size());
        }
        this.entryCountingEnable = enable;
    }

    @Override
    public CacheRecord put(Data key, CacheRecord value) {
        CacheRecord oldRecord = super.put(key, value);
        if (oldRecord == null && this.entryCountingEnable) {
            this.cacheContext.increaseEntryCount();
        }
        return oldRecord;
    }

    @Override
    public CacheRecord putIfAbsent(Data key, CacheRecord value) {
        CacheRecord oldRecord = super.putIfAbsent(key, value);
        if (oldRecord == null && this.entryCountingEnable) {
            this.cacheContext.increaseEntryCount();
        }
        return oldRecord;
    }

    @Override
    public CacheRecord remove(Object key) {
        CacheRecord removedRecord = (CacheRecord)super.remove(key);
        if (removedRecord != null && this.entryCountingEnable) {
            this.cacheContext.decreaseEntryCount();
        }
        return removedRecord;
    }

    @Override
    public boolean remove(Object key, Object value) {
        boolean removed = super.remove(key, value);
        if (removed && this.entryCountingEnable) {
            this.cacheContext.decreaseEntryCount();
        }
        return removed;
    }

    @Override
    public void clear() {
        int sizeBeforeClear = this.size();
        super.clear();
        if (this.entryCountingEnable) {
            this.cacheContext.decreaseEntryCount(sizeBeforeClear);
        }
    }

    @Override
    protected CacheEvictableSamplingEntry createSamplingEntry(Data key, CacheRecord value) {
        return new CacheEvictableSamplingEntry(key, value);
    }

    @Override
    public CacheKeyIterationResult fetchKeys(int nextTableIndex, int size) {
        ArrayList<Data> keys = new ArrayList<Data>(size);
        int tableIndex = this.fetchKeys(nextTableIndex, size, keys);
        return new CacheKeyIterationResult(keys, tableIndex);
    }

    @Override
    public CacheEntryIterationResult fetchEntries(int nextTableIndex, int size) {
        ArrayList entries = new ArrayList(size);
        int newTableIndex = this.fetchEntries(nextTableIndex, size, entries);
        ArrayList<Map.Entry<Data, Data>> entriesData = new ArrayList<Map.Entry<Data, Data>>(entries.size());
        for (Map.Entry entry : entries) {
            CacheRecord record = (CacheRecord)entry.getValue();
            Object dataValue = this.serializationService.toData(record.getValue());
            entriesData.add(new AbstractMap.SimpleEntry(entry.getKey(), dataValue));
        }
        return new CacheEntryIterationResult(entriesData, newTableIndex);
    }

    @Override
    public <C extends EvictionCandidate<Data, CacheRecord>> boolean tryEvict(C evictionCandidate, EvictionListener<Data, CacheRecord> evictionListener) {
        if (evictionCandidate == null) {
            return false;
        }
        if (this.remove(evictionCandidate.getAccessor()) == null) {
            return false;
        }
        if (evictionListener != null) {
            evictionListener.onEvict(evictionCandidate.getAccessor(), evictionCandidate.getEvictable(), false);
        }
        return true;
    }

    @Override
    public Iterable<CacheEvictableSamplingEntry> sample(int sampleCount) {
        return super.getRandomSamples(sampleCount);
    }

    private class CacheEvictableSamplingEntry
    extends SampleableConcurrentHashMap.SamplingEntry<Data, CacheRecord>
    implements EvictionCandidate,
    CacheEntryView {
        public CacheEvictableSamplingEntry(Data key, CacheRecord value) {
            super(key, value);
        }

        public Object getAccessor() {
            return this.key;
        }

        public Evictable getEvictable() {
            return (Evictable)this.value;
        }

        @Override
        public Object getKey() {
            return CacheRecordHashMap.this.serializationService.toObject(this.key);
        }

        @Override
        public Object getValue() {
            return CacheRecordHashMap.this.serializationService.toObject(((CacheRecord)this.value).getValue());
        }

        public ExpiryPolicy getExpiryPolicy() {
            return (ExpiryPolicy)CacheRecordHashMap.this.serializationService.toObject(((CacheRecord)this.value).getExpiryPolicy());
        }

        @Override
        public long getCreationTime() {
            return ((CacheRecord)this.value).getCreationTime();
        }

        @Override
        public long getExpirationTime() {
            return ((CacheRecord)this.value).getExpirationTime();
        }

        @Override
        public long getLastAccessTime() {
            return ((CacheRecord)this.value).getLastAccessTime();
        }

        @Override
        public long getAccessHit() {
            return ((CacheRecord)this.value).getAccessHit();
        }
    }
}

