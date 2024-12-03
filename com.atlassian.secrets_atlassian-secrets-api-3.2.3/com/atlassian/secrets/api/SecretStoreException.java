/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.api;

public class SecretStoreException
extends RuntimeException {
    public SecretStoreException(String message) {
        super(message);
    }

    public SecretStoreException(Throwable cause) {
        super(cause);
    }

    public SecretStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}

