/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.projectcreate.producer.link.entities;

public class LinkUrlComponents {
    private String baseUrl;
    private String entityType;
    private String projectKey;

    public LinkUrlComponents(String baseUrl, String entityType, String projectKey) {
        this.baseUrl = baseUrl;
        this.entityType = entityType;
        this.projectKey = projectKey;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public String getEntityType() {
        return this.entityType;
    }

    public String getProjectKey() {
        return this.projectKey;
    }
}

