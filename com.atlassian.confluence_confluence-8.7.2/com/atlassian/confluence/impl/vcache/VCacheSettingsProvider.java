/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsDefaultsProvider
 *  com.atlassian.vcache.ExternalCacheSettings
 *  com.atlassian.vcache.ExternalCacheSettingsBuilder
 *  com.atlassian.vcache.JvmCacheSettings
 *  com.atlassian.vcache.JvmCacheSettingsBuilder
 *  com.atlassian.vcache.internal.VCacheSettingsDefaultsProvider
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.vcache;

import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsDefaultsProvider;
import com.atlassian.vcache.ExternalCacheSettings;
import com.atlassian.vcache.ExternalCacheSettingsBuilder;
import com.atlassian.vcache.JvmCacheSettings;
import com.atlassian.vcache.JvmCacheSettingsBuilder;
import com.atlassian.vcache.internal.VCacheSettingsDefaultsProvider;
import java.time.Duration;
import org.checkerframework.checker.nullness.qual.NonNull;

class VCacheSettingsProvider
implements VCacheSettingsDefaultsProvider {
    private final CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider;

    public VCacheSettingsProvider(CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider) {
        this.cacheSettingsDefaultsProvider = cacheSettingsDefaultsProvider;
    }

    public @NonNull ExternalCacheSettings getExternalDefaults(String cacheName) {
        CacheSettings settings = this.getCacheSettings(cacheName);
        return new ExternalCacheSettingsBuilder().defaultTtl(Duration.ofMillis(settings.getExpireAfterAccess(Duration.ofDays(1L).toMillis()))).entryCountHint(settings.getMaxEntries(1000)).build();
    }

    public @NonNull JvmCacheSettings getJvmDefaults(String cacheName) {
        CacheSettings settings = this.getCacheSettings(cacheName);
        return new JvmCacheSettingsBuilder().defaultTtl(Duration.ofMillis(settings.getExpireAfterAccess(Duration.ofDays(1L).toMillis()))).maxEntries(settings.getMaxEntries(1000)).build();
    }

    private CacheSettings getCacheSettings(String cacheName) {
        return this.cacheSettingsDefaultsProvider.getDefaults(cacheName);
    }
}

