/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.bind;

import org.springframework.web.bind.MissingRequestValueException;

public class MissingServletRequestParameterException
extends MissingRequestValueException {
    private final String parameterName;
    private final String parameterType;

    public MissingServletRequestParameterException(String parameterName, String parameterType) {
        this(parameterName, parameterType, false);
    }

    public MissingServletRequestParameterException(String parameterName, String parameterType, boolean missingAfterConversion) {
        super("", missingAfterConversion);
        this.parameterName = parameterName;
        this.parameterType = parameterType;
    }

    @Override
    public String getMessage() {
        return "Required request parameter '" + this.parameterName + "' for method parameter type " + this.parameterType + " is " + (this.isMissingAfterConversion() ? "present but converted to null" : "not present");
    }

    public final String getParameterName() {
        return this.parameterName;
    }

    public final String getParameterType() {
        return this.parameterType;
    }
}

