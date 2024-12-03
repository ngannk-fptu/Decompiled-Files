/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.FailedAuthenticationException;

public class ExpiredCredentialException
extends FailedAuthenticationException {
    public ExpiredCredentialException() {
    }

    public ExpiredCredentialException(Throwable cause) {
        super(cause);
    }

    public ExpiredCredentialException(String message) {
        super(message);
    }

    public ExpiredCredentialException(String message, Throwable cause) {
        super(message, cause);
    }
}

