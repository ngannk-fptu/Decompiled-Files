/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginRestartState
 *  com.atlassian.plugin.StoredPluginState
 *  com.atlassian.plugin.manager.PluginEnabledState
 */
package com.atlassian.plugin.manager;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginRestartState;
import com.atlassian.plugin.StoredPluginState;
import com.atlassian.plugin.manager.DefaultPluginPersistentState;
import com.atlassian.plugin.manager.PluginEnabledState;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public interface PluginPersistentState
extends StoredPluginState {
    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(PluginPersistentState state) {
        return new Builder(state);
    }

    @Deprecated
    default public Map<String, Boolean> getMap() {
        return this.getStatesMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> ((PluginEnabledState)e.getValue()).isEnabled()));
    }

    @Deprecated
    default public Map<String, Boolean> getPluginStateMap(Plugin plugin) {
        return this.getPluginEnabledStateMap(plugin).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> ((PluginEnabledState)e.getValue()).isEnabled()));
    }

    public static class Util {
        public static final String RESTART_STATE_SEPARATOR = "--";

        static String buildStateKey(String pluginKey, PluginRestartState state) {
            return state.name() + RESTART_STATE_SEPARATOR + pluginKey;
        }
    }

    public static final class Builder {
        private final Map<String, PluginEnabledState> map = new HashMap<String, PluginEnabledState>();

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(PluginPersistentState state) {
            return new Builder(state);
        }

        Builder() {
        }

        Builder(PluginPersistentState state) {
            this.map.putAll(state.getStatesMap());
        }

        public PluginPersistentState toState() {
            return new DefaultPluginPersistentState(this.map, true);
        }

        public Builder setEnabled(ModuleDescriptor<?> pluginModule, boolean isEnabled) {
            this.setEnabled(pluginModule.getCompleteKey(), isEnabled);
            return this;
        }

        public Builder setEnabled(Plugin plugin, boolean isEnabled) {
            this.setEnabled(plugin.getKey(), isEnabled);
            return this;
        }

        private Builder setEnabled(String completeKey, boolean isEnabled) {
            this.map.put(completeKey, PluginEnabledState.getPluginEnabledStateWithCurrentTime((boolean)isEnabled));
            return this;
        }

        public Builder setState(PluginPersistentState state) {
            this.map.clear();
            this.map.putAll(state.getStatesMap());
            return this;
        }

        public Builder addPluginEnabledState(Map<String, PluginEnabledState> state) {
            this.map.putAll(state);
            return this;
        }

        @Deprecated
        public Builder addState(Map<String, Boolean> state) {
            this.map.putAll(state.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> new PluginEnabledState(((Boolean)e.getValue()).booleanValue(), 0L))));
            return this;
        }

        public Builder removeState(String key) {
            this.map.remove(key);
            return this;
        }

        public Builder setPluginRestartState(String pluginKey, PluginRestartState state) {
            for (PluginRestartState st : PluginRestartState.values()) {
                this.map.remove(Util.buildStateKey(pluginKey, st));
            }
            if (state != PluginRestartState.NONE) {
                this.map.put(Util.buildStateKey(pluginKey, state), PluginEnabledState.getPluginEnabledStateWithCurrentTime((boolean)true));
            }
            return this;
        }

        public Builder clearPluginRestartState() {
            HashSet<String> keys = new HashSet<String>(this.map.keySet());
            for (String key : keys) {
                if (!key.contains("--")) continue;
                this.map.remove(key);
            }
            return this;
        }
    }
}

