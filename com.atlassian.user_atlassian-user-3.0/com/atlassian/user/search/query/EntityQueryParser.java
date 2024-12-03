/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search.query;

import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.search.SearchResult;
import com.atlassian.user.search.query.Query;
import com.atlassian.user.search.query.QueryContext;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface EntityQueryParser {
    public SearchResult<User> findUsers(Query<User> var1) throws EntityException;

    public SearchResult<Group> findGroups(Query<Group> var1) throws EntityException;

    public SearchResult<User> findUsers(Query<User> var1, QueryContext var2) throws EntityException;

    public SearchResult<Group> findGroups(Query<Group> var1, QueryContext var2) throws EntityException;
}

