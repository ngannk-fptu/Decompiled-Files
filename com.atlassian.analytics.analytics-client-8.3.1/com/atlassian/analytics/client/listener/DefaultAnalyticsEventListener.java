/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.events.AnalyticsConfigChangedEvent
 *  com.atlassian.analytics.api.events.AnalyticsPluginReadyEvent
 *  com.atlassian.analytics.api.events.MauEvent
 *  com.atlassian.analytics.api.listener.ProductAnalyticsEventListener
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEvent
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 */
package com.atlassian.analytics.client.listener;

import com.atlassian.analytics.api.events.AnalyticsConfigChangedEvent;
import com.atlassian.analytics.api.events.AnalyticsPluginReadyEvent;
import com.atlassian.analytics.api.events.MauEvent;
import com.atlassian.analytics.api.listener.ProductAnalyticsEventListener;
import com.atlassian.analytics.client.api.browser.BrowserEvent;
import com.atlassian.analytics.client.email.TrackingBeaconEvent;
import com.atlassian.analytics.client.eventfilter.whitelist.AnalyticsWhitelistModuleDescriptor;
import com.atlassian.analytics.client.filter.UniversalAnalyticsFilter;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEvent;
import com.atlassian.sal.api.lifecycle.LifecycleAware;

public class DefaultAnalyticsEventListener
implements LifecycleAware {
    private final EventPublisher eventPublisher;
    private final ProductAnalyticsEventListener productAnalyticsEventListener;

    public DefaultAnalyticsEventListener(EventPublisher eventPublisher, ProductAnalyticsEventListener productAnalyticsEventListener) {
        this.eventPublisher = eventPublisher;
        this.productAnalyticsEventListener = productAnalyticsEventListener;
    }

    public void onStart() {
        this.eventPublisher.register((Object)this);
        this.eventPublisher.publish((Object)new AnalyticsPluginReadyEvent());
    }

    @EventListener
    public void onAnalyticsConfigChangedEvent(AnalyticsConfigChangedEvent event) {
        this.productAnalyticsEventListener.processEvent((Object)event);
    }

    @EventListener
    public void onMauEvent(MauEvent event) {
        this.productAnalyticsEventListener.processEvent((Object)event);
    }

    @EventListener
    public void onTrackingBeaconEvent(TrackingBeaconEvent event) {
        this.productAnalyticsEventListener.processEvent((Object)event);
    }

    @EventListener
    public void onFilteredEvent(UniversalAnalyticsFilter.FilteredEvent event) {
        this.productAnalyticsEventListener.processEvent((Object)event);
    }

    @EventListener
    public void onBrowserEvent(BrowserEvent event) {
        this.productAnalyticsEventListener.processEvent((Object)event);
    }

    @PluginEventListener
    public void onPluginModuleEnabledEvent(PluginModuleEnabledEvent event) {
        if (this.isWhitelistChangeEvent((PluginModuleEvent)event)) {
            this.productAnalyticsEventListener.processEvent((Object)event);
        }
    }

    @PluginEventListener
    public void onPluginModuleDisabledEvent(PluginModuleDisabledEvent event) {
        if (this.isWhitelistChangeEvent((PluginModuleEvent)event)) {
            this.productAnalyticsEventListener.processEvent((Object)event);
        }
    }

    public void onStop() {
        this.eventPublisher.unregister((Object)this);
    }

    private boolean isWhitelistChangeEvent(PluginModuleEvent pluginModuleEvent) {
        return pluginModuleEvent.getModule() instanceof AnalyticsWhitelistModuleDescriptor;
    }
}

