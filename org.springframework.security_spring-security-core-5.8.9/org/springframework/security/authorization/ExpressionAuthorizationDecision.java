/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.expression.Expression
 */
package org.springframework.security.authorization;

import org.springframework.expression.Expression;
import org.springframework.security.authorization.AuthorizationDecision;

public class ExpressionAuthorizationDecision
extends AuthorizationDecision {
    private final Expression expression;

    public ExpressionAuthorizationDecision(boolean granted, Expression expressionAttribute) {
        super(granted);
        this.expression = expressionAttribute;
    }

    public Expression getExpression() {
        return this.expression;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [granted=" + this.isGranted() + ", expressionAttribute=" + this.expression + ']';
    }
}

