/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheSettings
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.sf.ehcache.Cache
 *  net.sf.ehcache.CacheManager
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.config.CacheConfiguration
 *  net.sf.ehcache.config.CacheConfiguration$CacheEventListenerFactoryConfiguration
 *  net.sf.ehcache.config.PersistenceConfiguration
 *  net.sf.ehcache.config.PersistenceConfiguration$Strategy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.cache.ehcache;

import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.ehcache.SynchronizedLoadingCacheDecorator;
import com.atlassian.cache.ehcache.replication.EhCacheReplicatorConfigFactory;
import com.atlassian.cache.ehcache.replication.rmi.RMICacheReplicatorConfigFactory;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
class EhCacheHelper {
    private static final Logger log = LoggerFactory.getLogger(EhCacheHelper.class);
    static final PersistenceConfiguration.Strategy PERSISTENCE_STRATEGY = PersistenceConfiguration.Strategy.NONE;
    private static final PersistenceConfiguration PERSISTENCE_CONFIGURATION = new PersistenceConfiguration().strategy(PERSISTENCE_STRATEGY);
    @Nullable
    private final EhCacheReplicatorConfigFactory replicatorConfigFactory;

    @Deprecated
    EhCacheHelper() {
        this(new RMICacheReplicatorConfigFactory());
    }

    EhCacheHelper(@Nullable EhCacheReplicatorConfigFactory replicatorConfigFactory) {
        this.replicatorConfigFactory = replicatorConfigFactory;
    }

    private CacheConfiguration.CacheEventListenerFactoryConfiguration getCacheEventListenerFactoryConfiguration(CacheSettings settings, boolean selfLoading) {
        if (this.replicatorConfigFactory != null) {
            return this.replicatorConfigFactory.createCacheReplicatorConfiguration(settings, selfLoading);
        }
        throw new IllegalStateException("No EhCacheReplicatorConfigFactory has been configured");
    }

    @Nonnull
    Ehcache getEhcache(String name, CacheManager ehMgr, CacheSettings settings, boolean selfLoading, boolean statisticsEnabled) {
        CacheConfiguration config = EhCacheHelper.getBaseConfiguration(ehMgr).name(name).statistics(statisticsEnabled).persistence(PERSISTENCE_CONFIGURATION);
        boolean replicateCache = this.isReplicateCache(ehMgr, settings);
        if (replicateCache) {
            config.cacheEventListenerFactory(this.getCacheEventListenerFactoryConfiguration(settings, selfLoading));
        }
        if (null != settings.getMaxEntries()) {
            config.setMaxEntriesLocalHeap((long)settings.getMaxEntries().intValue());
        }
        if (settings.getExpireAfterAccess() != null || settings.getExpireAfterWrite() != null) {
            config.setEternal(false);
        }
        if (null != settings.getExpireAfterAccess()) {
            config.timeToIdleSeconds(TimeUnit.SECONDS.convert(settings.getExpireAfterAccess(), TimeUnit.MILLISECONDS));
        }
        if (null != settings.getExpireAfterWrite()) {
            config.timeToLiveSeconds(TimeUnit.SECONDS.convert(settings.getExpireAfterWrite(), TimeUnit.MILLISECONDS));
        }
        Object cache = new Cache(config);
        if (selfLoading) {
            cache = new SynchronizedLoadingCacheDecorator((Ehcache)cache);
        }
        return ehMgr.addCacheIfAbsent((Ehcache)cache);
    }

    @Nonnull
    private static CacheConfiguration getBaseConfiguration(CacheManager cacheManager) {
        CacheConfiguration defaultConfiguration = cacheManager.getConfiguration().getDefaultCacheConfiguration();
        if (defaultConfiguration == null) {
            return new CacheConfiguration();
        }
        return defaultConfiguration.clone();
    }

    private boolean isReplicateCache(CacheManager ehMgr, CacheSettings settings) {
        boolean isLocalCacheSetting = settings.getLocal(false);
        if (!isLocalCacheSetting && this.replicatorConfigFactory != null) {
            boolean hasPeerProvider;
            boolean bl = hasPeerProvider = !ehMgr.getCacheManagerPeerProviders().isEmpty();
            if (hasPeerProvider) {
                return true;
            }
            log.warn("No PeerProviders configured in ehcache, replication cannot be configured for non-local cache");
        }
        return false;
    }
}

