/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.client.api.lib.token;

public class TokenServiceException
extends Exception {
    public TokenServiceException(Throwable cause) {
        super(cause);
    }

    public TokenServiceException(String message) {
        super(message);
    }
}

