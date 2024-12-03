/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheEntryEvent
 *  com.atlassian.cache.CacheEntryListener
 *  com.atlassian.cache.CacheException
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheStatisticsKey
 *  com.atlassian.cache.Supplier
 *  com.atlassian.cache.impl.CacheEntryListenerSupport
 *  com.atlassian.cache.impl.CacheLoaderSupplier
 *  com.atlassian.cache.impl.DefaultCacheEntryEvent
 *  com.atlassian.cache.impl.ValueCacheEntryListenerSupport
 *  com.atlassian.hazelcast.serialization.OsgiSafe
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Suppliers
 *  com.google.common.base.Throwables
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSortedMap
 *  com.google.common.collect.ImmutableSortedMap$Builder
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  com.hazelcast.core.EntryAdapter
 *  com.hazelcast.core.EntryEvent
 *  com.hazelcast.core.IMap
 *  com.hazelcast.monitor.LocalMapStats
 *  com.hazelcast.monitor.NearCacheStats
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.cache.hazelcast;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheEntryEvent;
import com.atlassian.cache.CacheEntryListener;
import com.atlassian.cache.CacheException;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.cache.Supplier;
import com.atlassian.cache.hazelcast.CacheVersion;
import com.atlassian.cache.hazelcast.CacheVersionAwareCacheLoader;
import com.atlassian.cache.hazelcast.HazelcastCacheManager;
import com.atlassian.cache.hazelcast.ManagedCacheSupport;
import com.atlassian.cache.hazelcast.OsgiSafeUtils;
import com.atlassian.cache.impl.CacheEntryListenerSupport;
import com.atlassian.cache.impl.CacheLoaderSupplier;
import com.atlassian.cache.impl.DefaultCacheEntryEvent;
import com.atlassian.cache.impl.ValueCacheEntryListenerSupport;
import com.atlassian.hazelcast.serialization.OsgiSafe;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.IMap;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.monitor.NearCacheStats;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.function.Function;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HazelcastCache<K, V>
extends ManagedCacheSupport
implements Cache<K, V> {
    private static final Logger log = LoggerFactory.getLogger(HazelcastCache.class);
    private final CacheLoader<K, V> cacheLoader;
    private final IMap<K, OsgiSafe<V>> map;
    private final CacheVersion cacheVersion;
    private volatile boolean hasValueListeners = false;
    private final CacheEntryListenerSupport<K, OsgiSafe<V>> listenerSupport = new ValueCacheEntryListenerSupport<K, OsgiSafe<V>>(){

        protected void initValue(CacheEntryListenerSupport<K, OsgiSafe<V>> actualListenerSupport) {
            HazelcastCache.this.map.addEntryListener(new HazelcastCacheEntryListener(actualListenerSupport), true);
            HazelcastCache.this.hasValueListeners = true;
        }

        protected void initValueless(CacheEntryListenerSupport<K, OsgiSafe<V>> actualListenerSupport) {
            HazelcastCache.this.map.addEntryListener(new HazelcastCacheEntryListener(actualListenerSupport), false);
        }
    };

    public HazelcastCache(String name, IMap<K, OsgiSafe<V>> map, CacheLoader<K, V> cacheLoader, CacheVersion cacheVersion, HazelcastCacheManager cacheManager) {
        super(name, cacheManager);
        this.map = map;
        this.cacheVersion = (CacheVersion)Preconditions.checkNotNull((Object)cacheVersion);
        this.cacheLoader = cacheLoader != null ? new CacheVersionAwareCacheLoader<K, V>(cacheLoader, this.cacheVersion) : cacheLoader;
    }

    public void clear() {
        this.cleanupMap();
    }

    public boolean containsKey(@Nonnull K k) {
        return this.map.containsKey(k);
    }

    public V get(@Nonnull K key) {
        return this.getOrLoad(key, (Supplier<? extends V>)(this.cacheLoader == null ? null : new CacheLoaderSupplier(key, this.cacheLoader)));
    }

    @Nonnull
    public V get(@Nonnull K key, @Nonnull Supplier<? extends V> valueSupplier) {
        return this.getOrLoad(key, valueSupplier);
    }

    @Nonnull
    public Map<K, V> getBulk(@Nonnull Set<K> keys, @Nonnull Function<Set<K>, Map<K, V>> valuesSupplier) {
        Map valuesFromCache = Maps.transformValues((Map)this.map.getAll(keys), OsgiSafe::getValue);
        Sets.SetView keysToLoad = Sets.difference(keys, valuesFromCache.keySet());
        Map<K, V> loadedValues = valuesSupplier.apply((Set<K>)keysToLoad);
        this.map.putAll(Maps.transformValues(loadedValues, OsgiSafe::new));
        return ImmutableMap.builder().putAll(loadedValues).putAll(valuesFromCache).build();
    }

    @Nonnull
    public Collection<K> getKeys() {
        return this.map.keySet();
    }

    public void put(@Nonnull K key, @Nonnull V value) {
        if (this.hasValueListeners) {
            this.map.put(Preconditions.checkNotNull(key, (Object)"key"), OsgiSafeUtils.wrap(Preconditions.checkNotNull(value, (Object)"value")));
        } else {
            this.map.set(Preconditions.checkNotNull(key, (Object)"key"), OsgiSafeUtils.wrap(Preconditions.checkNotNull(value, (Object)"value")));
        }
    }

    public V putIfAbsent(@Nonnull K key, @Nonnull V value) {
        return (V)OsgiSafeUtils.unwrap((OsgiSafe)this.map.putIfAbsent(Preconditions.checkNotNull(key, (Object)"key"), OsgiSafeUtils.wrap(Preconditions.checkNotNull(value, (Object)"value"))));
    }

    public void remove(@Nonnull K key) {
        if (this.hasValueListeners) {
            this.map.remove(Preconditions.checkNotNull(key, (Object)"key"));
        } else {
            this.map.delete(Preconditions.checkNotNull(key, (Object)"key"));
        }
    }

    public boolean remove(@Nonnull K key, @Nonnull V value) {
        return this.map.remove(Preconditions.checkNotNull(key, (Object)"key"), OsgiSafeUtils.wrap(Preconditions.checkNotNull(value, (Object)"value")));
    }

    public void removeAll() {
        this.cleanupMap();
    }

    private void cleanupMap() {
        this.cacheVersion.incrementAndGet();
        this.map.clear();
    }

    public boolean replace(@Nonnull K key, @Nonnull V oldValue, @Nonnull V newValue) {
        return this.map.replace(Preconditions.checkNotNull(key, (Object)"key"), OsgiSafeUtils.wrap(Preconditions.checkNotNull(oldValue, (Object)"oldValue")), OsgiSafeUtils.wrap(Preconditions.checkNotNull(newValue, (Object)"newValue")));
    }

    public void addListener(@Nonnull CacheEntryListener<K, V> listener, boolean includeValues) {
        this.listenerSupport.add(new OsgiSafeCacheEntryListener(listener), includeValues);
    }

    public void removeListener(@Nonnull CacheEntryListener<K, V> listener) {
        this.listenerSupport.remove(new OsgiSafeCacheEntryListener(listener));
    }

    @Override
    @Nonnull
    protected String getHazelcastMapName() {
        return this.map.getName();
    }

    private V getOrLoad(K key, Supplier<? extends V> valueSupplier) {
        try {
            OsgiSafe<Object> value = (OsgiSafe<Object>)this.map.get(Preconditions.checkNotNull(key, (Object)"key"));
            if (value != null) {
                return (V)value.getValue();
            }
            if (valueSupplier == null) {
                return null;
            }
            Object newValue = valueSupplier.get();
            if (newValue == null) {
                throw new CacheException("The provided cacheLoader returned null. Null values are not supported.");
            }
            value = OsgiSafeUtils.wrap(newValue);
            OsgiSafe current = (OsgiSafe)this.map.putIfAbsent(key, value);
            return (V)OsgiSafeUtils.unwrap((OsgiSafe)MoreObjects.firstNonNull((Object)current, value));
        }
        catch (RuntimeException e) {
            Throwables.propagateIfInstanceOf((Throwable)e, CacheException.class);
            throw new CacheException("Problem retrieving a value from cache " + this.getName(), (Throwable)e);
        }
    }

    @Override
    @Nonnull
    public SortedMap<CacheStatisticsKey, java.util.function.Supplier<Long>> getStatistics() {
        ImmutableSortedMap.Builder builder = ImmutableSortedMap.orderedBy((Comparator)CacheStatisticsKey.SORT_BY_LABEL);
        LocalMapStats mapStats = this.map.getLocalMapStats();
        NearCacheStats nearCacheStats = mapStats.getNearCacheStats();
        if (nearCacheStats != null) {
            builder.put((Object)CacheStatisticsKey.HIT_COUNT, (Object)Suppliers.memoize(() -> ((NearCacheStats)nearCacheStats).getHits()));
            builder.put((Object)CacheStatisticsKey.MISS_COUNT, (Object)Suppliers.memoize(() -> ((NearCacheStats)nearCacheStats).getMisses()));
        } else {
            builder.put((Object)CacheStatisticsKey.HIT_COUNT, (Object)Suppliers.memoize(() -> ((LocalMapStats)mapStats).getHits()));
        }
        builder.put((Object)CacheStatisticsKey.SIZE, (Object)Suppliers.memoize(() -> this.map.size()));
        builder.put((Object)CacheStatisticsKey.HEAP_SIZE, (Object)Suppliers.memoize(() -> ((LocalMapStats)mapStats).getHeapCost()));
        builder.put((Object)CacheStatisticsKey.PUT_COUNT, (Object)Suppliers.memoize(() -> ((LocalMapStats)mapStats).getPutOperationCount()));
        builder.put((Object)CacheStatisticsKey.REMOVE_COUNT, (Object)Suppliers.memoize(() -> ((LocalMapStats)mapStats).getRemoveOperationCount()));
        builder.put((Object)CacheStatisticsKey.REQUEST_COUNT, (Object)Suppliers.memoize(() -> ((LocalMapStats)mapStats).getGetOperationCount()));
        return builder.build();
    }

    @Override
    public boolean isStatisticsEnabled() {
        return this.cacheManager.getMapConfig(this.getHazelcastMapName()).isStatisticsEnabled();
    }

    private static class HazelcastCacheEntryListener<K, V>
    extends EntryAdapter<K, V> {
        private final CacheEntryListenerSupport<K, V> listenerSupport;

        private HazelcastCacheEntryListener(CacheEntryListenerSupport<K, V> listenerSupport) {
            this.listenerSupport = (CacheEntryListenerSupport)Preconditions.checkNotNull(listenerSupport, (Object)"listenerSupport");
        }

        public void entryAdded(EntryEvent<K, V> event) {
            this.listenerSupport.notifyAdd(event.getKey(), event.getValue());
        }

        public void entryRemoved(EntryEvent<K, V> event) {
            this.listenerSupport.notifyRemove(event.getKey(), event.getOldValue());
        }

        public void entryUpdated(EntryEvent<K, V> event) {
            this.listenerSupport.notifyUpdate(event.getKey(), event.getValue(), event.getOldValue());
        }

        public void entryEvicted(EntryEvent<K, V> event) {
            this.listenerSupport.notifyEvict(event.getKey(), event.getOldValue());
        }
    }

    private static class OsgiSafeCacheEntryListener<K, V>
    implements CacheEntryListener<K, OsgiSafe<V>> {
        private final CacheEntryListener<K, V> delegate;

        private OsgiSafeCacheEntryListener(CacheEntryListener<K, V> listener) {
            this.delegate = (CacheEntryListener)Preconditions.checkNotNull(listener, (Object)"listener");
        }

        public void onAdd(@Nonnull CacheEntryEvent<K, OsgiSafe<V>> event) {
            this.delegate.onAdd(new OsgiSafeCacheEntryEvent<K, V>(event));
        }

        public void onEvict(@Nonnull CacheEntryEvent<K, OsgiSafe<V>> event) {
            this.delegate.onEvict(new OsgiSafeCacheEntryEvent<K, V>(event));
        }

        public void onRemove(@Nonnull CacheEntryEvent<K, OsgiSafe<V>> event) {
            this.delegate.onRemove(new OsgiSafeCacheEntryEvent<K, V>(event));
        }

        public void onUpdate(@Nonnull CacheEntryEvent<K, OsgiSafe<V>> event) {
            this.delegate.onUpdate(new OsgiSafeCacheEntryEvent<K, V>(event));
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            OsgiSafeCacheEntryListener that = (OsgiSafeCacheEntryListener)o;
            return this.delegate.equals(that.delegate);
        }

        public int hashCode() {
            return this.delegate.hashCode();
        }
    }

    private static class OsgiSafeCacheEntryEvent<K, V>
    extends DefaultCacheEntryEvent<K, V> {
        public OsgiSafeCacheEntryEvent(CacheEntryEvent<K, OsgiSafe<V>> event) {
            super(event.getKey(), OsgiSafeUtils.unwrap((OsgiSafe)event.getValue()), OsgiSafeUtils.unwrap((OsgiSafe)event.getOldValue()));
        }
    }
}

