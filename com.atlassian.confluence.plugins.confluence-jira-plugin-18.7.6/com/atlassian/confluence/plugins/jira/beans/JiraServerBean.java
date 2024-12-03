/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.jira.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JiraServerBean {
    @XmlElement
    private String id;
    @XmlElement
    private String name;
    @XmlElement
    private boolean selected;
    @XmlElement
    private String authUrl;
    @XmlElement
    private String url;
    @XmlElement
    private Long buildNumber;
    @XmlElement
    private String rpcUrl;
    @XmlElement
    private String displayUrl;

    public JiraServerBean(String id, String url, String name, boolean selected, String authUrl, Long buildNumber, String rpcUrl, String displayUrl) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.selected = selected;
        this.authUrl = authUrl;
        this.buildNumber = buildNumber;
        this.rpcUrl = rpcUrl;
        this.displayUrl = displayUrl;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getAuthUrl() {
        return this.authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getBuildNumber() {
        return this.buildNumber;
    }

    public void setBuildNumber(Long buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getRpcUrl() {
        return this.rpcUrl;
    }

    public void setRpcUrl(String rpcUrl) {
        this.rpcUrl = rpcUrl;
    }

    public String getDisplayUrl() {
        return this.displayUrl;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }
}

