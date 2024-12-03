/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.jmespath;

import com.amazonaws.jmespath.InvalidTypeException;
import com.amazonaws.jmespath.JmesPathExpression;
import com.amazonaws.jmespath.JmesPathVisitor;

public class JmesPathFilter
implements JmesPathExpression {
    private final JmesPathExpression lhsExpr;
    private final JmesPathExpression rhsExpr;
    private final JmesPathExpression comparator;

    public JmesPathFilter(JmesPathExpression lhsExpr, JmesPathExpression rhsExpr, JmesPathExpression comparator) {
        this.lhsExpr = lhsExpr;
        this.rhsExpr = rhsExpr;
        this.comparator = comparator;
    }

    public JmesPathExpression getRhsExpr() {
        return this.rhsExpr;
    }

    public JmesPathExpression getLhsExpr() {
        return this.lhsExpr;
    }

    public JmesPathExpression getComparator() {
        return this.comparator;
    }

    @Override
    public <Input, Output> Output accept(JmesPathVisitor<Input, Output> visitor, Input input) throws InvalidTypeException {
        return visitor.visit(this, input);
    }
}

