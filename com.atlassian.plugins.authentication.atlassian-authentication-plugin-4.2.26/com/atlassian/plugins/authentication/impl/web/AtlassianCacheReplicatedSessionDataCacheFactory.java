/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.inject.Inject
 */
package com.atlassian.plugins.authentication.impl.web;

import com.atlassian.cache.CacheManager;
import com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.web.AbstractSessionDataCacheFactory;
import com.atlassian.plugins.authentication.impl.web.SessionDataCache;
import com.atlassian.plugins.authentication.impl.web.SessionDataCacheConfiguration;
import javax.inject.Inject;

@BitbucketComponent
public class AtlassianCacheReplicatedSessionDataCacheFactory
extends AbstractSessionDataCacheFactory {
    private final CacheManager cacheManager;

    @Inject
    public AtlassianCacheReplicatedSessionDataCacheFactory(@ComponentImport CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public SessionDataCache createSessionDataCache(SessionDataCacheConfiguration configuration) {
        return this.getAtlassianCacheSessionDataCache(this.cacheManager, configuration);
    }
}

