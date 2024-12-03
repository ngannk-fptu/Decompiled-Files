/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.opensymphony.xwork2.config.entities.ResultConfig
 */
package com.atlassian.confluence.plugin.struts;

import com.atlassian.plugin.Plugin;
import com.opensymphony.xwork2.config.entities.ResultConfig;

public class PluginAwareResultConfig
extends ResultConfig {
    private Plugin plugin;

    public PluginAwareResultConfig(ResultConfig config, Plugin plugin) {
        super(config);
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }
}

