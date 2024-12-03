/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.user;

public class UserResolutionException
extends RuntimeException {
    public UserResolutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserResolutionException(Throwable cause) {
        super(cause);
    }

    public UserResolutionException(String message) {
        super(message);
    }
}

