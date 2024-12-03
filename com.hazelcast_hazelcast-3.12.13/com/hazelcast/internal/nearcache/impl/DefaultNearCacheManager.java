/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl;

import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.NearCacheConfigAccessor;
import com.hazelcast.config.NearCachePreloaderConfig;
import com.hazelcast.internal.adapter.DataStructureAdapter;
import com.hazelcast.internal.nearcache.NearCache;
import com.hazelcast.internal.nearcache.NearCacheManager;
import com.hazelcast.internal.nearcache.impl.DefaultNearCache;
import com.hazelcast.monitor.NearCacheStats;
import com.hazelcast.spi.TaskScheduler;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DefaultNearCacheManager
implements NearCacheManager {
    protected final TaskScheduler scheduler;
    protected final ClassLoader classLoader;
    protected final HazelcastProperties properties;
    protected final SerializationService serializationService;
    private final Object mutex = new Object();
    private final Queue<ScheduledFuture> preloadTaskFutures = new ConcurrentLinkedQueue<ScheduledFuture>();
    private final ConcurrentMap<String, NearCache> nearCacheMap = new ConcurrentHashMap<String, NearCache>();
    private volatile ScheduledFuture storageTaskFuture;

    public DefaultNearCacheManager(SerializationService ss, TaskScheduler es, ClassLoader classLoader, HazelcastProperties properties) {
        assert (ss != null);
        assert (es != null);
        this.serializationService = ss;
        this.scheduler = es;
        this.classLoader = classLoader;
        this.properties = properties;
    }

    @Override
    public <K, V> NearCache<K, V> getNearCache(String name) {
        return (NearCache)this.nearCacheMap.get(name);
    }

    @Override
    public <K, V> NearCache<K, V> getOrCreateNearCache(String name, NearCacheConfig nearCacheConfig) {
        return this.getOrCreateNearCache(name, nearCacheConfig, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <K, V> NearCache<K, V> getOrCreateNearCache(String name, NearCacheConfig nearCacheConfig, DataStructureAdapter dataStructureAdapter) {
        NearCache<K, V> nearCache = (NearCache<K, V>)this.nearCacheMap.get(name);
        if (nearCache == null) {
            Object object = this.mutex;
            synchronized (object) {
                nearCache = (NearCache)this.nearCacheMap.get(name);
                if (nearCache == null) {
                    nearCache = this.createNearCache(name, nearCacheConfig);
                    nearCache.initialize();
                    this.nearCacheMap.put(name, nearCache);
                    NearCachePreloaderConfig preloaderConfig = nearCacheConfig.getPreloaderConfig();
                    if (preloaderConfig.isEnabled()) {
                        this.createAndSchedulePreloadTask(nearCache, dataStructureAdapter);
                        this.createAndScheduleStorageTask(preloaderConfig);
                    }
                }
            }
        }
        return nearCache;
    }

    protected <K, V> NearCache<K, V> createNearCache(String name, NearCacheConfig nearCacheConfig) {
        NearCacheConfig copy = NearCacheConfigAccessor.copyWithInitializedDefaultMaxSizeForOnHeapMaps(nearCacheConfig);
        return new DefaultNearCache(name, copy, this.serializationService, this.scheduler, this.classLoader, this.properties);
    }

    @Override
    public Collection<NearCache> listAllNearCaches() {
        return this.nearCacheMap.values();
    }

    @Override
    public boolean clearNearCache(String name) {
        NearCache nearCache = (NearCache)this.nearCacheMap.get(name);
        if (nearCache != null) {
            nearCache.clear();
        }
        return nearCache != null;
    }

    @Override
    public void clearAllNearCaches() {
        for (NearCache nearCache : this.nearCacheMap.values()) {
            nearCache.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean destroyNearCache(String name) {
        NearCache nearCache = (NearCache)this.nearCacheMap.get(name);
        if (nearCache != null) {
            Object object = this.mutex;
            synchronized (object) {
                nearCache = (NearCache)this.nearCacheMap.remove(name);
                if (nearCache != null) {
                    nearCache.destroy();
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public void destroyAllNearCaches() {
        for (NearCache nearCache : new HashSet(this.nearCacheMap.values())) {
            this.destroyNearCache(nearCache.getName());
        }
        for (ScheduledFuture preloadTaskFuture : this.preloadTaskFutures) {
            preloadTaskFuture.cancel(true);
        }
        if (this.storageTaskFuture != null) {
            this.storageTaskFuture.cancel(true);
        }
    }

    private void createAndSchedulePreloadTask(NearCache nearCache, DataStructureAdapter adapter) {
        if (adapter != null) {
            PreloadTask preloadTask = new PreloadTask(nearCache, adapter);
            ScheduledFuture<?> scheduledFuture = this.scheduler.schedule(preloadTask, 3L, TimeUnit.SECONDS);
            preloadTask.scheduledFuture = scheduledFuture;
            this.preloadTaskFutures.add(scheduledFuture);
        }
    }

    private void createAndScheduleStorageTask(NearCachePreloaderConfig preloaderConfig) {
        if (this.storageTaskFuture == null) {
            StorageTask storageTask = new StorageTask(preloaderConfig);
            this.storageTaskFuture = this.scheduler.scheduleWithRepetition(storageTask, 0L, 1L, TimeUnit.SECONDS);
        }
    }

    protected SerializationService getSerializationService() {
        return this.serializationService;
    }

    protected TaskScheduler getScheduler() {
        return this.scheduler;
    }

    protected ClassLoader getClassLoader() {
        return this.classLoader;
    }

    private class StorageTask
    implements Runnable {
        private final long started = System.currentTimeMillis();
        private final NearCachePreloaderConfig preloaderConfig;

        public StorageTask(NearCachePreloaderConfig preloaderConfig) {
            this.preloaderConfig = preloaderConfig;
        }

        @Override
        public void run() {
            long now = System.currentTimeMillis();
            for (NearCache nearCache : DefaultNearCacheManager.this.nearCacheMap.values()) {
                if (!this.isScheduled(nearCache, now)) continue;
                nearCache.storeKeys();
            }
        }

        private boolean isScheduled(NearCache nearCache, long now) {
            long elapsedSeconds;
            long runningSeconds;
            NearCacheStats nearCacheStats = nearCache.getNearCacheStats();
            return !(nearCacheStats.getLastPersistenceTime() == 0L ? (runningSeconds = TimeUnit.MILLISECONDS.toSeconds(now - this.started)) < (long)this.preloaderConfig.getStoreInitialDelaySeconds() : (elapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(now - nearCacheStats.getLastPersistenceTime())) < (long)this.preloaderConfig.getStoreIntervalSeconds());
        }
    }

    private class PreloadTask
    implements Runnable {
        private final NearCache nearCache;
        private final DataStructureAdapter adapter;
        private volatile ScheduledFuture<?> scheduledFuture;

        PreloadTask(NearCache nearCache, DataStructureAdapter adapter) {
            this.nearCache = nearCache;
            this.adapter = adapter;
        }

        @Override
        public void run() {
            this.nearCache.preload(this.adapter);
            ScheduledFuture<?> future = this.scheduledFuture;
            if (future != null) {
                DefaultNearCacheManager.this.preloadTaskFutures.remove(future);
            }
        }
    }
}

