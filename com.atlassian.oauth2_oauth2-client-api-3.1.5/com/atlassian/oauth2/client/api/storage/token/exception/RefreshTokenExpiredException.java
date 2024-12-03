/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.client.api.storage.token.exception;

import com.atlassian.oauth2.client.api.storage.token.exception.UnrecoverableTokenException;
import javax.annotation.Nonnull;

public class RefreshTokenExpiredException
extends UnrecoverableTokenException {
    public RefreshTokenExpiredException(@Nonnull String message) {
        super(message);
    }

    public RefreshTokenExpiredException(@Nonnull String message, @Nonnull Throwable cause) {
        super(message, cause);
    }
}

