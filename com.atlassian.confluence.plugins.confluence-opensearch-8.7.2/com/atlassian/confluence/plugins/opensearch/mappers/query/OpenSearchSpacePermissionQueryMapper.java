/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.Expandable
 *  com.atlassian.confluence.search.v2.SearchExpander
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SpacePermissionQueryFactory
 *  com.atlassian.confluence.user.ConfluenceUser
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 *  org.springframework.context.annotation.Lazy
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query;

import com.atlassian.confluence.plugins.opensearch.DelegatingQueryMapper;
import com.atlassian.confluence.plugins.opensearch.mappers.query.OpenSearchQueryMapper;
import com.atlassian.confluence.search.v2.Expandable;
import com.atlassian.confluence.search.v2.SearchExpander;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SpacePermissionQueryFactory;
import com.atlassian.confluence.user.ConfluenceUser;
import java.lang.reflect.InvocationTargetException;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.springframework.context.annotation.Lazy;

public class OpenSearchSpacePermissionQueryMapper
implements OpenSearchQueryMapper<SearchQuery> {
    private final DelegatingQueryMapper delegatingQueryMapper;
    private final SpacePermissionQueryFactory spacePermissionQueryFactory;

    public OpenSearchSpacePermissionQueryMapper(@Lazy DelegatingQueryMapper delegatingQueryMapper, SpacePermissionQueryFactory spacePermissionQueryFactory) {
        this.delegatingQueryMapper = delegatingQueryMapper;
        this.spacePermissionQueryFactory = spacePermissionQueryFactory;
    }

    @Override
    public Query mapQueryToOpenSearch(SearchQuery query) {
        try {
            ConfluenceUser user = (ConfluenceUser)query.getClass().getMethod("getUser", new Class[0]).invoke((Object)query, new Object[0]);
            return this.delegatingQueryMapper.mapQueryToOpenSearch((SearchQuery)SearchExpander.expandAll((Expandable)this.spacePermissionQueryFactory.create(user)));
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalArgumentException("Could not get user", e);
        }
    }

    @Override
    public String getKey() {
        return "spacePermission";
    }
}

