/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheEntryListener
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.cache.hazelcast.asyncinvalidation.AsyncInvalidationCache
 *  com.atlassian.cache.hazelcast.asyncinvalidation.AsyncReplicationCache
 *  com.atlassian.cache.hazelcast.asyncinvalidation.CacheInvalidator
 *  com.atlassian.cache.hazelcast.asyncinvalidation.CacheInvalidatorFactory
 *  com.atlassian.cache.hazelcast.asyncinvalidation.CacheReplicator
 *  com.atlassian.cache.hazelcast.asyncinvalidation.Observability
 *  com.atlassian.confluence.cache.DefaultConfluenceCache
 *  com.atlassian.confluence.core.ConfluenceSystemProperties
 *  com.atlassian.confluence.impl.metrics.CoreMetrics
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.tenancy.api.event.TenantArrivedEvent
 *  com.hazelcast.core.HazelcastInstance
 *  io.atlassian.util.concurrent.LazyReference
 *  io.micrometer.core.instrument.MeterRegistry
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cache.hazelcast;

import com.atlassian.annotations.Internal;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheEntryListener;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.hazelcast.asyncinvalidation.AsyncInvalidationCache;
import com.atlassian.cache.hazelcast.asyncinvalidation.AsyncReplicationCache;
import com.atlassian.cache.hazelcast.asyncinvalidation.CacheInvalidator;
import com.atlassian.cache.hazelcast.asyncinvalidation.CacheInvalidatorFactory;
import com.atlassian.cache.hazelcast.asyncinvalidation.CacheReplicator;
import com.atlassian.cache.hazelcast.asyncinvalidation.Observability;
import com.atlassian.confluence.cache.DefaultConfluenceCache;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.impl.metrics.CoreMetrics;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.tenancy.api.event.TenantArrivedEvent;
import com.hazelcast.core.HazelcastInstance;
import io.atlassian.util.concurrent.LazyReference;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public final class AsyncInvalidationCacheFactory {
    private static final Logger log = LoggerFactory.getLogger(AsyncInvalidationCacheFactory.class);
    private static final Duration SEQUENCE_SNAPSHOT_SCHEDULE_INTERVAL = Duration.ofMinutes(1L);
    private static final String SEQUENCE_SNAPSHOT_JOB_KEY = AsyncInvalidationCache.class.getSimpleName() + ".sequenceSnapshot";
    private final LazyReference<CacheInvalidatorFactory> cacheInvalidatorFactoryRef;
    private final EventPublisher eventPublisher;
    private final SchedulerService schedulerService;
    private final CacheFactory localCacheFactory;
    private final MeterRegistry micrometer;
    private final DarkFeaturesManager darkFeaturesManager;

    public AsyncInvalidationCacheFactory(final HazelcastInstance hazelcastInstance, EventPublisher eventPublisher, SchedulerService schedulerService, CacheFactory localCacheFactory, MeterRegistry micrometer, DarkFeaturesManager darkFeaturesManager) {
        this.cacheInvalidatorFactoryRef = new LazyReference<CacheInvalidatorFactory>(){

            protected CacheInvalidatorFactory create() {
                return AsyncInvalidationCacheFactory.this.createCacheInvalidatorFactory(hazelcastInstance);
            }
        };
        this.eventPublisher = eventPublisher;
        this.schedulerService = schedulerService;
        this.localCacheFactory = localCacheFactory;
        this.micrometer = micrometer;
        this.darkFeaturesManager = darkFeaturesManager;
    }

    private CacheInvalidatorFactory createCacheInvalidatorFactory(HazelcastInstance hazelcastInstance) {
        log.debug("Creating new CacheInvalidatorFactory");
        return CacheInvalidatorFactory.create((HazelcastInstance)hazelcastInstance, AsyncInvalidationCacheFactory::createTopicName, (Observability)this.getObservability());
    }

    private static String createTopicName(String name) {
        return AsyncInvalidationCache.class.getSimpleName() + "." + name;
    }

    private boolean isInitialized() {
        return this.cacheInvalidatorFactoryRef.isInitialized();
    }

    private CacheInvalidatorFactory getCacheInvalidatorFactory() {
        return (CacheInvalidatorFactory)this.cacheInvalidatorFactoryRef.get();
    }

    @PostConstruct
    void registerSequenceSnapshotJobRunner() {
        this.schedulerService.registerJobRunner(AsyncInvalidationCacheFactory.jobRunnerKey(), request -> {
            if (this.isInitialized()) {
                log.debug("Publishing sequence snapshot");
                CoreMetrics.ASYNC_INVALIDATION_CACHE_PUBLISH_SEQUENCE_SNAPSHOT.counter(this.micrometer, new String[0]).increment();
                this.getCacheInvalidatorFactory().publishSequenceSnapshot();
                return JobRunnerResponse.success();
            }
            return JobRunnerResponse.aborted((String)"Cache invalidator factory hasn't been initialised");
        });
    }

    @PostConstruct
    void registerForTenantArrived() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    void unregisterFromEventListener() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onTenantArrived(TenantArrivedEvent event) throws SchedulerServiceException {
        if (this.isInitialized()) {
            this.scheduleJobRunner();
        }
    }

    private void scheduleJobRunner() throws SchedulerServiceException {
        log.info("Registering sequence snapshot job to run every {}", (Object)SEQUENCE_SNAPSHOT_SCHEDULE_INTERVAL);
        this.schedulerService.scheduleJob(JobId.of((String)SEQUENCE_SNAPSHOT_JOB_KEY), JobConfig.forJobRunnerKey((JobRunnerKey)AsyncInvalidationCacheFactory.jobRunnerKey()).withRunMode(RunMode.RUN_LOCALLY).withSchedule(Schedule.forInterval((long)SEQUENCE_SNAPSHOT_SCHEDULE_INTERVAL.toMillis(), null)));
    }

    private static JobRunnerKey jobRunnerKey() {
        return JobRunnerKey.of((String)SEQUENCE_SNAPSHOT_JOB_KEY);
    }

    public <K, V> Cache<K, V> createInvalidationCache(String cacheName, CacheLoader loader, CacheSettings settings) {
        Cache localCache = this.localCacheFactory.getCache(cacheName, loader, settings);
        ManagedCache localManagedCache = (ManagedCache)localCache;
        CacheInvalidator cacheInvalidator = this.getCacheInvalidatorFactory().createCacheInvalidator(localCache, localManagedCache);
        AsyncInvalidationCache cache = new AsyncInvalidationCache(localCache, localManagedCache, cacheInvalidator);
        if (ConfluenceSystemProperties.isDevMode()) {
            return new OperationWarningCacheWrapper(cache);
        }
        return cache;
    }

    public <K, V> Cache<K, V> createReplicatedCache(String cacheName, CacheLoader loader, CacheSettings settings) {
        Cache localCache = this.localCacheFactory.getCache(cacheName, loader, settings);
        ManagedCache localManagedCache = (ManagedCache)localCache;
        CacheInvalidator cacheInvalidator = this.getCacheInvalidatorFactory().createCacheInvalidator(localCache, localManagedCache);
        CacheReplicator cacheReplicator = this.getCacheInvalidatorFactory().createCacheReplicator(localCache);
        return new AsyncReplicationCache(localCache, (ManagedCache)localCache, cacheInvalidator, cacheReplicator);
    }

    public boolean isReplicatedCacheSupported(String cacheName) {
        return this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled(AsyncReplicationCache.class.getSimpleName());
    }

    private Observability getObservability() {
        return new Observability(){

            public void sequenceSnapshotInconsistent(ManagedCache cache) {
                log.warn("Cache flushed due to inconsistent sequence snapshot: '{}'", (Object)cache.getName());
                CoreMetrics.ASYNC_INVALIDATION_CACHE_SEQUENCE_SNAPSHOT_INCONSISTENT.counter(AsyncInvalidationCacheFactory.this.micrometer, new String[]{"cacheName", cache.getName()}).increment();
                AsyncInvalidationCacheFactory.this.eventPublisher.publish((Object)new CacheSequenceSnapshotInconsistentEvent(cache.getName()));
            }

            public void cacheInvalidationOutOfSequence(ManagedCache cache) {
                log.warn("Cache flushed due to out-of-sequence invalidation: '{}'", (Object)cache.getName());
                CoreMetrics.ASYNC_INVALIDATION_CACHE_INVALIDATION_OUT_OF_SEQUENCE.counter(AsyncInvalidationCacheFactory.this.micrometer, new String[]{"cacheName", cache.getName()}).increment();
                AsyncInvalidationCacheFactory.this.eventPublisher.publish((Object)new CacheInvalidationOutOfSequenceEvent(cache.getName()));
            }
        };
    }

    private static class OperationWarningCacheWrapper<K, V>
    extends DefaultConfluenceCache<K, V> {
        public OperationWarningCacheWrapper(Cache<K, V> delegate) {
            super(delegate);
        }

        public void put(K key, V value) {
            log.warn("The put operation should not be used with a replicate-via-invalidation cache. Use a CacheLoader or the get-with-loader method instead. Cache is '{}'", (Object)this.getName());
            super.put(key, value);
        }

        public V putIfAbsent(K key, V value) {
            log.warn("The putIfAbsent operation should not be used with a replicate-via-invalidation cache. Use a CacheLoader or the get-with-loader method instead. Cache is '{}'", (Object)this.getName());
            return (V)super.putIfAbsent(key, value);
        }

        public boolean remove(K key, V value) {
            log.warn("The conditional remove operation should not be used with a replicate-via-invalidation cache. Use a CacheLoader or the get-with-loader method instead. Cache is '{}'", (Object)this.getName());
            return super.remove(key, value);
        }

        public boolean replace(K key, V oldValue, V newValue) {
            log.warn("The replace operation should not be used with a replicate-via-invalidation cache. Use a CacheLoader or the get-with-loader method instead. Cache is '{}'", (Object)this.getName());
            return super.replace(key, oldValue, newValue);
        }

        public void addListener(CacheEntryListener<K, V> listener, boolean required) {
            log.warn("Cache listeners should not be used with a replicate-via-invalidation cache. They may not function correctly. Cache is '{}'", (Object)this.getName());
            super.addListener(listener, required);
        }
    }

    @AsynchronousPreferred
    public static class CacheSequenceSnapshotInconsistentEvent {
        private final String cacheName;

        public CacheSequenceSnapshotInconsistentEvent(String cacheName) {
            this.cacheName = cacheName;
        }

        public String getCacheName() {
            return this.cacheName;
        }
    }

    @AsynchronousPreferred
    public static class CacheInvalidationOutOfSequenceEvent {
        private final String cacheName;

        public CacheInvalidationOutOfSequenceEvent(String cacheName) {
            this.cacheName = cacheName;
        }

        public String getCacheName() {
            return this.cacheName;
        }
    }
}

