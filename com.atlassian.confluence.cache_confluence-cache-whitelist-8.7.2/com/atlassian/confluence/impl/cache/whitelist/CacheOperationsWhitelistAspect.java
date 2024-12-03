/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cache.ConfluenceCache
 *  com.atlassian.confluence.impl.cache.AbstractConfluenceCacheAspect
 *  org.aspectj.lang.annotation.Aspect
 */
package com.atlassian.confluence.impl.cache.whitelist;

import com.atlassian.confluence.cache.ConfluenceCache;
import com.atlassian.confluence.impl.cache.AbstractConfluenceCacheAspect;
import com.atlassian.confluence.impl.cache.whitelist.CacheOperationsWhitelistService;
import java.util.Objects;
import org.aspectj.lang.annotation.Aspect;

@Aspect
final class CacheOperationsWhitelistAspect
extends AbstractConfluenceCacheAspect {
    private final CacheOperationsWhitelistService whitelistService;
    private boolean enabled = Boolean.getBoolean("confluence.cache-operations-whitelist.enabled");

    CacheOperationsWhitelistAspect(CacheOperationsWhitelistService whitelistService) {
        this.whitelistService = Objects.requireNonNull(whitelistService);
    }

    protected <K, V> ConfluenceCache<K, V> wrapCache(ConfluenceCache<K, V> cache) {
        return this.whitelistService.wrap(cache);
    }

    protected boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

