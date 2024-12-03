/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.confluence.extra.jira.Channel
 *  com.atlassian.confluence.extra.jira.JiraIssuesManager
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  org.apache.commons.lang3.StringUtils
 *  org.jdom.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.jirareports;

import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.extra.jira.Channel;
import com.atlassian.confluence.extra.jira.JiraIssuesManager;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraIssuesHelper {
    private final JiraIssuesManager jiraIssuesManager;
    private final VelocityHelperService velocityHelperService;
    private static final Logger LOGGER = LoggerFactory.getLogger(JiraIssuesHelper.class);
    private static final Set<String> DEFAULT_COLUMNS = Stream.of("key", "summary", "status", "type").collect(Collectors.toSet());
    private static final String XML_SEARCH_REQUEST_URI = "/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=";
    private static final String JIRA_ISSUES_TEMPLATE = "com/atlassian/confluence/plugins/jirareports/velocity/jira-issues.html.vm";
    private static final List<String> DEFAULT_ISSUE_TYPES = Arrays.asList("Epic", "New Feature", "Improvement", "Bug");
    private static final String EMPTY_RESULT = "";
    private static final int DEFAULT_TOTAL_ISSUES = 0;
    private static final String DEFAULT_OPTION_VALUE = "-1";

    public JiraIssuesHelper(JiraIssuesManager jiraIssuesManager, VelocityHelperService velocityHelperService) {
        this.jiraIssuesManager = jiraIssuesManager;
        this.velocityHelperService = velocityHelperService;
    }

    public String renderJiraIssues(Channel channel, String rpcUrl, String displayUrl) {
        Element jiraIssuesElement;
        List items;
        if (channel != null && (items = (jiraIssuesElement = channel.getChannelElement()).getChildren("item")) != null && !items.isEmpty()) {
            List<Element> itemsWithAttributes = this.appendDisplayUrl(items, rpcUrl, displayUrl);
            StringBuilder jiraIssues = new StringBuilder();
            Map<String, List<Element>> mapIssueTypes = this.getMapIssueType(itemsWithAttributes);
            this.appendDefaultIssueTypes(mapIssueTypes, jiraIssues);
            this.appendOtherIssueTypes(mapIssueTypes, jiraIssues);
            return jiraIssues.toString();
        }
        return EMPTY_RESULT;
    }

    public int getTotalIssueNumber(Channel channel) {
        Element jiraIssuesElement;
        Element totalItemsElement;
        if (channel != null && (totalItemsElement = (jiraIssuesElement = channel.getChannelElement()).getChild("issue")) != null) {
            return Integer.parseInt(totalItemsElement.getAttributeValue("total"));
        }
        return 0;
    }

    private void appendDefaultIssueTypes(Map<String, List<Element>> mapIssueTypes, StringBuilder jiraIssues) {
        for (String issueType : DEFAULT_ISSUE_TYPES) {
            if (mapIssueTypes.get(issueType) == null) continue;
            jiraIssues.append(this.velocityRender(mapIssueTypes, issueType));
        }
    }

    private void appendOtherIssueTypes(Map<String, List<Element>> mapIssueTypes, StringBuilder jiraIssues) {
        Set<String> keys = mapIssueTypes.keySet();
        for (String key : keys) {
            if (DEFAULT_ISSUE_TYPES.contains(key)) continue;
            jiraIssues.append(this.velocityRender(mapIssueTypes, key));
        }
    }

    private List<Element> appendDisplayUrl(List<Element> elements, String rpcUrl, String displayUrl) {
        for (Element element : elements) {
            String elementUrl = element.getChild("link").getValue();
            String rebasedUrl = elementUrl.replace(rpcUrl, displayUrl);
            element.setAttribute("displayUrl", rebasedUrl);
        }
        return elements;
    }

    private String velocityRender(Map<String, List<Element>> mapIssueTypes, String issueType) {
        Map context = this.velocityHelperService.createDefaultVelocityContext();
        context.put("title", issueType);
        context.put("issues", mapIssueTypes.get(issueType));
        return this.velocityHelperService.getRenderedTemplate(JIRA_ISSUES_TEMPLATE, context);
    }

    private Map<String, List<Element>> getMapIssueType(List<Element> elements) {
        HashMap<String, List<Element>> mapIssueType = new HashMap<String, List<Element>>();
        for (Element element : elements) {
            String type = element.getChild("type").getValue();
            if (mapIssueType.get(type) == null) {
                ArrayList<Element> list = new ArrayList<Element>();
                list.add(element);
                mapIssueType.put(type, list);
                continue;
            }
            ((List)mapIssueType.get(type)).add(element);
        }
        return mapIssueType;
    }

    public String buildProjectVersionJQL(Map<String, Object> contextMap) {
        String project = (String)contextMap.get("jira-reports-project");
        if (project == null || project.equals(DEFAULT_OPTION_VALUE)) {
            return null;
        }
        StringBuilder url = new StringBuilder();
        url.append("project=\"");
        url.append(StringUtils.replace((String)project, (String)"'", (String)"''"));
        url.append("\"");
        String versions = (String)contextMap.get("multiVersion");
        if (!StringUtils.isBlank((CharSequence)versions)) {
            url.append(" AND fixVersion in (");
            url.append(StringUtils.replace((String)versions, (String)"'", (String)"''"));
            url.append(")");
        }
        return url.toString();
    }

    public Channel getChannel(ReadOnlyApplicationLink appLink, String jqlQuery, int maxResult) {
        String hostName = appLink.getRpcUrl().toString();
        String requestJiraUrl = hostName + XML_SEARCH_REQUEST_URI + jqlQuery + "&tempMax=" + maxResult;
        try {
            return this.jiraIssuesManager.retrieveXMLAsChannel(requestJiraUrl, DEFAULT_COLUMNS, appLink, false, false);
        }
        catch (CredentialsRequiredException e) {
            return this.getChannelByAnonymous(requestJiraUrl, appLink);
        }
        catch (Exception e) {
            LOGGER.error("Can not retrieve jira issues", (Throwable)e);
            return null;
        }
    }

    private Channel getChannelByAnonymous(String url, ReadOnlyApplicationLink appLink) {
        try {
            return this.jiraIssuesManager.retrieveXMLAsChannelByAnonymous(url, DEFAULT_COLUMNS, appLink, false, true);
        }
        catch (Exception e) {
            LOGGER.error("Can not retrieve jira issues", (Throwable)e);
            return null;
        }
    }
}

