/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.application;

public class PagingNotSupportedException
extends Exception {
    public PagingNotSupportedException(String message) {
        super(message);
    }

    public PagingNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PagingNotSupportedException(Throwable cause) {
        super(cause);
    }

    public PagingNotSupportedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

