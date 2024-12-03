/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.plugin.event.events;

import com.atlassian.annotations.PublicApi;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.event.events.PluginEvent;
import com.atlassian.plugin.event.events.PluginModuleEvent;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Predicate;

@PublicApi
public class PluginTransactionEndEvent {
    private final long threadId;
    private final ImmutableList<Object> events;

    public PluginTransactionEndEvent(List<Object> events) {
        this.events = ImmutableList.copyOf(events);
        this.threadId = Thread.currentThread().getId();
    }

    @Deprecated
    public ImmutableList<Object> getEvents() {
        return this.events;
    }

    public List<Object> getUnmodifiableEvents() {
        return this.events;
    }

    public int numberOfEvents() {
        return this.events.size();
    }

    public <T> boolean hasAnyEventOfTypeMatching(Class<T> eventTypeClass, Predicate<T> anyMatchEventPredicate) {
        return this.events.stream().filter(eventTypeClass::isInstance).map(eventTypeClass::cast).anyMatch(anyMatchEventPredicate);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean hasAnyEventWithModuleDescriptorMatching(Predicate<ModuleDescriptor<?>> anyMatchModuleDescriptorPredicate) {
        if (this.events.stream().filter(PluginModuleEvent.class::isInstance).map(PluginModuleEvent.class::cast).map(PluginModuleEvent::getModule).anyMatch(anyMatchModuleDescriptorPredicate)) return true;
        if (!this.events.stream().filter(PluginEvent.class::isInstance).map(PluginEvent.class::cast).map(PluginEvent::getPlugin).flatMap(plugin -> plugin.getModuleDescriptors().stream()).anyMatch(anyMatchModuleDescriptorPredicate)) return false;
        return true;
    }

    public long threadId() {
        return this.threadId;
    }
}

