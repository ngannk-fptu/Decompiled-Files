/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.application.jira.JiraApplicationType
 *  com.atlassian.applinks.api.event.ApplicationLinkAddedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinkDeletedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinkEvent
 *  com.atlassian.confluence.event.events.content.page.PageUpdateEvent
 *  com.atlassian.confluence.event.events.plugin.PluginInstallEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.metadata.jira.event;

import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.applinks.api.event.ApplicationLinkAddedEvent;
import com.atlassian.applinks.api.event.ApplicationLinkDeletedEvent;
import com.atlassian.applinks.api.event.ApplicationLinkEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.event.events.plugin.PluginInstallEvent;
import com.atlassian.confluence.plugins.metadata.jira.aggregate.JiraAggregateCache;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfluenceEventListener
implements DisposableBean {
    @VisibleForTesting
    static final String PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-jira-metadata";
    private final EventPublisher eventPublisher;
    private final JiraAggregateCache jiraAggregateCache;
    private final ScheduledExecutorService scheduledExecutorService;

    @Autowired
    public ConfluenceEventListener(EventPublisher eventPublisher, JiraAggregateCache jiraAggregateCache) {
        this.eventPublisher = eventPublisher;
        this.jiraAggregateCache = jiraAggregateCache;
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        eventPublisher.register((Object)this);
    }

    @EventListener
    public void onApplicationLinkAdded(ApplicationLinkAddedEvent event) {
        this.configureJiraAggregateCache((ApplicationLinkEvent)event);
    }

    @EventListener
    public void onApplicationLinkDeleted(ApplicationLinkDeletedEvent event) {
        this.configureJiraAggregateCache((ApplicationLinkEvent)event);
    }

    private void configureJiraAggregateCache(ApplicationLinkEvent event) {
        if (event.getApplicationType() instanceof JiraApplicationType) {
            this.scheduledExecutorService.schedule(this.jiraAggregateCache::configureCache, 30L, TimeUnit.SECONDS);
        }
    }

    @EventListener
    public void onPageUpdate(PageUpdateEvent event) {
        this.jiraAggregateCache.invalidateCache(event.getNew().getId());
    }

    @EventListener
    public void handlePluginInstalled(PluginInstallEvent pluginInstallEvent) {
        if (PLUGIN_KEY.equals(pluginInstallEvent.getPluginKey())) {
            this.jiraAggregateCache.invalidateCache();
        }
    }

    public void destroy() {
        this.scheduledExecutorService.shutdownNow();
        this.eventPublisher.unregister((Object)this);
    }
}

