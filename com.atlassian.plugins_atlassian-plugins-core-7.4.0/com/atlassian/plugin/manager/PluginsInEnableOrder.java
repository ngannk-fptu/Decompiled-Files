/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginRegistry$ReadOnly
 */
package com.atlassian.plugin.manager;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class PluginsInEnableOrder {
    final List<Plugin> sortedList = new ArrayList<Plugin>();

    public PluginsInEnableOrder(Collection<Plugin> pluginsToEnable, PluginRegistry.ReadOnly pluginRegistry) {
        HashSet<Plugin> visited = new HashSet<Plugin>();
        for (Plugin plugin : pluginsToEnable) {
            this.sortPluginForEnable(plugin, visited, pluginsToEnable, pluginRegistry);
        }
    }

    private void sortPluginForEnable(Plugin currentPlugin, Set<Plugin> visited, Collection<Plugin> allowedPlugins, PluginRegistry.ReadOnly pluginRegistry) {
        if (!visited.add(currentPlugin)) {
            return;
        }
        for (String key : currentPlugin.getDependencies().getAll()) {
            Plugin requiredPlugin = pluginRegistry.get(key);
            if (null == requiredPlugin) continue;
            this.sortPluginForEnable(requiredPlugin, visited, allowedPlugins, pluginRegistry);
        }
        if (allowedPlugins.contains(currentPlugin)) {
            this.sortedList.add(currentPlugin);
        }
    }

    public List<Plugin> get() {
        return Collections.unmodifiableList(this.sortedList);
    }
}

