/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.jmespath;

import com.amazonaws.jmespath.InvalidTypeException;
import com.amazonaws.jmespath.JmesPathExpression;
import com.amazonaws.jmespath.JmesPathVisitor;
import com.amazonaws.jmespath.NumericComparator;
import java.math.BigDecimal;

public class OpLessThanOrEqualTo
extends NumericComparator {
    public OpLessThanOrEqualTo(JmesPathExpression lhsExpr, JmesPathExpression rhsExpr) {
        super(lhsExpr, rhsExpr);
    }

    @Override
    public <Input, Output> Output accept(JmesPathVisitor<Input, Output> visitor, Input input) throws InvalidTypeException {
        return visitor.visit(this, input);
    }

    @Override
    public boolean matches(BigDecimal lhs, BigDecimal rhs) {
        return lhs.compareTo(rhs) <= 0;
    }
}

