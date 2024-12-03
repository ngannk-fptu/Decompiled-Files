/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pats.checker;

public class ActiveUserNotFoundException
extends RuntimeException {
    private static final long serialVersionUID = 6372226883763724005L;

    public ActiveUserNotFoundException(Throwable cause) {
        super(cause);
    }

    public ActiveUserNotFoundException(String message) {
        super(message);
    }
}

