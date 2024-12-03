/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.RequestCache
 *  com.atlassian.vcache.internal.NameValidator
 *  com.atlassian.vcache.internal.RequestContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.vcache.internal.core.service;

import com.atlassian.vcache.RequestCache;
import com.atlassian.vcache.internal.NameValidator;
import com.atlassian.vcache.internal.RequestContext;
import com.atlassian.vcache.internal.core.service.LocalCacheUtils;
import com.atlassian.vcache.internal.core.service.VCacheLock;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DefaultRequestCache<K, V>
implements RequestCache<K, V> {
    public static final Logger log = LoggerFactory.getLogger(DefaultRequestCache.class);
    private final String name;
    private final Supplier<RequestContext> contextSupplier;
    private final Duration lockTimeout;

    DefaultRequestCache(String name, Supplier<RequestContext> contextSupplier, Duration lockTimeout) {
        this.name = NameValidator.requireValidCacheName((String)name);
        this.contextSupplier = Objects.requireNonNull(contextSupplier);
        this.lockTimeout = Objects.requireNonNull(lockTimeout);
    }

    public Optional<V> get(K key) {
        return this.withLock(kvMap -> Optional.ofNullable(kvMap.get(key)));
    }

    public V get(K key, Supplier<? extends V> supplier) {
        Optional<V> current = this.get(key);
        return (V)current.orElseGet(() -> {
            Object candidateValue = Objects.requireNonNull(supplier.get());
            return this.withLock(kvMap -> {
                Object existing = kvMap.putIfAbsent(Objects.requireNonNull(key), candidateValue);
                return existing == null ? candidateValue : existing;
            });
        });
    }

    @SafeVarargs
    public final Map<K, V> getBulk(Function<Set<K>, Map<K, V>> factory, K ... keys) {
        return this.getBulk(factory, (Iterable<K>)Arrays.asList(keys));
    }

    public Map<K, V> getBulk(Function<Set<K>, Map<K, V>> factory, Iterable<K> keys) {
        return this.withLock(kvMap -> LocalCacheUtils.getBulk(factory, keys, this::get, args -> this.putIfAbsent(args.key, args.value), this.ensureDelegate().lock));
    }

    public void put(K key, V value) {
        this.withLock(kvMap -> kvMap.put(Objects.requireNonNull(key), Objects.requireNonNull(value)));
    }

    public Optional<V> putIfAbsent(K key, V value) {
        return this.withLock(kvMap -> Optional.ofNullable(kvMap.putIfAbsent(Objects.requireNonNull(key), Objects.requireNonNull(value))));
    }

    public boolean replaceIf(K key, V currentValue, V newValue) {
        return this.withLock(kvMap -> kvMap.replace(Objects.requireNonNull(key), Objects.requireNonNull(currentValue), Objects.requireNonNull(newValue)));
    }

    public boolean removeIf(K key, V value) {
        return this.withLock(kvMap -> kvMap.remove(Objects.requireNonNull(key), Objects.requireNonNull(value)));
    }

    public void remove(K key) {
        this.withLock(kvMap -> kvMap.remove(key));
    }

    public void removeAll() {
        this.withLock(kvMap -> {
            kvMap.clear();
            return false;
        });
    }

    public String getName() {
        return this.name;
    }

    private MapAndLock ensureDelegate() {
        RequestContext requestContext = this.contextSupplier.get();
        return (MapAndLock)requestContext.computeIfAbsent((Object)this, () -> new MapAndLock());
    }

    private <R> R withLock(Function<Map<K, V>, R> fn) {
        MapAndLock mal = this.ensureDelegate();
        return (R)mal.lock.withLock(() -> fn.apply(mal.map));
    }

    static /* synthetic */ String access$000(DefaultRequestCache x0) {
        return x0.name;
    }

    static /* synthetic */ Duration access$100(DefaultRequestCache x0) {
        return x0.lockTimeout;
    }

    private class MapAndLock {
        final Map<K, V> map = new HashMap();
        final VCacheLock lock = new VCacheLock(DefaultRequestCache.access$000(DefaultRequestCache.this), DefaultRequestCache.access$100(DefaultRequestCache.this));

        private MapAndLock() {
        }
    }
}

