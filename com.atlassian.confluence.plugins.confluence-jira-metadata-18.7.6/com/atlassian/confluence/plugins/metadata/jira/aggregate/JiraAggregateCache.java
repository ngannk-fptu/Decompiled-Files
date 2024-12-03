/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.metadata.jira.aggregate;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.plugins.metadata.jira.aggregate.JiraAggregateCacheLoader;
import com.atlassian.confluence.plugins.metadata.jira.aggregate.JiraAggregateCacheStore;
import com.atlassian.confluence.plugins.metadata.jira.helper.CapabilitiesHelper;
import com.atlassian.confluence.plugins.metadata.jira.helper.JiraMetadataErrorHelper;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraAggregate;
import com.atlassian.confluence.plugins.metadata.jira.util.JiraAggregates;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JiraAggregateCache {
    private static final Logger log = LoggerFactory.getLogger(JiraAggregateCache.class);
    private final JiraAggregateCacheStore jiraAggregateCacheStore;
    private final JiraAggregateCacheLoader jiraAggregateCacheLoader;
    private final CapabilitiesHelper capabilitiesHelper;

    @Autowired
    public JiraAggregateCache(JiraAggregateCacheStore jiraAggregateCacheStore, JiraAggregateCacheLoader jiraAggregateCacheLoader, CapabilitiesHelper capabilitiesHelper) {
        this.jiraAggregateCacheStore = jiraAggregateCacheStore;
        this.jiraAggregateCacheLoader = jiraAggregateCacheLoader;
        this.capabilitiesHelper = capabilitiesHelper;
    }

    public JiraAggregate getAggregateDataIfCached(long pageId, JiraMetadataErrorHelper errorHelper) {
        JiraAggregate aggregateData = this.jiraAggregateCacheStore.get(pageId);
        if (aggregateData == null) {
            this.jiraAggregateCacheLoader.loadCacheAsync(pageId, errorHelper);
            aggregateData = JiraAggregates.initial();
        }
        return aggregateData;
    }

    public void invalidateCache(long pageId) {
        this.jiraAggregateCacheLoader.invalidateCacheLoadingTask(pageId);
        this.jiraAggregateCacheStore.invalidate(pageId);
    }

    public void invalidateCache() {
        this.jiraAggregateCacheStore.invalidateAll();
        this.jiraAggregateCacheLoader.invalidateAllCacheLoadingTasks();
    }

    public JiraAggregate getAggregateData(long pageId, JiraMetadataErrorHelper errorHelper) {
        JiraAggregate aggregateData = this.jiraAggregateCacheStore.get(pageId);
        if (aggregateData == null && (aggregateData = this.jiraAggregateCacheLoader.getValue(pageId, errorHelper)) == null) {
            aggregateData = JiraAggregates.initial();
        }
        return aggregateData;
    }

    public void configureCache() {
        try {
            for (ReadOnlyApplicationLink jiraAppLink : this.capabilitiesHelper.getAggregateCapableJiraLinks()) {
                if (this.hasLongCacheExpiryCondition(jiraAppLink)) continue;
                this.jiraAggregateCacheStore.setTimeToLive(5L, TimeUnit.MINUTES);
                return;
            }
            this.jiraAggregateCacheStore.setTimeToLive(1L, TimeUnit.DAYS);
        }
        catch (Exception e) {
            log.debug("Error executing configureCache", (Throwable)e);
        }
    }

    boolean hasLongCacheExpiryCondition(ReadOnlyApplicationLink jiraAppLink) {
        boolean hasAggregateCacheInvalidation = this.capabilitiesHelper.isSupportedByAppLink("confluence-jira-metadata-aggregate-cache-invalidation", jiraAppLink);
        boolean hasAggregateCacheInvalidationForSprints = this.capabilitiesHelper.isSupportedByAppLink("remote-sprint-link-events", jiraAppLink);
        boolean hasRemoteSprintLinking = this.capabilitiesHelper.isSupportedByAppLink("gh-remote-sprint-link", jiraAppLink);
        return hasAggregateCacheInvalidation && (hasAggregateCacheInvalidationForSprints || !hasRemoteSprintLinking);
    }
}

