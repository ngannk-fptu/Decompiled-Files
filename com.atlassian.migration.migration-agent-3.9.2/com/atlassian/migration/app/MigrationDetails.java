/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.Container;
import java.util.Collection;

@Deprecated
public class MigrationDetails {
    private String migrationId;
    private String migrationScopeId;
    private String name;
    private Long createdAt;
    private String creator;
    private String jiraClientKey;
    private String confluenceClientKey;
    private String cloudUrl;
    private Collection<Container> containers;

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

    public String getCreator() {
        return this.creator;
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

    public Collection<Container> getContainers() {
        return this.containers;
    }
}

