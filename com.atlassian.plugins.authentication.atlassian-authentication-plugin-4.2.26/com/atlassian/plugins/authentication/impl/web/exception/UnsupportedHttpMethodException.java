/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.web.exception;

public class UnsupportedHttpMethodException
extends RuntimeException {
    private String httpMethod;

    public UnsupportedHttpMethodException(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Override
    public String getMessage() {
        return this.httpMethod + " method is not supported";
    }
}

