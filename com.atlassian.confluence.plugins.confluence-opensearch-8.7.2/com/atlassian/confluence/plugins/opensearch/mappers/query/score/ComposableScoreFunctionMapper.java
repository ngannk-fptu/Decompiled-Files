/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.score.ComposableScoreFunction
 *  com.atlassian.confluence.search.v2.score.ConstantScoreFunction
 *  com.atlassian.confluence.search.v2.score.DecayParameters
 *  com.atlassian.confluence.search.v2.score.DocValuesFieldValueSource
 *  com.atlassian.confluence.search.v2.score.ExpDecayFunction
 *  com.atlassian.confluence.search.v2.score.FieldValueSource
 *  com.atlassian.confluence.search.v2.score.FilteredScoreFunction
 *  com.atlassian.confluence.search.v2.score.GaussDecayFunction
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.StringUtils
 *  org.opensearch.client.json.JsonData
 *  org.opensearch.client.opensearch._types.query_dsl.DecayPlacement
 *  org.opensearch.client.opensearch._types.query_dsl.FunctionScore$Builder
 *  org.opensearch.client.opensearch._types.query_dsl.FunctionScore$Builder$ContainerBuilder
 *  org.opensearch.client.util.ObjectBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.annotation.Lazy
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query.score;

import com.atlassian.confluence.plugins.opensearch.DelegatingQueryMapper;
import com.atlassian.confluence.search.v2.score.ComposableScoreFunction;
import com.atlassian.confluence.search.v2.score.ConstantScoreFunction;
import com.atlassian.confluence.search.v2.score.DecayParameters;
import com.atlassian.confluence.search.v2.score.DocValuesFieldValueSource;
import com.atlassian.confluence.search.v2.score.ExpDecayFunction;
import com.atlassian.confluence.search.v2.score.FieldValueSource;
import com.atlassian.confluence.search.v2.score.FilteredScoreFunction;
import com.atlassian.confluence.search.v2.score.GaussDecayFunction;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.query_dsl.DecayPlacement;
import org.opensearch.client.opensearch._types.query_dsl.FunctionScore;
import org.opensearch.client.util.ObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;

public class ComposableScoreFunctionMapper {
    private static final Logger LOG = LoggerFactory.getLogger(ComposableScoreFunctionMapper.class);
    private final DelegatingQueryMapper queryMapper;
    private final Map<Class<? extends ComposableScoreFunction>, Mapper> scoreFunctionMappers = ImmutableMap.builder().put(ExpDecayFunction.class, this::expDecay).put(GaussDecayFunction.class, this::gaussDecay).put(ConstantScoreFunction.class, this::constant).put(FilteredScoreFunction.class, this::filtered).build();

    public ComposableScoreFunctionMapper(@Lazy DelegatingQueryMapper queryMapper) {
        this.queryMapper = queryMapper;
    }

    private FunctionScore.Builder.ContainerBuilder constant(ConstantScoreFunction constant, FunctionScore.Builder builder) {
        return this.constant(constant.getWeight(), builder);
    }

    private FunctionScore.Builder.ContainerBuilder expDecay(ExpDecayFunction exp, FunctionScore.Builder builder) {
        return builder.exp(e -> e.field(this.toField(exp.getSource())).placement(this.toPlacement(exp.getParameters())));
    }

    private FunctionScore.Builder.ContainerBuilder gaussDecay(GaussDecayFunction gauss, FunctionScore.Builder builder) {
        return builder.gauss(g -> g.field(this.toField(gauss.getSource())).placement(this.toPlacement(gauss.getParameters())));
    }

    private String toField(FieldValueSource source) {
        if (source instanceof DocValuesFieldValueSource) {
            throw new IllegalArgumentException("Invalid source " + source.getClass());
        }
        return source.getFieldName();
    }

    private DecayPlacement toPlacement(DecayParameters params) {
        return DecayPlacement.of(p -> p.origin(JsonData.of((Object)params.getOriginAsString())).scale(this.formatUnit(params.getScale(), params.getUnit())).decay(Double.valueOf(params.getDecay())).offset(this.formatUnit(params.getOffset(), params.getUnit())));
    }

    private JsonData formatUnit(double value, String unit) {
        return StringUtils.isEmpty((CharSequence)unit) ? JsonData.of((Object)value) : JsonData.of((Object)(Double.valueOf(value).longValue() + unit));
    }

    public FunctionScore.Builder.ContainerBuilder constant(double weight, FunctionScore.Builder builder) {
        return builder.scriptScore(ss -> ss.script(s -> s.inline(i -> (ObjectBuilder)i.source("params['constant']").params("constant", JsonData.of((Object)weight)))));
    }

    private FunctionScore.Builder.ContainerBuilder filtered(FilteredScoreFunction filtered, FunctionScore.Builder builder) {
        return this.toFunctionScore(filtered.getDelegate(), builder).filter(this.queryMapper.mapQueryToOpenSearch(filtered.getFilter()));
    }

    public FunctionScore.Builder.ContainerBuilder toFunctionScore(ComposableScoreFunction fun, FunctionScore.Builder builder) {
        Mapper mapper = this.scoreFunctionMappers.get(fun.getClass());
        if (mapper == null) {
            LOG.error("Invalid composable score function {}", fun.getClass());
            return this.constant(1.0, builder);
        }
        return (FunctionScore.Builder.ContainerBuilder)mapper.apply(fun, builder);
    }

    private static interface Mapper<T extends ComposableScoreFunction>
    extends BiFunction<T, FunctionScore.Builder, FunctionScore.Builder.ContainerBuilder> {
    }
}

