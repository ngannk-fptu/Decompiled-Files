/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.container.OsgiPersistentCache
 *  com.atlassian.plugin.osgi.container.impl.DefaultOsgiPersistentCache
 *  org.springframework.beans.factory.FactoryBean
 */
package com.atlassian.confluence.plugin.spring;

import com.atlassian.confluence.plugin.PluginDirectoryProvider;
import com.atlassian.plugin.osgi.container.OsgiPersistentCache;
import com.atlassian.plugin.osgi.container.impl.DefaultOsgiPersistentCache;
import org.springframework.beans.factory.FactoryBean;

public class OsgiPersistentCacheFactory
implements FactoryBean {
    private final PluginDirectoryProvider pluginDirectoryProvider;

    public OsgiPersistentCacheFactory(PluginDirectoryProvider pluginDirectoryProvider) {
        this.pluginDirectoryProvider = pluginDirectoryProvider;
    }

    public Object getObject() throws Exception {
        return new DefaultOsgiPersistentCache(this.pluginDirectoryProvider.getPluginsPersistentCacheDirectory());
    }

    public Class<OsgiPersistentCache> getObjectType() {
        return OsgiPersistentCache.class;
    }

    public boolean isSingleton() {
        return true;
    }
}

