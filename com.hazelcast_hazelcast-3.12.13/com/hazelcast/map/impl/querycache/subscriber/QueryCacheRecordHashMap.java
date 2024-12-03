/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.internal.eviction.Evictable;
import com.hazelcast.internal.eviction.EvictionCandidate;
import com.hazelcast.internal.eviction.EvictionListener;
import com.hazelcast.internal.eviction.impl.strategy.sampling.SampleableEvictableStore;
import com.hazelcast.map.impl.querycache.subscriber.record.QueryCacheRecord;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.SampleableConcurrentHashMap;

@SerializableByConvention
public class QueryCacheRecordHashMap
extends SampleableConcurrentHashMap<Data, QueryCacheRecord>
implements SampleableEvictableStore<Data, QueryCacheRecord> {
    private final SerializationService serializationService;

    public QueryCacheRecordHashMap(SerializationService serializationService, int initialCapacity) {
        super(initialCapacity);
        this.serializationService = serializationService;
    }

    @Override
    protected QueryCacheEvictableSamplingEntry createSamplingEntry(Data key, QueryCacheRecord value) {
        return new QueryCacheEvictableSamplingEntry(key, value);
    }

    @Override
    public <C extends EvictionCandidate<Data, QueryCacheRecord>> boolean tryEvict(C evictionCandidate, EvictionListener<Data, QueryCacheRecord> evictionListener) {
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
    public Iterable<QueryCacheEvictableSamplingEntry> sample(int sampleCount) {
        return super.getRandomSamples(sampleCount);
    }

    class QueryCacheEvictableSamplingEntry
    extends SampleableConcurrentHashMap.SamplingEntry<Data, QueryCacheRecord>
    implements EvictionCandidate {
        QueryCacheEvictableSamplingEntry(Data key, QueryCacheRecord value) {
            super(key, value);
        }

        public Object getAccessor() {
            return this.key;
        }

        public Evictable getEvictable() {
            return (Evictable)this.value;
        }

        public Object getKey() {
            return QueryCacheRecordHashMap.this.serializationService.toObject(this.key);
        }

        public Object getValue() {
            return QueryCacheRecordHashMap.this.serializationService.toObject(((QueryCacheRecord)this.value).getValue());
        }

        @Override
        public long getCreationTime() {
            return ((QueryCacheRecord)this.value).getCreationTime();
        }

        @Override
        public long getLastAccessTime() {
            return ((QueryCacheRecord)this.value).getLastAccessTime();
        }

        @Override
        public long getAccessHit() {
            return ((QueryCacheRecord)this.value).getAccessHit();
        }
    }
}

