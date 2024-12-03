/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.cache.CacheSettingsDefaultsProvider
 *  com.atlassian.dc.filestore.api.FileStore$Path
 *  org.springframework.core.io.Resource
 */
package com.atlassian.confluence.impl.cache.config;

import com.atlassian.beehive.ClusterLockService;
import com.atlassian.cache.CacheSettingsDefaultsProvider;
import com.atlassian.confluence.cache.CacheSettingsManager;
import com.atlassian.confluence.cache.DefaultCacheSettingsManager;
import com.atlassian.confluence.impl.cache.config.HoconCacheSettingsReader;
import com.atlassian.confluence.impl.cache.config.PropertiesFileCacheSettingsManager;
import com.atlassian.dc.filestore.api.FileStore;
import org.springframework.core.io.Resource;

public final class CacheSettingsManagerFactory {
    private final FileStore.Path fileStore;
    private final ClusterLockService clusterLockService;

    public CacheSettingsManagerFactory(FileStore.Path fileStore, ClusterLockService clusterLockService) {
        this.fileStore = fileStore;
        this.clusterLockService = clusterLockService;
    }

    private FileStore.Path getSharedConfigDir() {
        return this.fileStore.path(new String[]{"config"});
    }

    public CacheSettingsManager create(Resource cacheSettingsDefaults, String cacheSettingsOverrides) {
        return new DefaultCacheSettingsManager(this.createDefaultsProvider(cacheSettingsDefaults), this.createOverridesManager(cacheSettingsOverrides));
    }

    public CacheSettingsDefaultsProvider createDefaultsProvider(Resource cacheSettingsDefaults) {
        return new HoconCacheSettingsReader(this.getSharedConfigDir().path(new String[]{cacheSettingsDefaults.getFilename()}), cacheSettingsDefaults);
    }

    public CacheSettingsManager createOverridesManager(String cacheSettingsOverrides) {
        return new PropertiesFileCacheSettingsManager(this.getSharedConfigDir().path(new String[]{cacheSettingsOverrides}), this.clusterLockService);
    }
}

