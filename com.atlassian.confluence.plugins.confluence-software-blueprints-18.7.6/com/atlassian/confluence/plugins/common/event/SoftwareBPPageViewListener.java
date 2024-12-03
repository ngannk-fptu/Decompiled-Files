/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.content.page.PageViewEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.confluence.plugins.common.event;

import com.atlassian.confluence.event.events.content.page.PageViewEvent;
import com.atlassian.confluence.plugins.common.event.SoftwareBPAnalyticEvent;
import com.atlassian.confluence.plugins.common.event.SoftwareBPAnalyticEventUtils;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import org.springframework.beans.factory.DisposableBean;

public class SoftwareBPPageViewListener
implements DisposableBean {
    private EventPublisher eventPublisher;

    public SoftwareBPPageViewListener(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        eventPublisher.register((Object)this);
    }

    @EventListener
    public void viewPageEvent(PageViewEvent pageViewEvent) {
        String eventName = SoftwareBPAnalyticEventUtils.getAnalyticEventName(pageViewEvent.getContent().getLabels());
        if (eventName != null) {
            this.eventPublisher.publish((Object)new SoftwareBPAnalyticEvent(eventName));
        }
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }
}

