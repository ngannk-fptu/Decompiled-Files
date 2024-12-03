/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.data.activeobjects.repository.query.ActiveObjectsParameters;
import com.atlassian.data.activeobjects.repository.query.ActiveObjectsParametersParameterAccessor;
import com.atlassian.data.activeobjects.repository.query.DeclaredQuery;
import com.atlassian.data.activeobjects.repository.query.ParameterMetadataProvider;
import com.atlassian.data.activeobjects.repository.query.QueryParameterSetter;
import com.atlassian.data.activeobjects.repository.query.StringQuery;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

abstract class QueryParameterSetterFactory {
    QueryParameterSetterFactory() {
    }

    @Nullable
    abstract QueryParameterSetter create(StringQuery.ParameterBinding var1, DeclaredQuery var2);

    static QueryParameterSetterFactory basic(ActiveObjectsParameters parameters) {
        Assert.notNull((Object)parameters, (String)"ActiveObjectQueryParameters must not be null!");
        return new BasicQueryParameterSetterFactory(parameters);
    }

    static QueryParameterSetterFactory forCriteriaQuery(ActiveObjectsParameters parameters, List<ParameterMetadataProvider.ParameterMetadata<?>> metadata) {
        Assert.notNull((Object)parameters, (String)"ActiveObjectsParameters must not be null!");
        Assert.notNull(metadata, (String)"ParameterMetadata must not be null!");
        return new CriteriaQueryParameterSetterFactory(parameters, metadata);
    }

    static QueryParameterSetterFactory parsing(SpelExpressionParser parser, QueryMethodEvaluationContextProvider evaluationContextProvider, Parameters<?, ?> parameters) {
        Assert.notNull((Object)parser, (String)"SpelExpressionParser must not be null!");
        Assert.notNull((Object)evaluationContextProvider, (String)"EvaluationContextProvider must not be null!");
        Assert.notNull(parameters, (String)"Parameters must not be null!");
        return new ExpressionBasedQueryParameterSetterFactory(parser, evaluationContextProvider, parameters);
    }

    private static QueryParameterSetter createSetter(Function<Object[], Object> valueExtractor, StringQuery.ParameterBinding binding) {
        return new QueryParameterSetter.NamedOrIndexedQueryParameterSetter(valueExtractor.andThen(binding::prepare), binding);
    }

    private static class CriteriaQueryParameterSetterFactory
    extends QueryParameterSetterFactory {
        private final ActiveObjectsParameters parameters;
        private final List<ParameterMetadataProvider.ParameterMetadata<?>> expressions;

        CriteriaQueryParameterSetterFactory(ActiveObjectsParameters parameters, List<ParameterMetadataProvider.ParameterMetadata<?>> metadata) {
            Assert.notNull((Object)parameters, (String)"ActiveObjectsParameters must not be null!");
            Assert.notNull(metadata, (String)"Expressions must not be null!");
            this.parameters = parameters;
            this.expressions = metadata;
        }

        @Override
        public QueryParameterSetter create(StringQuery.ParameterBinding binding, DeclaredQuery declaredQuery) {
            int parameterIndex = binding.getRequiredPosition() - 1;
            Assert.isTrue((parameterIndex < this.expressions.size() ? 1 : 0) != 0, () -> String.format("At least %s parameter(s) provided but only %s parameter(s) present in query.", binding.getRequiredPosition(), this.expressions.size()));
            ParameterMetadataProvider.ParameterMetadata<?> metadata = this.expressions.get(parameterIndex);
            if (metadata.isIsNullParameter()) {
                return QueryParameterSetter.NOOP;
            }
            ActiveObjectsParameters.ActiveObjectsParameter parameter = (ActiveObjectsParameters.ActiveObjectsParameter)this.parameters.getBindableParameter(parameterIndex);
            return new QueryParameterSetter.PredicateParameterSetter(values -> this.getAndPrepare(parameter, metadata, (Object[])values), metadata.getExpression());
        }

        @Nullable
        private Object getAndPrepare(ActiveObjectsParameters.ActiveObjectsParameter parameter, ParameterMetadataProvider.ParameterMetadata<?> metadata, Object[] values) {
            ActiveObjectsParametersParameterAccessor accessor = new ActiveObjectsParametersParameterAccessor(this.parameters, values);
            return metadata.prepare(accessor.getValue(parameter));
        }
    }

    private static class BasicQueryParameterSetterFactory
    extends QueryParameterSetterFactory {
        private final ActiveObjectsParameters parameters;

        BasicQueryParameterSetterFactory(ActiveObjectsParameters parameters) {
            Assert.notNull((Object)parameters, (String)"ActiveObjectQueryParameters must not be null!");
            this.parameters = parameters;
        }

        @Override
        public QueryParameterSetter create(StringQuery.ParameterBinding binding, DeclaredQuery declaredQuery) {
            ActiveObjectsParameters.ActiveObjectsParameter parameter;
            Assert.notNull((Object)binding, (String)"Binding must not be null.");
            if (declaredQuery.hasNamedParameter()) {
                parameter = this.findParameterForBinding(binding);
            } else {
                ActiveObjectsParameters bindableParameters;
                int parameterIndex = binding.getRequiredPosition() - 1;
                Assert.isTrue((parameterIndex < (bindableParameters = (ActiveObjectsParameters)this.parameters.getBindableParameters()).getNumberOfParameters() ? 1 : 0) != 0, () -> String.format("At least %s parameter(s) provided but only %s parameter(s) present in query.", binding.getRequiredPosition(), bindableParameters.getNumberOfParameters()));
                parameter = (ActiveObjectsParameters.ActiveObjectsParameter)bindableParameters.getParameter(binding.getRequiredPosition() - 1);
            }
            return parameter == null ? QueryParameterSetter.NOOP : QueryParameterSetterFactory.createSetter(values -> this.getValue((Object[])values, parameter), binding);
        }

        @Nullable
        private ActiveObjectsParameters.ActiveObjectsParameter findParameterForBinding(StringQuery.ParameterBinding binding) {
            return ((ActiveObjectsParameters)this.parameters.getBindableParameters()).stream().filter(candidate -> binding.getRequiredName().equals(BasicQueryParameterSetterFactory.getName(candidate))).findFirst().orElse(null);
        }

        private Object getValue(Object[] values, Parameter parameter) {
            return new ActiveObjectsParametersParameterAccessor(this.parameters, values).getValue(parameter);
        }

        private static String getName(ActiveObjectsParameters.ActiveObjectsParameter p) {
            return p.getName().orElseThrow(() -> new IllegalStateException("For queries with named parameters you need to use provide names for method parameters. Use @Param for query method parameters, or when on Java 8+ use the javac flag -parameters."));
        }
    }

    private static class ExpressionBasedQueryParameterSetterFactory
    extends QueryParameterSetterFactory {
        private final SpelExpressionParser parser;
        private final QueryMethodEvaluationContextProvider evaluationContextProvider;
        private final Parameters<?, ?> parameters;

        ExpressionBasedQueryParameterSetterFactory(SpelExpressionParser parser, QueryMethodEvaluationContextProvider evaluationContextProvider, Parameters<?, ?> parameters) {
            Assert.notNull((Object)evaluationContextProvider, (String)"EvaluationContextProvider must not be null!");
            Assert.notNull((Object)parser, (String)"SpelExpressionParser must not be null!");
            Assert.notNull(parameters, (String)"Parameters must not be null!");
            this.evaluationContextProvider = evaluationContextProvider;
            this.parser = parser;
            this.parameters = parameters;
        }

        @Override
        @Nullable
        public QueryParameterSetter create(StringQuery.ParameterBinding binding, DeclaredQuery declaredQuery) {
            if (!binding.isExpression()) {
                return null;
            }
            Expression expression = this.parser.parseExpression(binding.getExpression());
            return QueryParameterSetterFactory.createSetter(values -> this.evaluateExpression(expression, (Object[])values), binding);
        }

        @Nullable
        private Object evaluateExpression(Expression expression, Object[] values) {
            EvaluationContext context = this.evaluationContextProvider.getEvaluationContext(this.parameters, values);
            return expression.getValue(context, Object.class);
        }
    }
}

