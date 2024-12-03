/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.plugin.Plugin
 *  javax.annotation.Nonnull
 */
package com.atlassian.upm.license.internal.impl.role;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.plugin.Plugin;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensedPlugins;
import com.atlassian.upm.license.internal.impl.role.RoleBasedPluginDescriptorMetadataCache;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

public class RoleBasedPluginDescriptorMetadataCacheImpl
implements RoleBasedPluginDescriptorMetadataCache {
    private static final String CACHE_NAME = "UpmRoleBasedMetadataCache";
    private static final CacheSettings CACHE_SETTINGS = new CacheSettingsBuilder().remote().replicateViaInvalidation().expireAfterWrite(1L, TimeUnit.HOURS).build();
    private final Cache<String, Option<RoleBasedLicensedPlugins.RoleBasedPluginDescriptorMetadata>> cache;
    private final UpmPluginAccessor pluginAccessor;

    public RoleBasedPluginDescriptorMetadataCacheImpl(CacheFactory cacheFactory, UpmPluginAccessor pluginAccessor) {
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor, "pluginAccessor");
        this.cache = Objects.requireNonNull(cacheFactory, "cacheFactory").getCache(CACHE_NAME, (CacheLoader)new MetadataCacheLoader(), CACHE_SETTINGS);
    }

    @Override
    public Option<RoleBasedLicensedPlugins.RoleBasedPluginDescriptorMetadata> getMetadata(String pluginKey) {
        return (Option)this.cache.get((Object)pluginKey);
    }

    @Override
    public void remove(String pluginKey) {
        this.cache.remove((Object)pluginKey);
    }

    @Override
    public void removeAll() {
        this.cache.removeAll();
    }

    private class MetadataCacheLoader
    implements CacheLoader<String, Option<RoleBasedLicensedPlugins.RoleBasedPluginDescriptorMetadata>> {
        private MetadataCacheLoader() {
        }

        @Nonnull
        public Option<RoleBasedLicensedPlugins.RoleBasedPluginDescriptorMetadata> load(@Nonnull String key) {
            Iterator<Plugin> iterator = RoleBasedPluginDescriptorMetadataCacheImpl.this.pluginAccessor.getPlugin(key).iterator();
            if (iterator.hasNext()) {
                Plugin p = iterator.next();
                return RoleBasedLicensedPlugins.getRoleBasedLicensingMetadata(p);
            }
            return Option.none();
        }
    }
}

