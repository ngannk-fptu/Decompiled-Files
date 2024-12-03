/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.jmespath;

import com.amazonaws.jmespath.InvalidTypeException;
import com.amazonaws.jmespath.JmesPathExpression;
import com.amazonaws.jmespath.JmesPathVisitor;
import com.amazonaws.jmespath.ObjectMapperSingleton;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public class JmesPathLiteral
implements JmesPathExpression {
    private final JsonNode value;

    public JmesPathLiteral(String value) {
        try {
            this.value = ObjectMapperSingleton.getObjectMapper().readTree(value);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JmesPathLiteral(JsonNode value) {
        this.value = value;
    }

    public JsonNode getValue() {
        return this.value;
    }

    @Override
    public <Input, Output> Output accept(JmesPathVisitor<Input, Output> visitor, Input input) throws InvalidTypeException {
        return visitor.visit(this, input);
    }
}

