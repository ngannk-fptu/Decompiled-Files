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

public class StatusReportContextProvider
extends AbstractBlueprintContextProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatusReportContextProvider.class);
    private static final String SOY_PIE_CHART_MACRO_TEMPLATE = "Confluence.Blueprints.JiraReports.Template.piechart.soy";
    private static final String SOY_JIRAISSUES_MACRO_TEMPLATE = "Confluence.Blueprints.JiraReports.Template.jiraissues.soy";
    private SoftwareBlueprintsContextProviderHelper helper;
    private ApplicationLinkService appLinkService;
    private JiraIssuesHelper jiraIssuesHelper;

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        block6: {
            String appId = (String)context.get("jira-reports-servers");
            if (StringUtils.isNotBlank((CharSequence)appId)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.helper.getDateFormat(), this.helper.getAuthenticatedUserLocale());
                context.put("date", (Object)simpleDateFormat.format(new Date()));
                try {
                    ApplicationLink appLink = this.appLinkService.getApplicationLink(new ApplicationId(appId));
                    if (appLink == null) {
                        return context;
                    }
                    String jql = null;
                    jql = "jirareports-statusreport-static".equals(context.get("statusreport-type")) ? URLEncoder.encode(this.jiraIssuesHelper.buildProjectVersionJQL(context.getMap()), "UTF-8") : (String)context.get("jira-query");
                    if ("dynamic".equals(context.get("issues-list-type")) && "jirareports-statusreport-dynamic".equals(context.get("statusreport-type"))) {
                        String totalIssues = this.getJiraIssuesMacro("jqlQuery", context.get("jira-query"), (ReadOnlyApplicationLink)appLink, true);
                        context.put("totalIssues", (Object)totalIssues);
                        context.put("pie_chart_summary", (Object)this.renderChart(jql, "statuses", (ReadOnlyApplicationLink)appLink, this.helper.getText("jirareports.statusreport.blueprint.form.statType.status"), totalIssues));
                        context.put("pie_chart_priority", (Object)this.renderChart(jql, "priorities", (ReadOnlyApplicationLink)appLink, this.helper.getText("jirareports.statusreport.blueprint.form.statType.priority"), totalIssues));
                        context.put("pie_chart_component", (Object)this.renderChart(jql, "components", (ReadOnlyApplicationLink)appLink, this.helper.getText("jirareports.statusreport.blueprint.form.statType.component"), totalIssues));
                        context.put("pie_chart_issuetype", (Object)this.renderChart(jql, "issuetype", (ReadOnlyApplicationLink)appLink, this.helper.getText("jirareports.statusreport.blueprint.form.statType.issuetype"), totalIssues));
                        break block6;
                    }
                    Channel channel = this.jiraIssuesHelper.getChannel((ReadOnlyApplicationLink)appLink, jql, 0);
                    int totalIssues = this.jiraIssuesHelper.getTotalIssueNumber(channel);
                    context.put("totalIssues", (Object)(totalIssues + " issues"));
                }
                catch (Exception e) {
                    LOGGER.error("error render content", (Throwable)e);
                }
            } else {
                LOGGER.info("appId is null or empty");
            }
        }
        this.doAnalytic(context);
        return context;
    }

    private String getJiraIssuesMacro(String keyJQL, Object valJQL, ReadOnlyApplicationLink appLink, boolean isCount) {
        HashMap jiraIssuesMacroContext = Maps.newHashMap();
        jiraIssuesMacroContext.put("serverId", appLink.getId().toString());
        jiraIssuesMacroContext.put("server", appLink.getName());
        jiraIssuesMacroContext.put("keyJQL", keyJQL);
        jiraIssuesMacroContext.put("valJQL", valJQL);
        jiraIssuesMacroContext.put("isCount", isCount);
        return this.helper.renderFromSoy("com.atlassian.confluence.plugins.confluence-software-blueprints:jirareports-resources", SOY_JIRAISSUES_MACRO_TEMPLATE, jiraIssuesMacroContext);
    }

    private String renderChart(String jql, String statisticType, ReadOnlyApplicationLink appLink, String titleType, String totalIssues) {
        HashMap piechartContext = Maps.newHashMap();
        piechartContext.put("totalIssues", totalIssues);
        piechartContext.put("titleType", titleType);
        piechartContext.put("serverId", appLink.getId().toString());
        piechartContext.put("server", appLink.getName());
        piechartContext.put("jql", jql);
        piechartContext.put("statType", statisticType);
        piechartContext.put("width", "");
        piechartContext.put("border", true);
        return this.helper.renderFromSoy("com.atlassian.confluence.plugins.confluence-software-blueprints:jirareports-resources", SOY_PIE_CHART_MACRO_TEMPLATE, piechartContext);
    }

    public void setHelper(SoftwareBlueprintsContextProviderHelper helper) {
        this.helper = helper;
    }

    public void setAppLinkService(ApplicationLinkService appLinkService) {
        this.appLinkService = appLinkService;
    }

    public void setJiraIssuesHelper(JiraIssuesHelper jiraIssuesHelper) {
        this.jiraIssuesHelper = jiraIssuesHelper;
    }

    private void doAnalytic(BlueprintContext context) {
        this.helper.publishAnalyticEvent("confluence.software.blueprints.statusreport.create");
        this.helper.publishAnalyticEvent("confluence.software.blueprints.statusreport.dynamic");
        if ("simple".equals(context.get("dialogMode"))) {
            this.helper.publishAnalyticEvent("confluence.software.blueprints.statusreport.simple");
        }
    }
}

