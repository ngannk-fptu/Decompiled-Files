/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.score.ComposableScoreFunction
 *  com.atlassian.confluence.search.v2.score.FirstScoreFunction
 *  com.atlassian.confluence.search.v2.score.ScoreFunction
 *  com.atlassian.confluence.search.v2.score.SumScoreFunction
 *  com.google.common.collect.ImmutableMap
 *  org.opensearch.client.opensearch._types.query_dsl.FunctionScore$Builder
 *  org.opensearch.client.opensearch._types.query_dsl.FunctionScoreMode
 *  org.opensearch.client.opensearch._types.query_dsl.FunctionScoreQuery$Builder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query.score;

import com.atlassian.confluence.plugins.opensearch.mappers.query.score.ComposableScoreFunctionMapper;
import com.atlassian.confluence.search.v2.score.ComposableScoreFunction;
import com.atlassian.confluence.search.v2.score.FirstScoreFunction;
import com.atlassian.confluence.search.v2.score.ScoreFunction;
import com.atlassian.confluence.search.v2.score.SumScoreFunction;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.opensearch.client.opensearch._types.query_dsl.FunctionScore;
import org.opensearch.client.opensearch._types.query_dsl.FunctionScoreMode;
import org.opensearch.client.opensearch._types.query_dsl.FunctionScoreQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScoreFunctionMapper {
    private static final Logger LOG = LoggerFactory.getLogger(ScoreFunctionMapper.class);
    private final ComposableScoreFunctionMapper composableScoreFunctionMapper;
    private final Map<Class<? extends ScoreFunction>, Mapper> aggregatingFunctionMappers = ImmutableMap.builder().put(SumScoreFunction.class, this::sum).put(FirstScoreFunction.class, this::first).build();

    public ScoreFunctionMapper(ComposableScoreFunctionMapper composableScoreFunctionMapper) {
        this.composableScoreFunctionMapper = composableScoreFunctionMapper;
    }

    private void first(FirstScoreFunction first, FunctionScoreQuery.Builder builder) {
        builder.scoreMode(FunctionScoreMode.First);
        for (ComposableScoreFunction fun : first.getFunctions()) {
            builder.functions(f -> this.composableScoreFunctionMapper.toFunctionScore(fun, (FunctionScore.Builder)f));
        }
    }

    private void sum(SumScoreFunction sum, FunctionScoreQuery.Builder builder) {
        builder.scoreMode(FunctionScoreMode.Sum);
        for (ComposableScoreFunction fun : sum.getFunctions()) {
            builder.functions(f -> this.composableScoreFunctionMapper.toFunctionScore(fun, (FunctionScore.Builder)f));
        }
        for (Double constant : sum.getConstants()) {
            builder.functions(f -> this.composableScoreFunctionMapper.constant(constant, (FunctionScore.Builder)f));
        }
    }

    public FunctionScoreQuery.Builder toFunctionScoreQuery(ScoreFunction function, FunctionScoreQuery.Builder builder) {
        if (ScoreFunctionMapper.isComposable(function)) {
            builder.functions(f -> this.composableScoreFunctionMapper.toFunctionScore((ComposableScoreFunction)function, (FunctionScore.Builder)f));
        } else {
            Mapper mapper = this.aggregatingFunctionMappers.get(function.getClass());
            if (mapper == null) {
                LOG.error("Invalid score function {}", function.getClass());
                mapper = (f, b) -> {};
            }
            mapper.accept(function, builder);
        }
        return builder;
    }

    private static boolean isComposable(ScoreFunction function) {
        return function instanceof ComposableScoreFunction && !(function instanceof SumScoreFunction);
    }

    private static interface Mapper<T extends ScoreFunction>
    extends BiConsumer<T, FunctionScoreQuery.Builder> {
    }
}

