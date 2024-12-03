/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.Entity
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.atlassian.user.search.DefaultSearchResult
 *  com.atlassian.user.search.SearchResult
 *  com.atlassian.user.search.page.DefaultPager
 *  com.atlassian.user.search.page.Pager
 *  com.atlassian.user.search.page.PagerUtils
 *  com.atlassian.user.search.query.AllRepositoriesQueryContext
 *  com.atlassian.user.search.query.EntityQueryException
 *  com.atlassian.user.search.query.EntityQueryParser
 *  com.atlassian.user.search.query.GroupNameTermQuery
 *  com.atlassian.user.search.query.MultiTermBooleanQuery
 *  com.atlassian.user.search.query.Query
 *  com.atlassian.user.search.query.QueryContext
 *  com.atlassian.user.search.query.TermQuery
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.SearchEntitiesManager;
import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.search.DefaultSearchResult;
import com.atlassian.user.search.SearchResult;
import com.atlassian.user.search.page.DefaultPager;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.PagerUtils;
import com.atlassian.user.search.query.AllRepositoriesQueryContext;
import com.atlassian.user.search.query.EntityQueryException;
import com.atlassian.user.search.query.EntityQueryParser;
import com.atlassian.user.search.query.GroupNameTermQuery;
import com.atlassian.user.search.query.MultiTermBooleanQuery;
import com.atlassian.user.search.query.Query;
import com.atlassian.user.search.query.QueryContext;
import com.atlassian.user.search.query.TermQuery;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DefaultSearchEntitiesManager
implements SearchEntitiesManager {
    private final PermissionManager permissionManager;
    private final EntityQueryParser entityQueryParser;

    public DefaultSearchEntitiesManager(PermissionManager permissionManager, EntityQueryParser entityQueryParser) {
        this.permissionManager = permissionManager;
        this.entityQueryParser = entityQueryParser;
    }

    @Override
    public List<Group> findGroupsAsList(TermQuery<Group> tQuery) throws EntityException {
        return PagerUtils.toList((Pager)this.findGroups(tQuery, true).pager());
    }

    @Override
    public List<Group> findGroupsAsList(TermQuery<Group> tQuery, boolean filterGroups) throws EntityException {
        return PagerUtils.toList((Pager)this.findGroups(tQuery, filterGroups).pager());
    }

    @Override
    public List<User> findUsersAsList(Query<User> userQuery) throws EntityException {
        return this.findUsersAsList(userQuery, false);
    }

    @Override
    public List<User> findUsersAsList(Query<User> userQuery, boolean showUnlicensedUsers) throws EntityException {
        Pager pager = this.findUsers(userQuery).pager();
        HashSet<String> uniqueUsernames = new HashSet<String>();
        ArrayList<User> results = new ArrayList<User>();
        for (User user : pager) {
            if (user == null || !uniqueUsernames.add(user.getName()) || !showUnlicensedUsers && !this.permissionManager.hasPermission(user, Permission.VIEW, PermissionManager.TARGET_APPLICATION)) continue;
            results.add(user);
        }
        return results;
    }

    @Override
    public <T extends Entity> TermQuery<T> getTermQuery(String term, Class<? extends TermQuery<T>> termQueryClass) throws EntityQueryException {
        TermQuery<T> tQuery;
        if (!term.contains("*")) {
            tQuery = this.getTermQueryObject(termQueryClass, term);
        } else if (term.indexOf("*") == 0 && term.lastIndexOf("*") == 0) {
            String copied = term.replaceFirst("\\*", "");
            tQuery = this.getTermQueryObject(termQueryClass, copied, "ends_with");
        } else if (term.indexOf("*") == term.length() - 1) {
            String copied = term.replaceFirst("\\*", "");
            tQuery = this.getTermQueryObject(termQueryClass, copied, "starts_with");
        } else if (term.indexOf("*") == 0 && term.indexOf("*", 1) == term.length() - 1) {
            String copied = term.replaceAll("\\*", "");
            tQuery = this.getTermQueryObject(termQueryClass, copied, "contains");
        } else {
            throw new EntityQueryException("You may only use two wildcards to wrap the search term - e.g. *smith*. Your term was: " + term);
        }
        return tQuery;
    }

    @Override
    public TermQuery<Group> getGroupNameTermQuery(String groupnameTerm) throws EntityQueryException {
        GroupNameTermQuery tQuery;
        if (!groupnameTerm.contains("*")) {
            tQuery = new GroupNameTermQuery(groupnameTerm, "starts_with");
        } else if (groupnameTerm.indexOf("*") == 0 && groupnameTerm.lastIndexOf("*") == 0) {
            String copied = groupnameTerm.replaceFirst("\\*", "");
            tQuery = new GroupNameTermQuery(copied, "ends_with");
        } else if (groupnameTerm.indexOf("*") == groupnameTerm.length() - 1) {
            String copied = groupnameTerm.replaceFirst("\\*", "");
            tQuery = new GroupNameTermQuery(copied, "starts_with");
        } else if (groupnameTerm.indexOf("*") == 0 && groupnameTerm.indexOf("*", 1) == groupnameTerm.length() - 1) {
            String copied = groupnameTerm.replaceAll("\\*", "");
            tQuery = new GroupNameTermQuery(copied, "contains");
        } else {
            throw new EntityQueryException("You may only use two wildcards to wrap the search term - e.g. *smith*. Your term was: " + groupnameTerm);
        }
        return tQuery;
    }

    @Override
    public <T extends Entity> Query<T> createUserQuery(List<? extends Query<T>> queries, String operator) throws EntityQueryException {
        MultiTermBooleanQuery finalQuery;
        if (queries.size() > 1) {
            if (operator == null) {
                throw new EntityQueryException("You must specify a boolean operator ('match all' or 'match any') for a multi-term user search.");
            }
            finalQuery = new MultiTermBooleanQuery(queries.toArray(new Query[queries.size()]), "match all".equals(operator));
        } else if (queries.size() > 0) {
            finalQuery = queries.get(0);
        } else {
            throw new IllegalArgumentException("No search terms specified");
        }
        return finalQuery;
    }

    private SearchResult<User> findUsers(Query<User> query) throws EntityException {
        AllRepositoriesQueryContext ctx = new AllRepositoriesQueryContext();
        SearchResult result = this.entityQueryParser.findUsers(query, (QueryContext)ctx);
        Object userList = result != null ? result.pager() : DefaultPager.emptyPager();
        return new DefaultSearchResult((Pager)userList, null);
    }

    private SearchResult<Group> findGroups(TermQuery<Group> tQuery, boolean filterGroups) throws EntityException {
        AllRepositoriesQueryContext ctx = new AllRepositoriesQueryContext();
        SearchResult result = this.entityQueryParser.findGroups(tQuery, (QueryContext)ctx);
        if (!filterGroups) {
            return result;
        }
        return new DefaultSearchResult(result.pager(), null);
    }

    private <T extends Entity> TermQuery<T> getTermQueryObject(Class<? extends TermQuery<T>> termQueryClass, String term) throws EntityQueryException {
        TermQuery<T> termQuery;
        try {
            Constructor<TermQuery<T>> constructor = termQueryClass.getConstructor(String.class);
            termQuery = constructor.newInstance(term);
        }
        catch (Exception e) {
            throw new EntityQueryException((Throwable)e);
        }
        return termQuery;
    }

    private <T extends Entity> TermQuery<T> getTermQueryObject(Class<? extends TermQuery<T>> termQueryClass, String term, String condition) throws EntityQueryException {
        TermQuery<T> termQuery;
        try {
            Constructor<TermQuery<T>> constructor = termQueryClass.getConstructor(String.class, String.class);
            termQuery = constructor.newInstance(term, condition);
        }
        catch (Exception e) {
            throw new EntityQueryException((Throwable)e);
        }
        return termQuery;
    }
}

