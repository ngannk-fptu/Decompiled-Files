/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.scope.ScopeManager
 *  javax.annotation.Nonnull
 */
package com.atlassian.event.internal;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.event.config.ListenerHandlersConfiguration;
import com.atlassian.event.internal.EventPublisherImpl;
import com.atlassian.event.internal.InvokerTransformer;
import com.atlassian.event.spi.EventDispatcher;
import com.atlassian.event.spi.ListenerInvoker;
import com.atlassian.plugin.scope.ScopeManager;
import javax.annotation.Nonnull;

@Deprecated
public final class LockFreeEventPublisher
implements EventPublisher {
    private final EventPublisher delegate;

    public LockFreeEventPublisher(EventDispatcher eventDispatcher, ListenerHandlersConfiguration configuration, ScopeManager scopeManager) {
        this(eventDispatcher, configuration);
    }

    public LockFreeEventPublisher(EventDispatcher eventDispatcher, ListenerHandlersConfiguration configuration) {
        this(eventDispatcher, configuration, (Iterable<ListenerInvoker> invokers, Object event) -> invokers);
    }

    public LockFreeEventPublisher(EventDispatcher eventDispatcher, ListenerHandlersConfiguration listenerHandlersConfiguration, InvokerTransformer transformer) {
        this.delegate = new EventPublisherImpl(eventDispatcher, listenerHandlersConfiguration, transformer);
    }

    @Override
    public void publish(@Nonnull Object event) {
        this.delegate.publish(event);
    }

    @Override
    public void register(@Nonnull Object listener) {
        this.delegate.register(listener);
    }

    @Override
    public void unregister(@Nonnull Object listener) {
        this.delegate.unregister(listener);
    }

    @Override
    public void unregisterAll() {
        this.delegate.unregisterAll();
    }
}

