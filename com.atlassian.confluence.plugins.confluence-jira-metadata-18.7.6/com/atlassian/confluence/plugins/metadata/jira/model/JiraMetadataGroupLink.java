/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.metadata.jira.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JiraMetadataGroupLink {
    @XmlElement
    private int numItems;
    @XmlElement
    private String url;
    @XmlElement
    private String appName;

    public JiraMetadataGroupLink(int numItems, String url, String appName) {
        this.numItems = numItems;
        this.url = url;
        this.appName = appName;
    }

    public int getNumItems() {
        return this.numItems;
    }

    public String getUrl() {
        return this.url;
    }

    public String getAppName() {
        return this.appName;
    }
}

