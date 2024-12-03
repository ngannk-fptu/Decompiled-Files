/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.app;

public class MigrationDetailsV1 {
    private String migrationId;
    private String migrationScopeId;
    private String name;
    private Long createdAt;
    private String jiraClientKey;
    private String confluenceClientKey;
    private String cloudUrl;

    public String getMigrationId() {
        return this.migrationId;
    }

    public String getMigrationScopeId() {
        return this.migrationScopeId;
    }

    public String getName() {
        return this.name;
    }

    public Long getCreatedAt() {
        return this.createdAt;
    }

    public String getJiraClientKey() {
        return this.jiraClientKey;
    }

    public String getConfluenceClientKey() {
        return this.confluenceClientKey;
    }

    public String getCloudUrl() {
        return this.cloudUrl;
    }
}

