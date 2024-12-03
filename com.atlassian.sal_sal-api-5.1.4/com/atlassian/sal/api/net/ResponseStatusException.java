/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.net;

import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;

public class ResponseStatusException
extends ResponseException {
    private final Response response;

    public ResponseStatusException(String message, Response response) {
        super(message);
        this.response = response;
    }

    public Response getResponse() {
        return this.response;
    }
}

