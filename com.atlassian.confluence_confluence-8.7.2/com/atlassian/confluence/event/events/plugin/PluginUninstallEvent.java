/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.plugin;

import com.atlassian.confluence.event.events.plugin.PluginEvent;

public class PluginUninstallEvent
extends PluginEvent {
    private static final long serialVersionUID = -870917020780033661L;
    private String pluginName;

    public PluginUninstallEvent(Object src, String pluginKey) {
        super(src, pluginKey);
        this.pluginName = null;
    }

    public PluginUninstallEvent(Object src, String pluginKey, String pluginName) {
        super(src, pluginKey);
        this.pluginName = pluginName;
    }

    public String getPluginName() {
        return this.pluginName;
    }
}

