/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.MapMaker
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.event.internal;

import com.atlassian.event.internal.ClassUtils;
import com.atlassian.event.internal.ComparableListenerInvoker;
import com.atlassian.event.internal.EventPublisherImpl;
import com.atlassian.event.internal.InvokerBuilder;
import com.atlassian.event.internal.InvokerRegistry;
import com.atlassian.event.spi.ListenerInvoker;
import com.google.common.collect.MapMaker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ListenerRegistry {
    private static final Logger log = LoggerFactory.getLogger(ListenerRegistry.class);
    private final AtomicInteger registerSeq;
    private final ConcurrentMap<Class<?>, InvokerRegistry> invokerRegistries = new MapMaker().weakKeys().makeMap();
    private final AtomicReference<ConcurrentMap<Class<?>, Iterable<ListenerInvoker>>> invokerCache = new AtomicReference(ListenerRegistry.createInvokerCacheInstance());
    private final InvokerBuilder invokerBuilder;

    ListenerRegistry(InvokerBuilder invokerBuilder) {
        this.invokerBuilder = invokerBuilder;
        this.registerSeq = new AtomicInteger();
    }

    void register(Object listener) {
        this.invokerBuilder.build(listener).forEach(invoker -> this.register(listener, (ListenerInvoker)invoker));
        this.clearInvokerCache();
    }

    Iterable<ListenerInvoker> findListenerInvokers(Object event) {
        return this.invokerCache.get().computeIfAbsent(Objects.requireNonNull(event).getClass(), eventClass -> {
            ArrayList unsorted = new ArrayList();
            AtomicInteger classHierarchyOrder = new AtomicInteger();
            for (Class<?> eventType : ClassUtils.findAllTypes(eventClass)) {
                this.invokerRegistries.getOrDefault(eventType, InvokerRegistry.EMPTY).forEach(x -> unsorted.add(new ComparableListenerInvoker(x.getListenerInvoker(), classHierarchyOrder.get(), x.getOrder())));
                classHierarchyOrder.incrementAndGet();
            }
            return unsorted.stream().sorted().distinct().collect(Collectors.toList());
        });
    }

    private void register(Object listener, ListenerInvoker invoker) {
        Set<Class<Object>> supportedEventTypes = invoker.getSupportedEventTypes();
        if (supportedEventTypes.isEmpty()) {
            supportedEventTypes = Collections.singleton(Object.class);
        }
        for (Class<?> eventClass : supportedEventTypes) {
            EventPublisherImpl.debugRegistration.ifPresent(classPrefix -> {
                if (eventClass.getName().startsWith((String)classPrefix)) {
                    log.warn("Listener registered event '{}' -> invoker {}", (Object)eventClass, (Object)invoker);
                    if (EventPublisherImpl.debugRegistrationLocation) {
                        log.warn("Registered from", (Throwable)new Exception());
                    }
                }
            });
            this.invokerRegistries.computeIfAbsent(eventClass, k -> new InvokerRegistry()).add(listener, invoker, this.registerSeq.get());
        }
        this.registerSeq.getAndIncrement();
    }

    void remove(Object listener) {
        this.invokerRegistries.forEach((k, registry) -> registry.remove(listener));
        this.clearInvokerCache();
    }

    void clear() {
        this.invokerRegistries.clear();
        this.clearInvokerCache();
    }

    private void clearInvokerCache() {
        this.invokerCache.set(ListenerRegistry.createInvokerCacheInstance());
    }

    private static ConcurrentMap<Class<?>, Iterable<ListenerInvoker>> createInvokerCacheInstance() {
        return new MapMaker().weakKeys().makeMap();
    }
}

