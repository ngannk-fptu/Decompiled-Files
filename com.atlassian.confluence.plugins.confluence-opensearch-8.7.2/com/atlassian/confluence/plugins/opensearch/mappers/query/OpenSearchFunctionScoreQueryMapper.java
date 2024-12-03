/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.query.FunctionScoreQuery
 *  com.atlassian.confluence.search.v2.query.FunctionScoreQuery$BoostMode
 *  com.google.common.collect.ImmutableMap
 *  org.opensearch.client.opensearch._types.query_dsl.FunctionBoostMode
 *  org.opensearch.client.opensearch._types.query_dsl.FunctionScoreQuery$Builder
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 *  org.springframework.context.annotation.Lazy
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query;

import com.atlassian.confluence.plugins.opensearch.DelegatingQueryMapper;
import com.atlassian.confluence.plugins.opensearch.mappers.query.OpenSearchQueryMapper;
import com.atlassian.confluence.plugins.opensearch.mappers.query.score.ScoreFunctionMapper;
import com.atlassian.confluence.search.v2.query.FunctionScoreQuery;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.opensearch.client.opensearch._types.query_dsl.FunctionBoostMode;
import org.opensearch.client.opensearch._types.query_dsl.FunctionScoreQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.springframework.context.annotation.Lazy;

public class OpenSearchFunctionScoreQueryMapper
implements OpenSearchQueryMapper<FunctionScoreQuery> {
    private final DelegatingQueryMapper queryMapper;
    private final ScoreFunctionMapper scoreFunctionMapper;
    private static final Map<FunctionScoreQuery.BoostMode, FunctionBoostMode> BOOST_MODE_MAPPER = ImmutableMap.builder().put((Object)FunctionScoreQuery.BoostMode.MULTIPLY, (Object)FunctionBoostMode.Multiply).put((Object)FunctionScoreQuery.BoostMode.SUM, (Object)FunctionBoostMode.Sum).put((Object)FunctionScoreQuery.BoostMode.MIN, (Object)FunctionBoostMode.Min).put((Object)FunctionScoreQuery.BoostMode.MAX, (Object)FunctionBoostMode.Max).put((Object)FunctionScoreQuery.BoostMode.REPLACE, (Object)FunctionBoostMode.Replace).build();

    public OpenSearchFunctionScoreQueryMapper(@Lazy DelegatingQueryMapper queryMapper, ScoreFunctionMapper scoreFunctionMapper) {
        this.queryMapper = queryMapper;
        this.scoreFunctionMapper = scoreFunctionMapper;
    }

    @Override
    public Query mapQueryToOpenSearch(FunctionScoreQuery query) {
        return Query.of(q -> q.functionScore(fs -> ((FunctionScoreQuery.Builder)this.scoreFunctionMapper.toFunctionScoreQuery(query.getFunction(), (FunctionScoreQuery.Builder)fs).query(this.queryMapper.mapQueryToOpenSearch(query.getWrappedQuery())).boost(Float.valueOf(query.getBoost()))).boostMode(this.mapBoostMode(query.getBoostMode()))));
    }

    private FunctionBoostMode mapBoostMode(FunctionScoreQuery.BoostMode boostMode) {
        if (boostMode == null) {
            return null;
        }
        FunctionBoostMode result = BOOST_MODE_MAPPER.get(boostMode);
        if (result == null) {
            throw new IllegalArgumentException(String.format("Invalid boost mode %s", boostMode));
        }
        return result;
    }

    @Override
    public String getKey() {
        return "functionScore";
    }
}

