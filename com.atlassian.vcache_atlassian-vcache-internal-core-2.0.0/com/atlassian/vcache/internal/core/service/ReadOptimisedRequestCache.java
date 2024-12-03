/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.RequestCache
 *  com.atlassian.vcache.VCacheException
 *  com.atlassian.vcache.internal.NameValidator
 *  com.atlassian.vcache.internal.RequestContext
 *  javax.annotation.Nullable
 */
package com.atlassian.vcache.internal.core.service;

import com.atlassian.vcache.RequestCache;
import com.atlassian.vcache.VCacheException;
import com.atlassian.vcache.internal.NameValidator;
import com.atlassian.vcache.internal.RequestContext;
import com.atlassian.vcache.internal.core.service.FactoryUtils;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

class ReadOptimisedRequestCache<K, V>
implements RequestCache<K, V> {
    private final ThreadLocal<Boolean> inWriteLock = ThreadLocal.withInitial(() -> false);
    private final String name;
    private final Supplier<RequestContext> contextSupplier;
    private final Duration lockTimeout;

    ReadOptimisedRequestCache(String name, Supplier<RequestContext> contextSupplier, Duration lockTimeout) {
        this.name = NameValidator.requireValidCacheName((String)name);
        this.contextSupplier = Objects.requireNonNull(contextSupplier);
        this.lockTimeout = Objects.requireNonNull(lockTimeout);
    }

    public Optional<V> get(K key) {
        return Optional.ofNullable(this.withOptimisticReadLock(map -> map.get(key)));
    }

    public V get(K key, Supplier<? extends V> supplier) {
        Optional<V> value = this.get(key);
        return (V)value.orElseGet(() -> {
            Object candidateValue = Objects.requireNonNull(supplier.get());
            Object existing = this.withWriteLock(map -> map.putIfAbsent(key, candidateValue));
            return existing == null ? candidateValue : existing;
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<K, V> getBulk(Function<Set<K>, Map<K, V>> factory, Iterable<K> keys) {
        Function<Map, Map> cacheOps = map -> {
            Map<Object, Optional> existingValues = StreamSupport.stream(keys.spliterator(), false).distinct().collect(Collectors.toMap(Objects::requireNonNull, k -> Optional.ofNullable(map.get(k))));
            Map<Object, Object> grandResult = existingValues.entrySet().stream().filter(e -> ((Optional)e.getValue()).isPresent()).collect(Collectors.toMap(Map.Entry::getKey, e -> ((Optional)e.getValue()).get()));
            if (grandResult.size() == existingValues.size()) {
                return grandResult;
            }
            Set missingKeys = existingValues.entrySet().stream().filter(e -> !((Optional)e.getValue()).isPresent()).map(Map.Entry::getKey).collect(Collectors.toSet());
            Map missingValues = (Map)factory.apply(missingKeys);
            FactoryUtils.verifyFactoryResult(missingValues, missingKeys);
            missingValues.forEach((key, value) -> {
                Optional<Object> existing = Optional.ofNullable(map.putIfAbsent(key, value));
                grandResult.put(key, existing.orElse(value));
            });
            return grandResult;
        };
        if (this.inWriteLock.get().booleanValue()) {
            return cacheOps.apply(this.ensureDelegate().map);
        }
        try {
            this.inWriteLock.set(true);
            Map map2 = this.withWriteLock(cacheOps);
            return map2;
        }
        finally {
            this.inWriteLock.set(false);
        }
    }

    public void put(K key, V value) {
        this.withWriteLock(map -> map.put(key, value));
    }

    public Optional<V> putIfAbsent(K key, V value) {
        return Optional.ofNullable(this.withWriteLock(map -> map.putIfAbsent(key, value)));
    }

    public boolean replaceIf(K key, V currentValue, V newValue) {
        return this.withWriteLock(map -> map.replace(Objects.requireNonNull(key), Objects.requireNonNull(currentValue), Objects.requireNonNull(newValue)));
    }

    public boolean removeIf(K key, V value) {
        return this.withWriteLock(map -> map.remove(Objects.requireNonNull(key), Objects.requireNonNull(value)));
    }

    public void remove(K key) {
        this.withWriteLock(map -> map.remove(key));
    }

    public void removeAll() {
        this.withWriteLock(map -> {
            map.clear();
            return 0;
        });
    }

    public String getName() {
        return this.name;
    }

    private MapAndLock<K, V> ensureDelegate() {
        RequestContext requestContext = this.contextSupplier.get();
        return (MapAndLock)requestContext.computeIfAbsent((Object)this, () -> new MapAndLock());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    private <R> R withOptimisticReadLock(Function<Map<K, V>, R> getV) {
        long readLockStamp;
        MapAndLock<K, V> mapAndLock = this.ensureDelegate();
        long optimisticStamp = mapAndLock.lock.tryOptimisticRead();
        R value = getV.apply(mapAndLock.map);
        if (mapAndLock.lock.validate(optimisticStamp)) {
            return value;
        }
        try {
            readLockStamp = mapAndLock.lock.tryReadLock(this.lockTimeout.toMillis(), TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            throw new VCacheException("Lock acquisition on cache interrupted.", (Throwable)e);
        }
        if (readLockStamp != 0L) {
            try {
                R r = getV.apply(mapAndLock.map);
                return r;
            }
            finally {
                mapAndLock.lock.unlock(readLockStamp);
            }
        }
        throw new VCacheException("Failed to lock cache");
    }

    @Nullable
    private <R> R withWriteLock(Function<Map<K, V>, R> putV) {
        MapAndLock<K, V> mapAndLock = this.ensureDelegate();
        long stamp = 0L;
        try {
            stamp = mapAndLock.lock.tryWriteLock(this.lockTimeout.toMillis(), TimeUnit.MILLISECONDS);
            if (stamp != 0L) {
                R r = putV.apply(mapAndLock.map);
                return r;
            }
            try {
                throw new VCacheException("Could not acquire write lock");
            }
            catch (InterruptedException e) {
                throw new VCacheException("Interrupted acquiring write lock", (Throwable)e);
            }
        }
        finally {
            if (stamp != 0L) {
                mapAndLock.lock.unlockWrite(stamp);
            }
        }
    }

    private static class MapAndLock<K, V> {
        final Map<K, V> map = new HashMap();
        final StampedLock lock = new StampedLock();

        private MapAndLock() {
        }
    }
}

