/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

public class InvalidSearchException
extends Exception {
    public InvalidSearchException(Throwable cause) {
        super(cause);
    }

    public InvalidSearchException(String message) {
        super(message);
    }

    public InvalidSearchException(String message, Throwable cause) {
        super(message, cause);
    }
}

