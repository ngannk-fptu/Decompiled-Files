/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.google.common.base.Preconditions
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster.shareddata;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.cluster.shareddata.PluginSharedDataKey;
import com.atlassian.confluence.cluster.shareddata.PluginSharedDataRegistry;
import com.atlassian.confluence.cluster.shareddata.PluginSharedDataStore;
import com.atlassian.confluence.cluster.shareddata.SharedData;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.google.common.base.Preconditions;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated(since="8.2", forRemoval=true)
@Internal
public class DefaultPluginSharedDataRegistry
implements PluginSharedDataRegistry {
    private static final Logger log = LoggerFactory.getLogger(DefaultPluginSharedDataRegistry.class);
    private final PluginAccessor pluginAccessor;
    private final PluginSharedDataStore store;
    private final EventListenerRegistrar eventListenerRegistrar;

    public DefaultPluginSharedDataRegistry(PluginAccessor pluginAccessor, EventListenerRegistrar eventListenerRegistrar, PluginSharedDataStore store) {
        this.eventListenerRegistrar = (EventListenerRegistrar)Preconditions.checkNotNull((Object)eventListenerRegistrar);
        this.pluginAccessor = (PluginAccessor)Preconditions.checkNotNull((Object)pluginAccessor);
        this.store = (PluginSharedDataStore)Preconditions.checkNotNull((Object)store);
    }

    @PostConstruct
    public void registerForEvents() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @PreDestroy
    public void unregisterForEvents() {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    @EventListener
    public void pluginDisabled(PluginDisabledEvent pluginDisabledEvent) {
        log.debug("Plugin [{}] has been disabled, unregistering associated shared data", (Object)pluginDisabledEvent.getPlugin().getKey());
        this.store.unregisterPluginSharedData(pluginDisabledEvent.getPlugin());
    }

    @Override
    public @NonNull SharedData getPluginSharedData(PluginSharedDataKey pluginSharedDataKey) {
        Plugin plugin = this.getInstalledPlugin(pluginSharedDataKey);
        return this.store.getPluginSharedData(pluginSharedDataKey, plugin);
    }

    private Plugin getInstalledPlugin(PluginSharedDataKey pluginSharedDataKey) {
        Plugin plugin = this.pluginAccessor.getPlugin(pluginSharedDataKey.getPluginKey());
        if (plugin == null) {
            throw new IllegalStateException("Plugin [" + pluginSharedDataKey.getPluginKey() + "] is not installed, cannot register shared data " + pluginSharedDataKey);
        }
        return plugin;
    }
}

