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
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
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
import com.google.common.collect.Maps;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeLogAdvanceDialogContextProvider
extends AbstractBlueprintContextProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeLogAdvanceDialogContextProvider.class);
    private static final String SOY_JIRAISSUES_MACRO_TEMPLATE = "Confluence.Blueprints.JiraReports.Template.jiraissues.soy";
    private static final String DYNAMIC_JIRA_REPORT_TYPE = "dynamic";
    private static final int MAX_RESULT = 1000;
    private SoftwareBlueprintsContextProviderHelper helper;
    private ApplicationLinkService appLinkService;
    private JiraIssuesHelper jiraIssuesHelper;

    public ChangeLogAdvanceDialogContextProvider(SoftwareBlueprintsContextProviderHelper helper, ApplicationLinkService applicationLinkService, JiraIssuesHelper jiraIssuesHelper) {
        this.helper = helper;
        this.appLinkService = applicationLinkService;
        this.jiraIssuesHelper = jiraIssuesHelper;
    }

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        context.setTitle((String)context.get("title"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.helper.getDateFormat(), this.helper.getAuthenticatedUserLocale());
        context.put("date", (Object)simpleDateFormat.format(new Date()));
        this.addJiraMacroToContextMap(context);
        this.doAnalytic(context);
        return context;
    }

    private void addJiraMacroToContextMap(BlueprintContext contextMap) {
        if (DYNAMIC_JIRA_REPORT_TYPE.equals(contextMap.get("jira-report-type"))) {
            this.renderDynamicJiraIssues(contextMap);
        } else {
            this.renderSnapshotJiraIssues(contextMap);
        }
    }

    private void renderSnapshotJiraIssues(BlueprintContext contextMap) {
        String appId = (String)contextMap.get("jira-server-id");
        if (StringUtils.isNotBlank((CharSequence)appId)) {
            try {
                ApplicationLink appLink = this.appLinkService.getApplicationLink(new ApplicationId(appId));
                String jqlSearch = URLEncoder.encode((String)contextMap.get("jira-query"), "UTF-8");
                Channel channel = this.jiraIssuesHelper.getChannel((ReadOnlyApplicationLink)appLink, jqlSearch, 1000);
                contextMap.put("jiraissuesmacro", (Object)this.jiraIssuesHelper.renderJiraIssues(channel, appLink.getRpcUrl().toString(), appLink.getDisplayUrl().toString()));
                contextMap.put("jiraissuescountmacro", (Object)(this.jiraIssuesHelper.getTotalIssueNumber(channel) + " " + this.helper.getText("jirareports.changelog.blueprint.total.issues.title")));
            }
            catch (Exception e) {
                LOGGER.error("Can not render jira issues", (Throwable)e);
                contextMap.put("jiraissuesmacro", (Object)this.helper.renderTimeout());
            }
        }
    }

    private void renderDynamicJiraIssues(BlueprintContext contextMap) {
        String serverId = (String)contextMap.get("jira-server-id");
        String serverName = (String)contextMap.get("jira-server-name");
        String jqlSearch = (String)contextMap.get("jira-query");
        if (StringUtils.isNotBlank((CharSequence)serverId) && StringUtils.isNotBlank((CharSequence)serverName) && StringUtils.isNotBlank((CharSequence)jqlSearch)) {
            String keyJQL = "jqlQuery";
            String valJQL = jqlSearch;
            String[] jqlItems = jqlSearch.split("=");
            if (jqlItems.length > 1 && jqlItems[0].trim().equals("key")) {
                keyJQL = "key";
                valJQL = jqlItems[1].trim();
            }
            contextMap.put("jiraissuesmacro", (Object)this.getJiraIssuesMacro(keyJQL, valJQL, serverId, serverName, false));
            contextMap.put("jiraissuescountmacro", (Object)this.getJiraIssuesMacro("jqlQuery", jqlSearch, serverId, serverName, true));
        }
    }

    private String getJiraIssuesMacro(String keyJQL, String valJQL, String serverId, String serverName, boolean isCount) {
        HashMap jiraIssuesMacroContext = Maps.newHashMap();
        jiraIssuesMacroContext.put("serverId", serverId);
        jiraIssuesMacroContext.put("server", serverName);
        jiraIssuesMacroContext.put("keyJQL", keyJQL);
        jiraIssuesMacroContext.put("valJQL", valJQL);
        jiraIssuesMacroContext.put("isCount", isCount);
        return this.helper.renderFromSoy("com.atlassian.confluence.plugins.confluence-software-blueprints:jirareports-resources", SOY_JIRAISSUES_MACRO_TEMPLATE, jiraIssuesMacroContext);
    }

    private void doAnalytic(BlueprintContext contextMap) {
        this.helper.publishAnalyticEvent("confluence.software.blueprints.changelog.create");
        if (DYNAMIC_JIRA_REPORT_TYPE.equals(contextMap.get("jira-report-type"))) {
            this.helper.publishAnalyticEvent("confluence.software.blueprints.changelog.dynamic");
        } else {
            this.helper.publishAnalyticEvent("confluence.software.blueprints.changelog.static");
        }
    }
}

