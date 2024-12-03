/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.cache.exception;

public class EntityNotFoundException
extends Exception {
    public EntityNotFoundException() {
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotFoundException(Throwable cause) {
        super(cause);
    }
}

