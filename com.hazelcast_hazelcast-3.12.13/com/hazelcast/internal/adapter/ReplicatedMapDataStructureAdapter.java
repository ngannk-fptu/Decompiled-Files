/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.expiry.ExpiryPolicy
 *  javax.cache.integration.CompletionListener
 *  javax.cache.processor.EntryProcessor
 *  javax.cache.processor.EntryProcessorException
 *  javax.cache.processor.EntryProcessorResult
 */
package com.hazelcast.internal.adapter;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.internal.adapter.DataStructureAdapter;
import com.hazelcast.internal.adapter.MethodNotAvailable;
import com.hazelcast.internal.adapter.MethodNotAvailableException;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.query.Predicate;
import com.hazelcast.util.MapUtil;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;

public class ReplicatedMapDataStructureAdapter<K, V>
implements DataStructureAdapter<K, V> {
    private final ReplicatedMap<K, V> map;

    public ReplicatedMapDataStructureAdapter(ReplicatedMap<K, V> map) {
        this.map = map;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public V get(K key) {
        return this.map.get(key);
    }

    @Override
    @MethodNotAvailable
    public ICompletableFuture<V> getAsync(K key) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public void set(K key, V value) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public ICompletableFuture<Void> setAsync(K key, V value) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public ICompletableFuture<Void> setAsync(K key, V value, long ttl, TimeUnit timeunit) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public ICompletableFuture<Void> setAsync(K key, V value, ExpiryPolicy expiryPolicy) {
        throw new MethodNotAvailableException();
    }

    @Override
    public V put(K key, V value) {
        return this.map.put(key, value);
    }

    @Override
    @MethodNotAvailable
    public ICompletableFuture<V> putAsync(K key, V value) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public ICompletableFuture<V> putAsync(K key, V value, long ttl, TimeUnit timeunit) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public ICompletableFuture<V> putAsync(K key, V value, ExpiryPolicy expiryPolicy) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public void putTransient(K key, V value, long ttl, TimeUnit timeunit) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public boolean putIfAbsent(K key, V value) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public ICompletableFuture<Boolean> putIfAbsentAsync(K key, V value) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public void setTtl(K key, long duration, TimeUnit timeUnit) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public V replace(K key, V newValue) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public boolean replace(K key, V oldValue, V newValue) {
        throw new MethodNotAvailableException();
    }

    @Override
    public V remove(K key) {
        return this.map.remove(key);
    }

    @Override
    @MethodNotAvailable
    public boolean remove(K key, V oldValue) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public ICompletableFuture<V> removeAsync(K key) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public void delete(K key) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public ICompletableFuture<Boolean> deleteAsync(K key) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public boolean evict(K key) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public <T> T invoke(K key, EntryProcessor<K, V, T> entryProcessor, Object ... arguments) throws EntryProcessorException {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public Object executeOnKey(K key, com.hazelcast.map.EntryProcessor entryProcessor) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public Map<K, Object> executeOnKeys(Set<K> keys, com.hazelcast.map.EntryProcessor entryProcessor) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public Map<K, Object> executeOnEntries(com.hazelcast.map.EntryProcessor entryProcessor) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public Map<K, Object> executeOnEntries(com.hazelcast.map.EntryProcessor entryProcessor, Predicate predicate) {
        throw new MethodNotAvailableException();
    }

    @Override
    public boolean containsKey(K key) {
        return this.map.containsKey(key);
    }

    @Override
    @MethodNotAvailable
    public void loadAll(boolean replaceExistingValues) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public void loadAll(Set<K> keys, boolean replaceExistingValues) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public void loadAll(Set<? extends K> keys, boolean replaceExistingValues, CompletionListener completionListener) {
        throw new MethodNotAvailableException();
    }

    @Override
    public Map<K, V> getAll(Set<K> keys) {
        Map result = MapUtil.createHashMap(keys.size());
        for (K key : keys) {
            result.put(key, this.map.get(key));
        }
        return result;
    }

    @Override
    public void putAll(Map<K, V> map) {
        this.map.putAll(map);
    }

    @Override
    @MethodNotAvailable
    public void removeAll() {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public void removeAll(Set<K> keys) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public void evictAll() {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> keys, EntryProcessor<K, V, T> entryProcessor, Object ... arguments) {
        throw new MethodNotAvailableException();
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    @MethodNotAvailable
    public void close() {
        throw new MethodNotAvailableException();
    }

    @Override
    public void destroy() {
        this.map.destroy();
    }

    @Override
    @MethodNotAvailable
    public void setExpiryPolicy(Set<K> keys, ExpiryPolicy expiryPolicy) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public boolean setExpiryPolicy(K key, ExpiryPolicy expiryPolicy) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public LocalMapStats getLocalMapStats() {
        throw new MethodNotAvailableException();
    }
}

