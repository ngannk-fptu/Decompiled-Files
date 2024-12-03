/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.jmespath;

import com.amazonaws.jmespath.InvalidTypeException;
import com.amazonaws.jmespath.JmesPathExpression;
import com.amazonaws.jmespath.JmesPathFunction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import java.util.Arrays;
import java.util.List;

public class JmesPathLengthFunction
extends JmesPathFunction {
    public JmesPathLengthFunction(JmesPathExpression ... expressions) {
        this(Arrays.asList(expressions));
    }

    public JmesPathLengthFunction(List<JmesPathExpression> arguments) {
        super(arguments);
    }

    @Override
    public JsonNode evaluate(List<JsonNode> evaluatedArgs) throws InvalidTypeException {
        JsonNode arg = evaluatedArgs.get(0);
        if (arg.isTextual()) {
            return JmesPathLengthFunction.getStringLength(arg);
        }
        if (arg.isArray() || arg.isObject()) {
            return new IntNode(arg.size());
        }
        throw new InvalidTypeException("Type mismatch. Expecting a string or an array or an object.");
    }

    private static IntNode getStringLength(JsonNode arg) {
        return new IntNode(arg.asText().length());
    }
}

