/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.auth;

import org.apache.commons.httpclient.auth.AuthenticationException;

public class InvalidCredentialsException
extends AuthenticationException {
    public InvalidCredentialsException() {
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}

