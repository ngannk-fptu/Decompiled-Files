/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth.serviceprovider;

public class StoreException
extends RuntimeException {
    public StoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public StoreException(String message) {
        super(message);
    }

    public StoreException(Throwable cause) {
        super(cause);
    }
}

