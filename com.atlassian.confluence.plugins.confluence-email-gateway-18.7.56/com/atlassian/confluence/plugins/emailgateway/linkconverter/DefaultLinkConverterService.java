/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.WeightedPluginModuleTracker
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 */
package com.atlassian.confluence.plugins.emailgateway.linkconverter;

import com.atlassian.confluence.plugin.descriptor.WeightedPluginModuleTracker;
import com.atlassian.confluence.plugins.emailgateway.api.LinkConverter;
import com.atlassian.confluence.plugins.emailgateway.api.descriptor.LinkConverterModuleDescriptor;
import com.atlassian.confluence.plugins.emailgateway.linkconverter.LinkConverterService;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;

public class DefaultLinkConverterService
implements LinkConverterService {
    private final WeightedPluginModuleTracker<LinkConverter<?, ?>, LinkConverterModuleDescriptor> pluginModuleTracker;

    public DefaultLinkConverterService(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager) {
        this.pluginModuleTracker = WeightedPluginModuleTracker.create((PluginAccessor)pluginAccessor, (PluginEventManager)pluginEventManager, LinkConverterModuleDescriptor.class);
    }

    @Override
    public Iterable<LinkConverter<?, ?>> getLinkConverters() {
        return this.pluginModuleTracker.getModules();
    }
}

