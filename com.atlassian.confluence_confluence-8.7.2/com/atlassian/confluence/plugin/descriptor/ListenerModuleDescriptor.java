/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;

public class ListenerModuleDescriptor
extends AbstractModuleDescriptor<Object>
implements PluginModuleFactory<Object> {
    private PluginModuleHolder<Object> eventListener = PluginModuleHolder.getInstance(this);
    private EventPublisher eventPublisher;

    public ListenerModuleDescriptor(ModuleFactory moduleFactory, EventPublisher eventPublisher) {
        super(moduleFactory);
        this.eventPublisher = eventPublisher;
    }

    public Object getModule() {
        return this.eventListener.getModule();
    }

    public void enabled() {
        super.enabled();
        this.eventListener.enabled(this.getModuleClass());
        this.eventPublisher.register(this.eventListener.getModule());
    }

    @Override
    public Object createModule() {
        return this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }

    public void disabled() {
        if (this.eventListener.isEnabled()) {
            this.eventPublisher.unregister(this.getModule());
            this.eventListener.disabled();
        }
        super.disabled();
    }
}

