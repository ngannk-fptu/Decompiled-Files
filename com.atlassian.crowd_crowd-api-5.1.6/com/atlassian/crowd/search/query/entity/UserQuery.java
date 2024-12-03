/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.api.User
 */
package com.atlassian.crowd.search.query.entity;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.query.QueryUtils;
import com.atlassian.crowd.search.query.entity.EntityQuery;

public class UserQuery<T>
extends EntityQuery<T> {
    public UserQuery(Class<T> returnType, SearchRestriction searchRestriction, int startIndex, int maxResults) {
        super(QueryUtils.checkAssignableFrom(returnType, String.class, User.class), EntityDescriptor.user(), searchRestriction, startIndex, maxResults);
    }
}

