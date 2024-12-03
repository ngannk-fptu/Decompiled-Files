/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.listener.ProductAnalyticsEventListener
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.sal.api.license.LicenseHandler
 *  org.osgi.framework.BundleContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.osgi.context.BundleContextAware
 */
package com.atlassian.analytics.client.listener;

import com.atlassian.analytics.api.listener.ProductAnalyticsEventListener;
import com.atlassian.analytics.client.EventTracer;
import com.atlassian.analytics.client.eventfilter.whitelist.AnalyticsWhitelistModuleDescriptor;
import com.atlassian.analytics.client.eventfilter.whitelist.WhitelistFilter;
import com.atlassian.analytics.client.pipeline.AnalyticsPipeline;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.sal.api.license.LicenseHandler;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.osgi.context.BundleContextAware;

public class ProductEventListener
implements ProductAnalyticsEventListener,
BundleContextAware {
    private static final Logger log = LoggerFactory.getLogger(ProductEventListener.class);
    private final EventTracer eventTracer = new EventTracer();
    private final WhitelistFilter whitelistFilter;
    private final boolean analyticsDisabledBySysprop = Boolean.getBoolean("atlassian.analytics.disable.collection");
    private final AnalyticsPipeline analyticsPipeline;
    private BundleContext bundleContext;

    public ProductEventListener(WhitelistFilter whitelistFilter, AnalyticsPipeline analyticsPipeline) {
        this.whitelistFilter = whitelistFilter;
        this.analyticsPipeline = analyticsPipeline;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void processEvent(Object event) {
        if (this.canProcessEvents()) {
            this.logEventSourceInfo(event);
            this.collectExternalWhitelists(event);
            this.analyticsPipeline.process(event);
        }
    }

    private boolean canProcessEvents() {
        if (this.analyticsDisabledBySysprop) {
            return false;
        }
        try {
            return this.bundleContext.getServiceReference(LicenseHandler.class.getName()) != null;
        }
        catch (IllegalStateException ex) {
            return false;
        }
    }

    private void logEventSourceInfo(Object event) {
        if (log.isDebugEnabled()) {
            this.eventTracer.logEventSourceInfo(event);
        }
    }

    private void collectExternalWhitelists(Object event) {
        if (this.whitelistModuleHasChanged(event)) {
            this.whitelistFilter.collectExternalWhitelists();
        }
    }

    private boolean whitelistModuleHasChanged(Object event) {
        if (event instanceof PluginModuleEnabledEvent) {
            return ((PluginModuleEnabledEvent)event).getModule() instanceof AnalyticsWhitelistModuleDescriptor;
        }
        if (event instanceof PluginModuleDisabledEvent) {
            return ((PluginModuleDisabledEvent)event).getModule() instanceof AnalyticsWhitelistModuleDescriptor;
        }
        return false;
    }
}

