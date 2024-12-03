/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core.service;

public class NotValidException
extends IllegalStateException {
    public NotValidException() {
    }

    public NotValidException(String message) {
        super(message);
    }

    public NotValidException(String message, Throwable cause) {
        super(message, cause);
    }
}

