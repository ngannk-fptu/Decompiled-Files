/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  javax.inject.Inject
 */
package com.atlassian.plugins.authentication.impl.web;

import com.atlassian.cache.CacheManager;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.web.AbstractSessionDataCacheFactory;
import com.atlassian.plugins.authentication.impl.web.SessionDataCache;
import com.atlassian.plugins.authentication.impl.web.SessionDataCacheConfiguration;
import com.atlassian.sal.api.features.DarkFeatureManager;
import javax.inject.Inject;

@ConfluenceComponent
public class DarkFeatureDrivenSessionDataCacheFactory
extends AbstractSessionDataCacheFactory {
    private static final String DARK_FEATURE_KEY = "atlassian.authentication.sso.replicate.session.data";
    private final DarkFeatureManager darkFeatureManager;
    private final CacheManager cacheManager;

    @Inject
    public DarkFeatureDrivenSessionDataCacheFactory(@ComponentImport DarkFeatureManager darkFeatureManager, @ComponentImport CacheManager cacheManager) {
        this.darkFeatureManager = darkFeatureManager;
        this.cacheManager = cacheManager;
    }

    @Override
    public SessionDataCache createSessionDataCache(SessionDataCacheConfiguration configuration) {
        if (this.darkFeatureManager.isEnabledForAllUsers(DARK_FEATURE_KEY).orElse(false).booleanValue()) {
            return this.getAtlassianCacheSessionDataCache(this.cacheManager, configuration);
        }
        return this.getGuavaSessionDataCache(configuration);
    }
}

