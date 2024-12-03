/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.jira.columns;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.extra.jira.JiraIssuesMacro;
import com.atlassian.confluence.extra.jira.JiraIssuesManager;
import com.atlassian.confluence.extra.jira.api.services.JiraIssueSortingManager;
import com.atlassian.confluence.extra.jira.api.services.JiraIssuesColumnManager;
import com.atlassian.confluence.extra.jira.columns.JiraColumnInfo;
import com.atlassian.confluence.extra.jira.columns.JiraIssueSortableHelper;
import com.atlassian.confluence.extra.jira.helper.JiraJqlHelper;
import com.atlassian.confluence.extra.jira.util.JiraUtil;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import org.apache.commons.lang3.StringUtils;

public class DefaultJiraIssueSortingManager
implements JiraIssueSortingManager {
    private final JiraIssuesColumnManager jiraIssuesColumnManager;
    private final JiraIssuesManager jiraIssuesManager;
    private final I18nResolver i18nResolver;

    public DefaultJiraIssueSortingManager(JiraIssuesColumnManager jiraIssuesColumnManager, JiraIssuesManager jiraIssuesManager, I18nResolver i18nResolver) {
        this.jiraIssuesColumnManager = jiraIssuesColumnManager;
        this.jiraIssuesManager = jiraIssuesManager;
        this.i18nResolver = i18nResolver;
    }

    @Override
    public String getRequestDataForSorting(Map<String, String> parameters, String requestData, JiraIssuesMacro.Type requestType, Set<JiraColumnInfo> jiraColumns, ConversionContext conversionContext, ReadOnlyApplicationLink applink) throws MacroExecutionException {
        String orderColumnName = (String)conversionContext.getProperty("orderColumnName");
        String order = (String)conversionContext.getProperty("order");
        if (StringUtils.isBlank((CharSequence)orderColumnName)) {
            return requestData;
        }
        parameters.put("cache", "off");
        String clauseName = this.getClauseName(parameters, jiraColumns, orderColumnName, applink);
        switch (requestType) {
            case URL: {
                return this.getUrlSortRequest(requestData, clauseName, order, jiraColumns, JiraUtil.getMaximumIssues(parameters.get("maximumIssues")), applink);
            }
            case JQL: {
                return this.getJQLSortRequest(requestData, clauseName, order, jiraColumns);
            }
        }
        return requestData;
    }

    private String getClauseName(Map<String, String> parameters, Set<JiraColumnInfo> jiraColumns, String orderColumnName, ReadOnlyApplicationLink applink) {
        Set<JiraColumnInfo> columns = this.jiraIssuesColumnManager.getColumnInfo(parameters, jiraColumns, applink);
        String orderStringById = null;
        String orderStringByName = null;
        for (JiraColumnInfo columnInfo : columns) {
            if (columnInfo.getKey().equalsIgnoreCase(orderColumnName)) {
                orderStringById = this.jiraIssuesColumnManager.getColumnMapping(columnInfo.getPrimaryClauseName(), JiraIssuesColumnManager.COLUMN_KEYS_MAPPING);
            }
            if (!columnInfo.getTitle().equalsIgnoreCase(orderColumnName)) continue;
            orderStringByName = this.jiraIssuesColumnManager.getColumnMapping(columnInfo.getPrimaryClauseName(), JiraIssuesColumnManager.COLUMN_KEYS_MAPPING);
        }
        return !StringUtils.isEmpty(orderStringById) ? orderStringById : (!StringUtils.isEmpty(orderStringByName) ? orderColumnName : "");
    }

    private String getUrlSortRequest(String requestData, String clauseName, String order, Set<JiraColumnInfo> jiraColumns, int maximumIssues, ReadOnlyApplicationLink applink) throws MacroExecutionException {
        StringBuilder urlSort = new StringBuilder();
        String jql = "";
        if (JiraJqlHelper.isUrlFilterType((String)requestData)) {
            jql = JiraJqlHelper.getJQLFromFilter(applink, (String)requestData, this.jiraIssuesManager, this.i18nResolver);
        }
        if (StringUtils.isNotBlank((CharSequence)jql)) {
            requestData = JiraUtil.normalizeUrl(applink.getRpcUrl()) + "/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=" + JiraUtil.utf8Encode(jql) + "&tempMax=" + maximumIssues;
        }
        Matcher matcher = JiraJqlHelper.XML_SORTING_PATTERN.matcher((CharSequence)requestData);
        Matcher matcher2 = JiraJqlHelper.XML_SORTING_PATTERN_TEMPMAX.matcher((CharSequence)requestData);
        if (matcher.find() || matcher2.find()) {
            Object orderData;
            String url;
            String tempMax;
            if (matcher.find()) {
                jql = JiraUtil.utf8Decode(JiraJqlHelper.getValueByRegEx((String)requestData, JiraJqlHelper.XML_SORTING_PATTERN, 2));
                tempMax = JiraJqlHelper.getValueByRegEx((String)requestData, JiraJqlHelper.XML_SORTING_PATTERN, 3);
                url = ((String)requestData).substring(0, matcher.end(1) + 1);
            } else {
                jql = JiraUtil.utf8Decode(JiraJqlHelper.getValueByRegEx((String)requestData, JiraJqlHelper.XML_SORTING_PATTERN_TEMPMAX, 3));
                tempMax = JiraJqlHelper.getValueByRegEx((String)requestData, JiraJqlHelper.XML_SORTING_PATTERN_TEMPMAX, 2);
                url = ((String)requestData).substring(0, matcher2.end(1) + 1).replaceAll("tempMax=([0-9]+)&", "");
            }
            Matcher orderMatch = JiraJqlHelper.SORTING_PATTERN.matcher(jql);
            if (orderMatch.find()) {
                String orderColumns = jql.substring(orderMatch.end() - 1);
                jql = jql.substring(0, orderMatch.end() - 1);
                orderData = JiraIssueSortableHelper.reoderColumns(order, clauseName, orderColumns, jiraColumns);
            } else {
                orderData = " ORDER BY \"" + JiraUtil.escapeDoubleQuote(clauseName) + "\" " + order;
            }
            urlSort.append(url).append(JiraUtil.utf8Encode(jql + (String)orderData)).append("&tempMax=").append(tempMax);
        }
        return urlSort.toString();
    }

    private String getJQLSortRequest(String requestData, String clauseName, String order, Set<JiraColumnInfo> jiraColumns) {
        StringBuilder jqlSort = new StringBuilder();
        Matcher matcher = JiraJqlHelper.SORTING_PATTERN.matcher((CharSequence)requestData);
        if (matcher.find()) {
            String orderColumns = ((String)requestData).substring(matcher.end() - 1);
            orderColumns = JiraIssueSortableHelper.reoderColumns(order, clauseName, orderColumns, jiraColumns);
            jqlSort.append((CharSequence)requestData, 0, matcher.end() - 1).append(orderColumns);
        } else {
            requestData = (String)requestData + " ORDER BY \"" + JiraUtil.escapeDoubleQuote(clauseName) + "\" " + order;
            jqlSort.append((String)requestData);
        }
        return jqlSort.toString();
    }
}

