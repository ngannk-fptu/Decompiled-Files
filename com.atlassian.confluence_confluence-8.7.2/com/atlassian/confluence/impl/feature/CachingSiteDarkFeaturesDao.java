/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CachedReference
 */
package com.atlassian.confluence.impl.feature;

import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CachedReference;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.impl.feature.SiteDarkFeaturesDao;
import java.util.Set;

public class CachingSiteDarkFeaturesDao
implements SiteDarkFeaturesDao {
    private final SiteDarkFeaturesDao delegate;
    private final CachedReference<Set<String>> cache;

    public static CachingSiteDarkFeaturesDao create(SiteDarkFeaturesDao delegate, CacheManager cacheManager) {
        return new CachingSiteDarkFeaturesDao(delegate, (CachedReference<Set<String>>)CoreCache.SITE_ENABLED_DARK_FEATURES.resolve(name -> CachingSiteDarkFeaturesDao.createCachedReference(cacheManager, name, delegate)));
    }

    private static CachedReference<Set<String>> createCachedReference(CacheManager cacheManager, String cacheName, SiteDarkFeaturesDao delegate) {
        return cacheManager.getCachedReference(cacheName, delegate::getSiteEnabledFeatures, new CacheSettingsBuilder().replicateViaInvalidation().build());
    }

    CachingSiteDarkFeaturesDao(SiteDarkFeaturesDao delegate, CachedReference<Set<String>> cache) {
        this.delegate = delegate;
        this.cache = cache;
    }

    @Override
    public Set<String> getSiteEnabledFeatures() {
        return (Set)this.cache.get();
    }

    @Override
    public boolean enableSiteFeature(String featureKey) {
        boolean enabled = this.delegate.enableSiteFeature(featureKey);
        if (enabled) {
            this.cache.reset();
        }
        return enabled;
    }

    @Override
    public boolean disableSiteFeature(String featureKey) {
        boolean disabled = this.delegate.disableSiteFeature(featureKey);
        if (disabled) {
            this.cache.reset();
        }
        return disabled;
    }
}

