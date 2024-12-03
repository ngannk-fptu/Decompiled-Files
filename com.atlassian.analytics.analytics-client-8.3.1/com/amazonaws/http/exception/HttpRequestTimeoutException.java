/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.http.exception;

import java.io.IOException;

public class HttpRequestTimeoutException
extends IOException {
    private static final long serialVersionUID = -2588353895012259837L;

    public HttpRequestTimeoutException(String message) {
        super(message);
    }

    public HttpRequestTimeoutException(Throwable throwable) {
        this("Request did not complete before the request timeout configuration.", throwable);
    }

    public HttpRequestTimeoutException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

