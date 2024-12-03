/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 */
package org.springframework.web.bind;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MissingRequestValueException;

public class MissingRequestCookieException
extends MissingRequestValueException {
    private final String cookieName;
    private final MethodParameter parameter;

    public MissingRequestCookieException(String cookieName, MethodParameter parameter) {
        this(cookieName, parameter, false);
    }

    public MissingRequestCookieException(String cookieName, MethodParameter parameter, boolean missingAfterConversion) {
        super("", missingAfterConversion);
        this.cookieName = cookieName;
        this.parameter = parameter;
    }

    @Override
    public String getMessage() {
        return "Required cookie '" + this.cookieName + "' for method parameter type " + this.parameter.getNestedParameterType().getSimpleName() + " is " + (this.isMissingAfterConversion() ? "present but converted to null" : "not present");
    }

    public final String getCookieName() {
        return this.cookieName;
    }

    public final MethodParameter getParameter() {
        return this.parameter;
    }
}

