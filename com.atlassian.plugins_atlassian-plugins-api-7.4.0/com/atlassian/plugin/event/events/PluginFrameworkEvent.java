/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.plugin.event.events;

import com.atlassian.annotations.PublicApi;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import java.util.Objects;

@PublicApi
public class PluginFrameworkEvent {
    private final PluginController pluginController;
    private final PluginAccessor pluginAccessor;

    public PluginFrameworkEvent(PluginController pluginController, PluginAccessor pluginAccessor) {
        this.pluginController = Objects.requireNonNull(pluginController);
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
    }

    public PluginController getPluginController() {
        return this.pluginController;
    }

    public PluginAccessor getPluginAccessor() {
        return this.pluginAccessor;
    }
}

