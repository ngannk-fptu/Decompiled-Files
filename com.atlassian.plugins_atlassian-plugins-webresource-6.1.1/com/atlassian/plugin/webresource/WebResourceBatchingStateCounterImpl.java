/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginFrameworkShutdownEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkStartedEvent
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginFrameworkShutdownEvent;
import com.atlassian.plugin.event.events.PluginFrameworkStartedEvent;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.webresource.WebResourceBatchingStateCounter;
import java.util.concurrent.atomic.AtomicLong;

public class WebResourceBatchingStateCounterImpl
implements WebResourceBatchingStateCounter {
    private final PluginEventManager pluginEventManager;
    private final AtomicLong counter;
    private volatile boolean active = false;

    public WebResourceBatchingStateCounterImpl(PluginEventManager pluginEventManager) {
        this.pluginEventManager = pluginEventManager;
        this.counter = new AtomicLong(0L);
        pluginEventManager.register((Object)this);
    }

    public void close() {
        this.pluginEventManager.unregister((Object)this);
    }

    @PluginEventListener
    public void onPluginFrameworkStarted(PluginFrameworkStartedEvent event) {
        this.active = true;
        this.incrementCounterIfActive();
    }

    @PluginEventListener
    public void onPluginFrameworkPluginFrameworkShutdown(PluginFrameworkShutdownEvent event) {
        this.active = false;
    }

    @PluginEventListener
    public void onPluginModuleEnabled(PluginModuleEnabledEvent event) {
        this.incrementCounterIfActive();
    }

    @PluginEventListener
    public void onPluginModuleDisabled(PluginModuleDisabledEvent event) {
        this.incrementCounterIfActive();
    }

    @Override
    public long getBatchingStateCounter() {
        return this.counter.get();
    }

    @Override
    public void incrementCounter() {
        this.incrementCounterIfActive();
    }

    private void incrementCounterIfActive() {
        if (this.active) {
            this.counter.incrementAndGet();
        }
    }
}

