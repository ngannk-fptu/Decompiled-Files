/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.metadata.jira.model;

import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataIssueStatusCategory;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JiraMetadataIssueStatus {
    @XmlElement
    private String name;
    @XmlElement
    private String description;
    @XmlElement
    private JiraMetadataIssueStatusCategory statusCategory;

    public JiraMetadataIssueStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public JiraMetadataIssueStatus(String name, String description, JiraMetadataIssueStatusCategory statusCategory) {
        this.name = name;
        this.description = description;
        this.statusCategory = statusCategory;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }
}

