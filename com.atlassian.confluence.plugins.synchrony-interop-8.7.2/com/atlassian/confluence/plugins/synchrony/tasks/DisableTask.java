/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.confluence.api.service.eviction.SynchronyDataService
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.confluence.plugins.synchrony.tasks;

import com.atlassian.cache.CacheManager;
import com.atlassian.cache.ManagedCache;
import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.api.service.eviction.SynchronyDataService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyMonitor;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyProcessManager;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.confluence.plugins.synchrony.service.CollaborativeEditingModeDuration;
import com.atlassian.confluence.plugins.synchrony.tasks.AbstractConfigLongRunningTask;
import com.atlassian.confluence.plugins.synchrony.tasks.SynchronyConfigTaskTracker;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Collection;
import java.util.Objects;

public class DisableTask
extends AbstractConfigLongRunningTask {
    private static final String CACHE_PREFIX_TO_FLUSH = ContentEntityObject.class.getName();
    private final TransactionTemplate transactionTemplate;
    private final SynchronyDataService synchronyDataService;

    public DisableTask(SynchronyConfigurationManager configManager, SynchronyProcessManager processManager, SynchronyMonitor processMonitor, CacheManager cacheManager, SynchronyConfigTaskTracker taskTracker, TransactionTemplate transactionTemplate, EventPublisher eventPublisher, CollaborativeEditingModeDuration collaborativeEditingModeDuration, SynchronyDataService synchronyDataService) {
        super(configManager, processManager, processMonitor, cacheManager, taskTracker, eventPublisher, collaborativeEditingModeDuration);
        this.transactionTemplate = Objects.requireNonNull(transactionTemplate);
        this.synchronyDataService = synchronyDataService;
    }

    @Override
    protected void execute() throws ConfigurationException {
        String appId = this.configManager.getAppID();
        this.configManager.disableSharedDrafts();
        if (!this.processManager.isSynchronyClusterManuallyManaged()) {
            this.processManager.stop();
        }
        this.processManager.setSynchronyOff(true);
        this.transactionTemplate.execute(() -> {
            this.synchronyDataService.dataCleanUpAfterTurningOffCollabEditing(appId);
            this.configManager.removeSynchronyCredentials();
            this.flushCaches();
            return null;
        });
    }

    private void flushCaches() {
        Collection managedCaches = this.cacheManager.getManagedCaches();
        managedCaches.stream().filter(managedCache -> managedCache.getName().startsWith(CACHE_PREFIX_TO_FLUSH)).forEach(ManagedCache::clear);
    }

    public String getName() {
        return "DisableTask";
    }
}

