/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.fugue.Option
 *  com.atlassian.user.User
 *  org.hibernate.query.Query
 */
package com.atlassian.confluence.internal.spaces;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.security.query.SpacePermissionQueryBuilder;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.fugue.Option;
import com.atlassian.user.User;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.hibernate.query.Query;

@Internal
public class SpacesQueryWithPermissionQueryBuilder {
    private final SpacesQuery spacesQuery;
    private final Option<SpacePermissionQueryBuilder> spacePermissionQueryBuilderOpt;

    private SpacesQueryWithPermissionQueryBuilder(SpacesQuery spacesQuery, Option<SpacePermissionQueryBuilder> spacePermissionQueryBuilderOpt) {
        this.spacesQuery = spacesQuery;
        this.spacePermissionQueryBuilderOpt = spacePermissionQueryBuilderOpt;
    }

    public static SpacesQueryWithPermissionQueryBuilder spacesQueryWithoutPermissionCheck(SpacesQuery spacesQuery) {
        return new SpacesQueryWithPermissionQueryBuilder(spacesQuery, (Option<SpacePermissionQueryBuilder>)Option.none());
    }

    public static SpacesQueryWithPermissionQueryBuilder spacesQueryWithPermissionCheck(SpacesQuery spacesQuery, SpacePermissionQueryBuilder permissionClauseBuilder) {
        return new SpacesQueryWithPermissionQueryBuilder(spacesQuery, (Option<SpacePermissionQueryBuilder>)Option.some((Object)permissionClauseBuilder));
    }

    public boolean includesPermissionCheck() {
        return this.spacePermissionQueryBuilderOpt.isDefined();
    }

    public String getHqlPermissionFilterString(String spacePermissionTableAlias) {
        SpacePermissionQueryBuilder queryBuilder = (SpacePermissionQueryBuilder)this.spacePermissionQueryBuilderOpt.getOrThrow(() -> new IllegalStateException("'getHqlPermissionFilterString' - attempted call without permission check"));
        return queryBuilder.getHqlPermissionFilterString(spacePermissionTableAlias);
    }

    public void substituteHqlQueryParameters(Query query) {
        SpacePermissionQueryBuilder queryBuilder = (SpacePermissionQueryBuilder)this.spacePermissionQueryBuilderOpt.getOrThrow(() -> new IllegalStateException("'substituteHqlQueryParameters' - attempted call without permission check"));
        queryBuilder.substituteHqlQueryParameters(query);
    }

    public SpaceType getSpaceType() {
        return this.spacesQuery.getSpaceType();
    }

    public String getPermissionType() {
        return this.spacesQuery.getPermissionType();
    }

    public User getUser() {
        return this.spacesQuery.getUser();
    }

    public List<String> getUserGroups() {
        return this.spacesQuery.getUserGroups();
    }

    public List<String> getSpaceKeys() {
        return this.spacesQuery.getSpaceKeys();
    }

    public List<String> getSortBy() {
        return this.spacesQuery.getSortBy();
    }

    public List<Label> getLabels() {
        return this.spacesQuery.getLabels();
    }

    public Optional<Boolean> getFavourite() {
        return this.spacesQuery.getFavourite();
    }

    public Date getCreationDate() {
        return this.spacesQuery.getCreationDate();
    }

    public Set<SpaceStatus> getSpaceStatuses() {
        return this.spacesQuery.getSpaceStatuses();
    }

    public Set<Long> getSpaceIds() {
        return this.spacesQuery.getSpaceIds();
    }

    public SpacesQuery getSpacesQuery() {
        return this.spacesQuery;
    }

    public Optional<Boolean> getHasRetentionPolicy() {
        return this.spacesQuery.getHasRetentionPolicy();
    }
}

