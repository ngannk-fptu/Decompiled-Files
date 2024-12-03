/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.jira.util;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.extra.jira.JiraIssuesMacro;
import com.atlassian.confluence.extra.jira.helper.JiraJqlHelper;
import com.atlassian.confluence.extra.jira.request.JiraRequestData;
import com.atlassian.confluence.extra.jira.util.JiraUtil;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class JiraIssueUtil {
    private static final String JIRA_URL_KEY_PARAM = "url";
    private static final String JQL_QUERY = "jqlQuery";
    private static final String POSITIVE_INTEGER_REGEX = "[0-9]+";
    private static final String ISSUE_NAVIGATOR_PATH = "secure/IssueNavigator.jspa";

    public static String getClickableUrl(String requestData, JiraIssuesMacro.Type requestType, ReadOnlyApplicationLink applicationLink, String baseUrl) {
        if (requestType != JiraIssuesMacro.Type.URL && applicationLink == null) {
            return null;
        }
        Object clickableUrl = null;
        switch (requestType) {
            case URL: {
                clickableUrl = JiraIssueUtil.makeClickableUrl(requestData);
                break;
            }
            case JQL: {
                clickableUrl = JiraUtil.normalizeUrl(applicationLink.getDisplayUrl()) + "/secure/IssueNavigator.jspa?reset=true&jqlQuery=" + JiraUtil.utf8Encode(requestData);
                break;
            }
            case KEY: {
                clickableUrl = JiraUtil.normalizeUrl(applicationLink.getDisplayUrl()) + "/browse/" + JiraUtil.utf8Encode(requestData);
                break;
            }
        }
        if (StringUtils.isNotEmpty((CharSequence)baseUrl)) {
            clickableUrl = JiraIssueUtil.rebaseUrl((String)clickableUrl, baseUrl.trim());
        }
        return JiraIssueUtil.appendSourceParam((String)clickableUrl);
    }

    public static JiraRequestData parseRequestData(Map<String, String> params, I18nResolver i18nResolver) throws MacroExecutionException {
        if (params.containsKey(JIRA_URL_KEY_PARAM)) {
            return JiraIssueUtil.createJiraRequestData(params.get(JIRA_URL_KEY_PARAM), JiraIssuesMacro.Type.URL, i18nResolver);
        }
        if (params.containsKey(JQL_QUERY)) {
            return JiraIssueUtil.createJiraRequestData(params.get(JQL_QUERY), JiraIssuesMacro.Type.JQL, i18nResolver);
        }
        if (params.containsKey("key")) {
            return JiraIssueUtil.createJiraRequestData(params.get("key"), JiraIssuesMacro.Type.KEY, i18nResolver);
        }
        String requestData = JiraIssueUtil.getPrimaryParam(params, i18nResolver);
        if (requestData.startsWith("http")) {
            return JiraIssueUtil.createJiraRequestData(requestData, JiraIssuesMacro.Type.URL, i18nResolver);
        }
        Matcher keyMatcher = JiraJqlHelper.ISSUE_KEY_PATTERN.matcher(requestData);
        if (keyMatcher.find() && keyMatcher.start() == 0) {
            return JiraIssueUtil.createJiraRequestData(requestData, JiraIssuesMacro.Type.KEY, i18nResolver);
        }
        return JiraIssueUtil.createJiraRequestData(requestData, JiraIssuesMacro.Type.JQL, i18nResolver);
    }

    public static String filterOutParam(StringBuffer baseUrl, String filter) {
        int tempMaxParamLocation = baseUrl.indexOf(filter);
        if (tempMaxParamLocation != -1) {
            String value;
            int nextParam = baseUrl.indexOf("&", tempMaxParamLocation);
            if (nextParam != -1) {
                value = baseUrl.substring(tempMaxParamLocation + filter.length(), nextParam);
                baseUrl.delete(tempMaxParamLocation, nextParam + 1);
            } else {
                value = baseUrl.substring(tempMaxParamLocation + filter.length(), baseUrl.length());
                baseUrl.delete(tempMaxParamLocation - 1, baseUrl.length());
            }
            return value;
        }
        return null;
    }

    private static JiraRequestData createJiraRequestData(String requestData, JiraIssuesMacro.Type requestType, I18nResolver i18nResolver) throws MacroExecutionException {
        if (requestType == JiraIssuesMacro.Type.KEY && ((String)requestData).indexOf(44) != -1) {
            String jql = "issuekey in (" + (String)requestData + ")";
            return new JiraRequestData(jql, JiraIssuesMacro.Type.JQL);
        }
        if (requestType == JiraIssuesMacro.Type.URL) {
            try {
                new URL((String)requestData);
                requestData = URLDecoder.decode((String)requestData, Charset.defaultCharset());
                URL url = new URL((String)requestData);
                if (url.getQuery() != null) {
                    String encodedQuery = URLEncoder.encode(url.getQuery(), Charset.defaultCharset());
                    requestData = url.getProtocol() + "://" + url.getHost() + url.getPath() + "?" + encodedQuery;
                }
            }
            catch (MalformedURLException e) {
                throw new MacroExecutionException(i18nResolver.getText("jiraissues.error.invalidurl", new Serializable[]{requestData}), (Throwable)e);
            }
            requestData = JiraIssueUtil.cleanUrlParentheses((String)requestData).trim().replaceFirst("/sr/jira.issueviews:searchrequest.*-rss/", "/sr/jira.issueviews:searchrequest-xml/");
        }
        return new JiraRequestData((String)requestData, requestType);
    }

    private static String cleanUrlParentheses(String url) {
        if (url.indexOf(40) > 0) {
            url = url.replaceAll("\\(", "%28");
        }
        if (url.indexOf(41) > 0) {
            url = url.replaceAll("\\)", "%29");
        }
        if (url.indexOf("&amp;") > 0) {
            url = url.replaceAll("&amp;", "&");
        }
        return url;
    }

    private static String getPrimaryParam(Map<String, String> params, I18nResolver i18nResolver) throws MacroExecutionException {
        if (params.get("data") != null) {
            return params.get("data").trim();
        }
        Set<String> keys = params.keySet();
        for (String key : keys) {
            if (!StringUtils.isNotBlank((CharSequence)key) || JiraIssuesMacro.MACRO_PARAMS.contains(key)) continue;
            return key.matches(POSITIVE_INTEGER_REGEX) ? params.get(key) : key + "=" + params.get(key);
        }
        throw new MacroExecutionException(i18nResolver.getText("jiraissues.error.invalidMacroFormat"));
    }

    private static String makeClickableUrl(String url) {
        StringBuffer link = new StringBuffer(url);
        JiraIssueUtil.filterOutParam(link, "view=");
        JiraIssueUtil.filterOutParam(link, "decorator=");
        JiraIssueUtil.filterOutParam(link, "os_username=");
        JiraIssueUtil.filterOutParam(link, "os_password=");
        JiraIssueUtil.filterOutParam(link, "returnMax=");
        String linkString = link.toString();
        linkString = linkString.replaceFirst("sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml\\?", "secure/IssueNavigator.jspa?reset=true&");
        linkString = linkString.replaceFirst("sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml", "secure/IssueNavigator.jspa?reset=true");
        linkString = linkString.replaceFirst("sr/jira.issueviews:searchrequest-xml/[0-9]+/SearchRequest-([0-9]+).xml\\?", "secure/IssueNavigator.jspa?requestId=$1&");
        linkString = linkString.replaceFirst("sr/jira.issueviews:searchrequest-xml/[0-9]+/SearchRequest-([0-9]+).xml", "secure/IssueNavigator.jspa?requestId=$1");
        return linkString;
    }

    private static String rebaseUrl(String clickableUrl, String baseUrl) {
        return clickableUrl.replaceFirst("^.*?://[^/]+", baseUrl);
    }

    private static String appendSourceParam(String clickableUrl) {
        String operator = clickableUrl.contains("?") ? "&" : "?";
        return clickableUrl + operator + "src=confmacro";
    }

    public static Set<String> getIssueKeys(List<MacroDefinition> macroDefinitions) {
        HashSet issueKeys = Sets.newHashSet();
        issueKeys.addAll(macroDefinitions.stream().map(macroDefinition -> macroDefinition.getParameter("key")).collect(Collectors.toSet()));
        return issueKeys;
    }

    public static String getUserKey(ConfluenceUser user) {
        return user != null ? user.getKey().getStringValue() : "anonymous";
    }
}

