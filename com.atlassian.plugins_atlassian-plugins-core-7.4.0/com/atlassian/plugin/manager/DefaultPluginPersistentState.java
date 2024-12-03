/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginRestartState
 *  com.atlassian.plugin.manager.PluginEnabledState
 */
package com.atlassian.plugin.manager;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginRestartState;
import com.atlassian.plugin.manager.PluginEnabledState;
import com.atlassian.plugin.manager.PluginPersistentState;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class DefaultPluginPersistentState
implements Serializable,
PluginPersistentState {
    private final Map<String, PluginEnabledState> map;

    DefaultPluginPersistentState(Map<String, PluginEnabledState> map, boolean ignore) {
        this.map = Collections.unmodifiableMap(new HashMap<String, PluginEnabledState>(map));
    }

    public Map<String, PluginEnabledState> getStatesMap() {
        return Collections.unmodifiableMap(this.map);
    }

    public boolean isEnabled(Plugin plugin) {
        PluginEnabledState state = this.map.get(plugin.getKey());
        return state == null ? plugin.isEnabledByDefault() : state.isEnabled().booleanValue();
    }

    public boolean isEnabled(ModuleDescriptor<?> pluginModule) {
        if (pluginModule == null) {
            return false;
        }
        PluginEnabledState state = this.map.get(pluginModule.getCompleteKey());
        return state == null ? pluginModule.isEnabledByDefault() : state.isEnabled().booleanValue();
    }

    public Map<String, PluginEnabledState> getPluginEnabledStateMap(Plugin plugin) {
        return this.getStatesMap().entrySet().stream().filter(e -> ((String)e.getKey()).startsWith(plugin.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public PluginRestartState getPluginRestartState(String pluginKey) {
        for (PluginRestartState state : PluginRestartState.values()) {
            if (!this.map.containsKey(PluginPersistentState.Util.buildStateKey(pluginKey, state))) continue;
            return state;
        }
        return PluginRestartState.NONE;
    }

    public static Map<String, PluginEnabledState> getPluginEnabledStateMap(Map<String, Boolean> map) {
        return Collections.unmodifiableMap(new HashMap<String, PluginEnabledState>(map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> new PluginEnabledState(((Boolean)e.getValue()).booleanValue(), 0L)))));
    }
}

