/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginRestartState
 *  com.atlassian.plugin.manager.PluginEnabledState
 *  com.atlassian.plugin.manager.PluginPersistentState
 */
package com.atlassian.confluence.plugin;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginRestartState;
import com.atlassian.plugin.manager.PluginEnabledState;
import com.atlassian.plugin.manager.PluginPersistentState;
import java.util.Collections;
import java.util.Map;

public class EmptyPluginPersistentState
implements PluginPersistentState {
    private Map<String, PluginEnabledState> map = Collections.emptyMap();

    public Map<String, PluginEnabledState> getStatesMap() {
        return this.map;
    }

    public boolean isEnabled(Plugin plugin) {
        return plugin.isEnabledByDefault();
    }

    public boolean isEnabled(ModuleDescriptor<?> moduleDescriptor) {
        if (moduleDescriptor == null) {
            return false;
        }
        return moduleDescriptor.isEnabledByDefault();
    }

    public Map<String, PluginEnabledState> getPluginEnabledStateMap(Plugin plugin) {
        return this.map;
    }

    public PluginRestartState getPluginRestartState(String s) {
        return PluginRestartState.NONE;
    }
}

