/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.EvaluationException
 *  org.springframework.expression.Expression
 */
package org.springframework.security.access.expression;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;

public final class ExpressionUtils {
    private ExpressionUtils() {
    }

    public static boolean evaluateAsBoolean(Expression expr, EvaluationContext ctx) {
        try {
            return (Boolean)expr.getValue(ctx, Boolean.class);
        }
        catch (EvaluationException ex) {
            throw new IllegalArgumentException("Failed to evaluate expression '" + expr.getExpressionString() + "'", ex);
        }
    }
}

