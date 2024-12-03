/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.BaseMultiMap;
import com.hazelcast.core.EntryListener;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.aggregation.Aggregation;
import com.hazelcast.mapreduce.aggregation.Supplier;
import com.hazelcast.monitor.LocalMultiMapStats;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface MultiMap<K, V>
extends BaseMultiMap<K, V> {
    @Override
    public boolean put(K var1, V var2);

    @Override
    public Collection<V> get(K var1);

    @Override
    public boolean remove(Object var1, Object var2);

    @Override
    public Collection<V> remove(Object var1);

    public void delete(Object var1);

    public Set<K> localKeySet();

    public Set<K> keySet();

    public Collection<V> values();

    public Set<Map.Entry<K, V>> entrySet();

    public boolean containsKey(K var1);

    public boolean containsValue(Object var1);

    public boolean containsEntry(K var1, V var2);

    @Override
    public int size();

    public void clear();

    @Override
    public int valueCount(K var1);

    public String addLocalEntryListener(EntryListener<K, V> var1);

    public String addEntryListener(EntryListener<K, V> var1, boolean var2);

    public boolean removeEntryListener(String var1);

    public String addEntryListener(EntryListener<K, V> var1, K var2, boolean var3);

    public void lock(K var1);

    public void lock(K var1, long var2, TimeUnit var4);

    public boolean isLocked(K var1);

    public boolean tryLock(K var1);

    public boolean tryLock(K var1, long var2, TimeUnit var4) throws InterruptedException;

    public boolean tryLock(K var1, long var2, TimeUnit var4, long var5, TimeUnit var7) throws InterruptedException;

    public void unlock(K var1);

    public void forceUnlock(K var1);

    public LocalMultiMapStats getLocalMultiMapStats();

    @Deprecated
    public <SuppliedValue, Result> Result aggregate(Supplier<K, V, SuppliedValue> var1, Aggregation<K, SuppliedValue, Result> var2);

    @Deprecated
    public <SuppliedValue, Result> Result aggregate(Supplier<K, V, SuppliedValue> var1, Aggregation<K, SuppliedValue, Result> var2, JobTracker var3);
}

