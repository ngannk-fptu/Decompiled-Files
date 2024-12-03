/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.api.User
 */
package com.atlassian.crowd.search.query.membership;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.QueryUtils;
import com.atlassian.crowd.search.query.membership.MembershipQuery;

public class UserMembersOfGroupQuery<T>
extends MembershipQuery<T> {
    @Deprecated
    public UserMembersOfGroupQuery(Class<T> returnType, boolean findMembers, EntityDescriptor entityToMatch, String entityNameToMatch, EntityDescriptor entityToReturn, int startIndex, int maxResults) {
        this(returnType, findMembers, entityToMatch, entityToReturn, startIndex, maxResults, QueryBuilder.NULL_RESTRICTION, entityNameToMatch);
    }

    public UserMembersOfGroupQuery(Class<T> returnType, boolean findMembers, EntityDescriptor entityToMatch, EntityDescriptor entityToReturn, int startIndex, int maxResults, SearchRestriction searchRestriction, String ... entityNamesToMatch) {
        super(QueryUtils.checkAssignableFrom(returnType, String.class, User.class), findMembers, entityToMatch, entityToReturn, startIndex, maxResults, searchRestriction, entityNamesToMatch);
    }
}

