/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.record;

import com.hazelcast.cache.impl.CacheEntryIterationResult;
import com.hazelcast.cache.impl.CacheKeyIterationResult;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.internal.eviction.EvictableStore;
import com.hazelcast.nio.serialization.Data;
import java.util.Map;

public interface CacheRecordMap<K extends Data, V extends CacheRecord>
extends Map<K, V>,
EvictableStore<K, V> {
    public void setEntryCounting(boolean var1);

    public CacheKeyIterationResult fetchKeys(int var1, int var2);

    public CacheEntryIterationResult fetchEntries(int var1, int var2);
}

