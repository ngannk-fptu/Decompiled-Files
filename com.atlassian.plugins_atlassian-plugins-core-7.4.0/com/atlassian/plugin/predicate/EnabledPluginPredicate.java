/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginState
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.predicate;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginState;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class EnabledPluginPredicate
implements Predicate<Plugin> {
    private final Set<Plugin> pluginsBeingEnabled;

    public EnabledPluginPredicate(Set<Plugin> pluginsBeingEnabled) {
        this.pluginsBeingEnabled = pluginsBeingEnabled;
    }

    @Override
    public boolean test(@Nonnull Plugin plugin) {
        return PluginState.ENABLED.equals((Object)plugin.getPluginState()) && !this.pluginsBeingEnabled.contains(plugin);
    }
}

