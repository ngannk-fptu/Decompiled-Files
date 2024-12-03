/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.AbstractInterceptor
 */
package com.atlassian.confluence.event;

import com.atlassian.confluence.event.Evented;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class EventPublisherInterceptor
extends AbstractInterceptor {
    private final Supplier<EventPublisher> eventPublisher = new LazyComponentReference("eventPublisher");

    public String intercept(ActionInvocation invocation) throws Exception {
        String result = invocation.invoke();
        this.after(invocation, result);
        return result;
    }

    private void after(ActionInvocation actionInvocation, String result) {
        Object o;
        EventPublisher publisher = this.getEventPublisher();
        if (publisher == null) {
            return;
        }
        Action action = (Action)actionInvocation.getAction();
        if (action instanceof Evented && (o = ((Evented)action).getEventToPublish(result)) != null) {
            publisher.publish(o);
        }
    }

    private EventPublisher getEventPublisher() {
        if (!ContainerManager.isContainerSetup()) {
            return null;
        }
        return (EventPublisher)this.eventPublisher.get();
    }
}

