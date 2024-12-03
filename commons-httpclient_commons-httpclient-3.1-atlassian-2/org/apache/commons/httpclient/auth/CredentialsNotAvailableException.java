/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.auth;

import org.apache.commons.httpclient.auth.AuthenticationException;

public class CredentialsNotAvailableException
extends AuthenticationException {
    public CredentialsNotAvailableException() {
    }

    public CredentialsNotAvailableException(String message) {
        super(message);
    }

    public CredentialsNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

