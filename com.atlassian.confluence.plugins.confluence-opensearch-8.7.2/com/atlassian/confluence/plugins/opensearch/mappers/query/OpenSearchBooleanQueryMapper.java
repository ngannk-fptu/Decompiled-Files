/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  org.opensearch.client.opensearch._types.query_dsl.BoolQuery$Builder
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 *  org.springframework.context.annotation.Lazy
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query;

import com.atlassian.confluence.plugins.opensearch.DelegatingQueryMapper;
import com.atlassian.confluence.plugins.opensearch.mappers.query.OpenSearchQueryMapper;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.springframework.context.annotation.Lazy;

public class OpenSearchBooleanQueryMapper
implements OpenSearchQueryMapper<BooleanQuery> {
    private final DelegatingQueryMapper queryMapper;

    public OpenSearchBooleanQueryMapper(@Lazy DelegatingQueryMapper queryMapper) {
        this.queryMapper = queryMapper;
    }

    @Override
    public Query mapQueryToOpenSearch(BooleanQuery query) {
        if (query.getSubClauses().findAny().isEmpty()) {
            return Query.of(q -> q.matchNone(n -> n));
        }
        return Query.of(q -> q.bool(b -> ((BoolQuery.Builder)b.boost(Float.valueOf(query.getBoost()))).must(this.mapQueries(query.getMustQueries())).mustNot(this.mapQueries(query.getMustNotQueries())).should(this.mapQueries(query.getShouldQueries())).filter(this.mapQueries(query.getFilters()))));
    }

    private List<Query> mapQueries(Collection<SearchQuery> queries) {
        return queries.stream().map(this.queryMapper::mapQueryToOpenSearch).collect(Collectors.toList());
    }

    @Override
    public String getKey() {
        return "boolean";
    }
}

