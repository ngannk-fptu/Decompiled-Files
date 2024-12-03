/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.client.api.storage.token.exception;

import javax.annotation.Nonnull;

public class UnrecoverableTokenException
extends Exception {
    public UnrecoverableTokenException(@Nonnull String message) {
        super(message);
    }

    public UnrecoverableTokenException(@Nonnull String message, @Nonnull Throwable cause) {
        super(message, cause);
    }
}

