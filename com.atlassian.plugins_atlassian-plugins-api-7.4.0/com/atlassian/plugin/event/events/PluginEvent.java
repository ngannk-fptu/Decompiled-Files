/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.plugin.event.events;

import com.atlassian.annotations.PublicApi;
import com.atlassian.plugin.Plugin;
import java.util.Objects;

@PublicApi
public class PluginEvent {
    private final Plugin plugin;

    public PluginEvent(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public String toString() {
        return this.getClass().getName() + " for " + this.plugin;
    }
}

