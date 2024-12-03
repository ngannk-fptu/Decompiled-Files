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

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.internal.adapter.DataStructureAdapter;
import com.hazelcast.internal.adapter.MethodNotAvailable;
import com.hazelcast.internal.adapter.MethodNotAvailableException;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.query.Predicate;
import com.hazelcast.transaction.TransactionContext;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;

public class TransactionalMapDataStructureAdapter<K, V>
implements DataStructureAdapter<K, V> {
    private final HazelcastInstance hazelcastInstance;
    private final String name;
    private TransactionContext transactionContext;
    private TransactionalMap<K, V> transactionalMap;

    public TransactionalMapDataStructureAdapter(HazelcastInstance hazelcastInstance, String name) {
        this.hazelcastInstance = hazelcastInstance;
        this.name = name;
    }

    @Override
    public int size() {
        this.begin();
        int size = this.transactionalMap.size();
        this.commit();
        return size;
    }

    @Override
    public V get(K key) {
        this.begin();
        V value = this.transactionalMap.get(key);
        this.commit();
        return value;
    }

    @Override
    @MethodNotAvailable
    public ICompletableFuture<V> getAsync(K key) {
        throw new MethodNotAvailableException();
    }

    @Override
    public void set(K key, V value) {
        this.begin();
        this.transactionalMap.set(key, value);
        this.commit();
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
        this.begin();
        V oldValue = this.transactionalMap.put(key, value);
        this.commit();
        return oldValue;
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
    public boolean putIfAbsent(K key, V value) {
        this.begin();
        V oldValue = this.transactionalMap.putIfAbsent(key, value);
        this.commit();
        return oldValue == null;
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
    public V replace(K key, V newValue) {
        this.begin();
        V oldValue = this.transactionalMap.replace(key, newValue);
        this.commit();
        return oldValue;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        this.begin();
        boolean result = this.transactionalMap.replace(key, oldValue, newValue);
        this.commit();
        return result;
    }

    @Override
    public V remove(K key) {
        this.begin();
        V oldValue = this.transactionalMap.remove(key);
        this.commit();
        return oldValue;
    }

    @Override
    public boolean remove(K key, V oldValue) {
        this.begin();
        boolean result = this.transactionalMap.remove(key, oldValue);
        this.commit();
        return result;
    }

    @Override
    @MethodNotAvailable
    public ICompletableFuture<V> removeAsync(K key) {
        throw new MethodNotAvailableException();
    }

    @Override
    public void delete(K key) {
        this.begin();
        this.transactionalMap.delete(key);
        this.commit();
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
    @MethodNotAvailable
    public Map<K, V> getAll(Set<K> keys) {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public void putAll(Map<K, V> map) {
        throw new MethodNotAvailableException();
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
    @MethodNotAvailable
    public void clear() {
        throw new MethodNotAvailableException();
    }

    @Override
    @MethodNotAvailable
    public void close() {
        throw new MethodNotAvailableException();
    }

    @Override
    public void destroy() {
        this.begin();
        this.transactionalMap.destroy();
        this.commit();
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

    @Override
    public boolean containsKey(K key) {
        this.begin();
        boolean result = this.transactionalMap.containsKey(key);
        this.commit();
        return result;
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

    public IMap<K, V> getMap() {
        return this.hazelcastInstance.getMap(this.name);
    }

    private void begin() {
        this.transactionContext = this.hazelcastInstance.newTransactionContext();
        this.transactionContext.beginTransaction();
        this.transactionalMap = this.transactionContext.getMap(this.name);
    }

    private void commit() {
        this.transactionContext.commitTransaction();
        this.transactionContext = null;
        this.transactionalMap = null;
    }
}

