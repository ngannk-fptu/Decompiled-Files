/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cache.CacheSettings
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cache;

import com.atlassian.annotations.Internal;
import com.atlassian.cache.CacheSettings;
import com.atlassian.confluence.cache.CacheSettingsManager;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class CachingCacheSettingsManager
implements CacheSettingsManager {
    private static final Logger log = LoggerFactory.getLogger(CachingCacheSettingsManager.class);
    private final CacheSettingsManager delegate;
    private final CacheLoader<String, CacheSettings> cacheLoader = new CacheLoader<String, CacheSettings>(){

        public CacheSettings load(String key) throws Exception {
            return CachingCacheSettingsManager.this.delegate.getDefaults(key);
        }
    };
    private final LoadingCache<String, CacheSettings> settingsCache = CacheBuilder.newBuilder().maximumSize(1024L).expireAfterAccess(1L, TimeUnit.HOURS).expireAfterWrite(24L, TimeUnit.HOURS).build(this.cacheLoader);

    public CachingCacheSettingsManager(CacheSettingsManager delegate) {
        this.delegate = Objects.requireNonNull(delegate);
        log.info("Settings will be memoized from now on");
    }

    @Override
    public Optional<Integer> changeMaxEntries(@NonNull String name, int newValue) {
        Optional<Integer> result = this.delegate.changeMaxEntries(name, newValue);
        this.settingsCache.invalidate((Object)name);
        return result;
    }

    @Override
    public boolean saveSettings() {
        return this.delegate.saveSettings();
    }

    @Override
    public void reloadSettings() {
        this.delegate.reloadSettings();
        this.settingsCache.invalidateAll();
    }

    public @NonNull CacheSettings getDefaults(@NonNull String name) {
        return (CacheSettings)this.settingsCache.getUnchecked((Object)name);
    }
}

