/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.vcache.VCacheFactory
 *  io.atlassian.fugue.Either
 *  javax.annotation.PostConstruct
 */
package com.atlassian.confluence.util.velocity;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.util.velocity.ConfluenceVelocityResourceManager;
import com.atlassian.vcache.VCacheFactory;
import io.atlassian.fugue.Either;
import javax.annotation.PostConstruct;

public class ConfluenceVelocityCacheConfigurator {
    private final Either<VCacheFactory, CacheFactory> cacheFactory;

    public ConfluenceVelocityCacheConfigurator(CacheFactory cacheFactory) {
        this.cacheFactory = Either.right((Object)cacheFactory);
    }

    @Deprecated
    public ConfluenceVelocityCacheConfigurator(VCacheFactory cacheFactory) {
        this.cacheFactory = Either.left((Object)cacheFactory);
    }

    @PostConstruct
    public void init() {
        this.cacheFactory.left().forEach(ConfluenceVelocityResourceManager::setCacheFactory);
        this.cacheFactory.right().forEach(ConfluenceVelocityResourceManager::setCacheFactory);
    }
}

