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
import com.hazelcast.core.IMap;
import com.hazelcast.internal.adapter.DataStructureAdapter;
import com.hazelcast.internal.adapter.MethodNotAvailable;
import com.hazelcast.internal.adapter.MethodNotAvailableException;
import com.hazelcast.map.impl.proxy.MapProxyImpl;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.TruePredicate;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;

public class IMapDataStructureAdapter<K, V>
implements DataStructureAdapter<K, V> {
    private final IMap<K, V> map;

    public IMapDataStructureAdapter(IMap<K, V> map) {
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
    public ICompletableFuture<V> getAsync(K key) {
        return this.map.getAsync((Object)key);
    }

    @Override
    public void set(K key, V value) {
        this.map.set(key, value);
    }

    @Override
    public ICompletableFuture<Void> setAsync(K key, V value) {
        return this.map.setAsync((Object)key, (Object)value);
    }

    @Override
    public ICompletableFuture<Void> setAsync(K key, V value, long ttl, TimeUnit timeunit) {
        return this.map.setAsync((Object)key, (Object)value, ttl, timeunit);
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
    public ICompletableFuture<V> putAsync(K key, V value) {
        return this.map.putAsync((Object)key, (Object)value);
    }

    @Override
    public ICompletableFuture<V> putAsync(K key, V value, long ttl, TimeUnit timeunit) {
        return this.map.putAsync((Object)key, (Object)value, ttl, timeunit);
    }

    @Override
    @MethodNotAvailable
    public ICompletableFuture<V> putAsync(K key, V value, ExpiryPolicy expiryPolicy) {
        throw new MethodNotAvailableException();
    }

    @Override
    public void putTransient(K key, V value, long ttl, TimeUnit timeunit) {
        this.map.putTransient(key, value, ttl, timeunit);
    }

    @Override
    public boolean putIfAbsent(K key, V value) {
        return this.map.putIfAbsent(key, value) == null;
    }

    @Override
    @MethodNotAvailable
    public ICompletableFuture<Boolean> putIfAbsentAsync(K key, V value) {
        throw new MethodNotAvailableException();
    }

    @Override
    public void setTtl(K key, long duration, TimeUnit timeUnit) {
        this.map.setTtl(key, duration, timeUnit);
    }

    @Override
    public V replace(K key, V newValue) {
        return this.map.replace(key, newValue);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return this.map.replace(key, oldValue, newValue);
    }

    @Override
    public V remove(K key) {
        return this.map.remove(key);
    }

    @Override
    public boolean remove(K key, V oldValue) {
        return this.map.remove(key, oldValue);
    }

    @Override
    public ICompletableFuture<V> removeAsync(K key) {
        return this.map.removeAsync((Object)key);
    }

    @Override
    public void delete(K key) {
        this.map.delete(key);
    }

    @Override
    @MethodNotAvailable
    public ICompletableFuture<Boolean> deleteAsync(K key) {
        throw new MethodNotAvailableException();
    }

    @Override
    public boolean evict(K key) {
        return this.map.evict(key);
    }

    @Override
    @MethodNotAvailable
    public <T> T invoke(K key, EntryProcessor<K, V, T> entryProcessor, Object ... arguments) throws EntryProcessorException {
        throw new MethodNotAvailableException();
    }

    @Override
    public Object executeOnKey(K key, com.hazelcast.map.EntryProcessor entryProcessor) {
        return this.map.executeOnKey(key, entryProcessor);
    }

    @Override
    public Map<K, Object> executeOnKeys(Set<K> keys, com.hazelcast.map.EntryProcessor entryProcessor) {
        return this.map.executeOnKeys(keys, entryProcessor);
    }

    @Override
    public Map<K, Object> executeOnEntries(com.hazelcast.map.EntryProcessor entryProcessor) {
        return this.map.executeOnEntries(entryProcessor);
    }

    @Override
    public Map<K, Object> executeOnEntries(com.hazelcast.map.EntryProcessor entryProcessor, Predicate predicate) {
        return this.map.executeOnEntries(entryProcessor, predicate);
    }

    @Override
    public boolean containsKey(K key) {
        return this.map.containsKey(key);
    }

    @Override
    public void loadAll(boolean replaceExistingValues) {
        this.map.loadAll(replaceExistingValues);
    }

    @Override
    public void loadAll(Set<K> keys, boolean replaceExistingValues) {
        this.map.loadAll(keys, replaceExistingValues);
    }

    @Override
    @MethodNotAvailable
    public void loadAll(Set<? extends K> keys, boolean replaceExistingValues, CompletionListener completionListener) {
        throw new MethodNotAvailableException();
    }

    @Override
    public Map<K, V> getAll(Set<K> keys) {
        return this.map.getAll(keys);
    }

    @Override
    public void putAll(Map<K, V> map) {
        this.map.putAll(map);
    }

    @Override
    public void removeAll() {
        this.map.removeAll(TruePredicate.INSTANCE);
    }

    @Override
    @MethodNotAvailable
    public void removeAll(Set<K> keys) {
        throw new MethodNotAvailableException();
    }

    @Override
    public void evictAll() {
        this.map.evictAll();
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
    public LocalMapStats getLocalMapStats() {
        return this.map.getLocalMapStats();
    }

    public void waitUntilLoaded() {
        if (this.map instanceof MapProxyImpl) {
            ((MapProxyImpl)this.map).waitUntilLoaded();
        }
    }
}

