/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.spring.container.ComponentNotFoundException
 *  com.atlassian.spring.container.ContainerContext
 */
package com.atlassian.confluence.spring;

import com.atlassian.event.Event;
import com.atlassian.spring.container.ComponentNotFoundException;
import com.atlassian.spring.container.ContainerContext;

public class DelegatingContainerContext
implements ContainerContext {
    protected final ContainerContext delegate;

    public DelegatingContainerContext(ContainerContext delegate) {
        this.delegate = delegate;
    }

    public Object getComponent(Object key) throws ComponentNotFoundException {
        return this.delegate.getComponent(key);
    }

    public Object createComponent(Class clazz) {
        return this.delegate.createComponent(clazz);
    }

    public Object createCompleteComponent(Class clazz) {
        return this.delegate.createCompleteComponent(clazz);
    }

    public void autowireComponent(Object component) {
        this.delegate.autowireComponent(component);
    }

    public void refresh() {
        this.delegate.refresh();
    }

    public boolean isSetup() {
        return this.delegate.isSetup();
    }

    public void publishEvent(Event e) {
        this.delegate.publishEvent(e);
    }
}

