/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.Entity
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.atlassian.user.search.query.EntityQueryException
 *  com.atlassian.user.search.query.Query
 *  com.atlassian.user.search.query.TermQuery
 */
package com.atlassian.confluence.user;

import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.search.query.EntityQueryException;
import com.atlassian.user.search.query.Query;
import com.atlassian.user.search.query.TermQuery;
import java.util.List;

@Deprecated
public interface SearchEntitiesManager {
    public static final String MATCH_ALL = "match all";
    public static final String MATCH_ANY = "match any";

    public <T extends Entity> TermQuery<T> getTermQuery(String var1, Class<? extends TermQuery<T>> var2) throws EntityQueryException;

    public TermQuery<Group> getGroupNameTermQuery(String var1) throws EntityQueryException;

    public <T extends Entity> Query<T> createUserQuery(List<? extends Query<T>> var1, String var2) throws EntityQueryException;

    public List<Group> findGroupsAsList(TermQuery<Group> var1) throws EntityException;

    public List<Group> findGroupsAsList(TermQuery<Group> var1, boolean var2) throws EntityException;

    public List<User> findUsersAsList(Query<User> var1) throws EntityException;

    public List<User> findUsersAsList(Query<User> var1, boolean var2) throws EntityException;
}

