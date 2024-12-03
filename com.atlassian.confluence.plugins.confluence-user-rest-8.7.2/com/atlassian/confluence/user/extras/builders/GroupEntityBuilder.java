/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Group
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.QueryBuilder$PartialEntityQuery
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.user.extras.builders;

import com.atlassian.confluence.user.extras.entities.GroupsEntity;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class GroupEntityBuilder {
    public static final int DEFAULT_MAX_RESULTS = 50;
    private final CrowdService crowdService;
    private static final QueryBuilder.PartialEntityQuery<Group> ALL_GROUPS = QueryBuilder.queryFor(Group.class, (EntityDescriptor)EntityDescriptor.group());

    public GroupEntityBuilder(CrowdService crowdService) {
        this.crowdService = crowdService;
    }

    public GroupsEntity getGroups(int startIndex, int maxResults) {
        GroupsEntity groupsEntity = new GroupsEntity();
        groupsEntity.setGroups(Lists.newArrayList((Iterable)Iterables.transform((Iterable)this.crowdService.search((Query)ALL_GROUPS.startingAt(startIndex).returningAtMost(maxResults)), Group::getName)));
        return groupsEntity;
    }
}

