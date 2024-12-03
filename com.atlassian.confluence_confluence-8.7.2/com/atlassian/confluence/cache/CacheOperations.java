/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.Supplier
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cache;

import com.atlassian.annotations.Internal;
import com.atlassian.cache.Cache;
import com.atlassian.cache.Supplier;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class CacheOperations<K, V> {
    private static final Logger log = LoggerFactory.getLogger(CacheOperations.class);
    private final Map<K, CacheOperation<K, V>> cachedOperations = Maps.newHashMap();
    private final Map<K, V> cachedValues = Maps.newHashMap();

    public V get(K key) {
        return this.cachedValues.get(key);
    }

    public boolean isRemoved(K key) {
        return this.cachedOperations.get(key) instanceof RemoveOperation;
    }

    public void cache(K key, V value) {
        this.cachedValues.put(key, value);
    }

    public void put(K key, V value) {
        this.cachedValues.put(key, value);
        this.cachedOperations.put(key, new PutOperation(key));
    }

    @Deprecated
    public void putIfAbsent(K key, V value) {
        this.cachedValues.put(key, value);
        this.cachedOperations.put(key, new PutIfAbsentOperation(key));
    }

    @Nonnull
    public V get(K key, Supplier<? extends V> supplier) {
        Object value = this.cachedValues.computeIfAbsent(key, k -> supplier.get());
        this.cachedOperations.compute(key, (k, previousOperation) -> new GetWithSupplierOperation(key, previousOperation, () -> value));
        return (V)value;
    }

    public void remove(K key) {
        this.cachedValues.remove(key);
        this.cachedOperations.put(key, new RemoveOperation(key));
    }

    public void removeAll(Collection<K> keys) {
        this.cachedValues.clear();
        for (K key : keys) {
            this.cachedOperations.put(key, new RemoveOperation(key));
        }
    }

    public void clear() {
        this.cachedOperations.clear();
        this.cachedValues.clear();
    }

    public Set<K> filter(Iterable<K> globalKeys) {
        Predicate keyExistsPredicate = key -> !(this.cachedOperations.get(key) instanceof RemoveOperation);
        Iterable notMarkedForRemovalKeys = Iterables.filter(globalKeys, (Predicate)keyExistsPredicate);
        Iterable allKeys = Iterables.concat((Iterable)notMarkedForRemovalKeys, this.cachedValues.keySet());
        return Sets.newHashSet((Iterable)allKeys);
    }

    public void perform(Cache<K, V> cache) {
        for (CacheOperation<K, V> operation : this.cachedOperations.values()) {
            operation.perform(cache, this.cachedValues);
        }
    }

    public int operationCount() {
        return this.cachedOperations.size();
    }

    public int putIfAbsentCount() {
        return Iterables.size((Iterable)Iterables.filter(this.cachedOperations.values(), PutIfAbsentOperation.class::isInstance));
    }

    public int putCount() {
        return Iterables.size((Iterable)Iterables.filter(this.cachedOperations.values(), PutOperation.class::isInstance));
    }

    public int removeCount() {
        return Iterables.size((Iterable)Iterables.filter(this.cachedOperations.values(), RemoveOperation.class::isInstance));
    }

    public int valueCount() {
        return this.cachedValues.size();
    }

    private static class GetWithSupplierOperation<K, V>
    implements CacheOperation<K, V> {
        private final K key;
        private final CacheOperation<K, V> previousOperation;
        private final Supplier<? extends V> supplier;

        GetWithSupplierOperation(K key, @Nullable CacheOperation<K, V> previousOperation, Supplier<? extends V> supplier) {
            this.key = Objects.requireNonNull(key);
            this.previousOperation = previousOperation;
            this.supplier = supplier;
        }

        @Override
        public void perform(Cache<K, V> cache, Map<K, V> sessionCache) {
            if (this.previousOperation != null) {
                this.previousOperation.perform(cache, sessionCache);
            }
            Object value = cache.get(this.key, this.supplier);
            sessionCache.put(this.key, value);
        }

        @Override
        public K key() {
            return this.key;
        }
    }

    @Internal
    static class PutIfAbsentOperation<K, V>
    implements CacheOperation<K, V> {
        private final K key;

        PutIfAbsentOperation(K key) {
            this.key = Objects.requireNonNull(key);
        }

        @Override
        public void perform(Cache<K, V> cache, Map<K, V> sessionCache) {
            V value = sessionCache.get(this.key);
            log.debug("Putting if absent entry [{},{}]", this.key, value);
            cache.putIfAbsent(this.key, value);
        }

        @Override
        public K key() {
            return this.key;
        }
    }

    @Internal
    static class PutOperation<K, V>
    implements CacheOperation<K, V> {
        private final K key;

        PutOperation(K key) {
            this.key = Objects.requireNonNull(key);
        }

        @Override
        public void perform(Cache<K, V> cache, Map<K, V> sessionCache) {
            V value = sessionCache.get(this.key);
            log.debug("Putting entry [{},{}]", this.key, value);
            cache.put(this.key, value);
        }

        @Override
        public K key() {
            return this.key;
        }
    }

    @Internal
    static class RemoveOperation<K, V>
    implements CacheOperation<K, V> {
        private final K key;

        RemoveOperation(K key) {
            this.key = Objects.requireNonNull(key);
        }

        @Override
        public void perform(Cache<K, V> cache, Map<K, V> sessionCache) {
            log.debug("Removing key [{}]", this.key);
            cache.remove(this.key);
        }

        @Override
        public K key() {
            return this.key;
        }
    }

    @Internal
    static interface CacheOperation<K, V> {
        public void perform(Cache<K, V> var1, Map<K, V> var2);

        public K key();
    }
}

