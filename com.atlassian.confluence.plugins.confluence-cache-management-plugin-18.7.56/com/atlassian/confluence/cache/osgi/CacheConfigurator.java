/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.cache.osgi;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.stereotype.Component;

@Component
public final class CacheConfigurator {
    private final CacheFactory cacheFactory;
    private final PluginAccessor pluginAccessor;

    CacheConfigurator(@ComponentImport CacheManager cacheFactory, @ComponentImport PluginAccessor pluginAccessor) {
        this.cacheFactory = cacheFactory;
        this.pluginAccessor = pluginAccessor;
    }

    <K, V> Cache<K, V> getCache(ModuleCompleteKey cacheSettingsModuleKey) {
        return this.getCache(cacheSettingsModuleKey.getCompleteKey());
    }

    private <K, V> Cache<K, V> getCache(String cacheSettingsModuleKey) {
        CacheSettings cacheSettings = this.getCacheSettings(cacheSettingsModuleKey);
        return this.cacheFactory.getCache(cacheSettingsModuleKey, null, cacheSettings);
    }

    private CacheSettings getCacheSettings(String moduleKey) {
        ModuleDescriptor moduleDescriptor = this.pluginAccessor.getEnabledPluginModule(moduleKey);
        if (moduleDescriptor == null) {
            throw new IllegalArgumentException("No such plugin module " + moduleKey);
        }
        if (!CacheSettings.class.isAssignableFrom(moduleDescriptor.getModuleClass())) {
            throw new IllegalArgumentException("Plugin module is not a CacheSettings module: " + moduleKey);
        }
        return (CacheSettings)moduleDescriptor.getModule();
    }
}

