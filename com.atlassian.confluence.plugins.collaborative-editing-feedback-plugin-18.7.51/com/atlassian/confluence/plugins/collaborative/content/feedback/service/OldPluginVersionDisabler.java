/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.plugin.PluginFrameworkStartedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.service;

import com.atlassian.confluence.event.events.plugin.PluginFrameworkStartedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OldPluginVersionDisabler
implements DisposableBean,
InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(OldPluginVersionDisabler.class);
    private static final String OLD_VERSION_PLUGIN_KEY = "com.atlassian.plugins.collaborative-editing-feedback-plugin";
    private final EventPublisher eventPublisher;
    private final PluginController pluginController;
    private final PluginAccessor pluginAccessor;

    @Autowired
    public OldPluginVersionDisabler(@ComponentImport EventPublisher eventPublisher, @ComponentImport(value="pluginController") PluginController pluginController, @ComponentImport(value="pluginAccessor") PluginAccessor pluginAccessor) {
        this.eventPublisher = eventPublisher;
        this.pluginController = pluginController;
        this.pluginAccessor = pluginAccessor;
    }

    @EventListener
    public void onPluginFrameworkStartedEvent(PluginFrameworkStartedEvent notUsed) {
        if (this.pluginAccessor.isPluginEnabled(OLD_VERSION_PLUGIN_KEY)) {
            log.warn("Old version of the feedback plugin ({}) is enabled. Disabling the old version of the plugin", (Object)OLD_VERSION_PLUGIN_KEY);
            this.pluginController.disablePlugin(OLD_VERSION_PLUGIN_KEY);
        }
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }
}

