/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 */
package com.atlassian.confluence.internal.search.v2;

import com.atlassian.confluence.internal.search.v2.SearchDecoratorProvider;
import com.atlassian.confluence.plugin.descriptor.SearchDecoratorModuleDescriptor;
import com.atlassian.confluence.plugin.descriptor.WeightedPluginModuleTracker;
import com.atlassian.confluence.search.v2.SearchDecorator;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;

class DefaultSearchDecoratorProvider
implements SearchDecoratorProvider {
    private final WeightedPluginModuleTracker<SearchDecorator, SearchDecoratorModuleDescriptor> pluginModuleTracker;

    public DefaultSearchDecoratorProvider(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager) {
        this.pluginModuleTracker = WeightedPluginModuleTracker.create(pluginAccessor, pluginEventManager, SearchDecoratorModuleDescriptor.class);
    }

    @Override
    public Iterable<SearchDecorator> get() {
        return this.pluginModuleTracker.getModules();
    }
}

