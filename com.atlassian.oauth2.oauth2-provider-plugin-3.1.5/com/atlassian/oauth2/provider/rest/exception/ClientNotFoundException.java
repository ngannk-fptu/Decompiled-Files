/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.provider.rest.exception;

import javax.annotation.Nonnull;

public class ClientNotFoundException
extends Exception {
    public ClientNotFoundException(@Nonnull String message) {
        super(message);
    }

    public ClientNotFoundException(@Nonnull String message, @Nonnull Throwable cause) {
        super(message, cause);
    }
}

