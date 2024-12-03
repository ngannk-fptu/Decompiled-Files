/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.query.ConstantScoreQuery
 *  org.opensearch.client.opensearch._types.query_dsl.ConstantScoreQuery$Builder
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 *  org.springframework.context.annotation.Lazy
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query;

import com.atlassian.confluence.plugins.opensearch.DelegatingQueryMapper;
import com.atlassian.confluence.plugins.opensearch.mappers.query.OpenSearchQueryMapper;
import com.atlassian.confluence.search.v2.query.ConstantScoreQuery;
import org.opensearch.client.opensearch._types.query_dsl.ConstantScoreQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.springframework.context.annotation.Lazy;

public class OpenSearchConstantScoreQueryMapper
implements OpenSearchQueryMapper<ConstantScoreQuery> {
    private final DelegatingQueryMapper queryMapper;

    public OpenSearchConstantScoreQueryMapper(@Lazy DelegatingQueryMapper queryMapper) {
        this.queryMapper = queryMapper;
    }

    @Override
    public Query mapQueryToOpenSearch(ConstantScoreQuery query) {
        return Query.of(q -> q.constantScore(c -> ((ConstantScoreQuery.Builder)c.boost(Float.valueOf(query.getBoost()))).filter(this.queryMapper.mapQueryToOpenSearch(query.getWrappedQuery()))));
    }

    @Override
    public String getKey() {
        return "constantScore";
    }
}

