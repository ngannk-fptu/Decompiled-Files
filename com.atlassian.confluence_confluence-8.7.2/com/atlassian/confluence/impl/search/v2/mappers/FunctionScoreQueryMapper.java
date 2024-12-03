/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.collect.ImmutableMap
 *  org.apache.lucene.search.Query
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneAverageScoreFunctionFactory;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneConstantScoreFunctionFactory;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneDocValuesFieldValueSourceFactory;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneDoubleFieldValueSourceFactory;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneExpDecayFunctionFactory;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneFactorScoreFunctionFactory;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneFilteredScoreFunctionFactory;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneFirstScoreFunctionFactory;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneFloatFieldValueSourceFactory;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneFunctionScoreQuery;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneGaussDecayFunctionFactory;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneIntFieldValueSourceFactory;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneLinearDecayFunctionFactory;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneLongFieldValueSourceFactory;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneMaxScoreFunctionFactory;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneMinScoreFunctionFactory;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneMultiplyScoreFunctionFactory;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunctionFactory;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneStaircaseFunctionFactory;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneSumScoreFunctionFactory;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.FunctionScoreQuery;
import com.atlassian.confluence.search.v2.score.AverageScoreFunction;
import com.atlassian.confluence.search.v2.score.ComposableScoreFunction;
import com.atlassian.confluence.search.v2.score.ConstantScoreFunction;
import com.atlassian.confluence.search.v2.score.DocValuesFieldValueSource;
import com.atlassian.confluence.search.v2.score.DoubleFieldValueSource;
import com.atlassian.confluence.search.v2.score.ExpDecayFunction;
import com.atlassian.confluence.search.v2.score.FieldValueFactorFunction;
import com.atlassian.confluence.search.v2.score.FieldValueSource;
import com.atlassian.confluence.search.v2.score.FilteredScoreFunction;
import com.atlassian.confluence.search.v2.score.FirstScoreFunction;
import com.atlassian.confluence.search.v2.score.FloatFieldValueSource;
import com.atlassian.confluence.search.v2.score.GaussDecayFunction;
import com.atlassian.confluence.search.v2.score.IntFieldValueSource;
import com.atlassian.confluence.search.v2.score.LinearDecayFunction;
import com.atlassian.confluence.search.v2.score.LongFieldValueSource;
import com.atlassian.confluence.search.v2.score.MaxScoreFunction;
import com.atlassian.confluence.search.v2.score.MinScoreFunction;
import com.atlassian.confluence.search.v2.score.MultiplyScoreFunction;
import com.atlassian.confluence.search.v2.score.ScoreFunction;
import com.atlassian.confluence.search.v2.score.StaircaseFunction;
import com.atlassian.confluence.search.v2.score.SumScoreFunction;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.lucene.search.Query;

@Internal
public class FunctionScoreQueryMapper
implements LuceneQueryMapper<FunctionScoreQuery> {
    private final Map<Class<? extends FieldValueSource>, FieldValueSourceMapper> FIELD_VALUE_SOURCE_MAPPER = ImmutableMap.builder().put(DoubleFieldValueSource.class, x -> new LuceneDoubleFieldValueSourceFactory(x.getFieldName())).put(FloatFieldValueSource.class, x -> new LuceneFloatFieldValueSourceFactory(x.getFieldName())).put(IntFieldValueSource.class, x -> new LuceneIntFieldValueSourceFactory(x.getFieldName())).put(LongFieldValueSource.class, x -> new LuceneLongFieldValueSourceFactory(x.getFieldName())).put(DocValuesFieldValueSource.class, x -> {
        DocValuesFieldValueSource source = (DocValuesFieldValueSource)x;
        return new LuceneDocValuesFieldValueSourceFactory(source.getFieldName(), source.getExtractor());
    }).build();
    private final Map<Class<? extends ScoreFunction>, ScoreFunctionMapper> SCORE_FUNCTION_MAPPER = ImmutableMap.builder().put(ConstantScoreFunction.class, f -> {
        ConstantScoreFunction function = (ConstantScoreFunction)f;
        return new LuceneConstantScoreFunctionFactory(function.getWeight());
    }).put(FieldValueFactorFunction.class, f -> {
        FieldValueFactorFunction function = (FieldValueFactorFunction)f;
        return new LuceneFactorScoreFunctionFactory(this.map(function.getSource()), function.getFactor(), function.getModifier());
    }).put(ExpDecayFunction.class, f -> {
        ExpDecayFunction function = (ExpDecayFunction)f;
        return new LuceneExpDecayFunctionFactory(this.map(function.getSource()), function.getParameters());
    }).put(FilteredScoreFunction.class, f -> {
        FilteredScoreFunction function = (FilteredScoreFunction)f;
        return new LuceneFilteredScoreFunctionFactory(this.mapQuery(function.getFilter()), this.map(function.getDelegate()));
    }).put(GaussDecayFunction.class, f -> {
        GaussDecayFunction function = (GaussDecayFunction)f;
        return new LuceneGaussDecayFunctionFactory(this.map(function.getSource()), function.getParameters());
    }).put(LinearDecayFunction.class, f -> {
        LinearDecayFunction function = (LinearDecayFunction)f;
        return new LuceneLinearDecayFunctionFactory(this.map(function.getSource()), function.getParameters());
    }).put(StaircaseFunction.class, f -> {
        StaircaseFunction function = (StaircaseFunction)f;
        return new LuceneStaircaseFunctionFactory(this.map(function.getSource()), function.getStaircases());
    }).put(AverageScoreFunction.class, f -> {
        AverageScoreFunction function = (AverageScoreFunction)f;
        return new LuceneAverageScoreFunctionFactory(this.map(function.getFunctions()), function.getWeights());
    }).put(FirstScoreFunction.class, f -> {
        FirstScoreFunction function = (FirstScoreFunction)f;
        return new LuceneFirstScoreFunctionFactory(this.map(function.getFunctions()));
    }).put(MaxScoreFunction.class, f -> {
        MaxScoreFunction function = (MaxScoreFunction)f;
        return new LuceneMaxScoreFunctionFactory(this.map(function.getFunctions()));
    }).put(MinScoreFunction.class, f -> {
        MinScoreFunction function = (MinScoreFunction)f;
        return new LuceneMinScoreFunctionFactory(this.map(function.getFunctions()));
    }).put(SumScoreFunction.class, f -> {
        SumScoreFunction function = (SumScoreFunction)f;
        return new LuceneSumScoreFunctionFactory(this.map(function.getFunctions()), function.getConstants());
    }).put(MultiplyScoreFunction.class, f -> {
        MultiplyScoreFunction function = (MultiplyScoreFunction)f;
        return new LuceneMultiplyScoreFunctionFactory(this.map(function.getFunctions()));
    }).build();
    private final LuceneQueryMapper searchMapper;

    public FunctionScoreQueryMapper(LuceneQueryMapper searchMapper) {
        this.searchMapper = searchMapper;
    }

    @Override
    public Query convertToLuceneQuery(FunctionScoreQuery query) {
        ScoreFunction scoreFunction = query.getFunction();
        LuceneScoreFunctionFactory luceneScoreFunctionFactory = this.map(scoreFunction);
        Query luceneQuery = this.mapQuery(query.getWrappedQuery());
        return new LuceneFunctionScoreQuery(luceneQuery, query.getBoostMode(), luceneScoreFunctionFactory);
    }

    private Query mapQuery(SearchQuery query) {
        return this.searchMapper.convertToLuceneQuery(query);
    }

    private LuceneScoreFunctionFactory map(FieldValueSource source) {
        FieldValueSourceMapper mapper = this.FIELD_VALUE_SOURCE_MAPPER.get(source.getClass());
        if (mapper == null) {
            throw new IllegalArgumentException(String.format("Field source of type %s is not supported", source.getClass()));
        }
        return mapper.map(source);
    }

    private LuceneScoreFunctionFactory map(ScoreFunction function) {
        ScoreFunctionMapper mapper = this.SCORE_FUNCTION_MAPPER.get(function.getClass());
        if (mapper == null) {
            throw new IllegalArgumentException(String.format("Function of type %s is not supported", function.getClass()));
        }
        return mapper.map(function);
    }

    private List<LuceneScoreFunctionFactory> map(List<ComposableScoreFunction> functions) {
        return functions.stream().map(this::map).collect(Collectors.toList());
    }

    @FunctionalInterface
    private static interface ScoreFunctionMapper {
        public LuceneScoreFunctionFactory map(ScoreFunction var1);
    }

    @FunctionalInterface
    private static interface FieldValueSourceMapper {
        public LuceneScoreFunctionFactory map(FieldValueSource var1);
    }
}

