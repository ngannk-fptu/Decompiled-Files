/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.jmespath;

import com.amazonaws.jmespath.InvalidTypeException;
import com.amazonaws.jmespath.JmesPathExpression;
import com.amazonaws.jmespath.JmesPathFunction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class JmesPathContainsFunction
extends JmesPathFunction {
    public JmesPathContainsFunction(JmesPathExpression ... expressions) {
        this(Arrays.asList(expressions));
    }

    public JmesPathContainsFunction(List<JmesPathExpression> expressions) {
        super(expressions);
    }

    @Override
    public JsonNode evaluate(List<JsonNode> evaluatedArgs) {
        JsonNode subject = evaluatedArgs.get(0);
        JsonNode search = evaluatedArgs.get(1);
        if (subject.isArray()) {
            return JmesPathContainsFunction.doesArrayContain(subject, search);
        }
        if (subject.isTextual()) {
            return JmesPathContainsFunction.doesStringContain(subject, search);
        }
        throw new InvalidTypeException("Type mismatch. Expecting a string or an array.");
    }

    private static BooleanNode doesArrayContain(JsonNode subject, JsonNode search) {
        Iterator<JsonNode> elements = subject.elements();
        while (elements.hasNext()) {
            if (!elements.next().equals(search)) continue;
            return BooleanNode.TRUE;
        }
        return BooleanNode.FALSE;
    }

    private static BooleanNode doesStringContain(JsonNode subject, JsonNode search) {
        if (subject.asText().contains(search.asText())) {
            return BooleanNode.TRUE;
        }
        return BooleanNode.FALSE;
    }
}

