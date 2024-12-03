/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.bind;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestBindingException;

public class MissingPathVariableException
extends ServletRequestBindingException {
    private final String variableName;
    private final MethodParameter parameter;

    public MissingPathVariableException(String variableName, MethodParameter parameter) {
        super("");
        this.variableName = variableName;
        this.parameter = parameter;
    }

    @Override
    public String getMessage() {
        return "Missing URI template variable '" + this.variableName + "' for method parameter of type " + this.parameter.getNestedParameterType().getSimpleName();
    }

    public final String getVariableName() {
        return this.variableName;
    }

    public final MethodParameter getParameter() {
        return this.parameter;
    }
}

