/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsDefaultsProvider
 *  com.atlassian.dc.filestore.api.FileStore$Path
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.core.io.ClassPathResource
 *  org.springframework.core.io.Resource
 */
package com.atlassian.confluence.cache;

import com.atlassian.beehive.ClusterLockService;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsDefaultsProvider;
import com.atlassian.confluence.cache.CacheSettingsManager;
import com.atlassian.confluence.impl.cache.config.CacheSettingsManagerFactory;
import com.atlassian.confluence.impl.filestore.FileStoreFactory;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.dc.filestore.api.FileStore;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class DefaultCacheSettingsManager
implements CacheSettingsManager,
InitializingBean {
    private final CacheSettingsDefaultsProvider configDefaultsReader;
    private final CacheSettingsManager configOverridesManager;

    @Deprecated
    public DefaultCacheSettingsManager(BootstrapManager bootstrapManager, ClusterLockService clusterLockService, String cacheSettingsDefaults, String cacheSettingsOverrides) {
        this(new CacheSettingsManagerFactory((FileStore.Path)new FileStoreFactory(bootstrapManager).getSharedHome(), clusterLockService), cacheSettingsDefaults, cacheSettingsOverrides);
    }

    private DefaultCacheSettingsManager(CacheSettingsManagerFactory factory, String cacheSettingsDefaults, String cacheSettingsOverrides) {
        this(factory.createDefaultsProvider((Resource)new ClassPathResource(cacheSettingsDefaults)), factory.createOverridesManager(cacheSettingsOverrides));
    }

    public DefaultCacheSettingsManager(CacheSettingsDefaultsProvider configDefaultsReader, CacheSettingsManager configOverridesManager) {
        this.configDefaultsReader = configDefaultsReader;
        this.configOverridesManager = configOverridesManager;
    }

    @Deprecated
    public DefaultCacheSettingsManager(BootstrapManager bootstrapManager, ClusterLockService clusterLockService, String cacheSettingsDefaults, String cacheSettingsOverrides, List<String> ignored) {
        this(bootstrapManager, clusterLockService, cacheSettingsDefaults, cacheSettingsOverrides);
    }

    @Deprecated
    public void afterPropertiesSet() {
    }

    @Override
    public Optional<Integer> changeMaxEntries(@Nonnull String name, int newValue) {
        return this.configOverridesManager.changeMaxEntries(name, newValue);
    }

    @Override
    public boolean saveSettings() {
        return this.configOverridesManager.saveSettings();
    }

    @Override
    public void reloadSettings() {
        this.configOverridesManager.reloadSettings();
    }

    @Nonnull
    public CacheSettings getDefaults(@Nonnull String cacheName) {
        return this.configDefaultsReader.getDefaults(cacheName).override(this.configOverridesManager.getDefaults(cacheName));
    }
}

