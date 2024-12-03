/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.jmespath;

import com.amazonaws.jmespath.JmesPathExpression;
import com.fasterxml.jackson.databind.JsonNode;

public abstract class Comparator
implements JmesPathExpression {
    protected final JmesPathExpression lhsExpr;
    protected final JmesPathExpression rhsExpr;

    public Comparator(JmesPathExpression lhsExpr, JmesPathExpression rhsExpr) {
        this.lhsExpr = lhsExpr;
        this.rhsExpr = rhsExpr;
    }

    public JmesPathExpression getLhsExpr() {
        return this.lhsExpr;
    }

    public JmesPathExpression getRhsExpr() {
        return this.rhsExpr;
    }

    public abstract boolean matches(JsonNode var1, JsonNode var2);
}

