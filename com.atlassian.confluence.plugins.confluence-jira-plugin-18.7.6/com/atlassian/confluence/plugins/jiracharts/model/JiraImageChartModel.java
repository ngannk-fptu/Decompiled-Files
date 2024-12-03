/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.jiracharts.model;

import com.atlassian.confluence.extra.jira.model.Locatable;

public class JiraImageChartModel
implements Locatable {
    private String location;
    private String filterUrl;
    private String base64Image;
    private String statType;
    private String issuesCreated;
    private String issuesResolved;

    @Override
    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFilterUrl() {
        return this.filterUrl;
    }

    public void setFilterUrl(String filterUrl) {
        this.filterUrl = filterUrl;
    }

    public String getStatType() {
        return this.statType;
    }

    public String getBase64Image() {
        return this.base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public void setStatType(String statType) {
        this.statType = statType;
    }

    public String getIssuesCreated() {
        return this.issuesCreated;
    }

    public void setIssuesCreated(String issuesCreated) {
        this.issuesCreated = issuesCreated;
    }

    public String getIssuesResolved() {
        return this.issuesResolved;
    }

    public void setIssuesResolved(String issuesResolved) {
        this.issuesResolved = issuesResolved;
    }
}

