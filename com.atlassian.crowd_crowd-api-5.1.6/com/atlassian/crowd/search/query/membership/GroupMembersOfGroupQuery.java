/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Group
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.model.group.Group
 */
package com.atlassian.crowd.search.query.membership;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.QueryUtils;
import com.atlassian.crowd.search.query.membership.MembershipQuery;

public class GroupMembersOfGroupQuery<T>
extends MembershipQuery<T> {
    @Deprecated
    public GroupMembersOfGroupQuery(Class<T> returnType, boolean findMembers, EntityDescriptor entityToMatch, String entityNameToMatch, EntityDescriptor entityToReturn, int startIndex, int maxResults) {
        this(returnType, findMembers, entityToMatch, entityToReturn, startIndex, maxResults, QueryBuilder.NULL_RESTRICTION, entityNameToMatch);
    }

    public GroupMembersOfGroupQuery(Class<T> returnType, boolean findMembers, EntityDescriptor entityToMatch, EntityDescriptor entityToReturn, int startIndex, int maxResults, SearchRestriction searchRestriction, String ... entityNamesToMatch) {
        super(QueryUtils.checkAssignableFrom(returnType, String.class, Group.class, com.atlassian.crowd.model.group.Group.class), findMembers, entityToMatch, entityToReturn, startIndex, maxResults, searchRestriction, entityNamesToMatch);
    }
}

