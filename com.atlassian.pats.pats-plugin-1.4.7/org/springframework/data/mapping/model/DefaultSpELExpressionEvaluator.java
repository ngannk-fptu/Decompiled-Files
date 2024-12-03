/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.expression.Expression
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.mapping.model;

import org.springframework.data.mapping.model.SpELContext;
import org.springframework.data.mapping.model.SpELExpressionEvaluator;
import org.springframework.expression.Expression;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class DefaultSpELExpressionEvaluator
implements SpELExpressionEvaluator {
    private final Object source;
    private final SpELContext factory;

    public DefaultSpELExpressionEvaluator(Object source, SpELContext factory) {
        Assert.notNull((Object)source, (String)"Source must not be null!");
        Assert.notNull((Object)factory, (String)"SpELContext must not be null!");
        this.source = source;
        this.factory = factory;
    }

    @Override
    @Nullable
    public <T> T evaluate(String expression) {
        Expression parseExpression = this.factory.getParser().parseExpression(expression);
        return (T)parseExpression.getValue(this.factory.getEvaluationContext(this.source));
    }
}

