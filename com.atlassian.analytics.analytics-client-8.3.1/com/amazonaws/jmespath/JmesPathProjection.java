/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.jmespath;

import com.amazonaws.jmespath.InvalidTypeException;
import com.amazonaws.jmespath.JmesPathExpression;
import com.amazonaws.jmespath.JmesPathVisitor;

public class JmesPathProjection
implements JmesPathExpression {
    private final JmesPathExpression lhsExpr;
    private final JmesPathExpression projectionExpr;

    public JmesPathProjection(JmesPathExpression lhsExpr, JmesPathExpression projectionExpr) {
        this.lhsExpr = lhsExpr;
        this.projectionExpr = projectionExpr;
    }

    public JmesPathExpression getLhsExpr() {
        return this.lhsExpr;
    }

    public JmesPathExpression getProjectionExpr() {
        return this.projectionExpr;
    }

    @Override
    public <Input, Output> Output accept(JmesPathVisitor<Input, Output> visitor, Input input) throws InvalidTypeException {
        return visitor.visit(this, input);
    }
}

