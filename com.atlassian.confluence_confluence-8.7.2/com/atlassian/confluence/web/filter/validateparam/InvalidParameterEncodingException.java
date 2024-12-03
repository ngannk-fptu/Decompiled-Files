/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.web.filter.validateparam;

import java.io.UnsupportedEncodingException;

public class InvalidParameterEncodingException
extends UnsupportedEncodingException {
    private final String paramName;
    private final String invalidValue;

    public InvalidParameterEncodingException(String paramName, String invalidValue) {
        this.paramName = paramName;
        this.invalidValue = invalidValue;
    }

    @Override
    public String getMessage() {
        return "Parameter '" + this.paramName + "' has an invalid value '" + this.invalidValue + "'";
    }
}

