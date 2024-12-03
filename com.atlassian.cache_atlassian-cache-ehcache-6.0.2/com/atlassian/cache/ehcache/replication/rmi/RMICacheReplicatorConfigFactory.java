/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheSettings
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.sf.ehcache.config.CacheConfiguration$CacheEventListenerFactoryConfiguration
 */
package com.atlassian.cache.ehcache.replication.rmi;

import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.ehcache.replication.EhCacheReplicatorConfigFactory;
import com.atlassian.cache.ehcache.replication.rmi.RMICacheReplicatorFactory;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.sf.ehcache.config.CacheConfiguration;

@ParametersAreNonnullByDefault
public class RMICacheReplicatorConfigFactory
implements EhCacheReplicatorConfigFactory {
    private static final String CACHE_PROPERTIES = "replicateAsynchronously=%s,replicatePuts=%s,replicatePutsViaCopy=%s,replicateUpdates=%s,replicateUpdatesViaCopy=%s,replicateRemovals=true";

    @Override
    @Nonnull
    public CacheConfiguration.CacheEventListenerFactoryConfiguration createCacheReplicatorConfiguration(CacheSettings settings, boolean selfLoadingCache) {
        boolean replicateAsynchronously = settings.getReplicateAsynchronously(false);
        boolean replicateViaCopy = settings.getReplicateViaCopy(false);
        boolean replicatePuts = !selfLoadingCache && replicateViaCopy;
        boolean replicateUpdates = !selfLoadingCache;
        String cacheProperties = String.format(CACHE_PROPERTIES, replicateAsynchronously, replicatePuts, replicateViaCopy, replicateUpdates, replicateViaCopy);
        return (CacheConfiguration.CacheEventListenerFactoryConfiguration)((CacheConfiguration.CacheEventListenerFactoryConfiguration)new CacheConfiguration.CacheEventListenerFactoryConfiguration().className(RMICacheReplicatorFactory.class.getName())).properties(cacheProperties);
    }
}

