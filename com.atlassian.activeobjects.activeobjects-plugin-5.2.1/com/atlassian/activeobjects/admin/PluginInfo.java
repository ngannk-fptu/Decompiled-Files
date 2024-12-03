/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginInformation
 */
package com.atlassian.activeobjects.admin;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginInformation;
import java.util.Objects;

public class PluginInfo {
    public final String key;
    public final String name;
    public final String version;
    public final String vendorName;
    public final String vendorUrl;

    public PluginInfo(String key, String name, String version, String vendorName, String vendorUrl) {
        this.key = Objects.requireNonNull(key);
        this.name = Objects.requireNonNull(name);
        this.version = Objects.requireNonNull(version);
        this.vendorName = vendorName;
        this.vendorUrl = vendorUrl;
    }

    public static PluginInfo of(Plugin plugin) {
        PluginInformation pluginInformation = plugin.getPluginInformation();
        return new PluginInfo(plugin.getKey(), plugin.getName(), pluginInformation.getVersion(), pluginInformation.getVendorName(), pluginInformation.getVendorUrl());
    }
}

