/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.confluence.plugins.metadata.jira.model;

import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataIssueStatus;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataItem;
import javax.xml.bind.annotation.XmlElement;

public class JiraMetadataIssueItem
extends JiraMetadataItem {
    @XmlElement
    private JiraMetadataIssueStatus status;

    public JiraMetadataIssueItem(String name, String description, String url, JiraMetadataIssueStatus status) {
        super(name, description, url);
        this.status = status;
    }

    public JiraMetadataIssueStatus getStatus() {
        return this.status;
    }
}

