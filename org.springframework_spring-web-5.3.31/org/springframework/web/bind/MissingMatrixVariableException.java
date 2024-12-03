/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 */
package org.springframework.web.bind;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MissingRequestValueException;

public class MissingMatrixVariableException
extends MissingRequestValueException {
    private final String variableName;
    private final MethodParameter parameter;

    public MissingMatrixVariableException(String variableName, MethodParameter parameter) {
        this(variableName, parameter, false);
    }

    public MissingMatrixVariableException(String variableName, MethodParameter parameter, boolean missingAfterConversion) {
        super("", missingAfterConversion);
        this.variableName = variableName;
        this.parameter = parameter;
    }

    @Override
    public String getMessage() {
        return "Required matrix variable '" + this.variableName + "' for method parameter type " + this.parameter.getNestedParameterType().getSimpleName() + " is " + (this.isMissingAfterConversion() ? "present but converted to null" : "not present");
    }

    public final String getVariableName() {
        return this.variableName;
    }

    public final MethodParameter getParameter() {
        return this.parameter;
    }
}

