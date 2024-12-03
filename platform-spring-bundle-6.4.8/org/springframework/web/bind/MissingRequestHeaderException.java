/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.bind;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MissingRequestValueException;

public class MissingRequestHeaderException
extends MissingRequestValueException {
    private final String headerName;
    private final MethodParameter parameter;

    public MissingRequestHeaderException(String headerName, MethodParameter parameter) {
        this(headerName, parameter, false);
    }

    public MissingRequestHeaderException(String headerName, MethodParameter parameter, boolean missingAfterConversion) {
        super("", missingAfterConversion);
        this.headerName = headerName;
        this.parameter = parameter;
    }

    @Override
    public String getMessage() {
        String typeName = this.parameter.getNestedParameterType().getSimpleName();
        return "Required request header '" + this.headerName + "' for method parameter type " + typeName + " is " + (this.isMissingAfterConversion() ? "present but converted to null" : "not present");
    }

    public final String getHeaderName() {
        return this.headerName;
    }

    public final MethodParameter getParameter() {
        return this.parameter;
    }
}

