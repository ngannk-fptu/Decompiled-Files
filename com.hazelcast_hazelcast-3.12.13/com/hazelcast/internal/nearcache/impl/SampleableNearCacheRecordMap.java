/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl;

import com.hazelcast.internal.eviction.impl.strategy.sampling.SampleableEvictableStore;
import com.hazelcast.internal.nearcache.NearCacheRecord;
import java.util.concurrent.ConcurrentMap;

public interface SampleableNearCacheRecordMap<K, V extends NearCacheRecord>
extends SampleableEvictableStore<K, V>,
ConcurrentMap<K, V> {
}

