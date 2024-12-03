/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.opensocial.spi;

public class ActivityServiceException
extends RuntimeException {
    public ActivityServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActivityServiceException(String message) {
        super(message);
    }

    public ActivityServiceException(Throwable cause) {
        super(cause);
    }
}

