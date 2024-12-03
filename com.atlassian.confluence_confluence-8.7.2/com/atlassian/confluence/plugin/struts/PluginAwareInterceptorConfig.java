/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.opensymphony.xwork2.config.entities.InterceptorConfig
 */
package com.atlassian.confluence.plugin.struts;

import com.atlassian.plugin.Plugin;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;

public class PluginAwareInterceptorConfig
extends InterceptorConfig {
    private Plugin plugin;

    public PluginAwareInterceptorConfig(InterceptorConfig config, Plugin plugin) {
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

