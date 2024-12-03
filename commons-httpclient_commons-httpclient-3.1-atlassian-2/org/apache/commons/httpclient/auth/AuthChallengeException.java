/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.auth;

import org.apache.commons.httpclient.auth.AuthenticationException;

public class AuthChallengeException
extends AuthenticationException {
    public AuthChallengeException() {
    }

    public AuthChallengeException(String message) {
        super(message);
    }

    public AuthChallengeException(String message, Throwable cause) {
        super(message, cause);
    }
}

