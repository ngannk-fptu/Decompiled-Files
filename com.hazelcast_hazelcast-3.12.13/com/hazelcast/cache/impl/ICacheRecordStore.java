/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.expiry.ExpiryPolicy
 *  javax.cache.processor.EntryProcessor
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.CacheEntryView;
import com.hazelcast.cache.CacheMergePolicy;
import com.hazelcast.cache.impl.CacheEntryIterationResult;
import com.hazelcast.cache.impl.CacheKeyIterationResult;
import com.hazelcast.cache.impl.CacheStatisticsImpl;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.internal.eviction.ExpiredKey;
import com.hazelcast.internal.nearcache.impl.invalidation.InvalidationQueue;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.wan.impl.CallerProvenance;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.processor.EntryProcessor;

public interface ICacheRecordStore {
    public Object get(Data var1, ExpiryPolicy var2);

    public CacheRecord put(Data var1, Object var2, ExpiryPolicy var3, String var4, int var5);

    public Object getAndPut(Data var1, Object var2, ExpiryPolicy var3, String var4, int var5);

    public boolean putIfAbsent(Data var1, Object var2, ExpiryPolicy var3, String var4, int var5);

    public Object getAndRemove(Data var1, String var2, int var3);

    public boolean remove(Data var1, String var2, String var3, int var4, CallerProvenance var5);

    public boolean remove(Data var1, String var2, String var3, int var4);

    public boolean remove(Data var1, Object var2, String var3, String var4, int var5);

    public boolean replace(Data var1, Object var2, ExpiryPolicy var3, String var4, int var5);

    public boolean replace(Data var1, Object var2, Object var3, ExpiryPolicy var4, String var5, int var6);

    public Object getAndReplace(Data var1, Object var2, ExpiryPolicy var3, String var4, int var5);

    public boolean setExpiryPolicy(Collection<Data> var1, Object var2, String var3);

    public Object getExpiryPolicy(Data var1);

    public boolean contains(Data var1);

    public MapEntries getAll(Set<Data> var1, ExpiryPolicy var2);

    public int size();

    public void clear();

    public void reset();

    public void removeAll(Set<Data> var1, int var2);

    public void init();

    public void close(boolean var1);

    public void destroy();

    public void destroyInternals();

    public CacheConfig getConfig();

    public String getName();

    public Map<Data, CacheRecord> getReadOnlyRecords();

    public boolean isExpirable();

    public CacheRecord getRecord(Data var1);

    public void putRecord(Data var1, CacheRecord var2, boolean var3);

    public CacheRecord removeRecord(Data var1);

    public CacheKeyIterationResult fetchKeys(int var1, int var2);

    public CacheEntryIterationResult fetchEntries(int var1, int var2);

    public Object invoke(Data var1, EntryProcessor var2, Object[] var3, int var4);

    public Set<Data> loadAll(Set<Data> var1, boolean var2);

    public CacheStatisticsImpl getCacheStats();

    public boolean evictIfRequired();

    public void sampleAndForceRemoveEntries(int var1);

    public boolean isWanReplicationEnabled();

    public ObjectNamespace getObjectNamespace();

    public CacheRecord merge(SplitBrainMergeTypes.CacheMergeTypes var1, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.CacheMergeTypes> var2, CallerProvenance var3);

    public CacheRecord merge(CacheEntryView<Data, Data> var1, CacheMergePolicy var2, String var3, String var4, int var5, CallerProvenance var6);

    public int getPartitionId();

    public void evictExpiredEntries(int var1);

    public InvalidationQueue<ExpiredKey> getExpiredKeysQueue();

    public void disposeDeferredBlocks();
}

