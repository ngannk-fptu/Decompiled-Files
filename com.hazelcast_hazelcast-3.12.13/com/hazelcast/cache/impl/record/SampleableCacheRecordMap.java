/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.record;

import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.cache.impl.record.CacheRecordMap;
import com.hazelcast.internal.eviction.impl.strategy.sampling.SampleableEvictableStore;
import com.hazelcast.nio.serialization.Data;

public interface SampleableCacheRecordMap<K extends Data, V extends CacheRecord>
extends CacheRecordMap<K, V>,
SampleableEvictableStore<K, V> {
}

