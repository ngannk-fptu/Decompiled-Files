/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.google.common.util.concurrent.SettableFuture
 */
package com.atlassian.mywork.client.service;

import com.atlassian.mywork.client.service.ServingRequestsAware;
import com.atlassian.mywork.service.SystemStatusService;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.google.common.util.concurrent.SettableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class SystemStatusServiceImpl
implements SystemStatusService,
LifecycleAware,
ServingRequestsAware {
    private final PluginEventManager pluginEventManager;
    private final SettableFuture<Boolean> servingRequests = SettableFuture.create();
    private final AtomicInteger readyCount = new AtomicInteger(3);

    public SystemStatusServiceImpl(PluginEventManager pluginEventManager) {
        this.pluginEventManager = pluginEventManager;
        pluginEventManager.register((Object)this);
    }

    public void onStart() {
        if (this.readyCount.decrementAndGet() == 0) {
            this.servingRequests.set((Object)true);
        }
    }

    public void onStop() {
        this.pluginEventManager.unregister((Object)this);
    }

    @PluginEventListener
    public void onPluginEnabledEvent(PluginEnabledEvent event) {
        if (event.getPlugin().getModuleDescriptor("my-work-client-rest") != null && this.readyCount.decrementAndGet() == 0) {
            this.servingRequests.set((Object)true);
        }
    }

    @Override
    public void onServingRequests() {
        if (this.readyCount.decrementAndGet() == 0) {
            this.servingRequests.set((Object)true);
        }
    }

    @Override
    public void runWhenCompletelyUp(Runnable listener, Executor executor) {
        this.servingRequests.addListener(listener, executor);
    }
}

