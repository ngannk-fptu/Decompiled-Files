/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.Expandable
 *  com.atlassian.confluence.search.v2.SearchExpander
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 *  org.springframework.context.annotation.Lazy
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query;

import com.atlassian.confluence.plugins.opensearch.DelegatingQueryMapper;
import com.atlassian.confluence.plugins.opensearch.mappers.query.OpenSearchQueryMapper;
import com.atlassian.confluence.search.v2.Expandable;
import com.atlassian.confluence.search.v2.SearchExpander;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.springframework.context.annotation.Lazy;

public class OpenSearchSiteSearchPermissionQueryMapper
implements OpenSearchQueryMapper<SearchQuery> {
    private final DelegatingQueryMapper delegatingQueryMapper;
    private final SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory;

    public OpenSearchSiteSearchPermissionQueryMapper(@Lazy DelegatingQueryMapper delegatingQueryMapper, SiteSearchPermissionsQueryFactory siteSearchPermissionQueryFactory) {
        this.delegatingQueryMapper = delegatingQueryMapper;
        this.siteSearchPermissionsQueryFactory = siteSearchPermissionQueryFactory;
    }

    @Override
    public Query mapQueryToOpenSearch(SearchQuery query) {
        return this.delegatingQueryMapper.mapQueryToOpenSearch((SearchQuery)SearchExpander.expandAll((Expandable)this.siteSearchPermissionsQueryFactory.create()));
    }

    @Override
    public String getKey() {
        return "siteSearchPermissions";
    }
}

