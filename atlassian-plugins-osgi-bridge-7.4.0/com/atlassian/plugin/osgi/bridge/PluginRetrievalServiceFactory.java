/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceFactory
 *  org.osgi.framework.ServiceRegistration
 */
package com.atlassian.plugin.osgi.bridge;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.osgi.bridge.PluginBundleUtils;
import com.atlassian.plugin.osgi.bridge.external.PluginRetrievalService;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public class PluginRetrievalServiceFactory
implements ServiceFactory {
    private final PluginAccessor pluginAccessor;

    public PluginRetrievalServiceFactory(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public Object getService(Bundle bundle, ServiceRegistration serviceRegistration) {
        return new PluginRetrievalServiceImpl(this.pluginAccessor, bundle);
    }

    public void ungetService(Bundle bundle, ServiceRegistration serviceRegistration, Object o) {
    }

    private static class PluginRetrievalServiceImpl
    implements PluginRetrievalService {
        private final Plugin plugin;

        public PluginRetrievalServiceImpl(PluginAccessor pluginAccessor, Bundle bundle) {
            String pluginKey = PluginBundleUtils.getPluginKey(bundle);
            this.plugin = pluginAccessor.getPlugin(pluginKey);
        }

        @Override
        public Plugin getPlugin() {
            return this.plugin;
        }
    }
}

