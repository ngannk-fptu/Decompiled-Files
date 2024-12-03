/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.hercules.cache;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.troubleshooting.api.PluginInfo;
import com.atlassian.troubleshooting.stp.hercules.LogScanMonitor;
import com.atlassian.troubleshooting.stp.hercules.cache.LogScanCache;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

public class LogScanCacheSupplier
implements DisposableBean {
    private static final Logger LOG = LoggerFactory.getLogger(LogScanCacheSupplier.class);
    private static final Long CACHE_EXPIRY_DURATION_MS = TimeUnit.DAYS.toMillis(1L);
    private final PluginInfo pluginInfo;
    private final ServiceTracker serviceTracker;
    private final ScheduledExecutorService executorService;
    private volatile LogScanCache cache;

    @Autowired
    public LogScanCacheSupplier(PluginInfo pluginInfo, BundleContext bundleContext) {
        this.pluginInfo = pluginInfo;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        ServiceTracker serviceTracker = null;
        try {
            bundleContext.getBundle().loadClass("com.atlassian.cache.CacheSettingsBuilder");
            serviceTracker = new ServiceTracker(bundleContext, "com.atlassian.cache.CacheManager", null);
            serviceTracker.open();
        }
        catch (ClassNotFoundException e) {
            LOG.debug("atlassian-cache 2.0 or higher is not available. Using standalone MonitoredTaskExecutor");
        }
        this.serviceTracker = serviceTracker;
    }

    public void destroy() {
        if (this.serviceTracker != null) {
            this.serviceTracker.close();
        }
    }

    public LogScanCache getCache() {
        if (this.cache == null) {
            this.cache = this.createCache();
        }
        return this.cache;
    }

    private LogScanCache createCache() {
        Object cacheManager;
        if (this.serviceTracker != null && (cacheManager = this.serviceTracker.getService()) != null) {
            return new ClusterLogScanCache(this.pluginInfo.getPluginKey(), (CacheManager)cacheManager);
        }
        return new SimpleLogScanCache();
    }

    private class SimpleLogScanCache
    implements LogScanCache,
    Runnable {
        private LogScanMonitor lastScan;
        private long expiry;

        private SimpleLogScanCache() {
        }

        @Override
        @Nullable
        public LogScanMonitor get() {
            this.checkExpired();
            return this.lastScan;
        }

        @Override
        public void run() {
            this.checkExpired();
        }

        @Override
        public void set(@Nonnull LogScanMonitor scan) {
            this.lastScan = scan;
            this.expiry = System.currentTimeMillis() + CACHE_EXPIRY_DURATION_MS;
            LogScanCacheSupplier.this.executorService.schedule(this, (long)CACHE_EXPIRY_DURATION_MS, TimeUnit.MILLISECONDS);
        }

        @Override
        public void destroy() {
            this.lastScan = null;
        }

        private void checkExpired() {
            if (this.expiry < System.currentTimeMillis()) {
                this.destroy();
            }
        }
    }

    private static class ClusterLogScanCache
    implements LogScanCache {
        private static final Integer KEY = 1;
        private final Cache<Integer, LogScanMonitor> lastScan;

        public ClusterLogScanCache(String pluginKey, CacheManager cacheManager) {
            this.lastScan = cacheManager.getCache(pluginKey + ".lastLogScan", null, new CacheSettingsBuilder().expireAfterWrite(CACHE_EXPIRY_DURATION_MS.longValue(), TimeUnit.MILLISECONDS).local().build());
        }

        @Override
        public LogScanMonitor get() {
            return (LogScanMonitor)this.lastScan.get((Object)KEY);
        }

        @Override
        public void set(@Nonnull LogScanMonitor scan) {
            this.destroy();
            this.lastScan.putIfAbsent((Object)KEY, (Object)scan);
        }

        @Override
        public void destroy() {
            this.lastScan.remove((Object)KEY);
        }
    }
}

