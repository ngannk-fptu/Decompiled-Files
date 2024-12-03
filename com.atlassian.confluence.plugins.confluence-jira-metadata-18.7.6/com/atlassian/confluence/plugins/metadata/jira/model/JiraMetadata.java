/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.metadata.jira.model;

import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataError;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataMergedGroup;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraUnauthorisedAppLink;
import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JiraMetadata {
    @XmlElement
    private int count;
    @XmlElement
    private Collection<JiraMetadataMergedGroup> groups;
    @XmlElement
    private Collection<JiraUnauthorisedAppLink> unauthorisedAppLinks;
    @XmlElement
    private Collection<JiraMetadataError> errors;

    public JiraMetadata(int count, Collection<JiraMetadataMergedGroup> groups, Collection<JiraUnauthorisedAppLink> unauthorisedAppLinks, Collection<JiraMetadataError> errors) {
        this.count = count;
        this.groups = groups;
        this.unauthorisedAppLinks = unauthorisedAppLinks;
        this.errors = errors;
    }

    public int getCount() {
        return this.count;
    }

    public Collection<JiraMetadataMergedGroup> getGroups() {
        return this.groups;
    }

    public Collection<JiraUnauthorisedAppLink> getUnauthorisedAppLinks() {
        return this.unauthorisedAppLinks;
    }

    public Collection<JiraMetadataError> getErrors() {
        return this.errors;
    }
}

