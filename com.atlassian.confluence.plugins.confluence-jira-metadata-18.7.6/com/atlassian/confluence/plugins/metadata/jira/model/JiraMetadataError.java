/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.confluence.plugins.metadata.jira.model;

import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;

public class JiraMetadataError {
    @XmlElement
    private String errorMessage;
    @XmlElement
    private Set<String> errorApplinks;

    public JiraMetadataError(String errorMessage) {
        this(errorMessage, new HashSet<String>());
    }

    public JiraMetadataError(String errorMessage, Set<String> errorApplinks) {
        this.errorMessage = errorMessage;
        this.errorApplinks = errorApplinks;
    }

    public void addErrorApplink(String applinkName) {
        this.errorApplinks.add(applinkName);
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public Set<String> getErrorApplinks() {
        return this.errorApplinks;
    }
}

