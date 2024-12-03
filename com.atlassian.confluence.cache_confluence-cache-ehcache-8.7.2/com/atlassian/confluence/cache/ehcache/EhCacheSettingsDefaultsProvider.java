/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsDefaultsProvider
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.cache.ehcache;

import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsDefaultsProvider;
import com.atlassian.confluence.cache.ehcache.EhCacheManagementConfig;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

public class EhCacheSettingsDefaultsProvider
implements CacheSettingsDefaultsProvider {
    private final CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider;
    private final EhCacheManagementConfig ehCacheManagementConfig;

    public EhCacheSettingsDefaultsProvider(CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider, EhCacheManagementConfig ehCacheManagementConfig) {
        this.cacheSettingsDefaultsProvider = Objects.requireNonNull(cacheSettingsDefaultsProvider);
        this.ehCacheManagementConfig = Objects.requireNonNull(ehCacheManagementConfig);
    }

    public @NonNull CacheSettings getDefaults(@NonNull String name) {
        return this.cacheSettingsDefaultsProvider.getDefaults(name);
    }

    boolean isReportBytesLocalHeap(String cacheName) {
        return this.ehCacheManagementConfig.reportBytesLocalHeap(cacheName);
    }
}

