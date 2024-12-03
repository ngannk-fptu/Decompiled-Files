/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.metadata.jira.model;

import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataGroup;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataGroupLink;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataItem;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JiraMetadataMergedGroup
extends JiraMetadataGroup {
    @XmlElement
    private List<JiraMetadataGroupLink> links;

    public JiraMetadataMergedGroup(JiraMetadataGroup.Type type, List<JiraMetadataItem> items) {
        super(type, items);
    }

    public JiraMetadataMergedGroup(JiraMetadataGroup.Type type, List<JiraMetadataItem> items, List<JiraMetadataGroupLink> links) {
        super(type, items);
        this.links = links;
    }

    public List<JiraMetadataGroupLink> getLinks() {
        return this.links;
    }
}

