/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.failurecache.Cache
 *  com.atlassian.failurecache.CacheFactory
 *  com.atlassian.failurecache.CacheLoader
 *  com.atlassian.failurecache.Cacheable
 *  com.atlassian.failurecache.Refreshable
 *  com.atlassian.failurecache.failures.ExponentialBackOffFailureCache$Builder
 *  com.atlassian.plugins.capabilities.api.LinkedApplicationCapabilities
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.util.concurrent.Futures
 *  com.google.common.util.concurrent.ListenableFuture
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.plugins.navlink.consumer.menu.services;

import com.atlassian.failurecache.Cache;
import com.atlassian.failurecache.CacheFactory;
import com.atlassian.failurecache.CacheLoader;
import com.atlassian.failurecache.Cacheable;
import com.atlassian.failurecache.Refreshable;
import com.atlassian.failurecache.failures.ExponentialBackOffFailureCache;
import com.atlassian.plugins.capabilities.api.LinkedApplicationCapabilities;
import com.atlassian.plugins.navlink.consumer.menu.services.CapabilitiesCacheLoader;
import com.atlassian.plugins.navlink.consumer.menu.services.RemoteApplications;
import com.atlassian.plugins.navlink.producer.capabilities.CapabilityKey;
import com.atlassian.plugins.navlink.producer.capabilities.RemoteApplicationWithCapabilities;
import com.atlassian.plugins.navlink.util.executor.DaemonExecutorService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class CachingLinkedApplicationCapabilitiesImpl
implements Cacheable,
InitializingBean,
Runnable,
Refreshable,
RemoteApplications,
LinkedApplicationCapabilities {
    private static final long INITIAL_DELAY_IN_SECONDS = Long.getLong("navlink.capabilitiescache.initialdelay", 30L);
    private static final long DELAY_IN_SECONDS = Long.getLong("navlink.capabilitiescache.delay", 10L);
    private static final double DEFAULT_BACK_OFF_RATE = Integer.getInteger("navlink.failurecache.backoff", 2).intValue();
    private static final long DEFAULT_INITIAL_EXPIRY_MS = Long.getLong("navlink.failurecache.initialexpiryMs", TimeUnit.SECONDS.toMillis(1L));
    private static final int DEFAULT_MAX_ENTRIES = Integer.getInteger("navlink.failurecache.maxEntries", 1000);
    private static final long DEFAULT_MAX_EXPIRY_MS = Long.getLong("navlink.failurecache.maxExpiryMs", TimeUnit.MINUTES.toMillis(1L));
    private static final Logger logger = LoggerFactory.getLogger(CachingLinkedApplicationCapabilitiesImpl.class);
    private final Cache<RemoteApplicationWithCapabilities> cache;
    private final DaemonExecutorService executorService;

    public CachingLinkedApplicationCapabilitiesImpl(DaemonExecutorService executorService, CapabilitiesCacheLoader capabilitiesCacheLoader, CacheFactory cacheFactory) {
        this(executorService, (Cache<RemoteApplicationWithCapabilities>)cacheFactory.createExpirationDateBasedCache((CacheLoader)capabilitiesCacheLoader, new ExponentialBackOffFailureCache.Builder().backOffRate(DEFAULT_BACK_OFF_RATE).initialExpiry(DEFAULT_INITIAL_EXPIRY_MS, TimeUnit.MILLISECONDS).maxEntries(DEFAULT_MAX_ENTRIES).maxExpiry(DEFAULT_MAX_EXPIRY_MS, TimeUnit.MILLISECONDS).build()));
    }

    @VisibleForTesting
    CachingLinkedApplicationCapabilitiesImpl(DaemonExecutorService executorService, Cache<RemoteApplicationWithCapabilities> cache) {
        this.executorService = executorService;
        this.cache = cache;
    }

    @Override
    public Set<RemoteApplicationWithCapabilities> capableOf(CapabilityKey capabilityKey) {
        return this.capableOf(capabilityKey.getKey());
    }

    @Override
    public Set<RemoteApplicationWithCapabilities> capableOf(String capabilityKey) {
        Objects.requireNonNull(capabilityKey, "capabilityKey");
        return StreamSupport.stream(this.cache.getValues().spliterator(), false).filter(this.filterBy(capabilityKey)).collect(Collectors.toSet());
    }

    @Override
    public void run() {
        this.refreshCache();
    }

    public int getCachePriority() {
        return 500;
    }

    public void clearCache() {
        this.cache.clear();
    }

    public ListenableFuture<?> refreshCache() {
        try {
            return this.cache.refresh();
        }
        catch (RuntimeException e) {
            logger.debug("Failed to refresh linked application capabilities cache", (Throwable)e);
            return Futures.immediateFailedFuture((Throwable)e);
        }
    }

    public void afterPropertiesSet() throws Exception {
        this.executorService.scheduleWithFixedDelay(this, INITIAL_DELAY_IN_SECONDS, DELAY_IN_SECONDS, TimeUnit.SECONDS);
    }

    private Predicate<RemoteApplicationWithCapabilities> filterBy(String capabilityKey) {
        return application -> application != null && application.hasCapability(capabilityKey);
    }
}

