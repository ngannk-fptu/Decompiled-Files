/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.voorhees;

import java.io.IOException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

class MethodData {
    private final String methodName;
    private final JsonNode params;
    private int arity;

    public MethodData(String methodName, JsonNode params) {
        this.methodName = methodName;
        this.params = params;
        if (params == null) {
            this.arity = 0;
        } else if (params.isObject()) {
            this.arity = 1;
        } else if (params.isArray()) {
            this.arity = params.size();
        }
    }

    public String getMethodName() {
        return this.methodName;
    }

    public int getArity() {
        return this.arity;
    }

    public Object[] getArguments(ObjectMapper objectMapper, Class[] argumentTypes) throws IOException {
        if (this.arity == 0) {
            return new Object[0];
        }
        if (this.params.isObject()) {
            return new Object[]{objectMapper.readValue(this.params, argumentTypes[0])};
        }
        Object[] arguments = new Object[argumentTypes.length];
        for (int i = 0; i < argumentTypes.length; ++i) {
            arguments[i] = objectMapper.readValue(this.params.get(i), argumentTypes[i]);
        }
        return arguments;
    }
}

