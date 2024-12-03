/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.apache.lucene.search.Query
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSearchMapper;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SiteTextSearchQueryFactory;
import com.atlassian.confluence.search.v2.query.SiteTextSearchQuery;
import com.google.common.base.Preconditions;
import org.apache.lucene.search.Query;

public class SiteTextSearchQueryMapper
implements LuceneQueryMapper<SiteTextSearchQuery> {
    private final SiteTextSearchQueryFactory queryFactory;
    private LuceneSearchMapper searchMapper;

    public SiteTextSearchQueryMapper(SiteTextSearchQueryFactory queryFactory) {
        this.queryFactory = (SiteTextSearchQueryFactory)Preconditions.checkNotNull((Object)queryFactory);
    }

    @Override
    public Query convertToLuceneQuery(SiteTextSearchQuery siteTextSearchQuery) {
        SearchQuery expandedQuery = this.queryFactory.getQuery(siteTextSearchQuery.getTextQuery());
        return this.searchMapper.convertToLuceneQuery(expandedQuery);
    }

    public void setSearchMapper(LuceneSearchMapper searchMapper) {
        this.searchMapper = searchMapper;
    }
}

