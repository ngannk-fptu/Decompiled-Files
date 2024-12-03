/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.api.exception;

public class InsufficientLoginOptionsException
extends RuntimeException {
    public static final int MINIMUM_NUMBER_OF_LOGIN_OPTIONS = 1;

    protected InsufficientLoginOptionsException(String message) {
        super(String.format("%s; there must be at least %d way%s to log into the product", message, 1, ""));
    }
}

