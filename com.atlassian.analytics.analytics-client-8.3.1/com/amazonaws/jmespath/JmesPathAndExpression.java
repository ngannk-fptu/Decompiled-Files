/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.jmespath;

import com.amazonaws.jmespath.InvalidTypeException;
import com.amazonaws.jmespath.JmesPathExpression;
import com.amazonaws.jmespath.JmesPathVisitor;

public class JmesPathAndExpression
implements JmesPathExpression {
    private final JmesPathExpression lhsExpr;
    private final JmesPathExpression rhsExpr;

    public JmesPathAndExpression(JmesPathExpression lhsExpr, JmesPathExpression rhsExpr) {
        this.lhsExpr = lhsExpr;
        this.rhsExpr = rhsExpr;
    }

    public JmesPathExpression getLhsExpr() {
        return this.lhsExpr;
    }

    public JmesPathExpression getRhsExpr() {
        return this.rhsExpr;
    }

    @Override
    public <Input, Output> Output accept(JmesPathVisitor<Input, Output> visitor, Input input) throws InvalidTypeException {
        return visitor.visit(this, input);
    }
}

