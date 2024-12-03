/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.PageUpdateTrigger
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.analytics;

import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.plugins.tasklist.TaskStatus;
import com.atlassian.confluence.plugins.tasklist.analytics.ConfluenceTaskResolveEvent;
import com.atlassian.confluence.plugins.tasklist.event.ConfluenceTaskCreateEvent;
import com.atlassian.confluence.plugins.tasklist.event.ConfluenceTaskUpdateEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InlineTaskAnalyticsEventPublisher
implements DisposableBean {
    private final EventPublisher eventPublisher;

    @Autowired
    public InlineTaskAnalyticsEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        eventPublisher.register((Object)this);
    }

    @EventListener
    public void inlineTaskCreated(ConfluenceTaskCreateEvent event) {
        if (event.getTask().getStatus() == TaskStatus.CHECKED) {
            this.eventPublisher.publish((Object)new ConfluenceTaskResolveEvent(event.getTask(), PageUpdateTrigger.EDIT_PAGE));
        }
    }

    @EventListener
    public void inlineTaskResolved(ConfluenceTaskUpdateEvent event) {
        if (event.hasStatusChanged() && event.getTask().getStatus() == TaskStatus.CHECKED) {
            this.eventPublisher.publish((Object)new ConfluenceTaskResolveEvent(event.getTask(), event.getUpdateTrigger()));
        }
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }
}

