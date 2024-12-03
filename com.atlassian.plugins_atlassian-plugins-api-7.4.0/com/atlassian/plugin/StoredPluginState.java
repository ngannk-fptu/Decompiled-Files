/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginRestartState;
import com.atlassian.plugin.manager.PluginEnabledState;
import java.util.Map;

public interface StoredPluginState {
    public Map<String, PluginEnabledState> getStatesMap();

    public boolean isEnabled(Plugin var1);

    public boolean isEnabled(ModuleDescriptor<?> var1);

    public Map<String, PluginEnabledState> getPluginEnabledStateMap(Plugin var1);

    public PluginRestartState getPluginRestartState(String var1);
}

