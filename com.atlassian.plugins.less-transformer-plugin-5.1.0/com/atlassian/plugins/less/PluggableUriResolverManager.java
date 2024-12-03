/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.lesscss.spi.UriResolver
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 */
package com.atlassian.plugins.less;

import com.atlassian.lesscss.spi.UriResolver;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.atlassian.plugins.less.UriResolverManager;
import com.atlassian.plugins.less.UriResolverModuleDescriptor;

public class PluggableUriResolverManager
implements UriResolverManager {
    private final PluginModuleTracker<UriResolver, UriResolverModuleDescriptor> uriResolvers;

    public PluggableUriResolverManager(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager) {
        this.uriResolvers = new DefaultPluginModuleTracker(pluginAccessor, pluginEventManager, UriResolverModuleDescriptor.class);
    }

    public void closeModuleTracker() {
        this.uriResolvers.close();
    }

    @Override
    public Iterable<UriResolver> getResolvers() {
        return this.uriResolvers.getModules();
    }
}

