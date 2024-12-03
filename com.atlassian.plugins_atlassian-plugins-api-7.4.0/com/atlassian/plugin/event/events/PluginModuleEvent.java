/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.plugin.event.events;

import com.atlassian.annotations.PublicApi;
import com.atlassian.plugin.ModuleDescriptor;
import java.util.Objects;

@PublicApi
public class PluginModuleEvent {
    private final ModuleDescriptor<?> module;

    public PluginModuleEvent(ModuleDescriptor<?> module) {
        this.module = Objects.requireNonNull(module);
    }

    public ModuleDescriptor<?> getModule() {
        return this.module;
    }

    public String toString() {
        return this.getClass().getName() + " for " + this.module;
    }
}

