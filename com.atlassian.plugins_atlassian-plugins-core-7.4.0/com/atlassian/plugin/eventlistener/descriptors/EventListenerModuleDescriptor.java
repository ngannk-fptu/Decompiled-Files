/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.RequirePermission
 *  com.atlassian.plugin.module.ModuleFactory
 *  io.atlassian.util.concurrent.LazyReference
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 */
package com.atlassian.plugin.eventlistener.descriptors;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.RequirePermission;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import io.atlassian.util.concurrent.LazyReference;
import javax.annotation.Nonnull;
import org.dom4j.Element;

@RequirePermission(value={"execute_java"})
public class EventListenerModuleDescriptor
extends AbstractModuleDescriptor<Object> {
    static final String FALLBACK_MODE = "EventListenerModuleDescriptor.Fallback.Mode";
    private final EventPublisher eventPublisher;
    private LazyReference<Object> moduleObj = new LazyReference<Object>(){

        protected Object create() throws Exception {
            return EventListenerModuleDescriptor.this.moduleFactory.createModule(EventListenerModuleDescriptor.this.moduleClassName, (ModuleDescriptor)EventListenerModuleDescriptor.this);
        }
    };

    public EventListenerModuleDescriptor(ModuleFactory moduleFactory, EventPublisher eventPublisher) {
        super(moduleFactory);
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void init(@Nonnull Plugin plugin, @Nonnull Element element) {
        super.init(plugin, element);
        this.checkPermissions();
    }

    @Override
    public Object getModule() {
        return this.moduleObj.get();
    }

    @Override
    public void enabled() {
        super.enabled();
        if (Boolean.getBoolean(FALLBACK_MODE)) {
            this.eventPublisher.register(this.getModule());
        } else {
            this.eventPublisher.register((Object)this);
        }
    }

    @Override
    public void disabled() {
        if (Boolean.getBoolean(FALLBACK_MODE)) {
            this.eventPublisher.unregister(this.getModule());
        } else {
            this.eventPublisher.unregister((Object)this);
        }
        super.disabled();
    }
}

