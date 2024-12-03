/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.jmespath;

import com.amazonaws.jmespath.InvalidTypeException;
import com.amazonaws.jmespath.JmesPathVisitor;

public interface JmesPathExpression {
    public <Input, Output> Output accept(JmesPathVisitor<Input, Output> var1, Input var2) throws InvalidTypeException;
}

