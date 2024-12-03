/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin;

import com.atlassian.confluence.event.events.plugin.PluginEvent;
import com.atlassian.confluence.event.events.plugin.PluginFrameworkStartedEvent;
import com.atlassian.event.api.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginEventLogger {
    private static final Logger log = LoggerFactory.getLogger(PluginEventLogger.class);

    @EventListener
    public void handleEvent(PluginEvent event) {
        log.info("Processing plugin event: {}", (Object)event);
    }

    @EventListener
    public void handleEvent(PluginFrameworkStartedEvent event) {
        log.info("Processing plugin event: {}", (Object)event);
    }
}

