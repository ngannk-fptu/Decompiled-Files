/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.opensymphony.xwork2.config.entities.ActionConfig
 */
package com.atlassian.confluence.plugin.struts;

import com.atlassian.plugin.Plugin;
import com.opensymphony.xwork2.config.entities.ActionConfig;

public class PluginAwareActionConfig
extends ActionConfig {
    private Plugin plugin;

    public PluginAwareActionConfig(ActionConfig config, Plugin plugin) {
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

