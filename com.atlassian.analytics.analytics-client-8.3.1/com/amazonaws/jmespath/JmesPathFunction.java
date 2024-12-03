/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.jmespath;

import com.amazonaws.jmespath.InvalidTypeException;
import com.amazonaws.jmespath.JmesPathExpression;
import com.amazonaws.jmespath.JmesPathVisitor;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public abstract class JmesPathFunction
implements JmesPathExpression {
    private final List<JmesPathExpression> expressions;

    public JmesPathFunction(List<JmesPathExpression> expressions) {
        this.expressions = expressions;
    }

    public List<JmesPathExpression> getExpressions() {
        return this.expressions;
    }

    @Override
    public <Input, Output> Output accept(JmesPathVisitor<Input, Output> visitor, Input input) throws InvalidTypeException {
        return visitor.visit(this, input);
    }

    public abstract JsonNode evaluate(List<JsonNode> var1);
}

