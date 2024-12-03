/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 *  org.springframework.expression.ParserContext
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 *  org.springframework.util.Assert
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.data.activeobjects.repository.query.DeclaredQuery;
import com.atlassian.data.activeobjects.repository.query.StringQuery;
import com.atlassian.data.activeobjects.repository.support.ActiveObjectsEntityMetadata;
import java.util.regex.Pattern;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

class ExpressionBasedStringQuery
extends StringQuery {
    private static final String EXPRESSION_PARAMETER = "?#{";
    private static final String QUOTED_EXPRESSION_PARAMETER = "?__HASH__{";
    private static final Pattern EXPRESSION_PARAMETER_QUOTING = Pattern.compile(Pattern.quote("?#{"));
    private static final Pattern EXPRESSION_PARAMETER_UNQUOTING = Pattern.compile(Pattern.quote("?__HASH__{"));
    private static final String ENTITY_NAME = "entityName";
    private static final String ENTITY_NAME_VARIABLE = "#entityName";
    private static final String ENTITY_NAME_VARIABLE_EXPRESSION = "#{#entityName}";

    public ExpressionBasedStringQuery(String query, ActiveObjectsEntityMetadata<?> metadata, SpelExpressionParser parser) {
        super(ExpressionBasedStringQuery.renderQueryIfExpressionOrReturnQuery(query, metadata, parser));
    }

    static ExpressionBasedStringQuery from(DeclaredQuery query, ActiveObjectsEntityMetadata metadata, SpelExpressionParser parser) {
        return new ExpressionBasedStringQuery(query.getQueryString(), metadata, parser);
    }

    private static String renderQueryIfExpressionOrReturnQuery(String query, ActiveObjectsEntityMetadata<?> metadata, SpelExpressionParser parser) {
        Assert.notNull((Object)query, (String)"query must not be null!");
        Assert.notNull(metadata, (String)"metadata must not be null!");
        Assert.notNull((Object)parser, (String)"parser must not be null!");
        if (!ExpressionBasedStringQuery.containsExpression(query)) {
            return query;
        }
        StandardEvaluationContext evalContext = new StandardEvaluationContext();
        evalContext.setVariable(ENTITY_NAME, (Object)metadata.getEntityName());
        query = ExpressionBasedStringQuery.potentiallyQuoteExpressionsParameter(query);
        Expression expr = parser.parseExpression(query, ParserContext.TEMPLATE_EXPRESSION);
        String result = (String)expr.getValue((EvaluationContext)evalContext, String.class);
        if (result == null) {
            return query;
        }
        return ExpressionBasedStringQuery.potentiallyUnquoteParameterExpressions(result);
    }

    private static String potentiallyUnquoteParameterExpressions(String result) {
        return EXPRESSION_PARAMETER_UNQUOTING.matcher(result).replaceAll(EXPRESSION_PARAMETER);
    }

    private static String potentiallyQuoteExpressionsParameter(String query) {
        return EXPRESSION_PARAMETER_QUOTING.matcher(query).replaceAll(QUOTED_EXPRESSION_PARAMETER);
    }

    private static boolean containsExpression(String query) {
        return query.contains(ENTITY_NAME_VARIABLE_EXPRESSION);
    }
}

