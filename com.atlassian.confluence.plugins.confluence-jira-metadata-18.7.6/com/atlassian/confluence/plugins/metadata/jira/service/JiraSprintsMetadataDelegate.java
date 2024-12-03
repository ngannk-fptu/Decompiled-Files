/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.metadata.jira.service;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.plugins.metadata.jira.helper.CapabilitiesHelper;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataGroup;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataItem;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataSingleGroup;
import com.atlassian.confluence.plugins.metadata.jira.service.JiraMetadataDelegate;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.sal.api.net.ResponseException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JiraSprintsMetadataDelegate
implements JiraMetadataDelegate {
    private final I18NBeanFactory i18NBeanFactory;
    private final CapabilitiesHelper capabilitiesHelper;

    @Autowired
    public JiraSprintsMetadataDelegate(I18NBeanFactory i18NBeanFactory, CapabilitiesHelper capabilitiesHelper) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.capabilitiesHelper = capabilitiesHelper;
    }

    @Override
    public String getUrl(List<String> globalIds, Map<String, String> parameters) {
        StringBuilder result = new StringBuilder("/rest/greenhopper/1.0/api/sprints/remotelink?");
        for (String id : globalIds) {
            result.append("globalId=").append(HtmlUtil.urlEncode((String)id)).append('&');
        }
        return result.toString();
    }

    @Override
    public List<JiraMetadataSingleGroup> getGroups(ReadOnlyApplicationLink jiraAppLink, ApplicationLinkRequest request, Map<String, String> parameters, List<String> globalIds) throws ResponseException {
        ArrayList<JiraMetadataItem> sprints = new ArrayList<JiraMetadataItem>();
        JsonObject result = new JsonParser().parse(request.execute()).getAsJsonObject();
        for (JsonElement element : result.getAsJsonArray("sprints")) {
            JsonObject sprint = element.getAsJsonObject();
            String title = sprint.get("title").getAsString();
            String status = sprint.get("status").getAsString();
            JsonElement url = sprint.get("url");
            JsonElement daysRemaining = sprint.get("daysRemaining");
            if (url == null) continue;
            sprints.add(new JiraMetadataItem(title, this.getSprintDescription(status, daysRemaining), jiraAppLink.getDisplayUrl() + "/" + url.getAsString()));
        }
        return Collections.singletonList(new JiraMetadataSingleGroup(JiraMetadataGroup.Type.SPRINTS, sprints, jiraAppLink));
    }

    @Override
    public boolean isSupported(ReadOnlyApplicationLink jiraAppLink) {
        return this.capabilitiesHelper.isSupportedByAppLink("gh-remote-sprint-link", jiraAppLink);
    }

    private String getSprintDescription(String status, JsonElement daysRemaining) {
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        String description = "";
        if ("complete".equalsIgnoreCase(status)) {
            description = i18NBean.getText("content.metadata.jira.sprints.item.description.complete");
        } else if ("future".equalsIgnoreCase(status)) {
            description = i18NBean.getText("content.metadata.jira.sprints.item.description.not.started");
        } else if (daysRemaining != null) {
            description = i18NBean.getText("content.metadata.jira.sprints.item.description", (Object[])new String[]{daysRemaining.getAsString()});
        }
        return description;
    }
}

