/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.events.AnalyticsPluginReadyEvent
 *  com.atlassian.analytics.api.listener.ProductAnalyticsEventListener
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.analytics.client.listener;

import com.atlassian.analytics.api.events.AnalyticsPluginReadyEvent;
import com.atlassian.analytics.api.listener.ProductAnalyticsEventListener;
import com.atlassian.analytics.client.eventfilter.whitelist.WhitelistFilter;
import com.atlassian.analytics.client.upload.PeriodicEventUploaderScheduler;
import com.atlassian.analytics.client.uuid.ProductUUIDProvider;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import org.springframework.beans.factory.DisposableBean;

public class FecruEventListener
implements DisposableBean,
LifecycleAware {
    private final EventPublisher eventPublisher;
    private final PluginEventManager pluginEventManager;
    private final ProductAnalyticsEventListener productAnalyticsEventListener;
    private final PeriodicEventUploaderScheduler periodicEventUploaderScheduler;
    private final WhitelistFilter whitelistFilter;
    private final ProductUUIDProvider productUUIDProvider;

    public FecruEventListener(EventPublisher eventPublisher, PluginEventManager pluginEventManager, ProductAnalyticsEventListener productAnalyticsEventListener, PeriodicEventUploaderScheduler periodicEventUploaderScheduler, WhitelistFilter whitelistFilter, ProductUUIDProvider productUUIDProvider) {
        this.eventPublisher = eventPublisher;
        this.pluginEventManager = pluginEventManager;
        this.productAnalyticsEventListener = productAnalyticsEventListener;
        this.periodicEventUploaderScheduler = periodicEventUploaderScheduler;
        this.whitelistFilter = whitelistFilter;
        this.productUUIDProvider = productUUIDProvider;
    }

    public void onStart() {
        this.productUUIDProvider.createUUID();
        this.whitelistFilter.collectExternalWhitelists();
        this.eventPublisher.register((Object)this);
        this.pluginEventManager.register((Object)this);
        this.eventPublisher.publish((Object)new AnalyticsPluginReadyEvent());
    }

    private void processEvent(Object event) {
        this.productAnalyticsEventListener.processEvent(event);
    }

    @EventListener
    public void onEvent(Object event) {
        this.processEvent(event);
    }

    @PluginEventListener
    public void onPluginEnabledEvent(PluginEnabledEvent event) {
        this.processEvent(event);
    }

    @PluginEventListener
    public void onPluginDisabledEvent(PluginDisabledEvent event) {
        this.processEvent(event);
    }

    public void destroy() {
        this.periodicEventUploaderScheduler.unscheduleJobs();
        this.eventPublisher.unregister((Object)this);
        this.pluginEventManager.unregister((Object)this);
    }

    public void onStop() {
    }
}

