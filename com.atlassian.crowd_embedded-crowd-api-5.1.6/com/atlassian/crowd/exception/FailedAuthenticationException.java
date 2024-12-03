/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.CrowdException;

public class FailedAuthenticationException
extends CrowdException {
    public FailedAuthenticationException() {
    }

    public FailedAuthenticationException(String message) {
        super(message);
    }

    public FailedAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedAuthenticationException(Throwable cause) {
        super(cause);
    }
}

