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
import com.atlassian.plugin.event.events.PluginFrameworkEvent;

@PublicApi
public class PluginFrameworkDelayedEvent
extends PluginFrameworkEvent {
    public PluginFrameworkDelayedEvent(PluginController pluginController, PluginAccessor pluginAccessor) {
        super(pluginController, pluginAccessor);
    }
}

