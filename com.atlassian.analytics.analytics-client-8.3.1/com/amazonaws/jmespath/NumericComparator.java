/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.jmespath;

import com.amazonaws.jmespath.Comparator;
import com.amazonaws.jmespath.JmesPathExpression;
import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;

public abstract class NumericComparator
extends Comparator {
    public NumericComparator(JmesPathExpression lhsExpr, JmesPathExpression rhsExpr) {
        super(lhsExpr, rhsExpr);
    }

    @Override
    public final boolean matches(JsonNode lhs, JsonNode rhs) {
        return this.matches(lhs.decimalValue(), rhs.decimalValue());
    }

    public abstract boolean matches(BigDecimal var1, BigDecimal var2);
}

