/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.api;

import com.atlassian.httpclient.api.Response;

public class ResponseTooLargeException
extends RuntimeException {
    static final long serialVersionUID = 1L;
    private final Response response;

    public ResponseTooLargeException(Response response, String message) {
        super(message);
        this.response = response;
    }

    public Response getResponse() {
        return this.response;
    }
}

