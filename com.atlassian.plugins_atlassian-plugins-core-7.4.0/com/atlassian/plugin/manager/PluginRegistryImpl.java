/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginRegistry$ReadWrite
 *  io.atlassian.util.concurrent.CopyOnWriteMap
 */
package com.atlassian.plugin.manager;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginRegistry;
import io.atlassian.util.concurrent.CopyOnWriteMap;
import java.util.Collection;
import java.util.Map;

public final class PluginRegistryImpl
implements PluginRegistry.ReadWrite {
    private final Map<String, Plugin> plugins = CopyOnWriteMap.builder().stableViews().newHashMap();

    public Collection<Plugin> getAll() {
        return this.plugins.values();
    }

    public Plugin get(String pluginKey) {
        return this.plugins.get(pluginKey);
    }

    public void clear() {
        this.plugins.clear();
    }

    public void put(Plugin plugin) {
        this.plugins.put(plugin.getKey(), plugin);
    }

    public Plugin remove(String key) {
        return this.plugins.remove(key);
    }
}

