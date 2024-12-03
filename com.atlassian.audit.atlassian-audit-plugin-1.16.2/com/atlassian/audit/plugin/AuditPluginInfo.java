/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.audit.plugin;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.plugin.PluginAccessor;

public class AuditPluginInfo {
    @VisibleForTesting
    public static final String PLUGIN_KEY = "com.atlassian.audit.atlassian-audit-plugin";
    private final String pluginVersion;

    public AuditPluginInfo(PluginAccessor pluginAccessor) {
        this.pluginVersion = pluginAccessor.getPlugin(PLUGIN_KEY).getPluginInformation().getVersion().replaceAll("[^0-9.]", "");
    }

    public String getPluginVersion() {
        return this.pluginVersion;
    }

    public String getPluginKey() {
        return PLUGIN_KEY;
    }
}

