/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.util.Assert
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.data.activeobjects.repository.query.ActiveObjectsParameters;
import com.atlassian.data.activeobjects.repository.query.DeclaredQuery;
import com.atlassian.data.activeobjects.repository.query.EmptyDeclaredQuery;
import com.atlassian.data.activeobjects.repository.query.ParameterBinder;
import com.atlassian.data.activeobjects.repository.query.ParameterMetadataProvider;
import com.atlassian.data.activeobjects.repository.query.QueryParameterSetter;
import com.atlassian.data.activeobjects.repository.query.QueryParameterSetterFactory;
import com.atlassian.data.activeobjects.repository.query.StringQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.util.StreamUtils;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;

final class ParameterBinderFactory {
    static ParameterBinder createBinder(ActiveObjectsParameters parameters) {
        Assert.notNull((Object)parameters, (String)"ActiveObjectsParameters must not be null!");
        QueryParameterSetterFactory setterFactory = QueryParameterSetterFactory.basic(parameters);
        List<StringQuery.ParameterBinding> bindings = ParameterBinderFactory.getBindings(parameters);
        return new ParameterBinder(parameters, ParameterBinderFactory.createSetters(bindings, setterFactory));
    }

    static ParameterBinder createCriteriaBinder(ActiveObjectsParameters parameters, List<ParameterMetadataProvider.ParameterMetadata<?>> metadata) {
        Assert.notNull((Object)parameters, (String)"ActiveObjectsParameters must not be null!");
        Assert.notNull(metadata, (String)"Parameter metadata must not be null!");
        QueryParameterSetterFactory setterFactory = QueryParameterSetterFactory.forCriteriaQuery(parameters, metadata);
        List<StringQuery.ParameterBinding> bindings = ParameterBinderFactory.getBindings(parameters);
        return new ParameterBinder(parameters, ParameterBinderFactory.createSetters(bindings, setterFactory));
    }

    static ParameterBinder createQueryAwareBinder(ActiveObjectsParameters parameters, DeclaredQuery query, SpelExpressionParser parser, QueryMethodEvaluationContextProvider evaluationContextProvider) {
        Assert.notNull((Object)parameters, (String)"ActiveObjectsParameters must not be null!");
        Assert.notNull((Object)query, (String)"StringQuery must not be null!");
        Assert.notNull((Object)parser, (String)"SpelExpressionParser must not be null!");
        Assert.notNull((Object)evaluationContextProvider, (String)"EvaluationContextProvider must not be null!");
        List<StringQuery.ParameterBinding> bindings = query.getParameterBindings();
        QueryParameterSetterFactory expressionSetterFactory = QueryParameterSetterFactory.parsing(parser, evaluationContextProvider, parameters);
        QueryParameterSetterFactory basicSetterFactory = QueryParameterSetterFactory.basic(parameters);
        return new ParameterBinder(parameters, ParameterBinderFactory.createSetters(bindings, query, expressionSetterFactory, basicSetterFactory), !query.usesPaging());
    }

    private static List<StringQuery.ParameterBinding> getBindings(ActiveObjectsParameters parameters) {
        ArrayList<StringQuery.ParameterBinding> result = new ArrayList<StringQuery.ParameterBinding>();
        int bindableParameterIndex = 0;
        for (ActiveObjectsParameters.ActiveObjectsParameter parameter : parameters) {
            if (!parameter.isBindable()) continue;
            result.add(new StringQuery.ParameterBinding(++bindableParameterIndex));
        }
        return result;
    }

    private static Iterable<QueryParameterSetter> createSetters(List<StringQuery.ParameterBinding> parameterBindings, QueryParameterSetterFactory ... factories) {
        return ParameterBinderFactory.createSetters(parameterBindings, EmptyDeclaredQuery.EMPTY_QUERY, factories);
    }

    private static Iterable<QueryParameterSetter> createSetters(List<StringQuery.ParameterBinding> parameterBindings, DeclaredQuery declaredQuery, QueryParameterSetterFactory ... strategies) {
        return parameterBindings.stream().map(it -> ParameterBinderFactory.createQueryParameterSetter(it, strategies, declaredQuery)).collect(StreamUtils.toUnmodifiableList());
    }

    private static QueryParameterSetter createQueryParameterSetter(StringQuery.ParameterBinding binding, QueryParameterSetterFactory[] strategies, DeclaredQuery declaredQuery) {
        return Arrays.stream(strategies).map(it -> it.create(binding, declaredQuery)).filter(Objects::nonNull).findFirst().orElse(QueryParameterSetter.NOOP);
    }

    private ParameterBinderFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

