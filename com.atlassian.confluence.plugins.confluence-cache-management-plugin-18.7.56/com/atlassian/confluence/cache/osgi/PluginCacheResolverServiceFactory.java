/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.osgi.util.OsgiHeaderUtil
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceFactory
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.cache.osgi;

import com.atlassian.cache.Cache;
import com.atlassian.confluence.cache.osgi.CacheConfigurator;
import com.atlassian.confluence.plugin.cache.PluginCacheResolver;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="pluginCacheResolverServiceFactory")
public class PluginCacheResolverServiceFactory
implements ServiceFactory<PluginCacheResolver> {
    private final CacheConfigurator cacheConfigurator;

    @Autowired
    public PluginCacheResolverServiceFactory(CacheConfigurator cacheConfigurator) {
        this.cacheConfigurator = cacheConfigurator;
    }

    public PluginCacheResolver getService(final Bundle bundle, ServiceRegistration<PluginCacheResolver> registration) {
        return new PluginCacheResolver(){

            @Override
            public <K, V> Cache<K, V> getCache(String cacheSettingsModuleKey) {
                return PluginCacheResolverServiceFactory.this.cacheConfigurator.getCache(new ModuleCompleteKey(OsgiHeaderUtil.getPluginKey((Bundle)bundle), cacheSettingsModuleKey));
            }
        };
    }

    public void ungetService(Bundle bundle, ServiceRegistration<PluginCacheResolver> registration, PluginCacheResolver service) {
    }
}

