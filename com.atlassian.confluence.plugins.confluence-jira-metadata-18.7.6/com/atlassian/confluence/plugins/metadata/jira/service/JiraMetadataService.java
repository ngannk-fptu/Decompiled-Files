/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.metadata.jira.service;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.confluence.plugins.metadata.jira.aggregate.JiraAggregateCache;
import com.atlassian.confluence.plugins.metadata.jira.helper.CapabilitiesHelper;
import com.atlassian.confluence.plugins.metadata.jira.helper.JiraMetadataErrorHelper;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraAggregate;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadata;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataSingleGroup;
import com.atlassian.confluence.plugins.metadata.jira.service.JiraIssuesMetadataDelegate;
import com.atlassian.confluence.plugins.metadata.jira.service.JiraMetadataDelegate;
import com.atlassian.confluence.plugins.metadata.jira.service.helper.FutureMetadataHelper;
import com.atlassian.confluence.plugins.metadata.jira.service.helper.JiraEpicPropertiesHelper;
import com.atlassian.confluence.plugins.metadata.jira.service.helper.SingleAppLinkMetadataHelper;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.util.concurrent.ThreadFactories;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JiraMetadataService
implements DisposableBean {
    private static final String THREAD_NAME_PREFIX = "JIRAMetadataPlugin";
    private static final int EXECUTOR_TIMEOUT = 5;
    private final HostApplication hostApplication;
    private final List<JiraMetadataDelegate> jiraMetadataDelegates;
    private final JiraAggregateCache jiraAggregateCache;
    private final JiraEpicPropertiesHelper epicPropertiesHelper;
    private final JiraIssuesMetadataDelegate issuesMetadataDelegate;
    private final CapabilitiesHelper capabilitiesHelper;
    private final I18NBeanFactory i18NBeanFactory;
    private final ExecutorService executorService;

    @Autowired
    public JiraMetadataService(HostApplication hostApplication, List<JiraMetadataDelegate> jiraMetadataDelegates, JiraAggregateCache jiraAggregateCache, JiraEpicPropertiesHelper epicPropertiesHelper, JiraIssuesMetadataDelegate issuesMetadataDelegate, CapabilitiesHelper capabilitiesHelper, I18NBeanFactory i18NBeanFactory) {
        this.hostApplication = hostApplication;
        this.jiraMetadataDelegates = jiraMetadataDelegates;
        this.jiraAggregateCache = jiraAggregateCache;
        this.epicPropertiesHelper = epicPropertiesHelper;
        this.issuesMetadataDelegate = issuesMetadataDelegate;
        this.capabilitiesHelper = capabilitiesHelper;
        this.i18NBeanFactory = i18NBeanFactory;
        this.executorService = Executors.newFixedThreadPool(10, ThreadFactories.namedThreadFactory((String)THREAD_NAME_PREFIX, (ThreadFactories.Type)ThreadFactories.Type.DAEMON));
    }

    public JiraAggregate getAggregateDataIfCached(long pageId) {
        return this.jiraAggregateCache.getAggregateDataIfCached(pageId, new JiraMetadataErrorHelper(this.i18NBeanFactory));
    }

    public JiraAggregate getAggregateData(long pageId) {
        return this.jiraAggregateCache.getAggregateData(pageId, new JiraMetadataErrorHelper(this.i18NBeanFactory));
    }

    public JiraMetadata getMetadata(long pageId) {
        JiraMetadataErrorHelper errorHelper = new JiraMetadataErrorHelper(this.i18NBeanFactory);
        ArrayList<Callable<List<JiraMetadataSingleGroup>>> tasks = new ArrayList<Callable<List<JiraMetadataSingleGroup>>>();
        ArrayList<Future<List<JiraMetadataSingleGroup>>> futures = new ArrayList<Future<List<JiraMetadataSingleGroup>>>();
        for (ReadOnlyApplicationLink appLink : this.capabilitiesHelper.getAggregateCapableJiraLinks()) {
            SingleAppLinkMetadataHelper helper = new SingleAppLinkMetadataHelper(this.jiraMetadataDelegates, this.epicPropertiesHelper, appLink, pageId, this.issuesMetadataDelegate, this.capabilitiesHelper, errorHelper, this.hostApplication);
            tasks.addAll(helper.process());
        }
        try {
            futures.addAll(this.executorService.invokeAll(tasks, 5L, TimeUnit.SECONDS));
        }
        catch (Exception e) {
            errorHelper.handleException(e);
        }
        return new FutureMetadataHelper(futures, errorHelper).process();
    }

    public void invalidateCachedAggregateData(long pageId) {
        this.jiraAggregateCache.invalidateCache(pageId);
    }

    public void destroy() {
        this.executorService.shutdownNow();
    }
}

