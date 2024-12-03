/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 */
package com.atlassian.confluence.plugins.metadata.jira.model;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataGroup;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataItem;
import java.util.List;

public class JiraMetadataSingleGroup
extends JiraMetadataGroup {
    private String url;
    private ReadOnlyApplicationLink appLink;

    public JiraMetadataSingleGroup(JiraMetadataGroup.Type type, List<JiraMetadataItem> items, ReadOnlyApplicationLink appLink) {
        this(type, items, appLink, null);
    }

    public JiraMetadataSingleGroup(JiraMetadataGroup.Type type, List<JiraMetadataItem> items, ReadOnlyApplicationLink appLink, String url) {
        super(type, items);
        this.appLink = appLink;
        this.url = url;
    }

    public ReadOnlyApplicationLink getAppLink() {
        return this.appLink;
    }

    public String getUrl() {
        return this.url;
    }
}

