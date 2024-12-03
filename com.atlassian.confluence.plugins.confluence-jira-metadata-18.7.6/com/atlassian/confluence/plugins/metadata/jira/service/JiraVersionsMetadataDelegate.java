/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.sal.api.net.ResponseException
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.metadata.jira.service;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataGroup;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataItem;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataSingleGroup;
import com.atlassian.confluence.plugins.metadata.jira.service.JiraMetadataDelegate;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.sal.api.net.ResponseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class JiraVersionsMetadataDelegate
implements JiraMetadataDelegate {
    @Override
    public String getUrl(List<String> globalId, Map<String, String> parameters) {
        return "/rest/api/2/search?jql=" + HtmlUtil.urlEncode((String)"summary IS EMPTY");
    }

    @Override
    public List<JiraMetadataSingleGroup> getGroups(ReadOnlyApplicationLink jiraAppLink, ApplicationLinkRequest request, Map<String, String> parameters, List<String> globalId) throws ResponseException {
        ArrayList<JiraMetadataItem> items = new ArrayList<JiraMetadataItem>();
        return Collections.singletonList(new JiraMetadataSingleGroup(JiraMetadataGroup.Type.VERSIONS, items, jiraAppLink));
    }

    @Override
    public boolean isSupported(ReadOnlyApplicationLink jiraAppLink) {
        return false;
    }
}

