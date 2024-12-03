/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.status;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SynchronyStatusCache {
    private static final Logger log = LoggerFactory.getLogger(SynchronyStatusCache.class);
    static final String CACHE_NAME = SynchronyStatusCache.class.getName();
    private static final boolean SYNCHRONY_RUNNING = true;
    @VisibleForTesting
    static final String SYNCHRONY_STATUS_TOKEN = "synchrony_status";
    private final Cache<String, Boolean> cache;

    @Autowired
    public SynchronyStatusCache(@ComponentImport CacheManager cacheManager) {
        this.cache = this.getCache((CacheFactory)cacheManager);
    }

    public boolean isSynchronyRunning() {
        try {
            return this.getStatus().orElse(true);
        }
        catch (RuntimeException e) {
            log.warn("Synchrony Status cache did not return the Synchrony Status. Defaulting to 'Running' ", (Throwable)e);
            return true;
        }
    }

    public Optional<Boolean> getStatus() {
        return Optional.ofNullable((Boolean)this.cache.get((Object)SYNCHRONY_STATUS_TOKEN));
    }

    public void setStatus(boolean isRunning) {
        this.cache.put((Object)SYNCHRONY_STATUS_TOKEN, (Object)isRunning);
    }

    private Cache<String, Boolean> getCache(CacheFactory cacheFactory) {
        return cacheFactory.getCache(CACHE_NAME, null, new CacheSettingsBuilder().local().maxEntries(1).build());
    }
}

