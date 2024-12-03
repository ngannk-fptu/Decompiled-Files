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
package org.springframework.data.repository.query;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.SpelQueryContext;
import org.springframework.data.spel.ExpressionDependencies;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class SpelEvaluator {
    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    private final QueryMethodEvaluationContextProvider evaluationContextProvider;
    private final Parameters<?, ?> parameters;
    private final SpelQueryContext.SpelExtractor extractor;

    public SpelEvaluator(QueryMethodEvaluationContextProvider evaluationContextProvider, Parameters<?, ?> parameters, SpelQueryContext.SpelExtractor extractor) {
        this.evaluationContextProvider = evaluationContextProvider;
        this.parameters = parameters;
        this.extractor = extractor;
    }

    public Map<String, Object> evaluate(Object[] values) {
        Assert.notNull((Object)values, (String)"Values must not be null.");
        return this.extractor.getParameters().collect(Collectors.toMap(Map.Entry::getKey, it -> this.getSpElValue((String)it.getValue(), values)));
    }

    public String getQueryString() {
        return this.extractor.getQueryString();
    }

    @Nullable
    private Object getSpElValue(String expressionString, Object[] values) {
        Expression expression = PARSER.parseExpression(expressionString);
        EvaluationContext evaluationContext = this.evaluationContextProvider.getEvaluationContext(this.parameters, values, ExpressionDependencies.discover(expression));
        return expression.getValue(evaluationContext);
    }
}

