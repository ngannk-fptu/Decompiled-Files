/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Collections2
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.metadata.jira.service;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataGroup;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataIssueItem;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataIssueStatus;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataIssueStatusCategory;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataItem;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataSingleGroup;
import com.atlassian.confluence.plugins.metadata.jira.service.JiraMetadataDelegate;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class JiraIssuesMetadataDelegate
implements JiraMetadataDelegate {
    @Override
    public String getUrl(List<String> globalIds, Map<String, String> parameters) {
        String jqlConditions = JiraIssuesMetadataDelegate.getJqlConditions(globalIds);
        String url = "/rest/api/2/search?jql=issue+in+(" + HtmlUtil.urlEncode((String)jqlConditions) + ")";
        if (parameters.containsKey("epicNameField")) {
            url = url + "&fields=summary,issuetype,status," + parameters.get("epicNameField");
        }
        return url;
    }

    private static String getJqlConditions(List<String> globalIds) {
        return Joiner.on((char)',').join((Iterable)Collections2.transform(globalIds, input -> "issuesWithRemoteLinksByGlobalId('" + input + "')"));
    }

    @Override
    public List<JiraMetadataSingleGroup> getGroups(ReadOnlyApplicationLink jiraAppLink, ApplicationLinkRequest request, Map<String, String> parameters, List<String> globalIds) throws ResponseException {
        ArrayList<JiraMetadataItem> issues = new ArrayList<JiraMetadataItem>();
        ArrayList<JiraMetadataItem> epics = new ArrayList<JiraMetadataItem>();
        JsonObject result = new JsonParser().parse(request.execute()).getAsJsonObject();
        for (JsonElement element : result.getAsJsonArray("issues")) {
            JsonObject issue = element.getAsJsonObject();
            JsonObject fields = issue.getAsJsonObject("fields");
            String key = issue.getAsJsonPrimitive("key").getAsString();
            String summary = fields.getAsJsonPrimitive("summary").getAsString();
            JiraMetadataIssueStatus status = this.constructIssueStatus(fields.getAsJsonObject("status"));
            String issueTypeId = fields.getAsJsonObject("issuetype").getAsJsonPrimitive("id").getAsString();
            if (issueTypeId.equals(parameters.get("epicTypeId"))) {
                JsonElement epicName = fields.get(parameters.get("epicNameField"));
                String name = epicName != null && !epicName.isJsonNull() ? epicName.getAsJsonPrimitive().getAsString() : key;
                epics.add(new JiraMetadataItem(name, summary, jiraAppLink.getDisplayUrl() + "/browse/" + key));
                continue;
            }
            issues.add(new JiraMetadataIssueItem(key, summary, jiraAppLink.getDisplayUrl() + "/browse/" + key, status));
        }
        return Arrays.asList(new JiraMetadataSingleGroup(JiraMetadataGroup.Type.ISSUES, issues, jiraAppLink, this.getDisplayURL(jiraAppLink, globalIds, parameters, JiraMetadataGroup.Type.ISSUES)), new JiraMetadataSingleGroup(JiraMetadataGroup.Type.EPICS, epics, jiraAppLink, this.getDisplayURL(jiraAppLink, globalIds, parameters, JiraMetadataGroup.Type.EPICS)));
    }

    private JiraMetadataIssueStatus constructIssueStatus(JsonObject root) {
        JiraMetadataIssueStatusCategory category = null;
        if (root.has("statusCategory")) {
            JsonObject categoryRoot = root.getAsJsonObject("statusCategory");
            String categoryKey = categoryRoot.get("key").getAsString();
            String colorName = categoryRoot.get("colorName").getAsString();
            category = new JiraMetadataIssueStatusCategory(categoryKey, colorName);
        }
        String name = root.get("name").getAsString();
        String description = root.get("description").getAsString();
        return new JiraMetadataIssueStatus(name, description, category);
    }

    private String getDisplayURL(ReadOnlyApplicationLink jiraAppLink, List<String> globalId, Map<String, String> parameters, JiraMetadataGroup.Type type) {
        return jiraAppLink.getDisplayUrl() + "/issues/?jql=" + HtmlUtil.urlEncode((String)("issue in (" + JiraIssuesMetadataDelegate.getJqlConditions(globalId) + ") and issuetype" + (type.equals((Object)JiraMetadataGroup.Type.EPICS) ? "=" : "!=") + parameters.get("epicTypeId")));
    }

    @Override
    public boolean isSupported(ReadOnlyApplicationLink jiraAppLink) {
        return true;
    }
}

