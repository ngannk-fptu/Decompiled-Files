/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SiteTextSearchQueryFactory
 *  com.atlassian.confluence.search.v2.query.SiteTextSearchQuery
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 *  org.springframework.context.annotation.Lazy
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query;

import com.atlassian.confluence.plugins.opensearch.DelegatingQueryMapper;
import com.atlassian.confluence.plugins.opensearch.mappers.query.OpenSearchQueryMapper;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SiteTextSearchQueryFactory;
import com.atlassian.confluence.search.v2.query.SiteTextSearchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.springframework.context.annotation.Lazy;

public class OpenSearchSiteTextSearchQueryMapper
implements OpenSearchQueryMapper<SiteTextSearchQuery> {
    private final DelegatingQueryMapper delegatingQueryMapper;
    private final SiteTextSearchQueryFactory siteTextSearchQueryFactory;

    public OpenSearchSiteTextSearchQueryMapper(@Lazy DelegatingQueryMapper delegatingQueryMapper, SiteTextSearchQueryFactory siteTextSearchQueryFactory) {
        this.delegatingQueryMapper = delegatingQueryMapper;
        this.siteTextSearchQueryFactory = siteTextSearchQueryFactory;
    }

    @Override
    public Query mapQueryToOpenSearch(SiteTextSearchQuery query) {
        SearchQuery v2Query = this.siteTextSearchQueryFactory.getQuery(query.getTextQuery());
        return this.delegatingQueryMapper.mapQueryToOpenSearch(v2Query);
    }

    @Override
    public String getKey() {
        return "siteTextSearch";
    }
}

