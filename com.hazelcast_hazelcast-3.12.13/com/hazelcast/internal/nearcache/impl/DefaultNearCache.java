/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl;

import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.internal.adapter.DataStructureAdapter;
import com.hazelcast.internal.nearcache.NearCache;
import com.hazelcast.internal.nearcache.NearCacheRecordStore;
import com.hazelcast.internal.nearcache.impl.store.NearCacheDataRecordStore;
import com.hazelcast.internal.nearcache.impl.store.NearCacheObjectRecordStore;
import com.hazelcast.monitor.NearCacheStats;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.TaskScheduler;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.Preconditions;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultNearCache<K, V>
implements NearCache<K, V> {
    protected final String name;
    protected final TaskScheduler scheduler;
    protected final ClassLoader classLoader;
    protected final NearCacheConfig nearCacheConfig;
    protected final SerializationService serializationService;
    protected ScheduledFuture expirationTaskFuture;
    protected NearCacheRecordStore<K, V> nearCacheRecordStore;
    private final boolean serializeKeys;
    private final HazelcastProperties properties;
    private volatile boolean preloadDone;

    public DefaultNearCache(String name, NearCacheConfig nearCacheConfig, SerializationService serializationService, TaskScheduler scheduler, ClassLoader classLoader, HazelcastProperties properties) {
        this(name, nearCacheConfig, null, serializationService, scheduler, classLoader, properties);
    }

    public DefaultNearCache(String name, NearCacheConfig nearCacheConfig, NearCacheRecordStore<K, V> nearCacheRecordStore, SerializationService serializationService, TaskScheduler scheduler, ClassLoader classLoader, HazelcastProperties properties) {
        this.name = name;
        this.nearCacheConfig = nearCacheConfig;
        this.serializationService = serializationService;
        this.classLoader = classLoader;
        this.scheduler = scheduler;
        this.nearCacheRecordStore = nearCacheRecordStore;
        this.serializeKeys = nearCacheConfig.isSerializeKeys();
        this.properties = properties;
    }

    @Override
    public void initialize() {
        if (this.nearCacheRecordStore == null) {
            this.nearCacheRecordStore = this.createNearCacheRecordStore(this.name, this.nearCacheConfig);
        }
        this.nearCacheRecordStore.initialize();
        this.expirationTaskFuture = this.createAndScheduleExpirationTask();
    }

    protected NearCacheRecordStore<K, V> createNearCacheRecordStore(String name, NearCacheConfig nearCacheConfig) {
        InMemoryFormat inMemoryFormat = nearCacheConfig.getInMemoryFormat();
        if (inMemoryFormat == null) {
            inMemoryFormat = NearCacheConfig.DEFAULT_MEMORY_FORMAT;
        }
        switch (inMemoryFormat) {
            case BINARY: {
                return new NearCacheDataRecordStore(name, nearCacheConfig, this.serializationService, this.classLoader);
            }
            case OBJECT: {
                return new NearCacheObjectRecordStore(name, nearCacheConfig, this.serializationService, this.classLoader);
            }
        }
        throw new IllegalArgumentException("Invalid in memory format: " + (Object)((Object)inMemoryFormat));
    }

    private ScheduledFuture createAndScheduleExpirationTask() {
        if ((long)this.nearCacheConfig.getMaxIdleSeconds() > 0L || (long)this.nearCacheConfig.getTimeToLiveSeconds() > 0L) {
            ExpirationTask expirationTask = new ExpirationTask();
            return expirationTask.schedule(this.scheduler);
        }
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public V get(K key) {
        this.checkKeyFormat(key);
        return this.nearCacheRecordStore.get(key);
    }

    @Override
    public void put(K key, Data keyData, V value, Data valueData) {
        this.checkKeyFormat(key);
        this.nearCacheRecordStore.doEviction(false);
        this.nearCacheRecordStore.put(key, keyData, value, valueData);
    }

    @Override
    public void invalidate(K key) {
        this.checkKeyFormat(key);
        this.nearCacheRecordStore.invalidate(key);
    }

    @Override
    public void clear() {
        this.nearCacheRecordStore.clear();
    }

    @Override
    public void destroy() {
        if (this.expirationTaskFuture != null) {
            this.expirationTaskFuture.cancel(true);
        }
        this.nearCacheRecordStore.destroy();
    }

    @Override
    public NearCacheStats getNearCacheStats() {
        return this.nearCacheRecordStore.getNearCacheStats();
    }

    @Override
    public boolean isSerializeKeys() {
        return this.serializeKeys;
    }

    @Override
    public int size() {
        return this.nearCacheRecordStore.size();
    }

    @Override
    public void preload(DataStructureAdapter<Object, ?> adapter) {
        this.nearCacheRecordStore.loadKeys(adapter);
        this.preloadDone = true;
    }

    @Override
    public void storeKeys() {
        if (this.preloadDone) {
            this.nearCacheRecordStore.storeKeys();
        }
    }

    @Override
    public boolean isPreloadDone() {
        return this.preloadDone;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        if (clazz.isAssignableFrom(this.getClass())) {
            return clazz.cast(this);
        }
        throw new IllegalArgumentException("Unwrapping to " + clazz + " is not supported by this implementation");
    }

    @Override
    public long tryReserveForUpdate(K key, Data keyData) {
        this.nearCacheRecordStore.doEviction(false);
        return this.nearCacheRecordStore.tryReserveForUpdate(key, keyData);
    }

    @Override
    public V tryPublishReserved(K key, V value, long reservationId, boolean deserialize) {
        return this.nearCacheRecordStore.tryPublishReserved(key, value, reservationId, deserialize);
    }

    public NearCacheRecordStore<K, V> getNearCacheRecordStore() {
        return this.nearCacheRecordStore;
    }

    private void checkKeyFormat(K key) {
        if (this.serializeKeys) {
            Preconditions.checkInstanceOf(Data.class, key, "key must be of type Data!");
        } else {
            Preconditions.checkNotInstanceOf(Data.class, key, "key cannot be of type Data!");
        }
    }

    private class ExpirationTask
    implements Runnable {
        private final AtomicBoolean expirationInProgress = new AtomicBoolean(false);

        private ExpirationTask() {
        }

        @Override
        public void run() {
            if (this.expirationInProgress.compareAndSet(false, true)) {
                try {
                    DefaultNearCache.this.nearCacheRecordStore.doExpiration();
                }
                finally {
                    this.expirationInProgress.set(false);
                }
            }
        }

        private ScheduledFuture schedule(TaskScheduler scheduler) {
            return scheduler.scheduleWithRepetition(this, DefaultNearCache.this.properties.getInteger(NearCache.TASK_INITIAL_DELAY_SECONDS), DefaultNearCache.this.properties.getInteger(NearCache.TASK_PERIOD_SECONDS), TimeUnit.SECONDS);
        }
    }
}

