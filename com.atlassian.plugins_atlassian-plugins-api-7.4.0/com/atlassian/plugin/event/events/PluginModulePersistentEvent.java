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
public class PluginModulePersistentEvent
extends PluginModuleEvent {
    private final boolean persistent;

    public PluginModulePersistentEvent(ModuleDescriptor<?> module, boolean persistent) {
        super(module);
        this.persistent = persistent;
    }

    public boolean isPersistent() {
        return this.persistent;
    }
}

