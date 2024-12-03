/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.confluence.cache.ClusterCacheCompactor
 *  com.atlassian.confluence.impl.cache.CacheCompactorSupport
 *  com.atlassian.confluence.impl.metrics.CoreMetrics
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.atlassian.event.api.EventPublisher
 *  io.micrometer.core.instrument.MeterRegistry
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cache.hazelcast;

import com.atlassian.annotations.Internal;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.ManagedCache;
import com.atlassian.confluence.cache.ClusterCacheCompactor;
import com.atlassian.confluence.cache.hazelcast.HazelcastHelper;
import com.atlassian.confluence.impl.cache.CacheCompactorSupport;
import com.atlassian.confluence.impl.metrics.CoreMetrics;
import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.event.api.EventPublisher;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class HazelcastCacheCompactor
extends CacheCompactorSupport
implements ClusterCacheCompactor {
    private static final Logger log = LoggerFactory.getLogger(HazelcastCacheCompactor.class);
    private final CacheManager cacheManager;
    private final HazelcastHelper hazelcastHelper;
    private final EventPublisher eventPublisher;
    private final MeterRegistry micrometerRegistry;

    public HazelcastCacheCompactor(CacheManager cacheManager, HazelcastHelper hazelcastHelper, EventPublisher eventPublisher, MeterRegistry micrometerRegistry) {
        this.cacheManager = Objects.requireNonNull(cacheManager);
        this.hazelcastHelper = Objects.requireNonNull(hazelcastHelper);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.micrometerRegistry = Objects.requireNonNull(micrometerRegistry);
    }

    public void compact() {
        log.info("Starting cache compaction");
        this.cacheManager.getManagedCaches().stream().filter(this::isIMapCache).forEach(this::compact);
        log.info("Finished cache compaction");
    }

    private boolean isIMapCache(ManagedCache cache) {
        return !cache.isLocal() && cache.isFlushable() && cache.isReplicateViaCopy();
    }

    private void compact(ManagedCache cache) {
        boolean thresholdExceeded;
        long ownedEntries = this.hazelcastHelper.getHazelcastMapForCache(cache.getName()).getLocalMapStats().getOwnedEntryCount();
        Integer maxEntries = cache.currentMaxEntries();
        boolean bl = thresholdExceeded = maxEntries != null && ownedEntries > (long)(2 * maxEntries);
        if (thresholdExceeded) {
            log.warn("Cache '{}' has overflown 2x its max size ({}/{}) and will be cleared", new Object[]{cache.getName(), ownedEntries, maxEntries});
            CoreMetrics.HAZELCAST_CACHE_COMPACTION.timer(this.micrometerRegistry, new String[]{"cacheName", cache.getName()}).record(() -> ((ManagedCache)cache).clear());
            this.eventPublisher.publish((Object)new CacheCompactionEvent(cache.getName(), ownedEntries, maxEntries));
        }
    }

    @AsynchronousPreferred
    public static class CacheCompactionEvent {
        private final String cacheName;
        private final long ownedEntries;
        private final int maxEntries;

        public CacheCompactionEvent(String cacheName, long ownedEntries, int maxEntries) {
            this.cacheName = cacheName;
            this.ownedEntries = ownedEntries;
            this.maxEntries = maxEntries;
        }

        public String getCacheName() {
            return this.cacheName;
        }

        public long getOwnedEntries() {
            return this.ownedEntries;
        }

        public int getMaxEntries() {
            return this.maxEntries;
        }
    }
}

