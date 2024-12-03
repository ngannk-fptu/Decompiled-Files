/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.crowd.audit.AuditLogEventSource
 *  com.atlassian.crowd.audit.AuditLogEventType
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.atlassian.crowd.audit.query;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.audit.AuditLogChangeset;
import com.atlassian.crowd.audit.AuditLogEventSource;
import com.atlassian.crowd.audit.AuditLogEventType;
import com.atlassian.crowd.audit.query.AuditLogChangesetProjection;
import com.atlassian.crowd.audit.query.AuditLogQuery;
import com.atlassian.crowd.audit.query.AuditLogQueryAuthorRestriction;
import com.atlassian.crowd.audit.query.AuditLogQueryEntityRestriction;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.time.Instant;
import java.util.Collection;

@ExperimentalApi
public class AuditLogQueryBuilder<RESULT> {
    Class<RESULT> resultClass;
    Instant beforeOrOn;
    Instant onOrAfter;
    ImmutableList.Builder<AuditLogEventType> actions = new ImmutableList.Builder();
    ImmutableList.Builder<AuditLogEventSource> sources = new ImmutableList.Builder();
    ImmutableList.Builder<AuditLogQueryAuthorRestriction> authors = new ImmutableList.Builder();
    ImmutableList.Builder<AuditLogQueryEntityRestriction> users = new ImmutableList.Builder();
    ImmutableList.Builder<AuditLogQueryEntityRestriction> groups = new ImmutableList.Builder();
    ImmutableList.Builder<AuditLogQueryEntityRestriction> applications = new ImmutableList.Builder();
    ImmutableList.Builder<AuditLogQueryEntityRestriction> directories = new ImmutableList.Builder();
    AuditLogChangesetProjection projection;
    int startIndex;
    int maxResults = 1000;

    public static AuditLogQueryBuilder<AuditLogChangeset> changesetQuery() {
        return AuditLogQueryBuilder.queryFor(AuditLogChangeset.class);
    }

    public static <RESULT> AuditLogQueryBuilder<RESULT> queryFor(Class<RESULT> result) {
        return new AuditLogQueryBuilder<RESULT>(result);
    }

    private AuditLogQueryBuilder(Class<RESULT> resultClass) {
        this.resultClass = resultClass;
    }

    public AuditLogQueryBuilder<RESULT> setBeforeOrOn(Instant beforeOrOn) {
        this.beforeOrOn = beforeOrOn;
        return this;
    }

    public AuditLogQueryBuilder<RESULT> setOnOrAfter(Instant onOrAfter) {
        this.onOrAfter = onOrAfter;
        return this;
    }

    public AuditLogQueryBuilder<RESULT> addAllActions(Collection<AuditLogEventType> actions) {
        this.actions.addAll(actions);
        return this;
    }

    public AuditLogQueryBuilder<RESULT> addAction(AuditLogEventType action) {
        this.actions.add((Object)action);
        return this;
    }

    public AuditLogQueryBuilder<RESULT> addAllSources(Collection<AuditLogEventSource> sources) {
        this.sources.addAll(sources);
        return this;
    }

    public AuditLogQueryBuilder<RESULT> addSource(AuditLogEventSource source) {
        this.sources.add((Object)source);
        return this;
    }

    public AuditLogQueryBuilder<RESULT> addAllAuthors(Collection<AuditLogQueryAuthorRestriction> authors) {
        this.authors.addAll(authors);
        return this;
    }

    public AuditLogQueryBuilder<RESULT> addAuthor(AuditLogQueryAuthorRestriction authors) {
        this.authors.add((Object)authors);
        return this;
    }

    public AuditLogQueryBuilder<RESULT> addAllUsers(Collection<AuditLogQueryEntityRestriction> user) {
        this.users.addAll(user);
        return this;
    }

    public AuditLogQueryBuilder<RESULT> addUser(AuditLogQueryEntityRestriction user) {
        this.users.add((Object)user);
        return this;
    }

    public AuditLogQueryBuilder<RESULT> setUsers(Collection<AuditLogQueryEntityRestriction> users) {
        this.users = ImmutableList.builder().addAll(users);
        return this;
    }

    public AuditLogQueryBuilder<RESULT> addAllGroups(Collection<AuditLogQueryEntityRestriction> groups) {
        this.groups.addAll(groups);
        return this;
    }

    public AuditLogQueryBuilder<RESULT> addGroup(AuditLogQueryEntityRestriction group) {
        this.groups.add((Object)group);
        return this;
    }

    public AuditLogQueryBuilder<RESULT> setGroups(Collection<AuditLogQueryEntityRestriction> groups) {
        this.groups = ImmutableList.builder().addAll(groups);
        return this;
    }

    public AuditLogQueryBuilder<RESULT> addAllApplications(Collection<AuditLogQueryEntityRestriction> applications) {
        this.applications.addAll(applications);
        return this;
    }

    public AuditLogQueryBuilder<RESULT> addApplication(AuditLogQueryEntityRestriction application) {
        this.applications.add((Object)application);
        return this;
    }

    public AuditLogQueryBuilder<RESULT> setApplications(Collection<AuditLogQueryEntityRestriction> applications) {
        this.applications = ImmutableList.builder().addAll(applications);
        return this;
    }

    public AuditLogQueryBuilder<RESULT> addAllDirectories(Collection<AuditLogQueryEntityRestriction> directories) {
        this.directories.addAll(directories);
        return this;
    }

    public AuditLogQueryBuilder<RESULT> addDirectory(AuditLogQueryEntityRestriction directory) {
        this.directories.add((Object)directory);
        return this;
    }

    public AuditLogQueryBuilder<RESULT> setDirectories(Collection<AuditLogQueryEntityRestriction> directories) {
        this.directories = ImmutableList.builder().addAll(directories);
        return this;
    }

    public AuditLogQueryBuilder<RESULT> setProjection(AuditLogChangesetProjection projection) {
        Preconditions.checkState((this.projection == null ? 1 : 0) != 0, (Object)("Query projection already set to " + (Object)((Object)this.projection)));
        this.projection = projection;
        return this;
    }

    public AuditLogQueryBuilder<RESULT> setStartIndex(int startIndex) {
        this.startIndex = startIndex;
        return this;
    }

    public AuditLogQueryBuilder<RESULT> setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    public AuditLogQuery<RESULT> build() {
        return new AuditLogQuery(this);
    }
}

