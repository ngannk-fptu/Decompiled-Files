/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.store;

import com.hazelcast.internal.eviction.EvictionCandidate;
import com.hazelcast.internal.eviction.EvictionListener;
import com.hazelcast.internal.nearcache.NearCacheRecord;
import com.hazelcast.internal.nearcache.impl.SampleableNearCacheRecordMap;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.SampleableConcurrentHashMap;

@SerializableByConvention
public class HeapNearCacheRecordMap<K, V extends NearCacheRecord>
extends SampleableConcurrentHashMap<K, V>
implements SampleableNearCacheRecordMap<K, V> {
    private final SerializationService serializationService;

    HeapNearCacheRecordMap(SerializationService serializationService, int initialCapacity) {
        super(initialCapacity);
        this.serializationService = serializationService;
    }

    @Override
    protected <E extends SampleableConcurrentHashMap.SamplingEntry> E createSamplingEntry(K key, V value) {
        return (E)new NearCacheEvictableSamplingEntry(this, key, value);
    }

    @Override
    public <C extends EvictionCandidate<K, V>> boolean tryEvict(C evictionCandidate, EvictionListener<K, V> evictionListener) {
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
    public Iterable<NearCacheEvictableSamplingEntry> sample(int sampleCount) {
        return super.getRandomSamples(sampleCount);
    }

    public static class NearCacheEvictableSamplingEntry
    extends SampleableConcurrentHashMap.SamplingEntry<K, V>
    implements EvictionCandidate<K, V> {
        final /* synthetic */ HeapNearCacheRecordMap this$0;

        NearCacheEvictableSamplingEntry(K key, V value) {
            this.this$0 = this$0;
            super(key, value);
        }

        @Override
        public K getAccessor() {
            return this.key;
        }

        @Override
        public V getEvictable() {
            return (NearCacheRecord)this.value;
        }

        public Object getKey() {
            return this.this$0.serializationService.toObject(this.key);
        }

        public Object getValue() {
            return this.this$0.serializationService.toObject(((NearCacheRecord)this.value).getValue());
        }

        @Override
        public long getCreationTime() {
            return ((NearCacheRecord)this.value).getCreationTime();
        }

        @Override
        public long getLastAccessTime() {
            return ((NearCacheRecord)this.value).getLastAccessTime();
        }

        @Override
        public long getAccessHit() {
            return ((NearCacheRecord)this.value).getAccessHit();
        }
    }
}

