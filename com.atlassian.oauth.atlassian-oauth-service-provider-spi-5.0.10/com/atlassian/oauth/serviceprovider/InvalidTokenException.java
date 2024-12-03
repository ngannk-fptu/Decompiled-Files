/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth.serviceprovider;

import com.atlassian.oauth.serviceprovider.StoreException;

public class InvalidTokenException
extends StoreException {
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTokenException(Throwable cause) {
        super(cause);
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}

