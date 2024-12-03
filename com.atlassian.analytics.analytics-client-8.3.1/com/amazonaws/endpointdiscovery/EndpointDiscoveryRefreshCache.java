/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.endpointdiscovery;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.cache.CacheLoader;
import com.amazonaws.endpointdiscovery.DaemonThreadFactory;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SdkInternalApi
public abstract class EndpointDiscoveryRefreshCache<K> {
    private static final Log log = LogFactory.getLog(EndpointDiscoveryRefreshCache.class);
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(DaemonThreadFactory.INSTANCE);
    private final CacheLoader<String, Map<String, String>> cacheLoader;
    protected final Map<String, URI> cache = new ConcurrentHashMap<String, URI>();

    public EndpointDiscoveryRefreshCache(CacheLoader cacheLoader) {
        this.cacheLoader = cacheLoader;
    }

    public abstract URI get(K var1, boolean var2, URI var3);

    public abstract URI put(String var1, Map<String, String> var2, URI var3);

    public void evict(String key) {
        this.cache.remove(key);
    }

    public URI discoverEndpoint(String key, boolean required, URI defaultEndpoint) {
        if (required) {
            try {
                return this.put(key, this.cacheLoader.load(key), defaultEndpoint);
            }
            catch (Exception e) {
                return defaultEndpoint;
            }
        }
        this.loadAndScheduleRefresh(key, 1L, defaultEndpoint);
        return defaultEndpoint;
    }

    public ScheduledFuture<URI> loadAndScheduleRefresh(final String key, long refreshPeriod, final URI defaultEndpoint) {
        return this.executorService.schedule(new Callable<URI>(){

            @Override
            public URI call() {
                try {
                    return EndpointDiscoveryRefreshCache.this.put(key, (Map)EndpointDiscoveryRefreshCache.this.cacheLoader.load(key), defaultEndpoint);
                }
                catch (Exception e) {
                    log.debug((Object)"Failed to refresh cached endpoint. Scheduling another refresh in 5 minutes");
                    EndpointDiscoveryRefreshCache.this.loadAndScheduleRefresh(key, 5L, defaultEndpoint);
                    return null;
                }
            }
        }, refreshPeriod, TimeUnit.MINUTES);
    }

    public ScheduledFuture<?> loadAndScheduleEvict(final String key, long refreshPeriod, TimeUnit refreshPeriodTimeUnit) {
        return this.executorService.schedule(new Runnable(){

            @Override
            public void run() {
                EndpointDiscoveryRefreshCache.this.evict(key);
            }
        }, refreshPeriod, refreshPeriodTimeUnit);
    }

    public void shutdown() {
        this.executorService.shutdownNow();
    }
}

