/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.mywork.providers.confluence;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.mywork.providers.confluence.ConfluenceEventListener;
import com.atlassian.mywork.providers.confluence.ConfluenceTasksEventListener;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class ConfluenceEventRegister
implements InitializingBean,
DisposableBean,
LifecycleAware {
    private final EventPublisher eventPublisher;
    private final List<?> listeners;

    public ConfluenceEventRegister(EventPublisher eventPublisher, ConfluenceEventListener eventListener, ConfluenceTasksEventListener tasksEventListener) {
        this.eventPublisher = eventPublisher;
        this.listeners = Arrays.asList(eventListener, tasksEventListener);
    }

    public void afterPropertiesSet() {
        this.listeners.forEach(arg_0 -> ((EventPublisher)this.eventPublisher).register(arg_0));
    }

    public void destroy() {
        this.listeners.forEach(arg_0 -> ((EventPublisher)this.eventPublisher).unregister(arg_0));
    }

    public void onStart() {
    }

    public void onStop() {
    }
}

