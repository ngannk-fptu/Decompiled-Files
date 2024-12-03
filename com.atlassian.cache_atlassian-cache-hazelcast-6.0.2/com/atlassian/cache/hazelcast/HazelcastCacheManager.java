/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CacheSettingsDefaultsProvider
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.cache.Supplier
 *  com.atlassian.cache.impl.AbstractCacheManager
 *  com.atlassian.cache.impl.ReferenceKey
 *  com.atlassian.cache.impl.StrongSupplier
 *  com.atlassian.cache.impl.WeakSupplier
 *  com.atlassian.cache.impl.metrics.CacheManagerMetricEmitter
 *  com.atlassian.cache.impl.metrics.InstrumentedCache
 *  com.atlassian.cache.impl.metrics.InstrumentedCachedReference
 *  com.google.common.base.Preconditions
 *  com.hazelcast.config.Config
 *  com.hazelcast.config.ConfigurationException
 *  com.hazelcast.config.MapConfig
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.hazelcast.core.ITopic
 *  com.hazelcast.core.MembershipAdapter
 *  com.hazelcast.core.MembershipEvent
 *  com.hazelcast.core.MembershipListener
 *  com.hazelcast.map.impl.MapContainer
 *  com.hazelcast.map.impl.MapService
 *  com.hazelcast.map.impl.MapServiceContext
 *  com.hazelcast.map.impl.proxy.MapProxyImpl
 *  com.hazelcast.map.listener.EntryAddedListener
 *  com.hazelcast.map.listener.EntryUpdatedListener
 *  com.hazelcast.map.listener.MapListener
 *  io.atlassian.util.concurrent.ManagedLock
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.cache.hazelcast;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CacheSettingsDefaultsProvider;
import com.atlassian.cache.CachedReference;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.Supplier;
import com.atlassian.cache.hazelcast.CacheVersion;
import com.atlassian.cache.hazelcast.HazelcastAsyncHybridCache;
import com.atlassian.cache.hazelcast.HazelcastAsyncHybridCachedReference;
import com.atlassian.cache.hazelcast.HazelcastCache;
import com.atlassian.cache.hazelcast.HazelcastCachedReference;
import com.atlassian.cache.hazelcast.HazelcastHybridCache;
import com.atlassian.cache.hazelcast.HazelcastHybridCachedReference;
import com.atlassian.cache.hazelcast.HazelcastMapConfigConfigurator;
import com.atlassian.cache.hazelcast.HazelcastNameFactory;
import com.atlassian.cache.hazelcast.LegacyPrefixedNameFactory;
import com.atlassian.cache.impl.AbstractCacheManager;
import com.atlassian.cache.impl.ReferenceKey;
import com.atlassian.cache.impl.StrongSupplier;
import com.atlassian.cache.impl.WeakSupplier;
import com.atlassian.cache.impl.metrics.CacheManagerMetricEmitter;
import com.atlassian.cache.impl.metrics.InstrumentedCache;
import com.atlassian.cache.impl.metrics.InstrumentedCachedReference;
import com.google.common.base.Preconditions;
import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigurationException;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MembershipAdapter;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.proxy.MapProxyImpl;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.map.listener.MapListener;
import io.atlassian.util.concurrent.ManagedLock;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class HazelcastCacheManager
extends AbstractCacheManager {
    private static final Logger log = LoggerFactory.getLogger(HazelcastCacheManager.class);
    protected static final String SETTINGS_MAP_NAME = "atlassian-cache.settings";
    private final HazelcastInstance hazelcast;
    private final CacheFactory localCacheFactory;
    private final IMap<String, CacheSettings> mapSettings;
    private final String mapSettingsUpdatedListenerId;
    private final String mapSettingsAddedListenerId;
    private final String membershipListenerId;
    private final HazelcastNameFactory nameFactory;
    private MapServiceContext mapServiceContext;

    public HazelcastCacheManager(HazelcastInstance hazelcast, CacheFactory localCacheFactory, CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider) {
        this(hazelcast, localCacheFactory, cacheSettingsDefaultsProvider, new LegacyPrefixedNameFactory());
    }

    public HazelcastCacheManager(HazelcastInstance hazelcast, CacheFactory localCacheFactory, CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider, HazelcastNameFactory nameFactory) {
        this(hazelcast, localCacheFactory, cacheSettingsDefaultsProvider, nameFactory, new CacheManagerMetricEmitter());
    }

    @VisibleForTesting
    HazelcastCacheManager(HazelcastInstance hazelcast, CacheFactory localCacheFactory, CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider, HazelcastNameFactory nameFactory, CacheManagerMetricEmitter metricEmitter) {
        super(cacheSettingsDefaultsProvider, metricEmitter);
        this.hazelcast = Objects.requireNonNull(hazelcast);
        this.localCacheFactory = Objects.requireNonNull(localCacheFactory);
        this.nameFactory = Objects.requireNonNull(nameFactory);
        this.mapSettings = hazelcast.getMap(SETTINGS_MAP_NAME);
        this.mapSettingsUpdatedListenerId = this.mapSettings.addEntryListener((MapListener)((EntryUpdatedListener)event -> this.reconfigureMap((String)event.getKey(), (CacheSettings)event.getValue())), true);
        this.mapSettingsAddedListenerId = this.mapSettings.addEntryListener((MapListener)((EntryAddedListener)event -> this.configureMap((String)event.getKey(), (CacheSettings)event.getValue())), true);
        this.membershipListenerId = hazelcast.getCluster().addMembershipListener((MembershipListener)new MembershipAdapter(){

            public void memberAdded(MembershipEvent membershipEvent) {
                HazelcastCacheManager.this.maybeUpdateMapContainers();
            }
        });
    }

    public HazelcastInstance getHazelcastInstance() {
        return this.hazelcast;
    }

    @PostConstruct
    public void init() {
        this.maybeUpdateMapContainers();
    }

    protected <K, V> ManagedCache createComputingCache(@Nonnull String name, @Nonnull CacheSettings settings, CacheLoader<K, V> loader) {
        this.checkSettingsAreCompatible(name, settings);
        return (ManagedCache)((ManagedLock)this.cacheCreationLocks.apply(name)).withLock((java.util.function.Supplier)((Supplier)() -> {
            ManagedCache cache = null;
            if (loader == null) {
                java.util.function.Supplier cacheSupplier = (java.util.function.Supplier)this.caches.get(name);
                ManagedCache managedCache = cache = cacheSupplier == null ? null : (ManagedCache)cacheSupplier.get();
            }
            if (loader != null || cache == null) {
                cache = (ManagedCache)this.doCreateCache(name, loader, settings);
                this.caches.put(name, new WeakSupplier((Object)cache));
            }
            return cache;
        }));
    }

    protected ManagedCache createSimpleCache(@Nonnull String name, @Nonnull CacheSettings settings) {
        this.checkSettingsAreCompatible(name, settings);
        ManagedCache existing = this.getManagedCache(name);
        if (existing != null) {
            return existing;
        }
        return (ManagedCache)((ManagedLock)this.cacheCreationLocks.apply(name)).withLock((java.util.function.Supplier)((Supplier)() -> {
            if (!this.caches.containsKey(name)) {
                this.caches.put(name, new StrongSupplier((Object)((ManagedCache)this.doCreateCache(name, null, settings))));
            }
            return (ManagedCache)((java.util.function.Supplier)this.caches.get(name)).get();
        }));
    }

    @PreDestroy
    public void destroy() {
        this.mapSettings.removeEntryListener(this.mapSettingsAddedListenerId);
        this.mapSettings.removeEntryListener(this.mapSettingsUpdatedListenerId);
        this.hazelcast.getCluster().removeMembershipListener(this.membershipListenerId);
    }

    @Nonnull
    public <V> CachedReference<V> getCachedReference(@Nonnull String name, @Nonnull Supplier<V> supplier, @Nonnull CacheSettings settings) {
        CacheSettings mergedSettings = this.mergeSettings(name, settings);
        this.checkSettingsAreCompatible(name, mergedSettings);
        return (CachedReference)((ManagedLock)this.cacheCreationLocks.apply(name)).withLock((java.util.function.Supplier)((Supplier)() -> {
            ManagedCache cache = (ManagedCache)this.doCreateCachedReference(name, supplier, mergedSettings);
            this.caches.put(name, new WeakSupplier((Object)cache));
            return (CachedReference)cache;
        }));
    }

    protected void checkSettingsAreCompatible(String name, CacheSettings settings) {
    }

    public boolean updateCacheSettings(@Nonnull String mapName, @Nonnull CacheSettings newSettings) {
        try {
            MapConfig mapConfig = this.getMapConfig(mapName);
            if (mapConfig == null || !Objects.equals(mapConfig.getName(), mapName)) {
                return false;
            }
            boolean result = this.reconfigureMap(mapName, newSettings);
            if (result) {
                this.mapSettings.put((Object)mapName, (Object)this.asSerializable(newSettings));
            }
            return result;
        }
        catch (ConfigurationException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Updating configuration of {} failed: ", (Object)mapName, (Object)exception);
            } else {
                log.warn("Updating cache settings on {} is not possible", (Object)mapName);
            }
            return false;
        }
    }

    protected <K, V> Cache<K, V> createAsyncHybridCache(String name, CacheLoader<K, V> loader, CacheSettings settings) {
        String topicName = this.nameFactory.getCacheInvalidationTopicName(name);
        ITopic topic = this.hazelcast.getTopic(topicName);
        return InstrumentedCache.wrap(new HazelcastAsyncHybridCache<K, V>(name, this.localCacheFactory, topic, loader, this, settings));
    }

    protected <V> CachedReference<V> createAsyncHybridCachedReference(String name, Supplier<V> supplier, CacheSettings settings) {
        String topicName = this.nameFactory.getCachedReferenceInvalidationTopicName(name);
        ITopic topic = this.hazelcast.getTopic(topicName);
        return InstrumentedCachedReference.wrap(new HazelcastAsyncHybridCachedReference<V>(name, this.localCacheFactory, (ITopic<ReferenceKey>)topic, supplier, this, settings));
    }

    protected <K, V> Cache<K, V> createDistributedCache(String name, CacheLoader<K, V> loader, CacheSettings settings) {
        String mapName = this.nameFactory.getCacheIMapName(name);
        String counterName = this.nameFactory.getCacheVersionCounterName(name);
        this.configureMap(mapName, settings);
        IMap map = this.hazelcast.getMap(mapName);
        CacheVersion cacheVersion = new CacheVersion(this.hazelcast.getAtomicLong(counterName));
        return InstrumentedCache.wrap(new HazelcastCache<K, V>(name, map, loader, cacheVersion, this));
    }

    protected <V> CachedReference<V> createDistributedCachedReference(String name, Supplier<V> supplier, CacheSettings settings) {
        CacheSettings overriddenSettings = ((CacheSettings)Preconditions.checkNotNull((Object)settings, (Object)"settings")).override(new CacheSettingsBuilder().flushable().maxEntries(1000).build());
        String mapName = this.nameFactory.getCachedReferenceIMapName(name);
        this.configureMap(mapName, overriddenSettings);
        IMap map = this.hazelcast.getMap(mapName);
        return InstrumentedCachedReference.wrap(new HazelcastCachedReference<V>(name, map, supplier, this));
    }

    protected <K, V> Cache<K, V> createHybridCache(String name, CacheLoader<K, V> loader, CacheSettings settings) {
        String mapName = this.nameFactory.getCacheIMapName(name);
        this.configureMap(mapName, settings);
        IMap map = this.hazelcast.getMap(mapName);
        return InstrumentedCache.wrap(new HazelcastHybridCache<K, V>(name, this.localCacheFactory, map, loader, this));
    }

    protected <V> CachedReference<V> createHybridCachedReference(String name, Supplier<V> supplier, CacheSettings settings) {
        String mapName = this.nameFactory.getCachedReferenceIMapName(name);
        this.configureMap(mapName, settings);
        IMap map = this.hazelcast.getMap(mapName);
        return InstrumentedCachedReference.wrap(new HazelcastHybridCachedReference<V>(name, this.localCacheFactory, (IMap<ReferenceKey, Long>)map, supplier, this));
    }

    private boolean configureMap(String mapName, CacheSettings settings) {
        Config config = this.hazelcast.getConfig();
        MapConfig mapConfig = config.findMapConfig(mapName);
        if (Objects.equals(mapConfig.getName(), mapName)) {
            log.debug("Using existing cache configuration for cache {}", (Object)mapName);
            this.mapSettings.computeIfAbsent((Object)mapName, key -> this.asSerializable(settings));
        } else {
            mapConfig = this.convertAndStoreMapConfig(mapName, settings, config, mapConfig);
            this.mapSettings.putIfAbsent((Object)mapName, (Object)this.asSerializable(settings));
        }
        return this.updateMapContainer(mapName, mapConfig);
    }

    private boolean reconfigureMap(String mapName, CacheSettings newSettings) {
        Config config = this.hazelcast.getConfig();
        MapConfig baseConfig = this.getMapConfig(mapName);
        MapConfig mapConfig = this.convertAndStoreMapConfig(mapName, newSettings, config, baseConfig);
        return this.updateMapContainer(mapName, mapConfig);
    }

    private MapConfig convertAndStoreMapConfig(String mapName, CacheSettings newSettings, Config config, MapConfig baseConfig) {
        MapConfig newConfig = new MapConfig(baseConfig);
        newConfig.setName(mapName);
        newConfig.setStatisticsEnabled(true);
        HazelcastMapConfigConfigurator.configureMapConfig(newSettings, newConfig, this.hazelcast.getPartitionService().getPartitions().size());
        MapConfig oldConfig = this.getMapConfig(mapName);
        if (oldConfig == null || !Objects.equals(oldConfig.getName(), mapName)) {
            config.addMapConfig(newConfig);
        }
        return newConfig;
    }

    private CacheSettings asSerializable(CacheSettings settings) {
        if (settings instanceof Serializable) {
            return settings;
        }
        return new CacheSettingsBuilder(settings).build();
    }

    private <K, V> Cache<K, V> doCreateCache(String name, CacheLoader<K, V> loader, CacheSettings settings) {
        if (settings.getLocal(false)) {
            return this.localCacheFactory.getCache(name, loader, settings);
        }
        if (settings.getReplicateViaCopy(true)) {
            return this.createDistributedCache(name, loader, settings);
        }
        if (settings.getReplicateAsynchronously(true)) {
            return this.createAsyncHybridCache(name, loader, settings);
        }
        return this.createHybridCache(name, loader, settings);
    }

    private <V> CachedReference<V> doCreateCachedReference(String name, Supplier<V> supplier, CacheSettings settings) {
        if (settings.getLocal(false)) {
            return this.localCacheFactory.getCachedReference(name, supplier, settings);
        }
        if (settings.getReplicateViaCopy(true)) {
            return this.createDistributedCachedReference(name, supplier, settings);
        }
        if (settings.getReplicateAsynchronously(true)) {
            return this.createAsyncHybridCachedReference(name, supplier, settings);
        }
        return this.createHybridCachedReference(name, supplier, settings);
    }

    private MapContainer getMapContainer(@Nonnull String name) {
        if (this.mapServiceContext == null) {
            MapProxyImpl proxy = (MapProxyImpl)this.hazelcast.getDistributedObject("hz:impl:mapService", SETTINGS_MAP_NAME);
            this.mapServiceContext = ((MapService)proxy.getService()).getMapServiceContext();
        }
        return this.mapServiceContext.getMapContainer(name);
    }

    private boolean updateMapContainer(String mapName, MapConfig config) {
        MapContainer container = this.getMapContainer(mapName);
        if (container == null) {
            log.debug("Map Container not found for map {}", (Object)mapName);
            return false;
        }
        container.setMapConfig(config);
        container.initEvictor();
        return true;
    }

    private void maybeUpdateMapContainers() {
        for (Map.Entry entry : this.mapSettings.entrySet()) {
            this.configureMap((String)entry.getKey(), (CacheSettings)entry.getValue());
        }
    }

    @Nullable
    MapConfig getMapConfig(@Nonnull String mapName) {
        MapContainer mapContainer = this.getMapContainer(mapName);
        MapConfig mapConfig = mapContainer.getMapConfig();
        if (!mapConfig.getName().equals(mapName)) {
            return null;
        }
        return mapConfig;
    }

    @Nullable
    CacheSettings getCacheSettings(@Nonnull String mapName) {
        return (CacheSettings)this.mapSettings.get((Object)mapName);
    }
}

