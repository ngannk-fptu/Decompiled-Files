/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.jira;

import com.atlassian.confluence.extra.jira.api.services.JiraIssuesColumnManager;
import com.atlassian.confluence.extra.jira.api.services.JiraIssuesUrlManager;
import com.atlassian.confluence.extra.jira.util.JiraUtil;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class DefaultJiraIssuesUrlManager
implements JiraIssuesUrlManager {
    private static final Pattern URL_WITHOUT_QUERY_STRING_PATTERN = Pattern.compile("(^.*)\\?");
    private static final Pattern TEMPMAX_REQUEST_PARAMETER_PATTERN = Pattern.compile("tempMax=\\d+");
    private static final Pattern JQLQUERY_REQUEST_PARAMER_PATTERN = Pattern.compile("jqlQuery=([^\\&]*)", 2);
    private final JiraIssuesColumnManager jiraIssuesColumnManager;

    public DefaultJiraIssuesUrlManager(JiraIssuesColumnManager jiraIssuesColumnManager) {
        this.jiraIssuesColumnManager = jiraIssuesColumnManager;
    }

    @Override
    public String getRequestUrl(String anyUrl) {
        Matcher urlWithoutQueryStringMatcher = URL_WITHOUT_QUERY_STRING_PATTERN.matcher(anyUrl);
        if (urlWithoutQueryStringMatcher.find()) {
            return urlWithoutQueryStringMatcher.group(1);
        }
        return anyUrl;
    }

    @Override
    public String getJiraXmlUrlFromFlexigridRequest(String url, String resultsPerPage, String sortField, String sortOrder) {
        return this.getJiraXmlUrlFromFlexigridRequest(url, resultsPerPage, null, sortField, sortOrder);
    }

    private String getAbsoluteUrlWithTempMaxRequestParametersSetToResultsPerPage(String url, String resultsPerPage) {
        Matcher tempMaxMatcher = TEMPMAX_REQUEST_PARAMETER_PATTERN.matcher(url);
        StringBuilder urlBuilder = new StringBuilder();
        if (tempMaxMatcher.find()) {
            urlBuilder.setLength(0);
            urlBuilder.append(tempMaxMatcher.replaceAll("tempMax=" + resultsPerPage));
        } else {
            urlBuilder.append(url).append(url.indexOf(63) >= 0 ? (char)'&' : '?').append("tempMax=").append(resultsPerPage).toString();
        }
        return urlBuilder.toString();
    }

    @Override
    public String getJiraXmlUrlFromFlexigridRequest(String url, String resultsPerPage, String page, String sortField, String sortOrder) {
        StringBuilder jiraXmlUrlBuilder = new StringBuilder(url);
        jiraXmlUrlBuilder.append(url.contains("?") ? "" : "?1=1");
        if (StringUtils.isNotBlank((CharSequence)resultsPerPage)) {
            int resultsPerPageInt = Integer.parseInt(resultsPerPage);
            jiraXmlUrlBuilder.setLength(0);
            jiraXmlUrlBuilder.append(this.getAbsoluteUrlWithTempMaxRequestParametersSetToResultsPerPage(url, resultsPerPage));
            if (StringUtils.isNotBlank((CharSequence)page)) {
                jiraXmlUrlBuilder.append("&pager/start=").append(resultsPerPageInt * (Integer.parseInt(page) - 1));
            }
        }
        if (StringUtils.isNotBlank((CharSequence)sortField)) {
            switch (sortField) {
                case "key": {
                    sortField = "issuekey";
                    break;
                }
                case "type": {
                    sortField = "issuetype";
                    break;
                }
                default: {
                    Map<String, String> columnMapForJiraInstance = this.jiraIssuesColumnManager.getColumnMap(this.getRequestUrl(url));
                    if (columnMapForJiraInstance == null || !columnMapForJiraInstance.containsKey(sortField)) break;
                    sortField = columnMapForJiraInstance.get(sortField);
                }
            }
            Matcher jqlMatcher = JQLQUERY_REQUEST_PARAMER_PATTERN.matcher(jiraXmlUrlBuilder);
            if (jqlMatcher.find()) {
                Object jqlQuery = URLDecoder.decode(jqlMatcher.group(1), StandardCharsets.UTF_8);
                String lcJqlQuery = ((String)jqlQuery).toLowerCase();
                int orderByIdx = lcJqlQuery.lastIndexOf(" order by");
                if (orderByIdx != -1) {
                    jqlQuery = ((String)jqlQuery).substring(0, orderByIdx);
                }
                if (((String)sortField).startsWith("customfield_")) {
                    sortField = "cf[" + ((String)sortField).substring("customfield_".length()) + "]";
                }
                jqlQuery = (String)jqlQuery + " ORDER BY " + (String)sortField;
                if (StringUtils.isNotBlank((CharSequence)sortOrder)) {
                    jqlQuery = (String)jqlQuery + " " + sortOrder;
                }
                jiraXmlUrlBuilder = new StringBuilder(jqlMatcher.replaceFirst("jqlQuery=" + JiraUtil.utf8Encode((String)jqlQuery)));
            } else {
                jiraXmlUrlBuilder.append("&sorter/field=").append(JiraUtil.utf8Encode((String)sortField));
                if (StringUtils.isNotBlank((CharSequence)sortOrder)) {
                    jiraXmlUrlBuilder.append("&sorter/order=").append(sortOrder.toUpperCase());
                }
            }
        }
        return jiraXmlUrlBuilder.toString().replaceFirst("\\?1=1&", "?");
    }
}

