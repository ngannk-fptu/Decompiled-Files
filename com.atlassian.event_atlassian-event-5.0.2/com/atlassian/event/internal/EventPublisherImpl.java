/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.scope.ScopeManager
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.ThreadSafe
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.event.internal;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.event.config.ListenerHandlersConfiguration;
import com.atlassian.event.internal.InvokerBuilder;
import com.atlassian.event.internal.InvokerTransformer;
import com.atlassian.event.internal.ListenerRegistry;
import com.atlassian.event.spi.EventDispatcher;
import com.atlassian.event.spi.ListenerInvoker;
import com.atlassian.plugin.scope.ScopeManager;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ThreadSafe
public final class EventPublisherImpl
implements EventPublisher {
    private static final Logger log = LoggerFactory.getLogger(EventPublisherImpl.class);
    private static final String PROPERTY_PREFIX = EventPublisherImpl.class.getName();
    static final Optional<String> debugRegistration = Optional.ofNullable(System.getProperty(PROPERTY_PREFIX + ".debugRegistration"));
    static final boolean debugRegistrationLocation = Boolean.getBoolean(PROPERTY_PREFIX + ".debugRegistrationLocation");
    static final Optional<String> debugInvocation = Optional.ofNullable(System.getProperty(PROPERTY_PREFIX + ".debugInvocation"));
    static final boolean debugInvocationLocation = Boolean.getBoolean(PROPERTY_PREFIX + ".debugInvocationLocation");
    private final ListenerRegistry listenerRegistry;
    private final EventDispatcher dispatcher;
    private final InvokerTransformer transformer;

    @Deprecated
    public EventPublisherImpl(EventDispatcher eventDispatcher, ListenerHandlersConfiguration configuration, ScopeManager scopeManager) {
        this(eventDispatcher, configuration, (Iterable<ListenerInvoker> invokers, Object event) -> invokers);
    }

    public EventPublisherImpl(EventDispatcher eventDispatcher, ListenerHandlersConfiguration configuration) {
        this(eventDispatcher, configuration, (Iterable<ListenerInvoker> invokers, Object event) -> invokers);
    }

    public EventPublisherImpl(EventDispatcher eventDispatcher, ListenerHandlersConfiguration listenerHandlersConfiguration, InvokerTransformer transformer) {
        this.dispatcher = Objects.requireNonNull(eventDispatcher);
        this.transformer = Objects.requireNonNull(transformer);
        InvokerBuilder invokerBuilder = new InvokerBuilder(Objects.requireNonNull(listenerHandlersConfiguration));
        ListenerRegistry listenerRegistry = new ListenerRegistry(invokerBuilder);
        this.listenerRegistry = Objects.requireNonNull(listenerRegistry);
    }

    @Override
    public void publish(@Nonnull Object event) {
        Objects.requireNonNull(event);
        Iterable<ListenerInvoker> invokers = this.listenerRegistry.findListenerInvokers(event);
        try {
            invokers = this.transformer.transformAll(invokers, event);
        }
        catch (Exception e) {
            log.error("Exception while transforming invokers. Dispatching original invokers instead.", (Throwable)e);
        }
        String eventClass = event.getClass().getName();
        boolean debugThisInvocation = debugInvocation.map(eventClass::startsWith).orElse(false);
        for (ListenerInvoker invoker : invokers) {
            try {
                if (debugThisInvocation) {
                    log.warn("Listener invoked event with class '{}' -> invoker {}", (Object)eventClass, (Object)invoker);
                    if (debugInvocationLocation) {
                        log.warn("Invoked from", (Throwable)new Exception());
                    }
                }
                this.dispatcher.dispatch(invoker, event);
            }
            catch (Exception e) {
                log.error("There was an exception thrown trying to dispatch event '{}' from the invoker '{}'.", new Object[]{event, invoker, e});
            }
        }
    }

    @Override
    public void register(@Nonnull Object listener) {
        Objects.requireNonNull(listener);
        this.unregister(listener);
        this.listenerRegistry.register(listener);
    }

    @Override
    public void unregister(@Nonnull Object listener) {
        this.listenerRegistry.remove(Objects.requireNonNull(listener));
    }

    @Override
    public void unregisterAll() {
        this.listenerRegistry.clear();
    }
}

