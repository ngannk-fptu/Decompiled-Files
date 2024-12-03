/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.expression.Expression
 *  org.springframework.expression.ParseException
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.util.Assert
 */
package org.springframework.security.access.expression.method;

import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.util.Assert;

@Deprecated
abstract class AbstractExpressionBasedMethodConfigAttribute
implements ConfigAttribute {
    private final Expression filterExpression;
    private final Expression authorizeExpression;

    AbstractExpressionBasedMethodConfigAttribute(String filterExpression, String authorizeExpression) throws ParseException {
        Assert.isTrue((filterExpression != null || authorizeExpression != null ? 1 : 0) != 0, (String)"Filter and authorization Expressions cannot both be null");
        SpelExpressionParser parser = new SpelExpressionParser();
        this.filterExpression = filterExpression != null ? parser.parseExpression(filterExpression) : null;
        this.authorizeExpression = authorizeExpression != null ? parser.parseExpression(authorizeExpression) : null;
    }

    AbstractExpressionBasedMethodConfigAttribute(Expression filterExpression, Expression authorizeExpression) throws ParseException {
        Assert.isTrue((filterExpression != null || authorizeExpression != null ? 1 : 0) != 0, (String)"Filter and authorization Expressions cannot both be null");
        this.filterExpression = filterExpression != null ? filterExpression : null;
        this.authorizeExpression = authorizeExpression != null ? authorizeExpression : null;
    }

    Expression getFilterExpression() {
        return this.filterExpression;
    }

    Expression getAuthorizeExpression() {
        return this.authorizeExpression;
    }

    @Override
    public String getAttribute() {
        return null;
    }
}

