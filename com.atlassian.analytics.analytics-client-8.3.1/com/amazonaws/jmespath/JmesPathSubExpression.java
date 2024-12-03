/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.jmespath;

import com.amazonaws.jmespath.InvalidTypeException;
import com.amazonaws.jmespath.JmesPathExpression;
import com.amazonaws.jmespath.JmesPathVisitor;
import java.util.Arrays;
import java.util.List;

public class JmesPathSubExpression
implements JmesPathExpression {
    private final List<JmesPathExpression> expressions;

    public JmesPathSubExpression(JmesPathExpression ... expressions) {
        this(Arrays.asList(expressions));
    }

    public JmesPathSubExpression(List<JmesPathExpression> expressions) {
        this.expressions = expressions;
    }

    public List<JmesPathExpression> getExpressions() {
        return this.expressions;
    }

    @Override
    public <Input, Output> Output accept(JmesPathVisitor<Input, Output> visitor, Input input) throws InvalidTypeException {
        return visitor.visit(this, input);
    }
}

