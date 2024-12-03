/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Group
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupType
 */
package com.atlassian.crowd.search.query.entity;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.query.QueryUtils;
import com.atlassian.crowd.search.query.entity.EntityQuery;

public class GroupQuery<T>
extends EntityQuery<T> {
    private final GroupType groupType;

    public GroupQuery(Class<T> returnType, GroupType groupType, SearchRestriction searchRestriction, int startIndex, int maxResults) {
        super(QueryUtils.checkAssignableFrom(returnType, String.class, Group.class, com.atlassian.crowd.model.group.Group.class), EntityDescriptor.group(groupType), searchRestriction, startIndex, maxResults);
        this.groupType = groupType;
    }

    public GroupType getGroupType() {
        return this.groupType;
    }
}

