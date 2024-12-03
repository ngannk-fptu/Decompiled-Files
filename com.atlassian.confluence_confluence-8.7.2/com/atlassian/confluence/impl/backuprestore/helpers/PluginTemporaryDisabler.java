/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginController
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.helpers;

import com.atlassian.confluence.event.events.plugin.AsyncPluginEnableEvent;
import com.atlassian.confluence.event.events.plugin.PluginEnableEvent;
import com.atlassian.confluence.util.Cleanup;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.google.common.collect.Lists;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginTemporaryDisabler {
    private static final Logger log = LoggerFactory.getLogger(PluginTemporaryDisabler.class);
    private final PluginController pluginController;
    private final PluginAccessor pluginAccessor;
    private final EventPublisher eventPublisher;

    public PluginTemporaryDisabler(PluginController pluginController, PluginAccessor pluginAccessor, EventPublisher eventPublisher) {
        this.pluginController = pluginController;
        this.pluginAccessor = pluginAccessor;
        this.eventPublisher = eventPublisher;
    }

    public final Cleanup temporarilyShutdownInterferingPlugins(List<String> pluginsToDisable) {
        HashSet disabledPlugins = new HashSet(pluginsToDisable.size());
        log.debug("Started disabling plugins [{}] during restore", pluginsToDisable);
        pluginsToDisable.stream().filter(arg_0 -> ((PluginAccessor)this.pluginAccessor).isPluginEnabled(arg_0)).forEachOrdered(pluginKey -> {
            this.pluginController.disablePluginWithoutPersisting(pluginKey);
            disabledPlugins.add(pluginKey);
        });
        log.debug("Finished disabling plugins [{}] during restore", pluginsToDisable);
        return () -> {
            List reversed = Lists.reverse((List)pluginsToDisable);
            log.debug("Started re-enabling plugins [{}] after restore", (Object)reversed);
            reversed.stream().filter(pluginKey -> !this.pluginAccessor.isPluginEnabled(pluginKey) && disabledPlugins.contains(pluginKey)).forEachOrdered(xva$0 -> this.enablePlugins((String)xva$0));
            log.debug("Finish re-enabling plugins [{}] after restore", (Object)reversed);
        };
    }

    private void enablePlugins(String ... pluginKeys) {
        this.pluginController.enablePlugins(pluginKeys);
        for (String key : pluginKeys) {
            this.eventPublisher.publish((Object)new PluginEnableEvent(this, key));
            this.eventPublisher.publish((Object)new AsyncPluginEnableEvent(this, key));
        }
    }
}

