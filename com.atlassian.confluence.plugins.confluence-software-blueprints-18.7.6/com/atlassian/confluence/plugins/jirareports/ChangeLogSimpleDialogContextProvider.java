/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.confluence.extra.jira.Channel
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.jirareports;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.extra.jira.Channel;
import com.atlassian.confluence.plugins.SoftwareBlueprintsContextProviderHelper;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.plugins.jirareports.JiraIssuesHelper;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeLogSimpleDialogContextProvider
extends AbstractBlueprintContextProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeLogSimpleDialogContextProvider.class);
    private SoftwareBlueprintsContextProviderHelper helper;
    private ApplicationLinkService appLinkService;
    private JiraIssuesHelper jiraIssuesHelper;
    private static final int MAX_RESULT = 1000;

    public ChangeLogSimpleDialogContextProvider(SoftwareBlueprintsContextProviderHelper helper, ApplicationLinkService appLinkService, JiraIssuesHelper jiraIssuesHelper) {
        this.helper = helper;
        this.appLinkService = appLinkService;
        this.jiraIssuesHelper = jiraIssuesHelper;
    }

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.helper.getDateFormat(), this.helper.getAuthenticatedUserLocale());
        context.put("date", (Object)simpleDateFormat.format(new Date()));
        this.setupStaticContent(context);
        this.helper.publishAnalyticEvent("confluence.software.blueprints.changelog.create");
        this.helper.publishAnalyticEvent("confluence.software.blueprints.changelog.static");
        this.helper.publishAnalyticEvent("confluence.software.blueprints.changelog.simple");
        return context;
    }

    private void setupStaticContent(BlueprintContext contextMap) {
        String appId = (String)contextMap.get("jira-reports-servers");
        if (appId != null) {
            try {
                ApplicationLink appLink = this.appLinkService.getApplicationLink(new ApplicationId(appId));
                String projectVersionJql = this.jiraIssuesHelper.buildProjectVersionJQL(contextMap.getMap());
                if (projectVersionJql != null) {
                    String jql = URLEncoder.encode(projectVersionJql, "UTF-8");
                    Channel channel = this.jiraIssuesHelper.getChannel((ReadOnlyApplicationLink)appLink, jql, 1000);
                    contextMap.put("jiraIssues", (Object)this.jiraIssuesHelper.renderJiraIssues(channel, appLink.getRpcUrl().toString(), appLink.getDisplayUrl().toString()));
                    contextMap.put("jiraIssuesCount", (Object)(this.jiraIssuesHelper.getTotalIssueNumber(channel) + " " + this.helper.getText("jirareports.changelog.blueprint.total.issues.title")));
                }
            }
            catch (Exception e) {
                LOGGER.error("Can not render jira issues", (Throwable)e);
            }
        }
    }
}

