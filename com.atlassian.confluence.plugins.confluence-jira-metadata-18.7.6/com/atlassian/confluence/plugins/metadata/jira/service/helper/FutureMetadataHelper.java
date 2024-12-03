/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 */
package com.atlassian.confluence.plugins.metadata.jira.service.helper;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.confluence.plugins.metadata.jira.helper.JiraMetadataErrorHelper;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadata;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataGroup;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataGroupLink;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataItem;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataMergedGroup;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataSingleGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class FutureMetadataHelper {
    private final Map<JiraMetadataGroup.Type, Map<ApplicationId, JiraMetadataSingleGroup>> typeGroups = new HashMap<JiraMetadataGroup.Type, Map<ApplicationId, JiraMetadataSingleGroup>>();
    private final List<JiraMetadataMergedGroup> mergedGroups = new ArrayList<JiraMetadataMergedGroup>();
    private int count = 0;
    private final List<Future<List<JiraMetadataSingleGroup>>> futures;
    private final JiraMetadataErrorHelper jiraMetadataErrorHelper;

    public FutureMetadataHelper(List<Future<List<JiraMetadataSingleGroup>>> futures, JiraMetadataErrorHelper jiraMetadataErrorHelper) {
        this.futures = futures;
        this.jiraMetadataErrorHelper = jiraMetadataErrorHelper;
        for (JiraMetadataGroup.Type type : JiraMetadataGroup.Type.values()) {
            this.typeGroups.put(type, new HashMap());
        }
    }

    public JiraMetadata process() {
        this.processFutures();
        for (JiraMetadataGroup.Type type : JiraMetadataGroup.Type.values()) {
            this.mergedGroups.add(this.mergeGroupsAcrossApplications(type, this.typeGroups.get((Object)type)));
        }
        return new JiraMetadata(this.count, this.mergedGroups, this.jiraMetadataErrorHelper.getUnauthorisedAppLinks().values(), this.jiraMetadataErrorHelper.getErrors().values());
    }

    private void processFutures() {
        for (Future<List<JiraMetadataSingleGroup>> future : this.futures) {
            try {
                for (JiraMetadataSingleGroup group : future.get()) {
                    this.typeGroups.get((Object)group.getType()).put(group.getAppLink().getId(), group);
                }
            }
            catch (Exception e) {
                this.jiraMetadataErrorHelper.handleException(e);
            }
        }
    }

    private JiraMetadataMergedGroup mergeGroupsAcrossApplications(JiraMetadataGroup.Type type, Map<ApplicationId, JiraMetadataSingleGroup> metadataMap) {
        ArrayList<JiraMetadataItem> items = new ArrayList<JiraMetadataItem>();
        ArrayList<JiraMetadataGroupLink> links = new ArrayList<JiraMetadataGroupLink>();
        for (JiraMetadataSingleGroup group : metadataMap.values()) {
            List<JiraMetadataItem> groupItems = group.getItems();
            if (groupItems.isEmpty()) continue;
            if (items.size() < type.getMaxItemsToDisplay()) {
                int needed = type.getMaxItemsToDisplay() - items.size();
                int remaining = groupItems.size() - needed;
                items.addAll(groupItems.subList(0, Math.min(needed, groupItems.size())));
                if (remaining > 0) {
                    links.add(new JiraMetadataGroupLink(remaining, group.getUrl(), group.getAppLink().getName()));
                }
            } else {
                links.add(new JiraMetadataGroupLink(groupItems.size(), group.getUrl(), group.getAppLink().getName()));
            }
            this.count += groupItems.size();
        }
        return new JiraMetadataMergedGroup(type, items, links);
    }
}

