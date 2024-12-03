/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.event.events;

import com.atlassian.annotations.PublicApi;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginState;
import com.atlassian.plugin.event.events.PluginEvent;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

@PublicApi
public class PluginDependentsChangedEvent
extends PluginEvent {
    final PluginState state;
    final List<Plugin> disabled;
    final List<Plugin> cycled;

    public PluginDependentsChangedEvent(Plugin plugin, @Nonnull PluginState state, @Nonnull List<Plugin> disabled, @Nonnull List<Plugin> cycled) {
        super(plugin);
        this.state = Objects.requireNonNull(state);
        if (state != PluginState.INSTALLED && state != PluginState.ENABLED && state != PluginState.UNINSTALLED && state != PluginState.DISABLED) {
            throw new IllegalArgumentException("state must be one of INSTALLED, ENABLED, UNINSTALLED, DISABLED");
        }
        this.disabled = Objects.requireNonNull(disabled);
        this.cycled = Objects.requireNonNull(cycled);
    }

    public PluginState getState() {
        return this.state;
    }

    public List<Plugin> getDisabled() {
        return this.disabled;
    }

    public List<Plugin> getCycled() {
        return this.cycled;
    }

    @Override
    public String toString() {
        return super.toString() + ", disabled=" + this.disabled + ", cycled=" + this.cycled;
    }
}

