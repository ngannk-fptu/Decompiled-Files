/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.core.authorization.exception;

public class AuthorizationCodeFlowException
extends Exception {
    private static final String FAILED_TO_CREATE_AUTHORIZATION = "Failed to create authorization";

    public AuthorizationCodeFlowException() {
        super(FAILED_TO_CREATE_AUTHORIZATION);
    }
}

