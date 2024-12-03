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

import com.hazelcast.cache.ICache;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.internal.adapter.DataStructureAdapter;
import com.hazelcast.internal.adapter.MethodNotAvailable;
import com.hazelcast.internal.adapter.MethodNotAvailableException;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.query.Predicate;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;

public class ICacheDataStructureAdapter<K, V>
implements DataStructureAdapter<K, V> {
    private final ICache<K, V> cache;

    public ICacheDataStructureAdapter(ICache<K, V> cache) {
        this.cache = cache;
    }

    @Override
    public int size() {
        return this.cache.size();
    }

    @Override
    public V get(K key) {
        return (V)this.cache.get(key);
    }

    @Override
    public ICompletableFuture<V> getAsync(K key) {
        return this.cache.getAsync(key);
    }

    @Override
    public void set(K key, V value) {
        this.cache.put(key, value);
    }

    @Override
    public ICompletableFuture<Void> setAsync(K key, V value) {
        return this.cache.putAsync(key, value);
    }

    @Override
    @MethodNotAvailable
    public ICompletableFuture<Void> setAsync(K key, V value, long ttl, TimeUnit timeunit) {
        throw new MethodNotAvailableException();
    }

    @Override
    public ICompletableFuture<Void> setAsync(K key, V value, ExpiryPolicy expiryPolicy) {
        return this.cache.putAsync(key, value, expiryPolicy);
    }

    @Override
    public V put(K key, V value) {
        return (V)this.cache.getAndPut(key, value);
    }

    @Override
    public ICompletableFuture<V> putAsync(K key, V value) {
        return this.cache.getAndPutAsync(key, value);
    }

    @Override
    @MethodNotAvailable
    public ICompletableFuture<V> putAsync(K key, V value, long time, TimeUnit unit) {
        throw new MethodNotAvailableException();
    }

    @Override
    public ICompletableFuture<V> putAsync(K key, V value, ExpiryPolicy expiryPolicy) {
        return this.cache.getAndPutAsync(key, value, expiryPolicy);
    }

    @Override
    @MethodNotAvailable
    public void putTransient(K key, V value, long ttl, TimeUnit timeunit) {
        throw new MethodNotAvailableException();
    }

    @Override
    public boolean putIfAbsent(K key, V value) {
        return this.cache.putIfAbsent(key, value);
    }

    @Override
    public ICompletableFuture<Boolean> putIfAbsentAsync(K key, V value) {
        return this.cache.putIfAbsentAsync(key, value);
    }

    @Override
    @MethodNotAvailable
    public void setTtl(K key, long duration, TimeUnit timeUnit) {
        throw new MethodNotAvailableException();
    }

    @Override
    public V replace(K key, V newValue) {
        return (V)this.cache.getAndReplace(key, newValue);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return this.cache.replace(key, oldValue, newValue);
    }

    @Override
    public V remove(K key) {
        return (V)this.cache.getAndRemove(key);
    }

    @Override
    public boolean remove(K key, V oldValue) {
        return this.cache.remove(key, oldValue);
    }

    @Override
    public ICompletableFuture<V> removeAsync(K key) {
        return this.cache.getAndRemoveAsync(key);
    }

    @Override
    public void delete(K key) {
        this.cache.remove(key);
    }

    @Override
    public ICompletableFuture<Boolean> deleteAsync(K key) {
        return this.cache.removeAsync(key);
    }

    @Override
    @MethodNotAvailable
    public boolean evict(K key) {
        throw new MethodNotAvailableException();
    }

    @Override
    public <T> T invoke(K key, EntryProcessor<K, V, T> entryProcessor, Object ... arguments) throws EntryProcessorException {
        return (T)this.cache.invoke(key, entryProcessor, arguments);
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
        return this.cache.containsKey(key);
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
    public void loadAll(Set<? extends K> keys, boolean replaceExistingValues, CompletionListener completionListener) {
        this.cache.loadAll(keys, replaceExistingValues, completionListener);
    }

    @Override
    public Map<K, V> getAll(Set<K> keys) {
        return this.cache.getAll(keys);
    }

    @Override
    public void putAll(Map<K, V> map) {
        this.cache.putAll(map);
    }

    @Override
    public void removeAll() {
        this.cache.removeAll();
    }

    @Override
    public void removeAll(Set<K> keys) {
        this.cache.removeAll(keys);
    }

    @Override
    @MethodNotAvailable
    public void evictAll() {
        throw new MethodNotAvailableException();
    }

    @Override
    public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> keys, EntryProcessor<K, V, T> entryProcessor, Object ... arguments) {
        return this.cache.invokeAll(keys, entryProcessor, arguments);
    }

    @Override
    public void clear() {
        this.cache.clear();
    }

    @Override
    public void close() {
        this.cache.close();
    }

    @Override
    public void destroy() {
        this.cache.destroy();
    }

    @Override
    public void setExpiryPolicy(Set<K> keys, ExpiryPolicy expiryPolicy) {
        this.cache.setExpiryPolicy(keys, expiryPolicy);
    }

    @Override
    public boolean setExpiryPolicy(K key, ExpiryPolicy expiryPolicy) {
        return this.cache.setExpiryPolicy(key, expiryPolicy);
    }

    @Override
    @MethodNotAvailable
    public LocalMapStats getLocalMapStats() {
        throw new MethodNotAvailableException();
    }
}

