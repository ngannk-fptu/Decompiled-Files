/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.synchrony.tasks;

import com.atlassian.cache.CacheManager;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyMonitor;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyProcessManager;
import com.atlassian.confluence.plugins.synchrony.api.events.SynchronyRestartedEvent;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.confluence.plugins.synchrony.service.CollaborativeEditingModeDuration;
import com.atlassian.confluence.plugins.synchrony.tasks.AbstractConfigLongRunningTask;
import com.atlassian.confluence.plugins.synchrony.tasks.SynchronyConfigTaskTracker;
import com.atlassian.event.api.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestartSynchronyTask
extends AbstractConfigLongRunningTask {
    private static final Logger log = LoggerFactory.getLogger(RestartSynchronyTask.class);

    public RestartSynchronyTask(SynchronyConfigurationManager configManager, SynchronyProcessManager processManager, SynchronyMonitor processMonitor, CacheManager cacheManager, SynchronyConfigTaskTracker taskTracker, EventPublisher eventPublisher, CollaborativeEditingModeDuration collaborativeEditingModeDuration) {
        super(configManager, processManager, processMonitor, cacheManager, taskTracker, eventPublisher, collaborativeEditingModeDuration);
    }

    @Override
    protected void execute() {
        try {
            boolean result = (Boolean)this.processManager.restart().claim();
            this.eventPublisher.publish((Object)new SynchronyRestartedEvent((Object)this, result));
        }
        catch (Exception e) {
            log.warn("Synchrony failed to restart", (Throwable)e);
            this.eventPublisher.publish((Object)new SynchronyRestartedEvent((Object)this, false));
            throw e;
        }
    }

    public String getName() {
        return "Restart Synchrony Task";
    }
}

