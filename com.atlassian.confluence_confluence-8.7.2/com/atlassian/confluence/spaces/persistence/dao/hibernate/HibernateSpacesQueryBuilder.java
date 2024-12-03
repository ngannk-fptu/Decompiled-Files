/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.query.Query
 */
package com.atlassian.confluence.spaces.persistence.dao.hibernate;

import com.atlassian.confluence.internal.spaces.SpacesQueryWithPermissionQueryBuilder;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.bean.EntityObject;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.Query;

class HibernateSpacesQueryBuilder {
    private final SpaceType spaceType;
    private final List<String> spaceKeys;
    private final List<String> sortBy;
    private final Date creationDate;
    private final List<String> spaceStatuses;
    private final List<Label> labels;
    private final Optional<Boolean> favourite;
    private final Set<Long> spaceIds;
    private final Optional<Boolean> hasRetentionPolicy;
    private final SpacesQueryWithPermissionQueryBuilder queryWithPermissionClauseBuilder;

    public HibernateSpacesQueryBuilder(SpacesQueryWithPermissionQueryBuilder queryWithPermissionClauseBuilder) {
        this.spaceType = queryWithPermissionClauseBuilder.getSpaceType();
        this.spaceKeys = Lists.newArrayList(queryWithPermissionClauseBuilder.getSpaceKeys());
        this.spaceIds = new HashSet<Long>(queryWithPermissionClauseBuilder.getSpaceIds());
        this.sortBy = Lists.newArrayList(queryWithPermissionClauseBuilder.getSortBy());
        this.creationDate = queryWithPermissionClauseBuilder.getCreationDate();
        this.spaceStatuses = this.toListOfStrings(queryWithPermissionClauseBuilder.getSpaceStatuses());
        this.queryWithPermissionClauseBuilder = queryWithPermissionClauseBuilder;
        this.labels = queryWithPermissionClauseBuilder.getLabels();
        this.favourite = queryWithPermissionClauseBuilder.getFavourite();
        this.hasRetentionPolicy = queryWithPermissionClauseBuilder.getHasRetentionPolicy();
    }

    public String getListQuery() {
        return this.createQuery(false);
    }

    public void fillInQueryParameters(Query query) {
        if (this.spaceType != null) {
            query.setParameter("type", (Object)this.spaceType.toString());
        }
        if (this.spaceKeys != null && !this.spaceKeys.isEmpty()) {
            query.setParameterList("spaceKeys", this.spaceKeys);
        }
        if (this.spaceIds != null && !this.spaceIds.isEmpty()) {
            query.setParameterList("spaceIds", this.spaceIds);
        }
        if (this.creationDate != null) {
            query.setParameter("creationDate", (Object)this.creationDate);
        }
        if (this.spaceStatuses != null && !this.spaceStatuses.isEmpty()) {
            query.setParameterList("spaceStatuses", this.spaceStatuses);
        }
        if (this.labels != null && !this.labels.isEmpty()) {
            query.setParameterList("labelIds", (Collection)this.labels.stream().map(EntityObject::getId).collect(Collectors.toList()));
        }
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (this.favourite.isPresent() && currentUser != null) {
            query.setParameter("userKey", (Object)currentUser.getKey());
        }
        if (this.queryWithPermissionClauseBuilder.includesPermissionCheck()) {
            this.queryWithPermissionClauseBuilder.substituteHqlQueryParameters(query);
        }
    }

    public String getCountQuery() {
        return this.createQuery(true);
    }

    private String createQuery(boolean countMode) {
        StringBuffer buf = new StringBuffer();
        if (this.queryWithPermissionClauseBuilder.includesPermissionCheck()) {
            this.createQueryWithPermissions(countMode, buf);
        } else {
            this.createQueryWithNoPermissions(countMode, buf);
        }
        if (!countMode && !this.sortBy.isEmpty()) {
            this.addSort(buf);
        }
        return buf.toString().trim();
    }

    private void addSort(StringBuffer buf) {
        buf.append(" order by ");
        for (int i = 0; i < this.sortBy.size(); ++i) {
            String sort = this.sortBy.get(i);
            buf.append("space.").append(sort);
            if (i == this.sortBy.size() - 1) continue;
            buf.append(", ");
        }
    }

    private void createQueryWithNoPermissions(boolean countMode, StringBuffer buf) {
        if (countMode) {
            buf.append("select count(space) from Space space");
        } else {
            buf.append("from Space space");
        }
        List<String> spaceFilterClauses = this.buildSpaceFilterConditions();
        if (!spaceFilterClauses.isEmpty()) {
            buf.append(" where ").append(StringUtils.join(spaceFilterClauses, (String)" and "));
        }
    }

    private void createQueryWithPermissions(boolean countMode, StringBuffer buf) {
        if (countMode) {
            buf.append("select count(distinct space)");
        } else {
            buf.append("select distinct space");
        }
        buf.append(" from SpacePermission as perm inner join perm.space as space where ");
        buf.append(this.queryWithPermissionClauseBuilder.getHqlPermissionFilterString("perm"));
        List<String> spaceFilterClauses = this.buildSpaceFilterConditions();
        if (!spaceFilterClauses.isEmpty()) {
            buf.append(" and ").append(StringUtils.join(spaceFilterClauses, (String)" and "));
        }
    }

    private List<String> buildSpaceFilterConditions() {
        ImmutableList.Builder clauseBuilder = ImmutableList.builder();
        if (this.spaceType != null) {
            clauseBuilder.add((Object)"space.spaceType = :type");
        }
        if (this.spaceKeys != null && !this.spaceKeys.isEmpty()) {
            clauseBuilder.add((Object)"space.key in (:spaceKeys)");
        }
        if (this.spaceIds != null && !this.spaceIds.isEmpty()) {
            clauseBuilder.add((Object)"space.id in (:spaceIds)");
        }
        if (this.spaceStatuses != null && !this.spaceStatuses.isEmpty()) {
            clauseBuilder.add((Object)"space.spaceStatus in (:spaceStatuses)");
        }
        if (this.creationDate != null) {
            clauseBuilder.add((Object)"space.creationDate > :creationDate");
        }
        if (this.labels != null && !this.labels.isEmpty()) {
            clauseBuilder.add((Object)"space.description in (select labelling.content from Labelling labelling, Label label where labelling.label = label and label.id in (:labelIds))");
        }
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (this.favourite.isPresent() && currentUser != null) {
            String in = this.favourite.get() != false ? "in" : "not in";
            clauseBuilder.add((Object)("space.description " + in + " (select labelling.content from Labelling labelling, Label label where labelling.label = label and (label.name = 'favourite' or label.name = 'favorite') and labelling.label.owningUser.key = :userKey)"));
        }
        this.hasRetentionPolicy.ifPresent(hasPolicy -> clauseBuilder.add((Object)("space.id " + (hasPolicy != false ? "in" : "not in") + " (select content.space.id from CustomContentEntityObject content where content.originalVersion is null and     content.contentStatus = 'current' and     content.space.id = space.id and     content.title = 'com.atlassian.confluence.impl.content.retentionrules:space-retention-policy' and     content.containerContent.id is null)")));
        return clauseBuilder.build();
    }

    @VisibleForTesting
    public List<String> getSpaceKeys() {
        return this.spaceKeys;
    }

    @VisibleForTesting
    public Set<Long> getSpaceIds() {
        return this.spaceIds;
    }

    @VisibleForTesting
    public List<String> getSortBy() {
        return this.sortBy;
    }

    private List<String> toListOfStrings(Set<SpaceStatus> spaceStatuses) {
        ArrayList<String> los = new ArrayList<String>(spaceStatuses.size());
        for (SpaceStatus status : spaceStatuses) {
            los.add(status.toString());
        }
        return los;
    }
}

