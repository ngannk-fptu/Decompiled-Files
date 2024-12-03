/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.rest.exception;

public class UserNotFoundException
extends RuntimeException {
    private static final long serialVersionUID = 8548478752163840723L;

    public UserNotFoundException(String message) {
        super(message);
    }
}

