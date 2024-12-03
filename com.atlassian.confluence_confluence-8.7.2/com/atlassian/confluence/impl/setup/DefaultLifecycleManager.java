/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.DefaultLifecycleContext
 *  com.atlassian.config.lifecycle.LifecycleContext
 *  com.atlassian.config.lifecycle.LifecycleItem
 *  com.atlassian.config.lifecycle.LifecycleManager
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.config.lifecycle.events.ApplicationStoppedEvent
 *  com.atlassian.config.lifecycle.events.ApplicationStoppingEvent
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.util.profiling.Ticker
 *  com.google.common.collect.Lists
 *  javax.servlet.ServletContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.setup;

import com.atlassian.config.lifecycle.DefaultLifecycleContext;
import com.atlassian.config.lifecycle.LifecycleContext;
import com.atlassian.config.lifecycle.LifecycleItem;
import com.atlassian.config.lifecycle.LifecycleManager;
import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.config.lifecycle.events.ApplicationStoppedEvent;
import com.atlassian.config.lifecycle.events.ApplicationStoppingEvent;
import com.atlassian.confluence.impl.setup.LifecyclePluginModuleDescriptor;
import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.setup.johnson.JohnsonUtils;
import com.atlassian.confluence.util.profiling.TimedAnalytics;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.util.profiling.Ticker;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultLifecycleManager
implements LifecycleManager {
    private static final Logger log = LoggerFactory.getLogger(LifecycleManager.class);
    private final PluginAccessor pluginAccessor;
    private final EventPublisher eventPublisher;
    private boolean startedUp;

    public DefaultLifecycleManager(EventPublisher eventPublisher, PluginAccessor pluginAccessor) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
    }

    public void startUp(ServletContext servletContext) {
        try (Ticker ticker = TimedAnalytics.timedAnalytics().start("confluence.profiling.startup.lifecycle");){
            List<LifecyclePluginModuleDescriptor> moduleDescriptors = this.getLifecyclePluginModuleDescriptors();
            DefaultLifecycleContext context = new DefaultLifecycleContext(servletContext);
            LifecyclePluginModuleDescriptor currentDescriptor = null;
            try {
                Iterator<LifecyclePluginModuleDescriptor> iterator = moduleDescriptors.iterator();
                while (iterator.hasNext()) {
                    LifecyclePluginModuleDescriptor descriptor;
                    currentDescriptor = descriptor = iterator.next();
                    log.info("Starting: " + descriptor);
                    ((LifecycleItem)descriptor.getModule()).startup((LifecycleContext)context);
                }
                this.eventPublisher.publish((Object)new ApplicationStartedEvent((Object)this));
                this.startedUp = true;
            }
            catch (Throwable t) {
                this.panicAndShutdown(t, (LifecycleContext)context, currentDescriptor);
            }
        }
    }

    public void shutDown(ServletContext servletContext) {
        this.shutDown(servletContext, null);
    }

    public boolean isStartedUp() {
        return this.startedUp;
    }

    private void panicAndShutdown(Throwable t, LifecycleContext context, LifecyclePluginModuleDescriptor descriptor) {
        String errorString = "Unable to start up Confluence. Fatal error during startup sequence: " + descriptor;
        log.error(errorString, t);
        JohnsonUtils.raiseJohnsonEvent(JohnsonEventType.STARTUP, errorString, t.getMessage(), JohnsonEventLevel.FATAL);
        this.shutDown(context.getServletContext(), descriptor.getCompleteKey());
    }

    private void shutDown(ServletContext servletContext, String startingPluginKey) {
        this.eventPublisher.publish((Object)new ApplicationStoppingEvent((Object)this));
        List<LifecyclePluginModuleDescriptor> moduleDescriptors = this.getLifecyclePluginModuleDescriptors();
        Collections.reverse(moduleDescriptors);
        DefaultLifecycleContext context = new DefaultLifecycleContext(servletContext);
        boolean started = startingPluginKey == null;
        for (LifecyclePluginModuleDescriptor descriptor : moduleDescriptors) {
            if (!started) {
                if (!descriptor.getCompleteKey().equals(startingPluginKey)) continue;
                started = true;
            }
            log.info("Shutting down: " + descriptor);
            LifecycleItem item = (LifecycleItem)descriptor.getModule();
            try {
                item.shutdown((LifecycleContext)context);
            }
            catch (Throwable t) {
                log.error("Error running shutdown plugin: " + descriptor.getDescription() + " - " + t, t);
            }
        }
        this.eventPublisher.publish((Object)new ApplicationStoppedEvent((Object)this));
    }

    private List<LifecyclePluginModuleDescriptor> getLifecyclePluginModuleDescriptors() {
        ArrayList modules = Lists.newArrayList((Iterable)this.pluginAccessor.getEnabledModuleDescriptorsByClass(LifecyclePluginModuleDescriptor.class));
        Collections.sort(modules);
        return modules;
    }
}

