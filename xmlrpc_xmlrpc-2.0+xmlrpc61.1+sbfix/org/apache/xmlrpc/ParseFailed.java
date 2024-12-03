/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

public class ParseFailed
extends RuntimeException {
    protected Exception cause;

    public ParseFailed(String message) {
        super(message);
        this.cause = null;
    }

    public ParseFailed(Exception cause) {
        super(cause.getMessage());
        this.cause = cause;
    }

    public Throwable getCause() {
        return this.cause;
    }
}

