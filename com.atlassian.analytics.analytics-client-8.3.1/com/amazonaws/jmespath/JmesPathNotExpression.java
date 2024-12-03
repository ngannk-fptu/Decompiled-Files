/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.jmespath;

import com.amazonaws.jmespath.InvalidTypeException;
import com.amazonaws.jmespath.JmesPathExpression;
import com.amazonaws.jmespath.JmesPathVisitor;

public class JmesPathNotExpression
implements JmesPathExpression {
    private final JmesPathExpression expr;

    public JmesPathNotExpression(JmesPathExpression expr) {
        this.expr = expr;
    }

    public JmesPathExpression getExpr() {
        return this.expr;
    }

    @Override
    public <Input, Output> Output accept(JmesPathVisitor<Input, Output> visitor, Input input) throws InvalidTypeException {
        return visitor.visit(this, input);
    }
}

