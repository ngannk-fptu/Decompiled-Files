/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.Cache
 *  javax.cache.Cache$Entry
 *  javax.cache.expiry.ExpiryPolicy
 */
package com.hazelcast.cache;

import com.hazelcast.cache.CacheStatistics;
import com.hazelcast.cache.impl.event.CachePartitionLostListener;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.PrefixedDistributedObject;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.cache.Cache;
import javax.cache.expiry.ExpiryPolicy;

public interface ICache<K, V>
extends Cache<K, V>,
PrefixedDistributedObject {
    public boolean setExpiryPolicy(K var1, ExpiryPolicy var2);

    public void setExpiryPolicy(Set<? extends K> var1, ExpiryPolicy var2);

    public ICompletableFuture<V> getAsync(K var1);

    public ICompletableFuture<V> getAsync(K var1, ExpiryPolicy var2);

    public ICompletableFuture<Void> putAsync(K var1, V var2);

    public ICompletableFuture<Void> putAsync(K var1, V var2, ExpiryPolicy var3);

    public ICompletableFuture<Boolean> putIfAbsentAsync(K var1, V var2);

    public ICompletableFuture<Boolean> putIfAbsentAsync(K var1, V var2, ExpiryPolicy var3);

    public ICompletableFuture<V> getAndPutAsync(K var1, V var2);

    public ICompletableFuture<V> getAndPutAsync(K var1, V var2, ExpiryPolicy var3);

    public ICompletableFuture<Boolean> removeAsync(K var1);

    public ICompletableFuture<Boolean> removeAsync(K var1, V var2);

    public ICompletableFuture<V> getAndRemoveAsync(K var1);

    public ICompletableFuture<Boolean> replaceAsync(K var1, V var2);

    public ICompletableFuture<Boolean> replaceAsync(K var1, V var2, ExpiryPolicy var3);

    public ICompletableFuture<Boolean> replaceAsync(K var1, V var2, V var3);

    public ICompletableFuture<Boolean> replaceAsync(K var1, V var2, V var3, ExpiryPolicy var4);

    public ICompletableFuture<V> getAndReplaceAsync(K var1, V var2);

    public ICompletableFuture<V> getAndReplaceAsync(K var1, V var2, ExpiryPolicy var3);

    public V get(K var1, ExpiryPolicy var2);

    public Map<K, V> getAll(Set<? extends K> var1, ExpiryPolicy var2);

    public void put(K var1, V var2, ExpiryPolicy var3);

    public V getAndPut(K var1, V var2, ExpiryPolicy var3);

    public void putAll(Map<? extends K, ? extends V> var1, ExpiryPolicy var2);

    public boolean putIfAbsent(K var1, V var2, ExpiryPolicy var3);

    public boolean replace(K var1, V var2, V var3, ExpiryPolicy var4);

    public boolean replace(K var1, V var2, ExpiryPolicy var3);

    public V getAndReplace(K var1, V var2, ExpiryPolicy var3);

    public int size();

    @Override
    public void destroy();

    public boolean isDestroyed();

    public CacheStatistics getLocalCacheStatistics();

    public String addPartitionLostListener(CachePartitionLostListener var1);

    public boolean removePartitionLostListener(String var1);

    public Iterator<Cache.Entry<K, V>> iterator(int var1);
}

