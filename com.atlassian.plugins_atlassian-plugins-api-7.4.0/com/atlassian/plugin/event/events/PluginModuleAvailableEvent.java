/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.plugin.event.events;

import com.atlassian.annotations.PublicApi;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.event.events.PluginModuleEvent;

@PublicApi
public class PluginModuleAvailableEvent
extends PluginModuleEvent {
    public PluginModuleAvailableEvent(ModuleDescriptor module) {
        super(module);
    }
}

