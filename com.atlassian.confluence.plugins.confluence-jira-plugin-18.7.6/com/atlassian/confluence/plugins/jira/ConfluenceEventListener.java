/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.event.ApplicationLinkDetailsChangedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinkMadePrimaryEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostRestoreEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent
 *  com.atlassian.confluence.event.events.content.page.PageCreateEvent
 *  com.atlassian.confluence.event.events.content.page.PageRemoveEvent
 *  com.atlassian.confluence.event.events.content.page.PageRestoreEvent
 *  com.atlassian.confluence.event.events.content.page.PageTrashedEvent
 *  com.atlassian.confluence.event.events.content.page.PageUpdateEvent
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.plugins.createcontent.events.BlueprintPageCreateEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Function
 *  com.google.common.collect.Maps
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.jira;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.event.ApplicationLinkDetailsChangedEvent;
import com.atlassian.applinks.api.event.ApplicationLinkMadePrimaryEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRestoreEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageRemoveEvent;
import com.atlassian.confluence.event.events.content.page.PageRestoreEvent;
import com.atlassian.confluence.event.events.content.page.PageTrashedEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.extra.jira.api.services.JiraConnectorManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugins.createcontent.events.BlueprintPageCreateEvent;
import com.atlassian.confluence.plugins.jira.event.PageCreatedFromJiraAnalyticsEvent;
import com.atlassian.confluence.plugins.jira.links.JiraRemoteEpicLinkManager;
import com.atlassian.confluence.plugins.jira.links.JiraRemoteIssueLinkManager;
import com.atlassian.confluence.plugins.jira.links.JiraRemoteSprintLinkManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfluenceEventListener.class);
    private static final Function<Object, String> PARAM_VALUE_TO_STRING_FUNCTION = input -> input != null ? input.toString() : "";
    private static final String APPLINK_ID = "applinkId";
    private static final String FALLBACK_URL = "fallbackUrl";
    private static final String AGILE_MODE = "agileMode";
    private static final String SPRINT_ID = "sprintId";
    private static final String ISSUE_KEY = "issueKey";
    private static final String CREATION_TOKEN = "creationToken";
    private static final String AGILE_MODE_VALUE_PLAN = "plan";
    private static final String AGILE_MODE_VALUE_REPORT = "report";
    private final EventPublisher eventPublisher;
    private final JiraRemoteSprintLinkManager jiraRemoteSprintLinkManager;
    private final JiraRemoteEpicLinkManager jiraRemoteEpicLinkManager;
    private final JiraRemoteIssueLinkManager jiraRemoteIssueLinkManager;
    private final JiraConnectorManager jiraConnectorManager;

    public ConfluenceEventListener(EventPublisher eventPublisher, JiraRemoteSprintLinkManager jiraRemoteSprintLinkManager, JiraRemoteIssueLinkManager jiraRemoteIssueLinkManager, JiraRemoteEpicLinkManager jiraRemoteEpicLinkManager, JiraConnectorManager jiraConnectorManager) {
        this.eventPublisher = eventPublisher;
        this.jiraRemoteSprintLinkManager = jiraRemoteSprintLinkManager;
        this.jiraRemoteEpicLinkManager = jiraRemoteEpicLinkManager;
        this.jiraRemoteIssueLinkManager = jiraRemoteIssueLinkManager;
        this.jiraConnectorManager = jiraConnectorManager;
    }

    @EventListener
    public void createJiraRemoteLinks(PageCreateEvent event) {
        this.createJiraRemoteLinksForNewPage((AbstractPage)event.getPage(), (Map<String, ?>)event.getContext());
    }

    @EventListener
    public void createJiraRemoteLinks(BlogPostCreateEvent event) {
        this.createJiraRemoteLinksForNewPage((AbstractPage)event.getBlogPost(), (Map<String, ?>)event.getContext());
    }

    @EventListener
    public void updateJiraRemoteLinks(BlogPostUpdateEvent event) {
        if (event.getBlogPost().isCurrent() && event.getOriginalBlogPost() != null) {
            this.updateJiraRemoteLinks((AbstractPage)event.getOriginalBlogPost(), (AbstractPage)event.getBlogPost());
        }
    }

    @EventListener
    public void updateJiraRemoteLinks(PageUpdateEvent event) {
        if (event.getPage().isCurrent() && event.getOriginalPage() != null) {
            this.updateJiraRemoteLinks(event.getOriginalPage(), (AbstractPage)event.getPage());
        }
    }

    @EventListener
    public void handleBlueprintPageCreate(BlueprintPageCreateEvent event) {
        this.handleBlueprintPageCreate((AbstractPage)event.getPage(), event.getBlueprintKey().getCompleteKey(), event.getContext());
    }

    @EventListener
    public void deleteJiraRemoteLinks(PageRemoveEvent event) {
        this.deleteJiraRemoteLinks((AbstractPage)event.getPage());
    }

    @EventListener
    public void deleteJiraRemoteLinks(PageTrashedEvent event) {
        this.deleteJiraRemoteLinks((AbstractPage)event.getPage());
    }

    @EventListener
    public void deleteJiraRemoteLinks(BlogPostTrashedEvent event) {
        this.deleteJiraRemoteLinks((AbstractPage)event.getBlogPost());
    }

    @EventListener
    public void restoreJiraRemoteLinks(PageRestoreEvent event) {
        this.createJiraRemoteLinksForRestoredPage((AbstractPage)event.getPage());
    }

    @EventListener
    public void restoreJiraRemoteLinks(BlogPostRestoreEvent event) {
        this.createJiraRemoteLinksForRestoredPage((AbstractPage)event.getBlogPost());
    }

    private void createJiraRemoteLinksForRestoredPage(AbstractPage newPage) {
        this.jiraRemoteIssueLinkManager.createIssueLinksForEmbeddedMacros(newPage);
    }

    private void createJiraRemoteLinksForNewPage(AbstractPage newPage, Map<String, ?> context) {
        this.jiraRemoteIssueLinkManager.createIssueLinksForEmbeddedMacros(newPage);
        this.handlePageCreateInitiatedFromJIRAEntity(newPage, "", Maps.transformValues(context, PARAM_VALUE_TO_STRING_FUNCTION));
    }

    private void updateJiraRemoteLinks(AbstractPage originalPage, AbstractPage currentPage) {
        this.jiraRemoteIssueLinkManager.updateIssueLinksForEmbeddedMacros(originalPage, currentPage);
    }

    private void deleteJiraRemoteLinks(AbstractPage page) {
        this.jiraRemoteIssueLinkManager.deleteIssueLinksForEmbeddedMacros(page);
    }

    private void handleBlueprintPageCreate(AbstractPage page, String blueprintKey, Map<String, ?> context) {
        this.handlePageCreateInitiatedFromJIRAEntity(page, blueprintKey, Maps.transformValues(context, PARAM_VALUE_TO_STRING_FUNCTION));
    }

    @EventListener
    public void updatePrimaryApplink(ApplicationLinkMadePrimaryEvent event) {
        this.jiraConnectorManager.updatePrimaryServer((ReadOnlyApplicationLink)event.getApplicationLink());
    }

    @EventListener
    public void updateDetailJiraServerInfor(ApplicationLinkDetailsChangedEvent event) {
        this.jiraConnectorManager.updateDetailJiraServerInfor((ReadOnlyApplicationLink)event.getApplicationLink());
    }

    private void handlePageCreateInitiatedFromJIRAEntity(AbstractPage page, String blueprintModuleKey, Map<String, String> params) {
        if (this.containsValue(APPLINK_ID, params, false)) {
            if (this.containsValue(ISSUE_KEY, params, false) && this.containsValue(FALLBACK_URL, params, true) && this.containsValue(CREATION_TOKEN, params, true)) {
                boolean successfulLink = this.jiraRemoteEpicLinkManager.createLinkToEpic(page, params.get(APPLINK_ID), params.get(ISSUE_KEY), params.get(FALLBACK_URL), params.get(CREATION_TOKEN));
                if (successfulLink) {
                    this.eventPublisher.publish((Object)new PageCreatedFromJiraAnalyticsEvent(this, PageCreatedFromJiraAnalyticsEvent.EventType.EPIC_FROM_PLAN_MODE, blueprintModuleKey));
                }
            } else if (this.containsValue(SPRINT_ID, params, false) && this.containsValue(FALLBACK_URL, params, true) && this.containsValue(CREATION_TOKEN, params, true) && this.containsValue(AGILE_MODE, params, true)) {
                boolean successfulLink = this.jiraRemoteSprintLinkManager.createLinkToSprint(page, params.get(APPLINK_ID), params.get(SPRINT_ID), params.get(FALLBACK_URL), params.get(CREATION_TOKEN));
                if (successfulLink && AGILE_MODE_VALUE_PLAN.equals(params.get(AGILE_MODE))) {
                    this.eventPublisher.publish((Object)new PageCreatedFromJiraAnalyticsEvent(this, PageCreatedFromJiraAnalyticsEvent.EventType.SPRINT_FROM_PLAN_MODE, blueprintModuleKey));
                } else if (successfulLink && AGILE_MODE_VALUE_REPORT.equals(params.get(AGILE_MODE))) {
                    this.eventPublisher.publish((Object)new PageCreatedFromJiraAnalyticsEvent(this, PageCreatedFromJiraAnalyticsEvent.EventType.SPRINT_FROM_REPORT_MODE, blueprintModuleKey));
                }
            }
        }
    }

    private boolean containsValue(String key, Map<String, String> params, boolean logIfNotPresent) {
        String value;
        boolean containsValue = false;
        if (params.containsKey(key) && (value = params.get(key)) != null && !value.isEmpty()) {
            containsValue = true;
        }
        if (!containsValue && logIfNotPresent) {
            LOGGER.warn("Link could not be created for a page created from Jira, as no value was provided for '{}'", (Object)key);
        }
        return containsValue;
    }

    @PostConstruct
    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }
}

