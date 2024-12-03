/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginRestartState
 *  com.atlassian.plugin.manager.PluginEnabledState
 *  io.atlassian.fugue.Effect
 */
package com.atlassian.plugin.manager;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginRestartState;
import com.atlassian.plugin.manager.DefaultPluginPersistentState;
import com.atlassian.plugin.manager.PluginEnabledState;
import com.atlassian.plugin.manager.PluginPersistentState;
import com.atlassian.plugin.manager.PluginPersistentStateStore;
import io.atlassian.fugue.Effect;
import java.util.Map;

public class PluginPersistentStateModifier {
    private final PluginPersistentStateStore store;

    public PluginPersistentStateModifier(PluginPersistentStateStore store) {
        this.store = store;
    }

    public PluginPersistentState getState() {
        return this.store.load();
    }

    public synchronized void apply(Effect<PluginPersistentState.Builder> effect) {
        PluginPersistentState.Builder builder = PluginPersistentState.Builder.create(this.store.load());
        effect.apply((Object)builder);
        this.store.save(builder.toState());
    }

    public void setEnabled(Plugin plugin, boolean enabled) {
        this.apply((Effect<PluginPersistentState.Builder>)((Effect)builder -> builder.setEnabled(plugin, enabled)));
    }

    public void disable(Plugin plugin) {
        this.setEnabled(plugin, false);
    }

    public void enable(Plugin plugin) {
        this.setEnabled(plugin, true);
    }

    public void setEnabled(ModuleDescriptor<?> module, boolean enabled) {
        this.apply((Effect<PluginPersistentState.Builder>)((Effect)builder -> builder.setEnabled(module, enabled)));
    }

    public void disable(ModuleDescriptor<?> module) {
        this.setEnabled(module, false);
    }

    public void enable(ModuleDescriptor<?> module) {
        this.setEnabled(module, true);
    }

    public void clearPluginRestartState() {
        this.apply((Effect<PluginPersistentState.Builder>)((Effect)PluginPersistentState.Builder::clearPluginRestartState));
    }

    public void setPluginRestartState(String pluginKey, PluginRestartState pluginRestartState) {
        this.apply((Effect<PluginPersistentState.Builder>)((Effect)builder -> builder.setPluginRestartState(pluginKey, pluginRestartState)));
    }

    @Deprecated
    public void addState(Map<String, Boolean> state) {
        this.apply((Effect<PluginPersistentState.Builder>)((Effect)builder -> builder.addPluginEnabledState(DefaultPluginPersistentState.getPluginEnabledStateMap(state))));
    }

    public void addPluginEnabledState(Map<String, PluginEnabledState> state) {
        this.apply((Effect<PluginPersistentState.Builder>)((Effect)builder -> builder.addPluginEnabledState(state)));
    }

    public void removeState(Plugin plugin) {
        this.apply((Effect<PluginPersistentState.Builder>)((Effect)builder -> {
            builder.removeState(plugin.getKey());
            for (ModuleDescriptor moduleDescriptor : plugin.getModuleDescriptors()) {
                builder.removeState(moduleDescriptor.getCompleteKey());
            }
        }));
    }
}

