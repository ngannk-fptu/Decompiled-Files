/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.context.ApplicationEvent
 */
package com.atlassian.event.legacy;

import com.atlassian.event.Event;
import com.atlassian.event.EventListener;
import com.google.common.base.Preconditions;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;

public class SpringContextEventPublisher
implements ApplicationContextAware,
EventListener {
    private ApplicationContext applicationContext;

    @Override
    public void handleEvent(Event event) {
        ((ApplicationContext)Preconditions.checkNotNull((Object)this.applicationContext)).publishEvent((ApplicationEvent)event);
    }

    @Override
    public Class[] getHandledEventClasses() {
        return new Class[]{Event.class};
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = (ApplicationContext)Preconditions.checkNotNull((Object)applicationContext);
    }

    public String toString() {
        return "SpringContextEventPublisher{applicationContext=" + this.applicationContext + '}';
    }
}

