/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.crowd.audit.AuditLogEventSource
 *  com.atlassian.crowd.audit.AuditLogEventType
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.audit.query;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.audit.AuditLogEventSource;
import com.atlassian.crowd.audit.AuditLogEventType;
import com.atlassian.crowd.audit.query.AuditLogChangesetProjection;
import com.atlassian.crowd.audit.query.AuditLogQueryAuthorRestriction;
import com.atlassian.crowd.audit.query.AuditLogQueryBuilder;
import com.atlassian.crowd.audit.query.AuditLogQueryEntityRestriction;
import com.google.common.base.MoreObjects;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

@ExperimentalApi
public class AuditLogQuery<RESULT> {
    private final Instant beforeOrOn;
    private final Instant onOrAfter;
    private final List<AuditLogEventType> actions;
    private final List<AuditLogEventSource> sources;
    private final List<AuditLogQueryAuthorRestriction> authors;
    private final List<AuditLogQueryEntityRestriction> users;
    private final List<AuditLogQueryEntityRestriction> groups;
    private final List<AuditLogQueryEntityRestriction> applications;
    private final List<AuditLogQueryEntityRestriction> directories;
    private final AuditLogChangesetProjection projection;
    private final int startIndex;
    private final int maxResults;
    private final Class<RESULT> resultClass;

    AuditLogQuery(AuditLogQueryBuilder<RESULT> builder) {
        this.beforeOrOn = builder.beforeOrOn;
        this.onOrAfter = builder.onOrAfter;
        this.actions = builder.actions.build();
        this.sources = builder.sources.build();
        this.authors = builder.authors.build();
        this.users = builder.users.build();
        this.groups = builder.groups.build();
        this.applications = builder.applications.build();
        this.directories = builder.directories.build();
        this.startIndex = builder.startIndex;
        this.maxResults = builder.maxResults;
        this.projection = builder.projection;
        this.resultClass = builder.resultClass;
    }

    public Instant getBeforeOrOn() {
        return this.beforeOrOn;
    }

    public Instant getOnOrAfter() {
        return this.onOrAfter;
    }

    public List<AuditLogEventType> getActions() {
        return this.actions;
    }

    public List<AuditLogEventSource> getSources() {
        return this.sources;
    }

    public List<AuditLogQueryAuthorRestriction> getAuthors() {
        return this.authors;
    }

    public List<AuditLogQueryEntityRestriction> getUsers() {
        return this.users;
    }

    public List<AuditLogQueryEntityRestriction> getGroups() {
        return this.groups;
    }

    public List<AuditLogQueryEntityRestriction> getApplications() {
        return this.applications;
    }

    public List<AuditLogQueryEntityRestriction> getDirectories() {
        return this.directories;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    @Nullable
    public AuditLogChangesetProjection getProjection() {
        return this.projection;
    }

    public Class<RESULT> getReturnType() {
        return this.resultClass;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditLogQuery that = (AuditLogQuery)o;
        return this.startIndex == that.startIndex && this.maxResults == that.maxResults && Objects.equals(this.beforeOrOn, that.beforeOrOn) && Objects.equals(this.onOrAfter, that.onOrAfter) && Objects.equals(this.actions, that.actions) && Objects.equals(this.sources, that.sources) && Objects.equals(this.authors, that.authors) && Objects.equals(this.users, that.users) && Objects.equals(this.groups, that.groups) && Objects.equals(this.applications, that.applications) && Objects.equals(this.directories, that.directories) && this.projection == that.projection && Objects.equals(this.resultClass, that.resultClass);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.beforeOrOn, this.onOrAfter, this.actions, this.sources, this.authors, this.users, this.groups, this.applications, this.directories, this.projection, this.startIndex, this.maxResults, this.resultClass});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("beforeOrOn", (Object)this.beforeOrOn).add("onOrAfter", (Object)this.onOrAfter).add("actions", this.actions).add("sources", this.sources).add("authors", this.authors).add("users", this.users).add("groups", this.groups).add("applications", this.applications).add("directories", this.directories).add("projection", (Object)this.projection).add("startIndex", this.startIndex).add("maxResults", this.maxResults).add("resultClass", this.resultClass).toString();
    }
}

