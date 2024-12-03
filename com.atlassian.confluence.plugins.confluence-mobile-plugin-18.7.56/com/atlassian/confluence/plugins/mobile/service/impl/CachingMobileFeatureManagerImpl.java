/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  javax.annotation.Nonnull
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.impl;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.plugins.mobile.service.MobileFeatureManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.features.DarkFeatureManager;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.springframework.stereotype.Component;

@Component
public class CachingMobileFeatureManagerImpl
implements MobileFeatureManager {
    private final DarkFeatureManager darkFeatureManager;
    private final CacheManager cacheManager;
    private final Cache<String, Boolean> mobileFeatureCache;
    private static final CacheSettings MOBILE_FEATURE_CACHE_SETTINGS = new CacheSettingsBuilder().expireAfterAccess(1L, TimeUnit.MINUTES).local().build();

    public CachingMobileFeatureManagerImpl(@ComponentImport DarkFeatureManager darkFeatureManager, @ComponentImport CacheManager cacheManager) {
        this.darkFeatureManager = darkFeatureManager;
        this.cacheManager = cacheManager;
        this.mobileFeatureCache = cacheManager.getCache(CachingMobileFeatureManagerImpl.class.getName() + ".featureCache", (CacheLoader)new FeatureLoader(), MOBILE_FEATURE_CACHE_SETTINGS);
    }

    @Override
    public boolean isStatusCodeRewritingEnabled() {
        return (Boolean)this.mobileFeatureCache.get((Object)"com.atlassian.confluence.mobile.legacy.session.behavior");
    }

    private class FeatureLoader
    implements CacheLoader<String, Boolean> {
        private FeatureLoader() {
        }

        @Nonnull
        public Boolean load(@Nonnull String key) {
            return CachingMobileFeatureManagerImpl.this.darkFeatureManager.isEnabledForAllUsers(key).orElse(Boolean.FALSE) == false;
        }
    }
}

