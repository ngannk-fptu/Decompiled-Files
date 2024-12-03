/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.net;

public class ResponseException
extends Exception {
    public ResponseException() {
    }

    public ResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseException(String message) {
        super(message);
    }

    public ResponseException(Throwable cause) {
        super(cause);
    }
}

