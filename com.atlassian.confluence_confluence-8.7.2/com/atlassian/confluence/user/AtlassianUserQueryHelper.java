/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.atlassian.user.search.DefaultSearchResult
 *  com.atlassian.user.search.SearchResult
 *  com.atlassian.user.search.page.Pager
 *  com.atlassian.user.search.page.PagerUtils
 *  com.atlassian.user.search.query.EmailTermQuery
 *  com.atlassian.user.search.query.EntityQueryParser
 *  com.atlassian.user.search.query.GroupNameTermQuery
 *  com.atlassian.user.search.query.MultiTermBooleanQuery
 *  com.atlassian.user.search.query.Query
 *  com.atlassian.user.search.query.UserNameTermQuery
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user;

import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.search.DefaultSearchResult;
import com.atlassian.user.search.SearchResult;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.PagerUtils;
import com.atlassian.user.search.query.EmailTermQuery;
import com.atlassian.user.search.query.EntityQueryParser;
import com.atlassian.user.search.query.GroupNameTermQuery;
import com.atlassian.user.search.query.MultiTermBooleanQuery;
import com.atlassian.user.search.query.Query;
import com.atlassian.user.search.query.UserNameTermQuery;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AtlassianUserQueryHelper {
    private static final Logger log = LoggerFactory.getLogger(AtlassianUserQueryHelper.class);
    private final EntityQueryParser entityQueryParser;

    AtlassianUserQueryHelper(EntityQueryParser entityQueryParser) {
        this.entityQueryParser = Objects.requireNonNull(entityQueryParser);
    }

    Collection<User> findUsersByName(Collection<String> usernames) throws EntityException {
        UserNameTermQuery[] queries = (UserNameTermQuery[])usernames.stream().map(UserNameTermQuery::new).toArray(UserNameTermQuery[]::new);
        return this.findUsersAsList((Query<User>)MultiTermBooleanQuery.anyOf((Query[])queries));
    }

    Collection<Group> findGroupsByName(Collection<String> groupNames) throws EntityException {
        GroupNameTermQuery[] queries = (GroupNameTermQuery[])groupNames.stream().map(GroupNameTermQuery::new).toArray(GroupNameTermQuery[]::new);
        return this.findGroupsAsList(queries);
    }

    SearchResult<User> findUsers(Query<User> query) throws EntityException {
        return this.entityQueryParser.findUsers(query);
    }

    List<User> findUsersAsList(Query<User> search) throws EntityException {
        return PagerUtils.toList((Pager)this.findUsers(search).pager());
    }

    List<Group> findGroupsAsList(GroupNameTermQuery[] queries) throws EntityException {
        return PagerUtils.toList((Pager)this.entityQueryParser.findGroups(MultiTermBooleanQuery.anyOf((Query[])queries)).pager());
    }

    SearchResult<User> getUsersByEmail(String email) {
        if (StringUtils.isEmpty((CharSequence)email)) {
            return new DefaultSearchResult();
        }
        SearchResult results = null;
        EmailTermQuery emailQuery = new EmailTermQuery(email);
        try {
            results = this.entityQueryParser.findUsers((Query)emailQuery);
        }
        catch (EntityException e) {
            log.error(e.getMessage());
        }
        return results;
    }
}

