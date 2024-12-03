/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.events.AnalyticsPluginReadyEvent
 *  com.atlassian.analytics.api.listener.ProductAnalyticsEventListener
 *  com.atlassian.config.lifecycle.events.ApplicationStoppingEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.analytics.client.listener;

import com.atlassian.analytics.api.events.AnalyticsPluginReadyEvent;
import com.atlassian.analytics.api.listener.ProductAnalyticsEventListener;
import com.atlassian.analytics.client.eventfilter.whitelist.WhitelistFilter;
import com.atlassian.analytics.client.upload.PeriodicEventUploaderScheduler;
import com.atlassian.analytics.client.uuid.ProductUUIDProvider;
import com.atlassian.config.lifecycle.events.ApplicationStoppingEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import org.springframework.beans.factory.DisposableBean;

public class ConfluenceEventListener
implements DisposableBean,
LifecycleAware {
    private final EventPublisher eventPublisher;
    private final ProductAnalyticsEventListener productAnalyticsEventListener;
    private final PeriodicEventUploaderScheduler periodicEventUploaderScheduler;
    private final WhitelistFilter whitelistFilter;
    private final ProductUUIDProvider productUUIDProvider;

    public ConfluenceEventListener(EventPublisher eventPublisher, ProductAnalyticsEventListener productAnalyticsEventListener, PeriodicEventUploaderScheduler periodicEventUploaderScheduler, WhitelistFilter whitelistFilter, ProductUUIDProvider productUUIDProvider) {
        this.eventPublisher = eventPublisher;
        this.productAnalyticsEventListener = productAnalyticsEventListener;
        this.periodicEventUploaderScheduler = periodicEventUploaderScheduler;
        this.whitelistFilter = whitelistFilter;
        this.productUUIDProvider = productUUIDProvider;
    }

    public void onStart() {
        this.productUUIDProvider.createUUID();
        this.whitelistFilter.collectExternalWhitelists();
        this.eventPublisher.register((Object)this);
        this.eventPublisher.publish((Object)new AnalyticsPluginReadyEvent());
    }

    @EventListener
    public void onEvent(Object event) {
        if (event instanceof ApplicationStoppingEvent) {
            this.periodicEventUploaderScheduler.unscheduleJobs();
            this.destroy();
            return;
        }
        this.productAnalyticsEventListener.processEvent(event);
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    public void onStop() {
    }
}

