/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.sal.api.net.Response
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.jdom.Attribute
 *  org.jdom.Element
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.util;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.extra.jira.JiraIssuesMacro;
import com.atlassian.confluence.extra.jira.api.services.JiraResponseHandler;
import com.atlassian.confluence.extra.jira.columns.JiraIssueSortableHelper;
import com.atlassian.confluence.extra.jira.exception.AuthenticationException;
import com.atlassian.confluence.extra.jira.exception.JiraPermissionException;
import com.atlassian.confluence.extra.jira.exception.JiraRuntimeException;
import com.atlassian.confluence.extra.jira.exception.MalformedRequestException;
import com.atlassian.confluence.extra.jira.helper.JiraJqlHelper;
import com.atlassian.confluence.extra.jira.request.JiraChannelResponseHandler;
import com.atlassian.confluence.extra.jira.request.JiraStringResponseHandler;
import com.atlassian.confluence.extra.jira.util.JiraIssuePredicates;
import com.atlassian.confluence.plugins.jira.beans.BasicJiraIssueBean;
import com.atlassian.confluence.plugins.jira.beans.JiraIssueBean;
import com.atlassian.sal.api.net.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.jdom.Attribute;
import org.jdom.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraUtil {
    private static final Logger log = LoggerFactory.getLogger(JiraUtil.class);
    public static final String JIRA_PLUGIN_KEY = "confluence.extra.jira";
    public static final int DEFAULT_NUMBER_OF_ISSUES = 20;
    public static final int MAXIMUM_ISSUES = 1000;
    public static final int PARAM_POSITION_1 = 1;
    public static final int PARAM_POSITION_2 = 2;
    public static final int PARAM_POSITION_4 = 4;
    public static final int PARAM_POSITION_5 = 5;
    public static final int PARAM_POSITION_6 = 6;
    public static final int SUMMARY_PARAM_POSITION = 7;
    public static final String EPIC_LINK_ID = "epicLinkId";

    private JiraUtil() {
    }

    public static void checkForErrors(Response response, String url) throws IOException {
        if (!response.isSuccessful()) {
            switch (response.getStatusCode()) {
                case 403: {
                    throw new JiraPermissionException(response.getStatusText());
                }
                case 401: {
                    throw new AuthenticationException(response.getStatusText());
                }
                case 400: {
                    throw new MalformedRequestException(response.getStatusText());
                }
            }
            log.warn("Received HTTP {} from {}. Error message: {}", new Object[]{response.getStatusCode(), url, StringUtils.defaultString((String)response.getStatusText(), (String)"No status message")});
            throw new JiraRuntimeException(response.getStatusText());
        }
        log.debug("Successful {} response from {}", (Object)response.getStatusCode(), (Object)url);
    }

    public static JiraResponseHandler createResponseHandler(JiraResponseHandler.HandlerType handlerType, String url) {
        if (handlerType == JiraResponseHandler.HandlerType.CHANNEL_HANDLER) {
            return new JiraChannelResponseHandler(url);
        }
        if (handlerType == JiraResponseHandler.HandlerType.STRING_HANDLER) {
            return new JiraStringResponseHandler();
        }
        throw new IllegalStateException("unable to handle " + handlerType);
    }

    public static String createJsonStringForJiraIssueBean(JiraIssueBean jiraIssueBean) {
        JSONObject issue = new JSONObject();
        JSONObject fields = new JSONObject();
        JSONObject project = new JSONObject();
        JSONObject issuetype = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : jiraIssueBean.getFields().entrySet()) {
                String value = entry.getValue().trim();
                Object jsonVal = value.startsWith("[") && value.endsWith("]") ? new JSONArray(value) : (value.startsWith("{") && value.endsWith("}") ? new JSONObject(value) : value);
                fields.put(entry.getKey(), jsonVal);
            }
            if (jiraIssueBean.getProjectId() != null) {
                project.put("id", (Object)jiraIssueBean.getProjectId());
                fields.put("project", (Object)project);
            }
            if (jiraIssueBean.getIssueTypeId() != null) {
                issuetype.put("id", (Object)jiraIssueBean.getIssueTypeId());
                fields.put("issuetype", (Object)issuetype);
            }
            if (jiraIssueBean.getSummary() != null) {
                fields.put("summary", (Object)jiraIssueBean.getSummary());
            }
            if (jiraIssueBean.getDescription() != null) {
                fields.put("description", (Object)StringUtils.trimToEmpty((String)jiraIssueBean.getDescription()));
            }
            issue.put("fields", (Object)fields);
            return issue.toString();
        }
        catch (JSONException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static BasicJiraIssueBean createBasicJiraIssueBeanFromResponse(String jiraIssueResponseString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        BasicJiraIssueBean basicJiraIssueBean = (BasicJiraIssueBean)mapper.readValue(jiraIssueResponseString, BasicJiraIssueBean.class);
        return basicJiraIssueBean;
    }

    public static void updateJiraIssue(JiraIssueBean jiraIssueBean, BasicJiraIssueBean basicJiraIssueBean) {
        jiraIssueBean.setId(basicJiraIssueBean.getId());
        jiraIssueBean.setKey(basicJiraIssueBean.getKey());
        jiraIssueBean.setSelf(basicJiraIssueBean.getSelf());
    }

    public static void checkAndCorrectDisplayUrl(List<Element> children, ReadOnlyApplicationLink appLink) {
        if (appLink == null || appLink.getDisplayUrl() == null || appLink.getDisplayUrl().equals(appLink.getRpcUrl())) {
            return;
        }
        for (Element element : children) {
            JiraUtil.checkAndCorrectLink(element, appLink);
            JiraUtil.checkAndCorrectIconURL(element, appLink);
        }
    }

    public static void checkAndCorrectIconURL(Element element, ReadOnlyApplicationLink appLink) {
        if (appLink == null || element == null) {
            return;
        }
        JiraUtil.correctIconURL(element, appLink.getDisplayUrl().toString(), appLink.getRpcUrl().toString());
    }

    public static void correctIconURL(Element element, String displayUrl, String rpcUrl) {
        if (displayUrl == null || rpcUrl == null) {
            return;
        }
        for (Element child : element.getChildren()) {
            Attribute iconUrl = child.getAttribute("iconUrl");
            if (iconUrl == null || StringUtils.isEmpty((CharSequence)iconUrl.getValue()) || !iconUrl.getValue().startsWith(rpcUrl)) continue;
            iconUrl.setValue(iconUrl.getValue().replace(rpcUrl, displayUrl));
        }
    }

    private static void checkAndCorrectLink(Element element, ReadOnlyApplicationLink appLink) {
        if (appLink == null || element == null || element.getChild("link") == null) {
            return;
        }
        Element link = element.getChild("link");
        String issueLink = link.getValue();
        if (issueLink.startsWith(appLink.getRpcUrl().toString())) {
            link.setText(issueLink.replace(appLink.getRpcUrl().toString(), appLink.getDisplayUrl().toString()));
        }
    }

    public static String utf8Encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    public static String utf8Decode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    public static JiraIssuesMacro.JiraIssuesType getJiraIssuesType(Map<String, String> params, JiraIssuesMacro.Type requestType, String requestData) {
        if (requestType == JiraIssuesMacro.Type.KEY || JiraJqlHelper.isUrlKeyType(requestData)) {
            return JiraIssuesMacro.JiraIssuesType.SINGLE;
        }
        if ("true".equalsIgnoreCase(params.get("count"))) {
            return JiraIssuesMacro.JiraIssuesType.COUNT;
        }
        return JiraIssuesMacro.JiraIssuesType.TABLE;
    }

    public static String getParamValue(Map<String, String> params, String paramName, int paramPosition) {
        String param = params.get(paramName);
        if (param == null) {
            param = StringUtils.defaultString((String)params.get(String.valueOf(paramPosition)));
        }
        return param.trim();
    }

    public static int getMaximumIssues(String maximumNumber) {
        String maximumIssuesStr = StringUtils.defaultString((String)maximumNumber, (String)String.valueOf(20));
        int maximumIssues = Integer.parseInt(maximumIssuesStr);
        if (maximumIssues > 1000) {
            maximumIssues = 1000;
        }
        return maximumIssues;
    }

    public static String normalizeUrl(URI rpcUrl) {
        String baseUrl = rpcUrl.toString();
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    public static String escapeDoubleQuote(String str) {
        if (StringUtils.isNotBlank((CharSequence)str)) {
            return str.replace("\"", "\\\"");
        }
        return "";
    }

    public static String getSingleIssueKey(Map<String, String> parameters) {
        if (parameters == null) {
            return null;
        }
        String key = parameters.get("key");
        if (key == null) {
            String defaultParam = parameters.get("");
            if (defaultParam != null && JiraIssuePredicates.ISSUE_KEY_PATTERN.matcher(defaultParam).matches()) {
                return defaultParam;
            }
            return null;
        }
        return key;
    }

    public static Set<String> getColumnNamesFromParams(Map<String, String> params, boolean addingEpicLink) {
        Set<String> columnNames;
        Object columnIdsString = JiraUtil.getParamValue(params, "columnIds", 1);
        boolean idParamPresent = !((String)columnIdsString).isEmpty();
        Set<String> columnNamesString = JiraIssueSortableHelper.getColumnNames(JiraUtil.getParamValue(params, "columns", 1));
        if (idParamPresent) {
            if (addingEpicLink) {
                columnIdsString = (String)columnIdsString + "," + JiraUtil.getParamValue(params, EPIC_LINK_ID, 1);
            }
            columnNames = new LinkedHashSet<String>(Arrays.asList(((String)columnIdsString).split(",")));
        } else {
            columnNames = columnNamesString;
        }
        return columnNames;
    }
}

