/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.jmespath;

import com.amazonaws.jmespath.Comparator;
import com.amazonaws.jmespath.InvalidTypeException;
import com.amazonaws.jmespath.JmesPathExpression;
import com.amazonaws.jmespath.JmesPathVisitor;
import com.fasterxml.jackson.databind.JsonNode;

public class OpNotEquals
extends Comparator {
    public OpNotEquals(JmesPathExpression lhsExpr, JmesPathExpression rhsExpr) {
        super(lhsExpr, rhsExpr);
    }

    @Override
    public <Input, Output> Output accept(JmesPathVisitor<Input, Output> visitor, Input input) throws InvalidTypeException {
        return visitor.visit(this, input);
    }

    @Override
    public boolean matches(JsonNode lhs, JsonNode rhs) {
        return !lhs.equals(rhs);
    }
}

