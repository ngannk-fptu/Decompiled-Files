/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.events.ApplicationStoppingEvent
 *  com.atlassian.event.api.EventListener
 *  com.opensymphony.xwork2.config.ConfigurationManager
 */
package com.atlassian.confluence.impl.struts;

import com.atlassian.config.lifecycle.events.ApplicationStoppingEvent;
import com.atlassian.confluence.event.events.plugin.PluginFrameworkStartedEvent;
import com.atlassian.confluence.event.events.plugin.XWorkStateChangeEvent;
import com.atlassian.event.api.EventListener;
import com.opensymphony.xwork2.config.ConfigurationManager;
import java.util.concurrent.atomic.AtomicBoolean;

public class StrutsStateChangeListener {
    private final ConfigurationManager configurationManager;
    private final AtomicBoolean pluginFrameworkStarted = new AtomicBoolean(false);
    private final AtomicBoolean applicationStopping = new AtomicBoolean(false);

    public StrutsStateChangeListener(ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    @EventListener
    public void handle(XWorkStateChangeEvent event) {
        if (this.pluginFrameworkStarted.get() && !this.applicationStopping.get()) {
            this.configurationManager.reload();
        }
    }

    @EventListener
    public void handle(PluginFrameworkStartedEvent event) {
        this.pluginFrameworkStarted.set(true);
    }

    @EventListener
    public void handle(ApplicationStoppingEvent event) {
        this.applicationStopping.set(true);
    }
}

