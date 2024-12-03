/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.okhttp;

public class HttpException
extends RuntimeException {
    public HttpException(String message) {
        super(message);
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }
}

