/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.expression.Expression
 */
package org.springframework.security.authorization.method;

import org.springframework.expression.Expression;

class ExpressionAttribute {
    static final ExpressionAttribute NULL_ATTRIBUTE = new ExpressionAttribute(null);
    private final Expression expression;

    ExpressionAttribute(Expression expression) {
        this.expression = expression;
    }

    Expression getExpression() {
        return this.expression;
    }

    public String toString() {
        return this.getClass().getSimpleName() + " [Expression=" + (this.expression != null ? this.expression.getExpressionString() : null) + "]";
    }
}

