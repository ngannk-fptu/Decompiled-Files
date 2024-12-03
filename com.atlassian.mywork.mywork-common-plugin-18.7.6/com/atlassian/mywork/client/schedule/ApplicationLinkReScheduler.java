/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.event.ApplicationLinkAddedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinkAuthConfigChangedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinkDetailsChangedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 */
package com.atlassian.mywork.client.schedule;

import com.atlassian.applinks.api.event.ApplicationLinkAddedEvent;
import com.atlassian.applinks.api.event.ApplicationLinkAuthConfigChangedEvent;
import com.atlassian.applinks.api.event.ApplicationLinkDetailsChangedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.mywork.client.schedule.Scheduler;
import com.atlassian.sal.api.lifecycle.LifecycleAware;

public class ApplicationLinkReScheduler
implements LifecycleAware {
    private final EventPublisher eventPublisher;
    private final Scheduler scheduler;

    public ApplicationLinkReScheduler(EventPublisher eventPublisher, Scheduler scheduler) {
        this.eventPublisher = eventPublisher;
        this.scheduler = scheduler;
    }

    public void onStart() {
        this.eventPublisher.register((Object)this);
    }

    public void onStop() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onAppLinkAdded(ApplicationLinkAddedEvent event) {
        this.rescheduleAll();
    }

    @EventListener
    public void onAppLinkConfigChanged(ApplicationLinkAuthConfigChangedEvent event) {
        this.rescheduleAll();
    }

    @EventListener
    public void onAppLinkDetailsChanged(ApplicationLinkDetailsChangedEvent event) {
        this.rescheduleAll();
    }

    private void rescheduleAll() {
        this.scheduler.rescheduleAll();
    }
}

