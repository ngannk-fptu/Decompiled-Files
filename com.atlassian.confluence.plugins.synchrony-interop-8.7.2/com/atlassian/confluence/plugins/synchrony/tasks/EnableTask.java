/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager
 *  com.atlassian.event.api.EventPublisher
 *  io.atlassian.util.concurrent.Promise
 *  io.atlassian.util.concurrent.Promises
 */
package com.atlassian.confluence.plugins.synchrony.tasks;

import com.atlassian.cache.CacheManager;
import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyMonitor;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyProcessManager;
import com.atlassian.confluence.plugins.synchrony.api.events.SynchronyRestartedEvent;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.confluence.plugins.synchrony.service.CollaborativeEditingModeDuration;
import com.atlassian.confluence.plugins.synchrony.tasks.AbstractConfigLongRunningTask;
import com.atlassian.confluence.plugins.synchrony.tasks.SynchronyConfigTaskTracker;
import com.atlassian.event.api.EventPublisher;
import io.atlassian.util.concurrent.Promise;
import io.atlassian.util.concurrent.Promises;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class EnableTask
extends AbstractConfigLongRunningTask {
    private final PageManager pageManager;

    public EnableTask(SynchronyConfigurationManager configManager, SynchronyProcessManager processManager, SynchronyMonitor processMonitor, CacheManager cacheManager, SynchronyConfigTaskTracker taskTracker, EventPublisher eventPublisher, PageManager pageManager, CollaborativeEditingModeDuration collaborativeEditingModeDuration) {
        super(configManager, processManager, processMonitor, cacheManager, taskTracker, eventPublisher, collaborativeEditingModeDuration);
        this.pageManager = Objects.requireNonNull(pageManager);
    }

    @Override
    protected void execute() throws ConfigurationException, ExecutionException, InterruptedException {
        this.processManager.setSynchronyOff(false);
        Promise synchronyPromise = !this.processManager.isSynchronyClusterManuallyManaged() ? this.processManager.restart().done(status -> this.eventPublisher.publish((Object)new SynchronyRestartedEvent((Object)this, (boolean)status))).fail(status -> this.eventPublisher.publish((Object)new SynchronyRestartedEvent((Object)this, false))) : Promises.promise((Object)true);
        int numberOfDraftsRemoved = this.pageManager.removeStaleSharedDrafts();
        log.info("Removed {} stale shared drafts.", (Object)numberOfDraftsRemoved);
        synchronyPromise.claim();
        this.configManager.enableSharedDrafts();
        this.configManager.registerWithSynchrony();
    }

    public String getName() {
        return "EnableTask";
    }
}

