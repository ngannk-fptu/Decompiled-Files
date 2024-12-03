/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.event.events.plugin.PluginInstallEvent
 *  com.atlassian.confluence.event.events.plugin.PluginModuleEvent
 *  com.atlassian.confluence.event.events.plugin.PluginUninstallEvent
 *  com.atlassian.confluence.plugin.webresource.DefaultCounter
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  javax.annotation.concurrent.GuardedBy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.plugins.gadgets;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.event.events.plugin.PluginInstallEvent;
import com.atlassian.confluence.event.events.plugin.PluginModuleEvent;
import com.atlassian.confluence.event.events.plugin.PluginUninstallEvent;
import com.atlassian.confluence.plugin.webresource.DefaultCounter;
import com.atlassian.confluence.plugins.gadgets.events.GadgetInstalledEvent;
import com.atlassian.confluence.plugins.gadgets.events.GadgetUninstalledEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.concurrent.GuardedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class GadgetCounter
extends DefaultCounter
implements LifecycleAware,
InitializingBean,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(GadgetCounter.class);
    private static final String PLUGIN_KEY = "com.atlassian.confluence.plugins.gadgets";
    private static final String key = "gadget.counter";
    private final EventPublisher eventPublisher;
    @GuardedBy(value="this")
    private final Set<LifecycleEvent> lifecycleEvents = EnumSet.noneOf(LifecycleEvent.class);

    public GadgetCounter(BandanaManager bandanaManager, EventPublisher eventPublisher) {
        super(key, bandanaManager);
        this.eventPublisher = eventPublisher;
    }

    public void afterPropertiesSet() {
        this.registerListener();
        this.onLifecycleEvent(LifecycleEvent.AFTER_PROPERTIES_SET);
    }

    public void onStart() {
        this.onLifecycleEvent(LifecycleEvent.LIFECYCLE_AWARE_ON_START);
    }

    public void onStop() {
        this.resetLifecycleEvents();
    }

    @EventListener
    public void onPluginEnabled(PluginEnabledEvent event) {
        if (PLUGIN_KEY.equals(event.getPlugin().getKey())) {
            this.onLifecycleEvent(LifecycleEvent.PLUGIN_ENABLED);
        }
        this.updateCounter();
    }

    public void destroy() throws Exception {
        this.unregisterListener();
    }

    private void onLifecycleEvent(LifecycleEvent event) {
        log.debug("onLifecycleEvent: " + event);
        if (this.isLifecycleReady(event)) {
            this.updateCounter();
            log.debug("Finished update counter after all lifecycle events");
        }
    }

    private synchronized void resetLifecycleEvents() {
        this.lifecycleEvents.removeAll(EnumSet.allOf(LifecycleEvent.class));
    }

    private synchronized boolean isLifecycleReady(LifecycleEvent event) {
        return this.lifecycleEvents.add(event) && this.lifecycleEvents.size() == LifecycleEvent.values().length;
    }

    private void registerListener() {
        log.debug("registerListeners");
        this.eventPublisher.register((Object)this);
    }

    private void unregisterListener() {
        log.debug("unregisterListeners");
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void handle(PluginInstallEvent e) {
        this.updateCounter();
    }

    @EventListener
    public void handle(PluginUninstallEvent e) {
        this.updateCounter();
    }

    @EventListener
    public void handle(PluginDisabledEvent e) {
        this.updateCounter();
    }

    @EventListener
    public void handle(GadgetInstalledEvent e) {
        this.updateCounter();
    }

    @EventListener
    public void handle(GadgetUninstalledEvent e) {
        this.updateCounter();
    }

    @EventListener
    public void handle(PluginModuleEvent e) {
        this.updateCounter();
    }

    public void updateCounter() {
        if (this.lifecycleEvents.size() == LifecycleEvent.values().length) {
            super.updateCounter();
        }
    }

    private static enum LifecycleEvent {
        AFTER_PROPERTIES_SET,
        PLUGIN_ENABLED,
        LIFECYCLE_AWARE_ON_START;

    }
}

