/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.business.insights.core.plugin;

import com.atlassian.plugin.PluginAccessor;

public class CorePluginInfo {
    private static final String PLUGIN_KEY = "com.atlassian.business.insights.core-plugin";
    private final String pluginVersion;

    public CorePluginInfo(PluginAccessor pluginAccessor) {
        this.pluginVersion = pluginAccessor.getPlugin(PLUGIN_KEY).getPluginInformation().getVersion().replaceAll("[^0-9.]", "");
    }

    public String getPluginVersion() {
        return this.pluginVersion;
    }

    public String getPluginKey() {
        return PLUGIN_KEY;
    }
}

