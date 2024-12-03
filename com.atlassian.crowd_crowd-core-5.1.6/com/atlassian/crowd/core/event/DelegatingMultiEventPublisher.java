/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.crowd.core.event;

import com.atlassian.crowd.core.event.MultiEventPublisher;
import com.atlassian.event.api.EventPublisher;
import java.util.Collection;

public class DelegatingMultiEventPublisher
implements MultiEventPublisher {
    protected final EventPublisher delegate;

    public DelegatingMultiEventPublisher(EventPublisher delegate) {
        this.delegate = delegate;
    }

    @Override
    public void publishAll(Collection<Object> events) {
        events.forEach(arg_0 -> ((EventPublisher)this.delegate).publish(arg_0));
    }

    public void publish(Object event) {
        this.delegate.publish(event);
    }

    public void register(Object listener) {
        this.delegate.register(listener);
    }

    public void unregister(Object listener) {
        this.delegate.unregister(listener);
    }

    public void unregisterAll() {
        this.delegate.unregisterAll();
    }
}

