/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.eventlistener.descriptors.EventListenerModuleDescriptor
 *  com.atlassian.plugin.scope.ScopeManager
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 */
package com.atlassian.event.internal;

import com.atlassian.event.internal.ClassUtils;
import com.atlassian.event.internal.EventPublisherImpl;
import com.atlassian.event.internal.ListenerInvokerWithClassHierarchyAndRegisterOrder;
import com.atlassian.event.internal.ListenerInvokerWithRegisterOrder;
import com.atlassian.event.spi.ListenerInvoker;
import com.atlassian.plugin.eventlistener.descriptors.EventListenerModuleDescriptor;
import com.atlassian.plugin.scope.ScopeManager;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;

class EventPublisherUtils {
    private static final String PROPERTY_PREFIX = EventPublisherImpl.class.getName();
    private static final Optional<String> DEBUG_REGISTRATION = Optional.ofNullable(System.getProperty(PROPERTY_PREFIX + ".debugRegistration"));
    private static final boolean DEBUG_REGISTRATION_LOCATION = Boolean.getBoolean(PROPERTY_PREFIX + ".debugRegistrationLocation");
    private static final Optional<String> DEBUG_INVOCATION = Optional.ofNullable(System.getProperty(PROPERTY_PREFIX + ".debugInvocation"));
    private static final boolean DEBUG_INVOCATION_LOCATION = Boolean.getBoolean(PROPERTY_PREFIX + ".debugInvocationLocation");

    EventPublisherUtils() {
    }

    static Set<ListenerInvokerWithClassHierarchyAndRegisterOrder> getInvokersWithClassHierarchyOrder(Object event, Function<Class<?>, Collection<ListenerInvokerWithRegisterOrder>> eventToListeners) {
        HashSet<ListenerInvokerWithClassHierarchyAndRegisterOrder> invokers = new HashSet<ListenerInvokerWithClassHierarchyAndRegisterOrder>();
        AtomicInteger classHierarchyOrder = new AtomicInteger();
        for (Class<?> eventClass : ClassUtils.findAllTypes(Preconditions.checkNotNull((Object)event).getClass())) {
            invokers.addAll(eventToListeners.apply(eventClass).stream().map(invoker -> new ListenerInvokerWithClassHierarchyAndRegisterOrder((ListenerInvokerWithRegisterOrder)invoker, classHierarchyOrder.get())).collect(Collectors.toList()));
            classHierarchyOrder.incrementAndGet();
        }
        return invokers;
    }

    static Set<ListenerInvoker> sortInvokers(ScopeManager scopeManager, Set<ListenerInvokerWithClassHierarchyAndRegisterOrder> invokers) {
        Comparator<ListenerInvokerWithClassHierarchyAndRegisterOrder> byDeclaredOrderThenClassHierarychOrderThenRegisterOrder = Comparator.comparingInt(value -> value.getListenerInvokerWithRegisterOrder().getOrder()).thenComparingInt(value -> value.classHierarchyOrder).thenComparingInt(value -> value.getListenerInvokerWithRegisterOrder().getRegisterOrder());
        return invokers.stream().filter(i -> i.keyedListenerInvoker.getScope().map(arg_0 -> ((ScopeManager)scopeManager).isScopeActive(arg_0)).orElse(true)).sorted(byDeclaredOrderThenClassHierarychOrderThenRegisterOrder).map(ListenerInvokerWithClassHierarchyAndRegisterOrder::getListenerInvokerWithRegisterOrder).map(ListenerInvokerWithRegisterOrder::getInvoker).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    static Object getListener(Object listener) {
        if (listener instanceof EventListenerModuleDescriptor) {
            EventListenerModuleDescriptor descriptor = (EventListenerModuleDescriptor)listener;
            return descriptor.getModule();
        }
        return listener;
    }

    static boolean shouldDebugThisInvocation(Object event) {
        String eventClassName = event.getClass().getName();
        return DEBUG_INVOCATION.map(eventClassName::startsWith).orElse(false);
    }

    static void logInvocation(Logger log, Object event, ListenerInvoker invoker) {
        log.warn("Listener invoked event with class '{}' -> invoker {}", (Object)event.getClass().getName(), (Object)invoker);
        if (DEBUG_INVOCATION_LOCATION) {
            log.warn("Invoked from", (Throwable)new Exception());
        }
    }

    static void logRegistration(Logger log, Class<?> eventClass, ListenerInvoker invoker) {
        DEBUG_REGISTRATION.ifPresent(classPrefix -> {
            if (eventClass.getName().startsWith((String)classPrefix)) {
                log.warn("Listener registered event '{}' -> invoker {}", (Object)eventClass, (Object)invoker);
                if (DEBUG_REGISTRATION_LOCATION) {
                    log.warn("Registered from", (Throwable)new Exception());
                }
            }
        });
    }
}

