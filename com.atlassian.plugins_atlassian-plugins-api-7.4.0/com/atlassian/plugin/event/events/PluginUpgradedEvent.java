/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.plugin.event.events;

import com.atlassian.annotations.PublicApi;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.event.events.PluginEvent;

@PublicApi
public class PluginUpgradedEvent
extends PluginEvent {
    public PluginUpgradedEvent(Plugin plugin) {
        super(plugin);
    }
}

