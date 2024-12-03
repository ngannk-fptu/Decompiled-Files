/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.EntryView;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.MapInterceptor;
import com.hazelcast.map.QueryCache;
import com.hazelcast.map.impl.LegacyAsyncMap;
import com.hazelcast.map.listener.MapListener;
import com.hazelcast.map.listener.MapPartitionLostListener;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.aggregation.Aggregation;
import com.hazelcast.mapreduce.aggregation.Supplier;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.projection.Projection;
import com.hazelcast.query.Predicate;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public interface IMap<K, V>
extends ConcurrentMap<K, V>,
LegacyAsyncMap<K, V> {
    @Override
    public void putAll(Map<? extends K, ? extends V> var1);

    @Override
    public boolean containsKey(Object var1);

    @Override
    public boolean containsValue(Object var1);

    @Override
    public V get(Object var1);

    @Override
    public V put(K var1, V var2);

    @Override
    public V remove(Object var1);

    @Override
    public boolean remove(Object var1, Object var2);

    public void removeAll(Predicate<K, V> var1);

    @Override
    public void delete(Object var1);

    public void flush();

    public Map<K, V> getAll(Set<K> var1);

    public void loadAll(boolean var1);

    public void loadAll(Set<K> var1, boolean var2);

    @Override
    public void clear();

    @Override
    public ICompletableFuture<V> getAsync(K var1);

    @Override
    public ICompletableFuture<V> putAsync(K var1, V var2);

    @Override
    public ICompletableFuture<V> putAsync(K var1, V var2, long var3, TimeUnit var5);

    public ICompletableFuture<V> putAsync(K var1, V var2, long var3, TimeUnit var5, long var6, TimeUnit var8);

    public ICompletableFuture<Void> setAsync(K var1, V var2);

    public ICompletableFuture<Void> setAsync(K var1, V var2, long var3, TimeUnit var5);

    public ICompletableFuture<Void> setAsync(K var1, V var2, long var3, TimeUnit var5, long var6, TimeUnit var8);

    @Override
    public ICompletableFuture<V> removeAsync(K var1);

    public boolean tryRemove(K var1, long var2, TimeUnit var4);

    public boolean tryPut(K var1, V var2, long var3, TimeUnit var5);

    @Override
    public V put(K var1, V var2, long var3, TimeUnit var5);

    public V put(K var1, V var2, long var3, TimeUnit var5, long var6, TimeUnit var8);

    public void putTransient(K var1, V var2, long var3, TimeUnit var5);

    public void putTransient(K var1, V var2, long var3, TimeUnit var5, long var6, TimeUnit var8);

    @Override
    public V putIfAbsent(K var1, V var2);

    public V putIfAbsent(K var1, V var2, long var3, TimeUnit var5);

    public V putIfAbsent(K var1, V var2, long var3, TimeUnit var5, long var6, TimeUnit var8);

    @Override
    public boolean replace(K var1, V var2, V var3);

    @Override
    public V replace(K var1, V var2);

    @Override
    public void set(K var1, V var2);

    public void set(K var1, V var2, long var3, TimeUnit var5);

    public void set(K var1, V var2, long var3, TimeUnit var5, long var6, TimeUnit var8);

    public void lock(K var1);

    public void lock(K var1, long var2, TimeUnit var4);

    public boolean isLocked(K var1);

    public boolean tryLock(K var1);

    public boolean tryLock(K var1, long var2, TimeUnit var4) throws InterruptedException;

    public boolean tryLock(K var1, long var2, TimeUnit var4, long var5, TimeUnit var7) throws InterruptedException;

    public void unlock(K var1);

    public void forceUnlock(K var1);

    public String addLocalEntryListener(MapListener var1);

    public String addLocalEntryListener(EntryListener var1);

    public String addLocalEntryListener(MapListener var1, Predicate<K, V> var2, boolean var3);

    public String addLocalEntryListener(EntryListener var1, Predicate<K, V> var2, boolean var3);

    public String addLocalEntryListener(MapListener var1, Predicate<K, V> var2, K var3, boolean var4);

    public String addLocalEntryListener(EntryListener var1, Predicate<K, V> var2, K var3, boolean var4);

    public String addInterceptor(MapInterceptor var1);

    public void removeInterceptor(String var1);

    public String addEntryListener(MapListener var1, boolean var2);

    public String addEntryListener(EntryListener var1, boolean var2);

    public boolean removeEntryListener(String var1);

    public String addPartitionLostListener(MapPartitionLostListener var1);

    public boolean removePartitionLostListener(String var1);

    public String addEntryListener(MapListener var1, K var2, boolean var3);

    public String addEntryListener(EntryListener var1, K var2, boolean var3);

    public String addEntryListener(MapListener var1, Predicate<K, V> var2, boolean var3);

    public String addEntryListener(EntryListener var1, Predicate<K, V> var2, boolean var3);

    public String addEntryListener(MapListener var1, Predicate<K, V> var2, K var3, boolean var4);

    public String addEntryListener(EntryListener var1, Predicate<K, V> var2, K var3, boolean var4);

    public EntryView<K, V> getEntryView(K var1);

    public boolean evict(K var1);

    public void evictAll();

    @Override
    public Set<K> keySet();

    @Override
    public Collection<V> values();

    @Override
    public Set<Map.Entry<K, V>> entrySet();

    @Override
    public Set<K> keySet(Predicate var1);

    public Set<Map.Entry<K, V>> entrySet(Predicate var1);

    @Override
    public Collection<V> values(Predicate var1);

    public Set<K> localKeySet();

    public Set<K> localKeySet(Predicate var1);

    public void addIndex(String var1, boolean var2);

    public LocalMapStats getLocalMapStats();

    public Object executeOnKey(K var1, EntryProcessor var2);

    public Map<K, Object> executeOnKeys(Set<K> var1, EntryProcessor var2);

    public void submitToKey(K var1, EntryProcessor var2, ExecutionCallback var3);

    @Override
    public ICompletableFuture submitToKey(K var1, EntryProcessor var2);

    public Map<K, Object> executeOnEntries(EntryProcessor var1);

    public Map<K, Object> executeOnEntries(EntryProcessor var1, Predicate var2);

    public <R> R aggregate(Aggregator<Map.Entry<K, V>, R> var1);

    public <R> R aggregate(Aggregator<Map.Entry<K, V>, R> var1, Predicate<K, V> var2);

    public <R> Collection<R> project(Projection<Map.Entry<K, V>, R> var1);

    public <R> Collection<R> project(Projection<Map.Entry<K, V>, R> var1, Predicate<K, V> var2);

    @Deprecated
    public <SuppliedValue, Result> Result aggregate(Supplier<K, V, SuppliedValue> var1, Aggregation<K, SuppliedValue, Result> var2);

    @Deprecated
    public <SuppliedValue, Result> Result aggregate(Supplier<K, V, SuppliedValue> var1, Aggregation<K, SuppliedValue, Result> var2, JobTracker var3);

    public QueryCache<K, V> getQueryCache(String var1);

    public QueryCache<K, V> getQueryCache(String var1, Predicate<K, V> var2, boolean var3);

    public QueryCache<K, V> getQueryCache(String var1, MapListener var2, Predicate<K, V> var3, boolean var4);

    public boolean setTtl(K var1, long var2, TimeUnit var4);
}

