/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cache.ConfluenceCache
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.cache.whitelist;

import com.atlassian.confluence.cache.ConfluenceCache;
import com.atlassian.confluence.impl.cache.whitelist.CacheOperationsWhitelist;
import java.util.Set;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class CacheOperationsWhitelistImpl
implements CacheOperationsWhitelist {
    private static final Logger log = LoggerFactory.getLogger(CacheOperationsWhitelistImpl.class);
    private final Function<CacheOperationsWhitelist.Operation, Set<String>> whitelistedCaches;

    public CacheOperationsWhitelistImpl(Function<CacheOperationsWhitelist.Operation, Set<String>> whitelistedCaches) {
        this.whitelistedCaches = whitelistedCaches;
    }

    @Override
    public void assertPermitted(CacheOperationsWhitelist.Operation operation, ConfluenceCache<?, ?> cache) {
        if (!this.isPermitted(operation, cache)) {
            throw new CacheOperationNotPermittedException(operation, cache.getName());
        }
        log.debug("Operation {} permitted for cache '{}'", (Object)operation, (Object)cache.getName());
    }

    private boolean isPermitted(CacheOperationsWhitelist.Operation operation, ConfluenceCache<?, ?> cache) {
        return cache.isLocal() || this.whitelistedCaches.apply(operation).contains(cache.getName());
    }

    static class CacheOperationNotPermittedException
    extends UnsupportedOperationException {
        public CacheOperationNotPermittedException(CacheOperationsWhitelist.Operation operation, String cacheName) {
            super(String.format("Cache is not whitelisted to perform operation %s: '%s'", new Object[]{operation, cacheName}));
        }
    }
}

