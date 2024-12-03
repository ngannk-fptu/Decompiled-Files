/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.eventlistener.descriptors.EventListenerModuleDescriptor
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.atlassian.event.internal;

import com.atlassian.event.config.ListenerHandlersConfiguration;
import com.atlassian.event.spi.ListenerHandler;
import com.atlassian.event.spi.ListenerInvoker;
import com.atlassian.plugin.eventlistener.descriptors.EventListenerModuleDescriptor;
import com.google.common.collect.ImmutableList;
import java.util.Objects;

class InvokerBuilder {
    private final Iterable<ListenerHandler> listenerHandlers;

    InvokerBuilder(ListenerHandlersConfiguration listenerHandlersConfiguration) {
        this.listenerHandlers = Objects.requireNonNull(listenerHandlersConfiguration.getListenerHandlers());
    }

    Iterable<ListenerInvoker> build(Object listenerOrMd) throws IllegalArgumentException {
        Object listener = this.getListener(listenerOrMd);
        ImmutableList.Builder builder = ImmutableList.builder();
        for (ListenerHandler listenerHandler : this.listenerHandlers) {
            builder.addAll(listenerHandler.getInvokers(listener));
        }
        ImmutableList invokers = builder.build();
        if (invokers.isEmpty()) {
            throw new IllegalArgumentException("No listener invokers were found for listener <" + listener + ">");
        }
        return invokers;
    }

    private Object getListener(Object listenerOrMd) {
        if (listenerOrMd instanceof EventListenerModuleDescriptor) {
            EventListenerModuleDescriptor descriptor = (EventListenerModuleDescriptor)listenerOrMd;
            return descriptor.getModule();
        }
        return listenerOrMd;
    }
}

