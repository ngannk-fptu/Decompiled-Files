/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.ResponseException
 */
package com.atlassian.streams.internal;

import com.atlassian.sal.api.net.ResponseException;

public class ApplinkResponseException
extends ResponseException {
    private final int statusCode;

    public ApplinkResponseException(int statusCode) {
        this.statusCode = statusCode;
    }

    public ApplinkResponseException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public ApplinkResponseException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ApplinkResponseException(Throwable cause, int statusCode) {
        super(cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}

