/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.analytics.client.eventfilter.whitelist;

import com.atlassian.analytics.client.eventfilter.whitelist.AnalyticsWhitelistModuleDescriptor;
import com.atlassian.analytics.client.eventfilter.whitelist.Whitelist;
import com.atlassian.analytics.client.eventfilter.whitelist.WhitelistCollector;
import com.atlassian.plugin.PluginAccessor;
import java.util.ArrayList;
import java.util.List;

public class PluginsWhitelistCollector
implements WhitelistCollector {
    private final PluginAccessor pluginAccessor;

    public PluginsWhitelistCollector(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public List<Whitelist> collectExternalWhitelists() {
        ArrayList<Whitelist> externalWhitelists = new ArrayList<Whitelist>();
        for (AnalyticsWhitelistModuleDescriptor descriptor : this.pluginAccessor.getEnabledModuleDescriptorsByClass(AnalyticsWhitelistModuleDescriptor.class)) {
            Whitelist whitelist = descriptor.getWhitelist();
            if (whitelist == null) continue;
            externalWhitelists.add(whitelist);
        }
        return externalWhitelists;
    }
}

