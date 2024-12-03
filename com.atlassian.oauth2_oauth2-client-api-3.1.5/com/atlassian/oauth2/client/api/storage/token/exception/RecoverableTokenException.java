/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.client.api.storage.token.exception;

import java.time.Instant;

public class RecoverableTokenException
extends Exception {
    private final Instant invalidSince;

    public RecoverableTokenException(String message, Instant invalidSince) {
        super(message);
        this.invalidSince = invalidSince;
    }

    public RecoverableTokenException(String message, Throwable cause, Instant invalidSince) {
        super(message, cause);
        this.invalidSince = invalidSince;
    }

    public Instant getInvalidSince() {
        return this.invalidSince;
    }
}

