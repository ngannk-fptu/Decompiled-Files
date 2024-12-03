/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheSettings
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.sf.ehcache.config.CacheConfiguration$CacheEventListenerFactoryConfiguration
 */
package com.atlassian.cache.ehcache.replication;

import com.atlassian.cache.CacheSettings;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.sf.ehcache.config.CacheConfiguration;

@ParametersAreNonnullByDefault
public interface EhCacheReplicatorConfigFactory {
    @Nonnull
    public CacheConfiguration.CacheEventListenerFactoryConfiguration createCacheReplicatorConfiguration(CacheSettings var1, boolean var2);
}

