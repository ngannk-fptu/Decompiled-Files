/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl;

import com.hazelcast.internal.eviction.EvictableStore;
import com.hazelcast.internal.nearcache.NearCacheRecord;
import java.util.concurrent.ConcurrentMap;

public interface NearCacheRecordMap<K, V extends NearCacheRecord>
extends ConcurrentMap<K, V>,
EvictableStore<K, V> {
}

