/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Sets
 */
package com.atlassian.event.internal;

import com.atlassian.event.spi.ListenerInvoker;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

final class SingleParameterMethodListenerInvoker
implements ListenerInvoker {
    private final Method method;
    private final Object listener;
    private final Optional<String> scope;
    private final int order;

    public SingleParameterMethodListenerInvoker(Object listener, Method method) {
        this(listener, method, Optional.empty(), 0);
    }

    public SingleParameterMethodListenerInvoker(Object listener, Method method, Optional<String> scope, int order) {
        this.listener = Preconditions.checkNotNull((Object)listener);
        this.method = (Method)Preconditions.checkNotNull((Object)method);
        this.scope = (Optional)Preconditions.checkNotNull(scope);
        this.order = order;
    }

    @Override
    public Set<Class<?>> getSupportedEventTypes() {
        return Sets.newHashSet((Object[])this.method.getParameterTypes());
    }

    @Override
    public void invoke(Object event) {
        try {
            this.method.invoke(this.listener, event);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(this.getInvocationErrorMessage(event), e);
        }
        catch (InvocationTargetException e) {
            if (e.getCause() == null) {
                throw new RuntimeException(this.getInvocationErrorMessage(event), e);
            }
            if (e.getCause().getMessage() == null) {
                throw new RuntimeException(this.getInvocationErrorMessage(event), e.getCause());
            }
            throw new RuntimeException(e.getCause().getMessage() + ". " + this.getInvocationErrorMessage(event), e.getCause());
        }
    }

    @Override
    public boolean supportAsynchronousEvents() {
        return true;
    }

    @Override
    public Optional<String> getScope() {
        return this.scope;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public String toString() {
        return "SingleParameterMethodListenerInvoker{method=" + this.method + ", listener=" + this.paranoidToString(this.listener) + '}';
    }

    private String paranoidToString(Object object) {
        try {
            return String.valueOf(object);
        }
        catch (RuntimeException e) {
            return object.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(object));
        }
    }

    private String getInvocationErrorMessage(Object event) {
        return "Listener: " + this.listener.getClass().getName() + " event: " + event.getClass().getName();
    }
}

