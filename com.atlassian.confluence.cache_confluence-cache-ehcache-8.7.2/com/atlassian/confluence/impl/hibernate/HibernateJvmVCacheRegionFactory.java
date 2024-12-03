/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsDefaultsProvider
 *  com.atlassian.cache.ManagedCache
 *  javax.cache.CacheManager
 *  net.sf.ehcache.CacheManager
 *  org.hibernate.boot.spi.SessionFactoryOptions
 *  org.hibernate.cache.jcache.internal.JCacheRegionFactory
 *  org.hibernate.cache.spi.QueryResultsRegion
 *  org.hibernate.cache.spi.TimestampsRegion
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 */
package com.atlassian.confluence.impl.hibernate;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsDefaultsProvider;
import com.atlassian.cache.ManagedCache;
import com.atlassian.confluence.impl.hibernate.JCacheProxyFactory;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.jcache.internal.JCacheRegionFactory;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public class HibernateJvmVCacheRegionFactory
extends JCacheRegionFactory {
    private final CacheManager atlassianCacheManager;
    private final net.sf.ehcache.CacheManager ehCacheManager;
    private final CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider;

    public HibernateJvmVCacheRegionFactory(CacheManager atlassianCacheManager, net.sf.ehcache.CacheManager ehCacheManager, CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider) {
        this.atlassianCacheManager = Objects.requireNonNull(atlassianCacheManager);
        this.ehCacheManager = Objects.requireNonNull(ehCacheManager);
        this.cacheSettingsDefaultsProvider = Objects.requireNonNull(cacheSettingsDefaultsProvider);
    }

    protected javax.cache.CacheManager resolveCacheManager(SessionFactoryOptions settings, Map properties) {
        return JCacheProxyFactory.createProxy(this.ehCacheManager);
    }

    public QueryResultsRegion buildQueryResultsRegion(String regionName, SessionFactoryImplementor sessionFactory) {
        return this.withCacheSettingSynced(regionName, () -> super.buildQueryResultsRegion(regionName, sessionFactory));
    }

    public TimestampsRegion buildTimestampsRegion(String regionName, SessionFactoryImplementor sessionFactory) {
        return this.withCacheSettingSynced(regionName, () -> super.buildTimestampsRegion(regionName, sessionFactory));
    }

    @VisibleForTesting
    <T> T withCacheSettingSynced(String cacheName, Supplier<T> regionSupplier) {
        T region = regionSupplier.get();
        this.atlassianCacheManager.getCache(cacheName);
        ManagedCache cache = this.atlassianCacheManager.getManagedCache(cacheName);
        if (cache != null) {
            CacheSettings settings = this.cacheSettingsDefaultsProvider.getDefaults(cacheName);
            Optional.ofNullable(settings.getMaxEntries()).ifPresent(arg_0 -> ((ManagedCache)cache).updateMaxEntries(arg_0));
            Optional.ofNullable(settings.getExpireAfterAccess()).ifPresent(duration -> cache.updateExpireAfterAccess(duration.longValue(), TimeUnit.MILLISECONDS));
            Optional.ofNullable(settings.getExpireAfterWrite()).ifPresent(duration -> cache.updateExpireAfterWrite(duration.longValue(), TimeUnit.MILLISECONDS));
        }
        return region;
    }
}

