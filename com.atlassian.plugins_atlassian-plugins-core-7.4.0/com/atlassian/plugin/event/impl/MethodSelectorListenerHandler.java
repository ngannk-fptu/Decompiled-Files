/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.spi.ListenerHandler
 *  com.atlassian.event.spi.ListenerInvoker
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 */
package com.atlassian.plugin.event.impl;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.spi.ListenerHandler;
import com.atlassian.event.spi.ListenerInvoker;
import com.atlassian.plugin.event.impl.ListenerMethodSelector;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

final class MethodSelectorListenerHandler
implements ListenerHandler {
    private final ListenerMethodSelector listenerMethodSelector;

    public MethodSelectorListenerHandler(ListenerMethodSelector listenerMethodSelector) {
        this.listenerMethodSelector = listenerMethodSelector;
    }

    public List<ListenerInvoker> getInvokers(Object listener) {
        List<Method> validMethods = this.getValidMethods(Preconditions.checkNotNull((Object)listener));
        return Lists.newArrayList((Iterable)Iterables.transform(validMethods, method -> new ListenerInvoker((Method)method, listener){
            final /* synthetic */ Method val$method;
            final /* synthetic */ Object val$listener;
            {
                this.val$method = method;
                this.val$listener = object;
            }

            public Set<Class<?>> getSupportedEventTypes() {
                return Sets.newHashSet((Object[])this.val$method.getParameterTypes());
            }

            public void invoke(Object event) {
                try {
                    this.val$method.invoke(this.val$listener, event);
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                catch (InvocationTargetException e) {
                    if (e.getCause() == null) {
                        throw new RuntimeException(e);
                    }
                    if (e.getCause().getMessage() == null) {
                        throw new RuntimeException(e.getCause());
                    }
                    throw new RuntimeException(e.getCause().getMessage(), e);
                }
            }

            public Optional<String> getScope() {
                EventListener annotation = this.val$method.getAnnotation(EventListener.class);
                return Optional.ofNullable(annotation).map(EventListener::scope).filter(scopeName -> !"".equals(scopeName));
            }

            public int getOrder() {
                EventListener annotation = this.val$method.getAnnotation(EventListener.class);
                int order = annotation != null ? annotation.order() : 0;
                return order;
            }

            public boolean supportAsynchronousEvents() {
                return true;
            }
        }));
    }

    private List<Method> getValidMethods(Object listener) {
        ArrayList listenerMethods = Lists.newArrayList();
        for (Method method : listener.getClass().getMethods()) {
            if (!this.isValidMethod(method)) continue;
            listenerMethods.add(method);
        }
        return listenerMethods;
    }

    private boolean isValidMethod(Method method) {
        if (this.listenerMethodSelector.isListenerMethod(method)) {
            if (this.hasOneAndOnlyOneParameter(method)) {
                return true;
            }
            throw new RuntimeException("Method <" + method + "> of class <" + method.getDeclaringClass() + "> is being registered as a listener but has 0 or more than 1 parameters! Listener methods MUST have 1 and only 1 parameter.");
        }
        return false;
    }

    private boolean hasOneAndOnlyOneParameter(Method method) {
        return method.getParameterTypes().length == 1;
    }
}

