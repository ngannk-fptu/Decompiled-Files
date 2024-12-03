/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.ResponseException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.helper;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.extra.jira.JiraIssuesManager;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.ResponseException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraJqlHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(JiraJqlHelper.class);
    public static final String ISSUE_KEY_REGEX = "(^|[^a-zA-Z]|\n)(([A-Z][A-Z]+)-[0-9]+)";
    public static final String XML_KEY_REGEX = ".+/([A-Za-z]+-[0-9]+)/.+";
    public static final String URL_KEY_REGEX = ".+/(i#)?browse/([A-Za-z]+-[0-9]+)";
    public static final String URL_JQL_REGEX = ".+(jqlQuery|jql)=([^&]+)";
    public static final String FILTER_URL_REGEX = ".+(requestId|filter)=([^&]+)";
    public static final String FILTER_XML_REGEX = ".+searchrequest-xml/([0-9]+)/SearchRequest.+";
    public static final String SORTING_REGEX = "(Order\\s*BY) (.?)";
    public static final String XML_SORT_REGEX = ".+(jqlQuery|jql)=([^&]+).+tempMax=([0-9]+)";
    public static final String XML_SORT_REGEX_TEMPMAX = ".+(tempMax=([0-9]+).+jqlQuery|jql)=([^&]+)";
    public static final String TEMPMAX = "tempMax=([0-9]+)&";
    public static final String SINGLE_ISSUE_REGEX = "^\\s*((KEY|ISSUEKEY)\\s*=)?\\s*\"*[A-Z]+[A-Z_0-9]*-[0-9]+\"*\\s*$";
    public static final Pattern ISSUE_KEY_PATTERN = Pattern.compile("(^|[^a-zA-Z]|\n)(([A-Z][A-Z]+)-[0-9]+)");
    public static final Pattern XML_KEY_PATTERN = Pattern.compile(".+/([A-Za-z]+-[0-9]+)/.+");
    public static final Pattern URL_KEY_PATTERN = Pattern.compile(".+/(i#)?browse/([A-Za-z]+-[0-9]+)");
    public static final Pattern URL_JQL_PATTERN = Pattern.compile(".+(jqlQuery|jql)=([^&]+)");
    public static final Pattern FILTER_URL_PATTERN = Pattern.compile(".+(requestId|filter)=([^&]+)");
    public static final Pattern FILTER_XML_PATTERN = Pattern.compile(".+searchrequest-xml/([0-9]+)/SearchRequest.+");
    public static final Pattern SORTING_PATTERN = Pattern.compile("(Order\\s*BY) (.?)", 2);
    public static final Pattern XML_SORTING_PATTERN = Pattern.compile(".+(jqlQuery|jql)=([^&]+).+tempMax=([0-9]+)", 2);
    public static final Pattern XML_SORTING_PATTERN_TEMPMAX = Pattern.compile(".+(tempMax=([0-9]+).+jqlQuery|jql)=([^&]+)", 2);
    public static final String XML_SEARCH_REQUEST_URI = "/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml";

    public static String getJQLFromJQLURL(String requestData) {
        String jql = JiraJqlHelper.getValueByRegEx(requestData, URL_JQL_PATTERN, 2);
        if (jql != null) {
            jql = URLDecoder.decode(jql, StandardCharsets.UTF_8);
        }
        return jql;
    }

    public static String getKeyFromURL(String url) {
        String key = JiraJqlHelper.getValueByRegEx(url, XML_KEY_PATTERN, 1);
        if (key != null) {
            return key;
        }
        key = JiraJqlHelper.getValueByRegEx(url, URL_KEY_PATTERN, 2);
        return key != null ? key : url;
    }

    private static String getFilterIdFromURL(String url) {
        String filterId = JiraJqlHelper.getValueByRegEx(url, FILTER_URL_PATTERN, 2);
        if (filterId != null) {
            return filterId;
        }
        filterId = JiraJqlHelper.getValueByRegEx(url, FILTER_XML_PATTERN, 1);
        return filterId != null ? filterId : url;
    }

    public static String getJQLFromFilter(ReadOnlyApplicationLink appLink, String url, JiraIssuesManager jiraIssuesManager, I18nResolver i18nResolver) throws MacroExecutionException {
        String filterId = JiraJqlHelper.getFilterIdFromURL(url);
        try {
            return jiraIssuesManager.retrieveJQLFromFilter(filterId, appLink);
        }
        catch (ResponseException e) {
            throw new MacroExecutionException(i18nResolver.getText("insert.jira.issue.message.nofilter"), (Throwable)e);
        }
    }

    public static String getValueByRegEx(String data, Pattern pattern, int group) {
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            return matcher.group(group);
        }
        return null;
    }

    public static boolean isUrlKeyType(String url) {
        return url.matches(URL_KEY_REGEX) || url.matches(XML_KEY_REGEX);
    }

    public static boolean isUrlFilterType(String url) {
        return url.matches(FILTER_URL_REGEX) || url.matches(FILTER_XML_REGEX);
    }

    public static boolean isJqlKeyType(String jql) {
        return jql.toUpperCase().matches(SINGLE_ISSUE_REGEX);
    }
}

