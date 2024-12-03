/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheEntryEvent
 *  com.atlassian.cache.CacheEntryListener
 *  com.atlassian.cache.CacheException
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.cache.Supplier
 *  com.atlassian.cache.impl.CacheEntryListenerSupport
 *  com.atlassian.cache.impl.CacheLoaderSupplier
 *  com.atlassian.cache.impl.ValueCacheEntryListenerSupport
 *  com.google.common.base.Objects
 *  com.google.common.base.Throwables
 *  com.hazelcast.core.IMap
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.cache.hazelcast;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheEntryEvent;
import com.atlassian.cache.CacheEntryListener;
import com.atlassian.cache.CacheException;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.Supplier;
import com.atlassian.cache.hazelcast.GetOrInitVersionEntryProcessor;
import com.atlassian.cache.hazelcast.HazelcastCacheManager;
import com.atlassian.cache.hazelcast.IncrementVersionEntryProcessor;
import com.atlassian.cache.hazelcast.ManagedHybridCacheSupport;
import com.atlassian.cache.hazelcast.Versioned;
import com.atlassian.cache.impl.CacheEntryListenerSupport;
import com.atlassian.cache.impl.CacheLoaderSupplier;
import com.atlassian.cache.impl.ValueCacheEntryListenerSupport;
import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.hazelcast.core.IMap;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HazelcastHybridCache<K, V>
extends ManagedHybridCacheSupport
implements Cache<K, V> {
    private static final Logger log = LoggerFactory.getLogger(HazelcastHybridCache.class);
    private final Cache<K, Versioned<V>> localCache;
    private final boolean selfLoading;
    private final IMap<K, Long> versionMap;
    private final CacheEntryListenerSupport<K, V> listenerSupport = new ValueCacheEntryListenerSupport<K, V>(){

        protected void initValue(CacheEntryListenerSupport<K, V> actualListenerSupport) {
            HazelcastHybridCache.this.localCache.addListener(new DelegatingCacheEntryListener(actualListenerSupport), true);
        }

        protected void initValueless(CacheEntryListenerSupport<K, V> actualListenerSupport) {
            HazelcastHybridCache.this.localCache.addListener(new DelegatingCacheEntryListener(actualListenerSupport), false);
        }
    };

    public HazelcastHybridCache(String name, CacheFactory localCacheFactory, IMap<K, Long> versionMap, final CacheLoader<K, V> cacheLoader, HazelcastCacheManager cacheManager) {
        super(name, cacheManager);
        this.selfLoading = cacheLoader != null;
        CacheLoader versionedCacheLoader = this.selfLoading ? new CacheLoader<K, Versioned<V>>(){

            @Nonnull
            public Versioned<V> load(@Nonnull K key) {
                return HazelcastHybridCache.this.loadAndVersion(key, (Supplier)new CacheLoaderSupplier(key, cacheLoader));
            }
        } : null;
        this.versionMap = versionMap;
        this.localCache = localCacheFactory.getCache(name, versionedCacheLoader, this.getCacheSettings());
    }

    public void clear() {
        this.removeAll();
    }

    public boolean containsKey(@Nonnull K k) {
        return this.localCache.containsKey(k);
    }

    public V get(@Nonnull K key) {
        return this.getInternal(key).getValue();
    }

    @Nonnull
    public V get(final @Nonnull K key, final @Nonnull Supplier<? extends V> valueSupplier) {
        return this.getInternal(key, new Supplier<Versioned<V>>(){

            public Versioned<V> get() {
                return HazelcastHybridCache.this.loadAndVersion(key, valueSupplier);
            }
        }).getValue();
    }

    @Nonnull
    public Collection<K> getKeys() {
        return this.localCache.getKeys();
    }

    @Override
    @Nonnull
    public String getName() {
        return this.localCache.getName();
    }

    public boolean isReplicateAsynchronously() {
        return false;
    }

    public void put(@Nonnull K key, @Nonnull V value) {
        Long version = this.incrementVersion(key);
        this.localCache.put(key, new Versioned<V>(value, version));
    }

    public V putIfAbsent(@Nonnull K key, @Nonnull V value) {
        Long nextVersion = this.getNextVersion(key);
        Versioned<V> versioned = new Versioned<V>(value, nextVersion);
        Versioned oldValue = (Versioned)this.localCache.putIfAbsent(key, versioned);
        if (oldValue == null) {
            this.incrementVersion(key);
            return null;
        }
        return (V)oldValue.getValue();
    }

    public void remove(@Nonnull K key) {
        this.incrementVersion(key);
        this.localCache.remove(key);
    }

    public boolean remove(@Nonnull K key, @Nonnull V value) {
        Versioned<V> currentValue = null;
        try {
            currentValue = this.getInternal(key);
        }
        catch (CacheException e) {
            log.debug("Swallowing exception thrown during call to remove, when looking up cache key: " + key, (Throwable)e);
        }
        if (currentValue != null && Objects.equal(value, currentValue.getValue()) && this.localCache.remove(key, currentValue)) {
            this.incrementVersion(key);
            return true;
        }
        return false;
    }

    public void removeAll() {
        this.versionMap.executeOnEntries(IncrementVersionEntryProcessor.getInstance());
        this.localCache.removeAll();
    }

    public boolean replace(@Nonnull K key, @Nonnull V oldValue, @Nonnull V newValue) {
        Long nextVersion;
        Versioned<V> currentValue = this.getInternal(key);
        if (Objects.equal(oldValue, currentValue.getValue()) && this.localCache.replace(key, currentValue, new Versioned<V>(newValue, nextVersion = this.getNextVersion(key)))) {
            this.incrementVersion(key);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateExpireAfterAccess(long expireAfter, @Nonnull TimeUnit timeUnit) {
        if (!super.updateExpireAfterAccess(expireAfter, timeUnit)) {
            return false;
        }
        CacheSettings settings = new CacheSettingsBuilder(this.getCacheSettings()).expireAfterAccess(expireAfter * 2L, timeUnit).build();
        this.cacheManager.updateCacheSettings(this.getHazelcastMapName(), settings);
        return true;
    }

    @Override
    public boolean updateExpireAfterWrite(long expireAfter, @Nonnull TimeUnit timeUnit) {
        if (!super.updateExpireAfterAccess(expireAfter, timeUnit)) {
            return false;
        }
        CacheSettings settings = new CacheSettingsBuilder(this.getCacheSettings()).expireAfterAccess(expireAfter * 2L, timeUnit).build();
        this.cacheManager.updateCacheSettings(this.getHazelcastMapName(), settings);
        return true;
    }

    @Override
    public boolean updateMaxEntries(int newValue) {
        if (!super.updateMaxEntries(newValue)) {
            return false;
        }
        CacheSettings settings = new CacheSettingsBuilder(this.getCacheSettings()).maxEntries(2 * newValue).build();
        this.cacheManager.updateCacheSettings(this.getHazelcastMapName(), settings);
        return true;
    }

    public void addListener(@Nonnull CacheEntryListener<K, V> listener, boolean includeValues) {
        this.listenerSupport.add(listener, includeValues);
    }

    public void removeListener(@Nonnull CacheEntryListener<K, V> listener) {
        this.listenerSupport.remove(listener);
    }

    @Override
    protected ManagedCache getLocalCache() {
        return (ManagedCache)this.localCache;
    }

    private CacheSettings getCacheSettings() {
        return this.cacheManager.getCacheSettings(this.getHazelcastMapName());
    }

    private String getHazelcastMapName() {
        return this.versionMap.getName();
    }

    public boolean isFlushable() {
        return this.getCacheSettings().getFlushable(true);
    }

    private Versioned<V> loadAndVersion(K key, Supplier<? extends V> supplier) {
        try {
            long version = this.getVersion(key);
            Object value = supplier.get();
            if (value == null) {
                throw new CacheException("The generated value for cache '" + this.getName() + "' was null for key '" + key + "'. Null values are not supported.");
            }
            log.debug("Generated value '{}' for key '{}' in cache with name '{}'", new Object[]{value, key, this.localCache.getName()});
            return new Versioned<Object>(value, version);
        }
        catch (RuntimeException e) {
            Throwables.propagateIfInstanceOf((Throwable)e, CacheException.class);
            throw new CacheException("Error generating a value for key '" + key + "' in cache '" + this.localCache.getName() + "'", (Throwable)e);
        }
    }

    @Nonnull
    private Versioned<V> getInternal(K key) {
        Versioned versioned = (Versioned)this.localCache.get(key);
        if (versioned != null) {
            Long version = (Long)this.versionMap.get(key);
            if (version != null && version.longValue() == versioned.getVersion()) {
                return versioned;
            }
            this.localCache.remove(key);
            if (this.selfLoading) {
                return (Versioned)this.localCache.get(key);
            }
        }
        return Versioned.empty();
    }

    @Nonnull
    private Versioned<V> getInternal(K key, Supplier<Versioned<V>> valueSupplier) {
        Versioned versioned = (Versioned)this.localCache.get(key, valueSupplier);
        Long version = (Long)this.versionMap.get(key);
        if (version != null && version.longValue() == versioned.getVersion()) {
            return versioned;
        }
        this.localCache.remove(key);
        return (Versioned)this.localCache.get(key, valueSupplier);
    }

    private Long getNextVersion(K key) {
        Long version = (Long)this.versionMap.get(key);
        return version == null ? 1L : version + 1L;
    }

    private Long getVersion(K key) {
        Long version = (Long)this.versionMap.get(key);
        if (version == null) {
            version = (Long)this.versionMap.executeOnKey(key, GetOrInitVersionEntryProcessor.getInstance());
        }
        return version;
    }

    private Long incrementVersion(K key) {
        return (Long)this.versionMap.executeOnKey(key, IncrementVersionEntryProcessor.getInstance());
    }

    private static class DelegatingCacheEntryListener<K, V>
    implements CacheEntryListener<K, Versioned<V>> {
        private final CacheEntryListenerSupport<K, V> listenerSupport;

        private DelegatingCacheEntryListener(CacheEntryListenerSupport<K, V> listenerSupport) {
            this.listenerSupport = listenerSupport;
        }

        public void onAdd(@Nonnull CacheEntryEvent<K, Versioned<V>> event) {
            this.listenerSupport.notifyAdd(event.getKey(), this.get((Versioned)event.getValue()));
        }

        public void onEvict(@Nonnull CacheEntryEvent<K, Versioned<V>> event) {
            this.listenerSupport.notifyEvict(event.getKey(), this.get((Versioned)event.getOldValue()));
        }

        public void onRemove(@Nonnull CacheEntryEvent<K, Versioned<V>> event) {
            this.listenerSupport.notifyRemove(event.getKey(), this.get((Versioned)event.getOldValue()));
        }

        public void onUpdate(@Nonnull CacheEntryEvent<K, Versioned<V>> event) {
            this.listenerSupport.notifyUpdate(event.getKey(), this.get((Versioned)event.getValue()), this.get((Versioned)event.getOldValue()));
        }

        private V get(Versioned<V> versioned) {
            return versioned != null ? (V)versioned.getValue() : null;
        }
    }
}

